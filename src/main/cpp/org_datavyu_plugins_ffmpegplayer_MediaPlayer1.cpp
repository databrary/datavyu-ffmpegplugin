#include "org_datavyu_plugins_ffmpegplayer_MediaPlayer1.h"
#include "Logger.h"
#include "AVLogger.h"
#include "PacketQueue.hpp"
#include "FrameQueue.hpp"
#include "Clock.hpp"
#include "Decoder.hpp"
#include <jni.h>
#include <cstdio>
#include <cstdlib>
#include <cmath>
#include <cassert>
#include <vector>
#include <thread>
#include <chrono>
#include <algorithm>
#include <string>
#include <sstream>
#include <map>

#include "config.h"
#include <inttypes.h>
#include <math.h>
#include <limits.h>
#include <signal.h>
#include <stdint.h>

#include <assert.h>



extern "C" {
    #include <libavutil/eval.h>
    #include <libavutil/mathematics.h>
    #include <libavutil/pixdesc.h>
    #include <libavutil/imgutils.h>
    #include <libavutil/dict.h>
    #include <libavutil/parseutils.h>
    #include <libavutil/samplefmt.h>
    #include <libavutil/avassert.h>
    #include <libavutil/time.h>
    #include <libavformat/avformat.h>
    #include <libavdevice/avdevice.h>
    #include <libswscale/swscale.h>
    #include <libavutil/opt.h>
    #include <libavcodec/avfft.h>
    #include <libswresample/swresample.h>
    #include <SDL.h>
    #include <SDL_thread.h>
}

#define MAX_QUEUE_SIZE (15 * 1024 * 1024)
#define MIN_FRAMES 25
#define EXTERNAL_CLOCK_MIN_FRAMES 2
#define EXTERNAL_CLOCK_MAX_FRAMES 10

/* Minimum SDL audio buffer size, in samples. */
#define SDL_AUDIO_MIN_BUFFER_SIZE 512
/* Calculate actual buffer size keeping in mind not cause too frequent audio callbacks */
#define SDL_AUDIO_MAX_CALLBACKS_PER_SEC 30

/* Step size for volume control in dB */
#define SDL_VOLUME_STEP (0.75)

/* no AV sync correction is done if below the minimum AV sync threshold */
#define AV_SYNC_THRESHOLD_MIN 0.04
/* AV sync correction is done if above the maximum AV sync threshold */
#define AV_SYNC_THRESHOLD_MAX 0.1
/* If a frame duration is longer than this, it will not be duplicated to compensate AV sync */
#define AV_SYNC_FRAMEDUP_THRESHOLD 0.1
/* no AV correction is done if too big error */
#define AV_NOSYNC_THRESHOLD 10.0

/* maximum audio speed change to get correct sync */
#define SAMPLE_CORRECTION_PERCENT_MAX 10

/* external clock speed adjustment constants for realtime sources based on buffer fullness */
#define EXTERNAL_CLOCK_SPEED_MIN  0.900
#define EXTERNAL_CLOCK_SPEED_MAX  1.010
#define EXTERNAL_CLOCK_SPEED_STEP 0.001

/* we use about AUDIO_DIFF_AVG_NB A-V differences to make the average */
#define AUDIO_DIFF_AVG_NB   20

/* polls for possible required screen refresh at least this often, should be less than 1/fps */
#define REFRESH_RATE 0.01

/* NOTE: the size must be big enough to compensate the hardware audio buffersize size */
/* TODO: We assume that a decoded and resampled frame fits into this buffer */
#define SAMPLE_ARRAY_SIZE (8 * 65536)

#define CURSOR_HIDE_DELAY 1000000

#define USE_ONEPASS_SUBTITLE_RENDER 1

class MediaPlayer {
    private:
     typedef struct AudioParams {
            int freq;
            int channels;
            int64_t channel_layout;
            enum AVSampleFormat fmt;
            int frame_size;
            int bytes_per_sec;
        } AudioParams;
        
        enum {
            AV_SYNC_AUDIO_MASTER, /* default choice */
            AV_SYNC_VIDEO_MASTER,
            AV_SYNC_EXTERNAL_CLOCK, /* synchronize to an external clock */
        };

        // std::thread *read_tid;
        SDL_Thread *read_tid;
        AVInputFormat *iformat;

        int abort_request;
        int force_refresh;
        int paused;
        int last_paused;
        int queue_attachments_req;
        int seek_req;
        int seek_flags;
        int64_t seek_pos;
        int64_t seek_rel;
        int read_pause_return;
        AVFormatContext *ic;
        int realtime;

        Clock *pAudclk;
        Clock *pVidclk;
        Clock *pExtclk;

        FrameQueue pictq;
        FrameQueue sampq;

        Decoder *pAuddec;
        Decoder *pViddec;

        int audio_stream;

        int av_sync_type;

        double audio_clock;
        int audio_clock_serial;
        double audio_diff_cum; /* used for AV difference average computation */
        double audio_diff_avg_coef;
        double audio_diff_threshold;
        int audio_diff_avg_count;
        AVStream *audio_st;
        PacketQueue audioq;
        int audio_hw_buf_size;
        uint8_t *audio_buf;
        uint8_t *audio_buf1;
        unsigned int audio_buf_size; /* in bytes */
        unsigned int audio_buf1_size;
        int audio_buf_index; /* in bytes */
        int audio_write_buf_size;
        int audio_volume;
        int muted;
        struct AudioParams audio_src;

        struct AudioParams audio_tgt;
        struct SwrContext *swr_ctx;
        int frame_drops_early;
        int frame_drops_late;

        enum ShowMode {
            SHOW_MODE_NONE = -1, 
            SHOW_MODE_VIDEO = 0, 
            SHOW_MODE_WAVES, SHOW_MODE_RDFT, SHOW_MODE_NB
        } show_mode;

        int16_t sample_array[SAMPLE_ARRAY_SIZE];
        int sample_array_index;
        int last_i_start;
        RDFTContext *rdft;
        int rdft_bits;
        FFTSample *rdft_data;
        int xpos;
        double last_vis_time;
        
        SDL_Texture *vis_texture;
        SDL_Texture *sub_texture;
        SDL_Texture *vid_texture;

        double frame_timer;
        double frame_last_returned_time;
        double frame_last_filter_delay;
        int video_stream;
        AVStream *video_st;
        PacketQueue videoq;
        double max_frame_duration;      // maximum duration of a frame - above this, we consider the jump a timestamp discontinuity
        struct SwsContext *img_convert_ctx;
        int eof;

        char *filename;
        int width, height, xleft, ytop;
        int step;

        int last_video_stream, last_audio_stream;

        int64_t start_time;
        int64_t duration;

        double rdftspeed = 0.02;
        
        SDL_RendererInfo renderer_info = {0};

        // std::condition_variable *continue_read_thread;
        SDL_cond *continue_read_thread;

        /* options specified by the user */
        static AVInputFormat *file_iformat;
        static const char *input_filename;
        static const char *window_title;
        
        static AVPacket flush_pkt;
        static int id;
        static int default_width;
        static int default_height;
        static int screen_width;
        static int screen_height;
        static int subtitle_disable;
        static const char* wanted_stream_spec[AVMEDIA_TYPE_NB];
        static int seek_by_bytes;
        static int startup_volume;
        static int show_status;
        static int fast;
        static int genpts;
        static int lowres;
        static int decoder_reorder_pts;
        static int loop;
        static int framedrop;
        static int infinite_buffer;
        static int cursor_hidden;
        static int autorotate;
        static int find_stream_info;
        static int audio_disable;
        static int video_disable;
        static int display_disable;
        static int borderless;
        static int autoexit;
        static int exit_on_keydown;
        static int exit_on_mousedown;
        static const char *audio_codec_name;
        static const char *subtitle_codec_name;
        static const char *video_codec_name;
        static int64_t cursor_last_shown;

        /* current context */
        static int is_full_screen;
        static int64_t audio_callback_time;

        #define FF_QUIT_EVENT    (SDL_USEREVENT + 2)

        static SDL_Window *window;
        static SDL_Renderer *renderer;
        static SDL_AudioDeviceID audio_dev;

        struct TextureFormatEntry {
            enum AVPixelFormat format;
            int texture_fmt;
        };
        

        static inline int cmp_audio_fmts(enum AVSampleFormat fmt1, int64_t channel_count1,
                        enum AVSampleFormat fmt2, int64_t channel_count2) {
            /* If channel count == 1, planar and non-planar formats are the same */
            if (channel_count1 == 1 && channel_count2 == 1)
                return av_get_packed_sample_fmt(fmt1) != av_get_packed_sample_fmt(fmt2);
            else
                return channel_count1 != channel_count2 || fmt1 != fmt2;
        }

        static inline int64_t get_valid_channel_layout(int64_t channel_layout, int channels) {
            if (channel_layout && av_get_channel_layout_nb_channels(channel_layout) == channels)
                return channel_layout;
            else
                return 0;
        }

        static inline int compute_mod(int a, int b) {
            return a < 0 ? a%b + b : a%b;
        }

        static inline void fill_rectangle(int x, int y, int w, int h){
            SDL_Rect rect;
            rect.x = x;
            rect.y = y;
            rect.w = w;
            rect.h = h;
            if (w && h)
                SDL_RenderFillRect(renderer, &rect);
        }

        static int realloc_texture(SDL_Texture **texture, Uint32 new_format, int new_width, int new_height, SDL_BlendMode blendmode, int init_texture){
            Uint32 format;
            int access, w, h;
            if (!*texture || SDL_QueryTexture(*texture, &format, &access, &w, &h) < 0 || new_width != w || new_height != h || new_format != format) {
                void *pixels;
                int pitch;
                if (*texture)
                    SDL_DestroyTexture(*texture);
                if (!(*texture = SDL_CreateTexture(renderer, new_format, SDL_TEXTUREACCESS_STREAMING, new_width, new_height)))
                    return -1;
                if (SDL_SetTextureBlendMode(*texture, blendmode) < 0)
                    return -1;
                if (init_texture) {
                    if (SDL_LockTexture(*texture, NULL, &pixels, &pitch) < 0)
                        return -1;
                    memset(pixels, 0, pitch * new_height);
                    SDL_UnlockTexture(*texture);
                }
                av_log(NULL, AV_LOG_VERBOSE, "Created %dx%d texture with %s.\n", new_width, new_height, SDL_GetPixelFormatName(new_format));
            }
            return 0;
        }

        static void calculate_display_rect(SDL_Rect *rect,
                                   int scr_xleft, int scr_ytop, int scr_width, int scr_height,
                                   int pic_width, int pic_height, AVRational pic_sar)
        {
            float aspect_ratio;
            int width, height, x, y;

            if (pic_sar.num == 0)
                aspect_ratio = 0;
            else
                aspect_ratio = av_q2d(pic_sar);

            if (aspect_ratio <= 0.0)
                aspect_ratio = 1.0;
            aspect_ratio *= (float)pic_width / (float)pic_height;

            /* XXX: we suppose the screen has a 1.0 pixel ratio */
            height = scr_height;
            width = lrint(height * aspect_ratio) & ~1;
            if (width > scr_width) {
                width = scr_width;
                height = lrint(width / aspect_ratio) & ~1;
            }
            x = (scr_width - width) / 2;
            y = (scr_height - height) / 2;
            rect->x = scr_xleft + x;
            rect->y = scr_ytop  + y;
            rect->w = FFMAX(width,  1);
            rect->h = FFMAX(height, 1);
        }

        static void get_sdl_pix_fmt_and_blendmode(int format, Uint32 *sdl_pix_fmt, SDL_BlendMode *sdl_blendmode)
        {
            int i;
            *sdl_blendmode = SDL_BLENDMODE_NONE;
            *sdl_pix_fmt = SDL_PIXELFORMAT_UNKNOWN;
            if (format == AV_PIX_FMT_RGB32   ||
                format == AV_PIX_FMT_RGB32_1 ||
                format == AV_PIX_FMT_BGR32   ||
                format == AV_PIX_FMT_BGR32_1)
                *sdl_blendmode = SDL_BLENDMODE_BLEND;
            for (i = 0; i < FF_ARRAY_ELEMS(sdl_texture_format_map) - 1; i++) {
                if (format == sdl_texture_format_map[i].format) {
                    *sdl_pix_fmt = sdl_texture_format_map[i].texture_fmt;
                    return;
                }
            }
        }

        static int upload_texture(SDL_Texture **tex, AVFrame *frame, struct SwsContext **img_convert_ctx) {
            int ret = 0;
            Uint32 sdl_pix_fmt;
            SDL_BlendMode sdl_blendmode;
            get_sdl_pix_fmt_and_blendmode(frame->format, &sdl_pix_fmt, &sdl_blendmode);
            if (realloc_texture(tex, sdl_pix_fmt == SDL_PIXELFORMAT_UNKNOWN ? SDL_PIXELFORMAT_ARGB8888 : sdl_pix_fmt, frame->width, frame->height, sdl_blendmode, 0) < 0)
                return -1;
            switch (sdl_pix_fmt) {
                case SDL_PIXELFORMAT_UNKNOWN:
                    /* This should only happen if we are not using avfilter... */
                    *img_convert_ctx = sws_getCachedContext(*img_convert_ctx,
                        frame->width, frame->height, frame->format, frame->width, frame->height,
                        AV_PIX_FMT_BGRA, sws_flags, NULL, NULL, NULL);
                    if (*img_convert_ctx != NULL) {
                        uint8_t *pixels[4];
                        int pitch[4];
                        if (!SDL_LockTexture(*tex, NULL, (void **)pixels, pitch)) {
                            sws_scale(*img_convert_ctx, (const uint8_t * const *)frame->data, frame->linesize,
                                    0, frame->height, pixels, pitch);
                            SDL_UnlockTexture(*tex);
                        }
                    } else {
                        av_log(NULL, AV_LOG_FATAL, "Cannot initialize the conversion context\n");
                        ret = -1;
                    }
                    break;
                case SDL_PIXELFORMAT_IYUV:
                    if (frame->linesize[0] > 0 && frame->linesize[1] > 0 && frame->linesize[2] > 0) {
                        ret = SDL_UpdateYUVTexture(*tex, NULL, frame->data[0], frame->linesize[0],
                                                            frame->data[1], frame->linesize[1],
                                                            frame->data[2], frame->linesize[2]);
                    } else if (frame->linesize[0] < 0 && frame->linesize[1] < 0 && frame->linesize[2] < 0) {
                        ret = SDL_UpdateYUVTexture(*tex, NULL, frame->data[0] + frame->linesize[0] * (frame->height                    - 1), -frame->linesize[0],
                                                            frame->data[1] + frame->linesize[1] * (AV_CEIL_RSHIFT(frame->height, 1) - 1), -frame->linesize[1],
                                                            frame->data[2] + frame->linesize[2] * (AV_CEIL_RSHIFT(frame->height, 1) - 1), -frame->linesize[2]);
                    } else {
                        av_log(NULL, AV_LOG_ERROR, "Mixed negative and positive linesizes are not supported.\n");
                        return -1;
                    }
                    break;
                default:
                    if (frame->linesize[0] < 0) {
                        ret = SDL_UpdateTexture(*tex, NULL, frame->data[0] + frame->linesize[0] * (frame->height - 1), -frame->linesize[0]);
                    } else {
                        ret = SDL_UpdateTexture(*tex, NULL, frame->data[0], frame->linesize[0]);
                    }
                    break;
            }
            return ret;
        }

        static void video_image_display(MediaPlayer *is)
        {
            Frame *vp;
            Frame *sp = NULL;
            SDL_Rect rect;

            vp = frame_queue_peek_last(&is->pictq);

            calculate_display_rect(&rect, is->xleft, is->ytop, is->width, is->height, vp->width, vp->height, vp->sar);

            if (!vp->uploaded) {
                if (upload_texture(&is->vid_texture, vp->frame, &is->img_convert_ctx) < 0)
                    return;
                vp->uploaded = 1;
                vp->flip_v = vp->frame->linesize[0] < 0;
            }

            SDL_RenderCopyEx(renderer, is->vid_texture, NULL, &rect, 0, NULL, vp->flip_v ? SDL_FLIP_VERTICAL : 0);
            if (sp) {
        #if USE_ONEPASS_SUBTITLE_RENDER
                SDL_RenderCopy(renderer, is->sub_texture, NULL, &rect);
        #else
                int i;
                double xratio = (double)rect.w / (double)sp->width;
                double yratio = (double)rect.h / (double)sp->height;
                for (i = 0; i < sp->sub.num_rects; i++) {
                    SDL_Rect *sub_rect = (SDL_Rect*)sp->sub.rects[i];
                    SDL_Rect target = {.x = rect.x + sub_rect->x * xratio,
                                    .y = rect.y + sub_rect->y * yratio,
                                    .w = sub_rect->w * xratio,
                                    .h = sub_rect->h * yratio};
                    SDL_RenderCopy(renderer, is->sub_texture, sub_rect, &target);
                }
        #endif
            }
        }

        static void video_audio_display(MediaPlayer *s)
        {
            int i, i_start, x, y1, y, ys, delay, n, nb_display_channels;
            int ch, channels, h, h2;
            int64_t time_diff;
            int rdft_bits, nb_freq;

            for (rdft_bits = 1; (1 << rdft_bits) < 2 * s->height; rdft_bits++)
                ;
            nb_freq = 1 << (rdft_bits - 1);

            /* compute display index : center on currently output samples */
            channels = s->audio_tgt.channels;
            nb_display_channels = channels;
            if (!s->paused) {
                int data_used= s->show_mode == SHOW_MODE_WAVES ? s->width : (2*nb_freq);
                n = 2 * channels;
                delay = s->audio_write_buf_size;
                delay /= n;

                /* to be more precise, we take into account the time spent since
                the last buffer computation */
                if (audio_callback_time) {
                    time_diff = av_gettime_relative() - audio_callback_time;
                    delay -= (time_diff * s->audio_tgt.freq) / 1000000;
                }

                delay += 2 * data_used;
                if (delay < data_used)
                    delay = data_used;

                i_start= x = compute_mod(s->sample_array_index - delay * channels, SAMPLE_ARRAY_SIZE);
                if (s->show_mode == SHOW_MODE_WAVES) {
                    h = INT_MIN;
                    for (i = 0; i < 1000; i += channels) {
                        int idx = (SAMPLE_ARRAY_SIZE + x - i) % SAMPLE_ARRAY_SIZE;
                        int a = s->sample_array[idx];
                        int b = s->sample_array[(idx + 4 * channels) % SAMPLE_ARRAY_SIZE];
                        int c = s->sample_array[(idx + 5 * channels) % SAMPLE_ARRAY_SIZE];
                        int d = s->sample_array[(idx + 9 * channels) % SAMPLE_ARRAY_SIZE];
                        int score = a - d;
                        if (h < score && (b ^ c) < 0) {
                            h = score;
                            i_start = idx;
                        }
                    }
                }

                s->last_i_start = i_start;
            } else {
                i_start = s->last_i_start;
            }

            if (s->show_mode == SHOW_MODE_WAVES) {
                SDL_SetRenderDrawColor(renderer, 255, 255, 255, 255);

                /* total height for one channel */
                h = s->height / nb_display_channels;
                /* graph height / 2 */
                h2 = (h * 9) / 20;
                for (ch = 0; ch < nb_display_channels; ch++) {
                    i = i_start + ch;
                    y1 = s->ytop + ch * h + (h / 2); /* position of center line */
                    for (x = 0; x < s->width; x++) {
                        y = (s->sample_array[i] * h2) >> 15;
                        if (y < 0) {
                            y = -y;
                            ys = y1 - y;
                        } else {
                            ys = y1;
                        }
                        fill_rectangle(s->xleft + x, ys, 1, y);
                        i += channels;
                        if (i >= SAMPLE_ARRAY_SIZE)
                            i -= SAMPLE_ARRAY_SIZE;
                    }
                }

                SDL_SetRenderDrawColor(renderer, 0, 0, 255, 255);

                for (ch = 1; ch < nb_display_channels; ch++) {
                    y = s->ytop + ch * h;
                    fill_rectangle(s->xleft, y, s->width, 1);
                }
            } else {
                if (realloc_texture(&s->vis_texture, SDL_PIXELFORMAT_ARGB8888, s->width, s->height, SDL_BLENDMODE_NONE, 1) < 0)
                    return;

                nb_display_channels= FFMIN(nb_display_channels, 2);
                if (rdft_bits != s->rdft_bits) {
                    av_rdft_end(s->rdft);
                    av_free(s->rdft_data);
                    s->rdft = av_rdft_init(rdft_bits, DFT_R2C);
                    s->rdft_bits = rdft_bits;
                    s->rdft_data = av_malloc_array(nb_freq, 4 *sizeof(*s->rdft_data));
                }
                if (!s->rdft || !s->rdft_data){
                    av_log(NULL, AV_LOG_ERROR, "Failed to allocate buffers for RDFT, switching to waves display\n");
                    s->show_mode = SHOW_MODE_WAVES;
                } else {
                    FFTSample *data[2];
                    SDL_Rect rect = {.x = s->xpos, .y = 0, .w = 1, .h = s->height};
                    uint32_t *pixels;
                    int pitch;
                    for (ch = 0; ch < nb_display_channels; ch++) {
                        data[ch] = s->rdft_data + 2 * nb_freq * ch;
                        i = i_start + ch;
                        for (x = 0; x < 2 * nb_freq; x++) {
                            double w = (x-nb_freq) * (1.0 / nb_freq);
                            data[ch][x] = s->sample_array[i] * (1.0 - w * w);
                            i += channels;
                            if (i >= SAMPLE_ARRAY_SIZE)
                                i -= SAMPLE_ARRAY_SIZE;
                        }
                        av_rdft_calc(s->rdft, data[ch]);
                    }
                    /* Least efficient way to do this, we should of course
                    * directly access it but it is more than fast enough. */
                    if (!SDL_LockTexture(s->vis_texture, &rect, (void **)&pixels, &pitch)) {
                        pitch >>= 2;
                        pixels += pitch * s->height;
                        for (y = 0; y < s->height; y++) {
                            double w = 1 / sqrt(nb_freq);
                            int a = sqrt(w * sqrt(data[0][2 * y + 0] * data[0][2 * y + 0] + data[0][2 * y + 1] * data[0][2 * y + 1]));
                            int b = (nb_display_channels == 2 ) ? sqrt(w * hypot(data[1][2 * y + 0], data[1][2 * y + 1]))
                                                                : a;
                            a = FFMIN(a, 255);
                            b = FFMIN(b, 255);
                            pixels -= pitch;
                            *pixels = (a << 16) + (b << 8) + ((a+b) >> 1);
                        }
                        SDL_UnlockTexture(s->vis_texture);
                    }
                    SDL_RenderCopy(renderer, s->vis_texture, NULL, NULL);
                }
                if (!s->paused)
                    s->xpos++;
                if (s->xpos >= s->width)
                    s->xpos= s->xleft;
            }
        }
        
        static void stream_component_close(MediaPlayer *is, int stream_index) {
            AVFormatContext *ic = is->ic;
            AVCodecParameters *codecpar;

            if (stream_index < 0 || stream_index >= ic->nb_streams)
                return;
            codecpar = ic->streams[stream_index]->codecpar;

            switch (codecpar->codec_type) {
            case AVMEDIA_TYPE_AUDIO:
                is->pAuddec->decoder_abort(&is->sampq);
                SDL_CloseAudioDevice(audio_dev);
                is->pAuddec->decoder_destroy();
                swr_free(&is->swr_ctx);
                av_freep(&is->audio_buf1);
                is->audio_buf1_size = 0;
                is->audio_buf = NULL;

                if (is->rdft) {
                    av_rdft_end(is->rdft);
                    av_freep(&is->rdft_data);
                    is->rdft = NULL;
                    is->rdft_bits = 0;
                }
                break;
            case AVMEDIA_TYPE_VIDEO:
                is->pViddec->decoder_abort(&is->pictq);
                is->pViddec->decoder_destroy();
                break;
            default:
                break;
            }

            ic->streams[stream_index]->discard = AVDISCARD_ALL;
            switch (codecpar->codec_type) {
            case AVMEDIA_TYPE_AUDIO:
                is->audio_st = NULL;
                is->audio_stream = -1;
                break;
            case AVMEDIA_TYPE_VIDEO:
                is->video_st = NULL;
                is->video_stream = -1;
                break;
            default:
                break;
            }
        }

        /* pause or resume the video */
        static void stream_toggle_pause(MediaPlayer *is)
        {
            if (is->paused) {
                is->frame_timer += av_gettime_relative() / 1000000.0 - is->pVidclk->last_updated;
                if (is->read_pause_return != AVERROR(ENOSYS)) {
                    is->pVidclk->paused = 0;
                }
                is->pVidclk->set_clock(is->pVidclk->get_clock(), is->pVidclk->serial);
            }
            is->pExtclk->set_clock(is->pExtclk->get_clock(), is->pExtclk->serial);
            is->paused = is->pAudclk->paused = is->pVidclk->paused = is->pExtclk->paused = !is->paused;
        }

        static double compute_target_delay(double delay, MediaPlayer *is)
        {
            double sync_threshold, diff = 0;

            /* update delay to follow master synchronisation source */
            if (get_master_sync_type() != AV_SYNC_VIDEO_MASTER) {
                /* if video is slave, we try to correct big delays by
                duplicating or deleting a frame */
                diff = is->pVidclk->get_clock() - get_master_clock();

                /* skip or repeat frame. We take into account the
                delay to compute the threshold. I still don't know
                if it is the best guess */
                sync_threshold = FFMAX(AV_SYNC_THRESHOLD_MIN, FFMIN(AV_SYNC_THRESHOLD_MAX, delay));
                if (!isnan(diff) && fabs(diff) < is->max_frame_duration) {
                    if (diff <= -sync_threshold)
                        delay = FFMAX(0, delay + diff);
                    else if (diff >= sync_threshold && delay > AV_SYNC_FRAMEDUP_THRESHOLD)
                        delay = delay + diff;
                    else if (diff >= sync_threshold)
                        delay = 2 * delay;
                }
            }

            av_log(NULL, AV_LOG_TRACE, "video: delay=%0.3f A-V=%f\n",
                    delay, -diff);

            return delay;
        }

        static double vp_duration(MediaPlayer *is, Frame *vp, Frame *nextvp) {
            if (vp->serial == nextvp->serial) {
                double duration = nextvp->pts - vp->pts;
                if (isnan(duration) || duration <= 0 || duration > is->max_frame_duration)
                    return vp->duration;
                else
                    return duration;
            } else {
                return 0.0;
            }
        }

        static void update_video_pts(MediaPlayer *is, double pts, int64_t pos, int serial) {
            /* update current video pts */
            is->pVidclk->set_clock(pts, serial);
            Clock::sync_clock_to_slave(is->pExtclk, is->pVidclk);
        }

        
        /* called to display each frame */
        static void video_refresh(void *opaque, double *remaining_time) {
            MediaPlayer *is = opaque;
            double time;

            Frame *sp, *sp2;

            if (!is->paused && get_master_sync_type() == AV_SYNC_EXTERNAL_CLOCK && is->realtime)
                check_external_clock_speed();

            // if (!display_disable && is->show_mode != SHOW_MODE_VIDEO && is->audio_st) {
            //     time = av_gettime_relative() / 1000000.0;
            //     if (is->force_refresh || is->last_vis_time + rdftspeed < time) {
            //         video_display(is);
            //         is->last_vis_time = time;
            //     }
            //     *remaining_time = FFMIN(*remaining_time, is->last_vis_time + rdftspeed - time);
            // }

            if (is->video_st) {
        retry:
                if (FrameQueue::frame_queue_nb_remaining(&is->pictq) == 0) {
                    // nothing to do, no picture to display in the queue
                } else {
                    double last_duration, duration, delay;
                    Frame *vp, *lastvp;

                    /* dequeue the picture */
                    lastvp = FrameQueue::frame_queue_peek_last(&is->pictq);
                    vp = FrameQueue::frame_queue_peek(&is->pictq);

                    if (vp->serial != is->videoq.serial) {
                        FrameQueue::frame_queue_next(&is->pictq);
                        goto retry;
                    }

                    if (lastvp->serial != vp->serial)
                        is->frame_timer = av_gettime_relative() / 1000000.0;

                    if (is->paused)
                        goto display;

                    /* compute nominal last_duration */
                    last_duration = vp_duration(is, lastvp, vp);
                    delay = compute_target_delay(last_duration, is);

                    time= av_gettime_relative()/1000000.0;
                    if (time < is->frame_timer + delay) {
                        *remaining_time = FFMIN(is->frame_timer + delay - time, *remaining_time);
                        goto display;
                    }

                    is->frame_timer += delay;
                    if (delay > 0 && time - is->frame_timer > AV_SYNC_THRESHOLD_MAX)
                        is->frame_timer = time;

                    std::unique_lock<std::mutex> locker(is->pictq.mutex);
                    if (!isnan(vp->pts))
                        update_video_pts(is, vp->pts, vp->pos, vp->serial);
                    locker.unlock();

                    if (FrameQueue::frame_queue_nb_remaining(&is->pictq) > 1) {
                        Frame *nextvp = FrameQueue::frame_queue_peek_next(&is->pictq);
                        duration = vp_duration(is, vp, nextvp);
                        if(!is->step && (framedrop>0 || (framedrop && get_master_sync_type() != AV_SYNC_VIDEO_MASTER)) && time > is->frame_timer + duration){
                            is->frame_drops_late++;
                            FrameQueue::frame_queue_next(&is->pictq);
                            goto retry;
                        }
                    }

                    FrameQueue::frame_queue_next(&is->pictq);
                    is->force_refresh = 1;

                    if (is->step && !is->paused)
                        stream_toggle_pause(is);
                }
        //TODO: Should use it for logging until and fill the buffers
        display:
            /* display picture */
            if (!display_disable && is->force_refresh && is->show_mode == SHOW_MODE_VIDEO && is->pictq.rindex_shown)
                video_display(is);
            }
            is->force_refresh = 0;
            if (show_status) {
                static int64_t last_time;
                int64_t cur_time;
                int aqsize, vqsize, sqsize;
                double av_diff;

                cur_time = av_gettime_relative();
                if (!last_time || (cur_time - last_time) >= 30000) {
                    aqsize = 0;
                    vqsize = 0;
                    sqsize = 0;
                    if (is->audio_st)
                        aqsize = is->audioq.size;
                    if (is->video_st)
                        vqsize = is->videoq.size;
                    av_diff = 0;
                    if (is->audio_st && is->video_st)
                        av_diff = is->pAudclk->get_clock() - is->pVidclk->get_clock();
                    else if (is->video_st)
                        av_diff = get_master_clock() - is->pVidclk->get_clock();
                    else if (is->audio_st)
                        av_diff = get_master_clock() - is->pAudclk->get_clock();
                    av_log(NULL, AV_LOG_INFO,
                        "%7.2f %s:%7.3f fd=%4d aq=%5dKB vq=%5dKB sq=%5dB f=%"PRId64"/%"PRId64"   \r",
                        get_master_clock(),
                        (is->audio_st && is->video_st) ? "A-V" : (is->video_st ? "M-V" : (is->audio_st ? "M-A" : "   ")),
                        av_diff,
                        is->frame_drops_early + is->frame_drops_late,
                        aqsize / 1024,
                        vqsize / 1024,
                        sqsize,
                        is->video_st ? is->pViddec->avctx->pts_correction_num_faulty_dts : 0,
                        is->video_st ? is->pViddec->avctx->pts_correction_num_faulty_pts : 0);
                    fflush(stdout);
                    last_time = cur_time;
                }
            }
        }

        static int queue_picture(MediaPlayer *is, AVFrame *src_frame, double pts, double duration, int64_t pos, int serial)
        {
            Frame *vp;

            if (!(vp = FrameQueue::frame_queue_peek_writable(&is->pictq)))
                return -1;

            vp->sar = src_frame->sample_aspect_ratio;
            vp->uploaded = 0;

            vp->width = src_frame->width;
            vp->height = src_frame->height;
            vp->format = src_frame->format;

            vp->pts = pts;
            vp->duration = duration;
            vp->pos = pos;
            vp->serial = serial;

            // set_default_window_size(vp->width, vp->height, vp->sar);

            av_frame_move_ref(vp->frame, src_frame);
            FrameQueue::frame_queue_push(&is->pictq);
            return 0;
        }

        static int get_video_frame(MediaPlayer *is, AVFrame *frame)
        {
            int got_picture;

            if ((got_picture = is->pViddec->decoder_decode_frame(frame, NULL)) < 0)
                return -1;

            if (got_picture) {
                double dpts = NAN;

                if (frame->pts != AV_NOPTS_VALUE)
                    dpts = av_q2d(is->video_st->time_base) * frame->pts;

                frame->sample_aspect_ratio = av_guess_sample_aspect_ratio(is->ic, is->video_st, frame);

                if (framedrop>0 || (framedrop && get_master_sync_type() != AV_SYNC_VIDEO_MASTER)) {
                    if (frame->pts != AV_NOPTS_VALUE) {
                        double diff = dpts - get_master_clock();
                        if (!isnan(diff) && fabs(diff) < AV_NOSYNC_THRESHOLD &&
                            diff - is->frame_last_filter_delay < 0 &&
                            is->pViddec.pkt_serial == is->pVidclk.serial &&
                            is->videoq.nb_packets) {
                            is->frame_drops_early++;
                            av_frame_unref(frame);
                            got_picture = 0;
                        }
                    }
                }
            }

            return got_picture;
        }

        static int audio_thread(MediaPlayer *is) {
            MediaPlayer *is = is;
            AVFrame *frame = av_frame_alloc();
            Frame *af;
            int got_frame = 0;
            AVRational tb;
            int ret = 0;

            if (!frame)
                return AVERROR(ENOMEM);

            do {
                if ((got_frame = is->pAuddec->decoder_decode_frame(frame, NULL)) < 0)
                    goto the_end;

                if (got_frame) {
                        tb = (AVRational){1, frame->sample_rate};


                        if (!(af = FrameQueue::frame_queue_peek_writable(&is->sampq)))
                            goto the_end;

                        af->pts = (frame->pts == AV_NOPTS_VALUE) ? NAN : frame->pts * av_q2d(tb);
                        af->pos = frame->pkt_pos;
                        af->serial = is->pAuddec.pkt_serial;
                        af->duration = av_q2d((AVRational){frame->nb_samples, frame->sample_rate});

                        av_frame_move_ref(af->frame, frame);
                        FrameQueue::frame_queue_push(&is->sampq);
                }
            } while (ret >= 0 || ret == AVERROR(EAGAIN) || ret == AVERROR_EOF);
        the_end:
            av_frame_free(&frame);
            return ret;
        }


        static int video_thread(MediaPlayer *is) {
            MediaPlayer *is = is;
            AVFrame *frame = av_frame_alloc();
            double pts;
            double duration;
            int ret;
            AVRational tb = is->video_st->time_base;
            AVRational frame_rate = av_guess_frame_rate(is->ic, is->video_st, NULL);

            if (!frame) {
                return AVERROR(ENOMEM);
            }

            for (;;) {
                ret = get_video_frame(is, frame);
                if (ret < 0)
                    goto the_end;
                if (!ret)
                    continue;
                    duration = (frame_rate.num && frame_rate.den ? av_q2d((AVRational){frame_rate.den, frame_rate.num}) : 0);
                    pts = (frame->pts == AV_NOPTS_VALUE) ? NAN : frame->pts * av_q2d(tb);
                    ret = queue_picture(is, frame, pts, duration, frame->pkt_pos, is->pViddec.pkt_serial);
                    av_frame_unref(frame);
                if (ret < 0)
                    goto the_end;
            }
        the_end:
            av_frame_free(&frame);
            return 0;
        }

        /* copy samples for viewing in editor window */
        static void update_sample_display(MediaPlayer *is, short *samples, int samples_size) {
            int size, len;

            size = samples_size / sizeof(short);
            while (size > 0) {
                len = SAMPLE_ARRAY_SIZE - is->sample_array_index;
                if (len > size)
                    len = size;
                memcpy(is->sample_array + is->sample_array_index, samples, len * sizeof(short));
                samples += len;
                is->sample_array_index += len;
                if (is->sample_array_index >= SAMPLE_ARRAY_SIZE)
                    is->sample_array_index = 0;
                size -= len;
            }
        }

        /* return the wanted number of samples to get better sync if sync_type is video
        * or external master clock */
        static int synchronize_audio(MediaPlayer *is, int nb_samples) {
            int wanted_nb_samples = nb_samples;

            /* if not master, then we try to remove or add samples to correct the clock */
            if (get_master_sync_type() != AV_SYNC_AUDIO_MASTER) {
                double diff, avg_diff;
                int min_nb_samples, max_nb_samples;

                diff = is->pAudclk->get_clock() - get_master_clock();

                if (!isnan(diff) && fabs(diff) < AV_NOSYNC_THRESHOLD) {
                    is->audio_diff_cum = diff + is->audio_diff_avg_coef * is->audio_diff_cum;
                    if (is->audio_diff_avg_count < AUDIO_DIFF_AVG_NB) {
                        /* not enough measures to have a correct estimate */
                        is->audio_diff_avg_count++;
                    } else {
                        /* estimate the A-V difference */
                        avg_diff = is->audio_diff_cum * (1.0 - is->audio_diff_avg_coef);

                        if (fabs(avg_diff) >= is->audio_diff_threshold) {
                            wanted_nb_samples = nb_samples + (int)(diff * is->audio_src.freq);
                            min_nb_samples = ((nb_samples * (100 - SAMPLE_CORRECTION_PERCENT_MAX) / 100));
                            max_nb_samples = ((nb_samples * (100 + SAMPLE_CORRECTION_PERCENT_MAX) / 100));
                            wanted_nb_samples = av_clip(wanted_nb_samples, min_nb_samples, max_nb_samples);
                        }
                        av_log(NULL, AV_LOG_TRACE, "diff=%f adiff=%f sample_diff=%d apts=%0.3f %f\n",
                                diff, avg_diff, wanted_nb_samples - nb_samples,
                                is->audio_clock, is->audio_diff_threshold);
                    }
                } else {
                    /* too big difference : may be initial PTS errors, so
                    reset A-V filter */
                    is->audio_diff_avg_count = 0;
                    is->audio_diff_cum       = 0;
                }
            }

            return wanted_nb_samples;
        }

        /**
         * COuld be used in the java side to get the audio buffer
         * Decode one audio frame and return its uncompressed size.
         *
         * The processed audio frame is decoded, converted if required, and
         * stored in is->audio_buf, with size in bytes given by the return
         * value.
         */
        static int audio_decode_frame(MediaPlayer *is) {
            MediaPlayer *is = is;
            int data_size, resampled_data_size;
            int64_t dec_channel_layout;
            av_unused double audio_clock0;
            int wanted_nb_samples;
            Frame *af;

            if (is->paused)
                return -1;

            do {
        #if defined(_WIN32)
                while (FrameQueue::frame_queue_nb_remaining(&is->sampq) == 0) {
                    if ((av_gettime_relative() - audio_callback_time) > 1000000LL * is->audio_hw_buf_size / is->audio_tgt.bytes_per_sec / 2)
                        return -1;
                    av_usleep (1000);
                }
        #endif
                if (!(af = FrameQueue::frame_queue_peek_readable(&is->sampq)))
                    return -1;
                FrameQueue::frame_queue_next(&is->sampq);
            } while (af->serial != is->audioq.serial);

            data_size = av_samples_get_buffer_size(NULL, af->frame->channels,
                                                af->frame->nb_samples,
                                                af->frame->format, 1);

            dec_channel_layout =
                (af->frame->channel_layout && af->frame->channels == av_get_channel_layout_nb_channels(af->frame->channel_layout)) ?
                af->frame->channel_layout : av_get_default_channel_layout(af->frame->channels);
            wanted_nb_samples = synchronize_audio(is, af->frame->nb_samples);

            if (af->frame->format        != is->audio_src.fmt            ||
                dec_channel_layout       != is->audio_src.channel_layout ||
                af->frame->sample_rate   != is->audio_src.freq           ||
                (wanted_nb_samples       != af->frame->nb_samples && !is->swr_ctx)) {
                swr_free(&is->swr_ctx);
                is->swr_ctx = swr_alloc_set_opts(NULL,
                                                is->audio_tgt.channel_layout, is->audio_tgt.fmt, is->audio_tgt.freq,
                                                dec_channel_layout,           af->frame->format, af->frame->sample_rate,
                                                0, NULL);
                if (!is->swr_ctx || swr_init(is->swr_ctx) < 0) {
                    av_log(NULL, AV_LOG_ERROR,
                        "Cannot create sample rate converter for conversion of %d Hz %s %d channels to %d Hz %s %d channels!\n",
                            af->frame->sample_rate, av_get_sample_fmt_name(af->frame->format), af->frame->channels,
                            is->audio_tgt.freq, av_get_sample_fmt_name(is->audio_tgt.fmt), is->audio_tgt.channels);
                    swr_free(&is->swr_ctx);
                    return -1;
                }
                is->audio_src.channel_layout = dec_channel_layout;
                is->audio_src.channels       = af->frame->channels;
                is->audio_src.freq = af->frame->sample_rate;
                is->audio_src.fmt = af->frame->format;
            }

            if (is->swr_ctx) {
                const uint8_t **in = (const uint8_t **)af->frame->extended_data;
                uint8_t **out = &is->audio_buf1;
                int out_count = (int64_t)wanted_nb_samples * is->audio_tgt.freq / af->frame->sample_rate + 256;
                int out_size  = av_samples_get_buffer_size(NULL, is->audio_tgt.channels, out_count, is->audio_tgt.fmt, 0);
                int len2;
                if (out_size < 0) {
                    av_log(NULL, AV_LOG_ERROR, "av_samples_get_buffer_size() failed\n");
                    return -1;
                }
                if (wanted_nb_samples != af->frame->nb_samples) {
                    if (swr_set_compensation(is->swr_ctx, (wanted_nb_samples - af->frame->nb_samples) * is->audio_tgt.freq / af->frame->sample_rate,
                                                wanted_nb_samples * is->audio_tgt.freq / af->frame->sample_rate) < 0) {
                        av_log(NULL, AV_LOG_ERROR, "swr_set_compensation() failed\n");
                        return -1;
                    }
                }
                av_fast_malloc(&is->audio_buf1, &is->audio_buf1_size, out_size);
                if (!is->audio_buf1)
                    return AVERROR(ENOMEM);
                len2 = swr_convert(is->swr_ctx, out, out_count, in, af->frame->nb_samples);
                if (len2 < 0) {
                    av_log(NULL, AV_LOG_ERROR, "swr_convert() failed\n");
                    return -1;
                }
                if (len2 == out_count) {
                    av_log(NULL, AV_LOG_WARNING, "audio buffer is probably too small\n");
                    if (swr_init(is->swr_ctx) < 0)
                        swr_free(&is->swr_ctx);
                }
                is->audio_buf = is->audio_buf1;
                resampled_data_size = len2 * is->audio_tgt.channels * av_get_bytes_per_sample(is->audio_tgt.fmt);
            } else {
                is->audio_buf = af->frame->data[0];
                resampled_data_size = data_size;
            }

            audio_clock0 = is->audio_clock;
            /* update the audio clock with the pts */
            if (!isnan(af->pts))
                is->audio_clock = af->pts + (double) af->frame->nb_samples / af->frame->sample_rate;
            else
                is->audio_clock = NAN;
            is->audio_clock_serial = af->serial;
            return resampled_data_size;
        }

        static int audio_open(void *opaque, int64_t wanted_channel_layout, int wanted_nb_channels, int wanted_sample_rate, struct AudioParams *audio_hw_params) {
            SDL_AudioSpec wanted_spec, spec;
            const char *env;
            static const int next_nb_channels[] = {0, 0, 1, 6, 2, 6, 4, 6};
            static const int next_sample_rates[] = {0, 44100, 48000, 96000, 192000};
            int next_sample_rate_idx = FF_ARRAY_ELEMS(next_sample_rates) - 1;

            env = SDL_getenv("SDL_AUDIO_CHANNELS");
            if (env) {
                wanted_nb_channels = atoi(env);
                wanted_channel_layout = av_get_default_channel_layout(wanted_nb_channels);
            }
            if (!wanted_channel_layout || wanted_nb_channels != av_get_channel_layout_nb_channels(wanted_channel_layout)) {
                wanted_channel_layout = av_get_default_channel_layout(wanted_nb_channels);
                wanted_channel_layout &= ~AV_CH_LAYOUT_STEREO_DOWNMIX;
            }
            wanted_nb_channels = av_get_channel_layout_nb_channels(wanted_channel_layout);
            wanted_spec.channels = wanted_nb_channels;
            wanted_spec.freq = wanted_sample_rate;
            if (wanted_spec.freq <= 0 || wanted_spec.channels <= 0) {
                av_log(NULL, AV_LOG_ERROR, "Invalid sample rate or channel count!\n");
                return -1;
            }
            while (next_sample_rate_idx && next_sample_rates[next_sample_rate_idx] >= wanted_spec.freq)
                next_sample_rate_idx--;
            wanted_spec.format = AUDIO_S16SYS;
            wanted_spec.silence = 0;
            wanted_spec.samples = FFMAX(SDL_AUDIO_MIN_BUFFER_SIZE, 2 << av_log2(wanted_spec.freq / SDL_AUDIO_MAX_CALLBACKS_PER_SEC));
            wanted_spec.callback = sdl_audio_callback;
            wanted_spec.userdata = opaque;
            while (!(audio_dev = SDL_OpenAudioDevice(NULL, 0, &wanted_spec, &spec, SDL_AUDIO_ALLOW_FREQUENCY_CHANGE | SDL_AUDIO_ALLOW_CHANNELS_CHANGE))) {
                av_log(NULL, AV_LOG_WARNING, "SDL_OpenAudio (%d channels, %d Hz): %s\n",
                    wanted_spec.channels, wanted_spec.freq, SDL_GetError());
                wanted_spec.channels = next_nb_channels[FFMIN(7, wanted_spec.channels)];
                if (!wanted_spec.channels) {
                    wanted_spec.freq = next_sample_rates[next_sample_rate_idx--];
                    wanted_spec.channels = wanted_nb_channels;
                    if (!wanted_spec.freq) {
                        av_log(NULL, AV_LOG_ERROR,
                            "No more combinations to try, audio open failed\n");
                        return -1;
                    }
                }
                wanted_channel_layout = av_get_default_channel_layout(wanted_spec.channels);
            }
            if (spec.format != AUDIO_S16SYS) {
                av_log(NULL, AV_LOG_ERROR,
                    "SDL advised audio format %d is not supported!\n", spec.format);
                return -1;
            }
            if (spec.channels != wanted_spec.channels) {
                wanted_channel_layout = av_get_default_channel_layout(spec.channels);
                if (!wanted_channel_layout) {
                    av_log(NULL, AV_LOG_ERROR,
                        "SDL advised channel count %d is not supported!\n", spec.channels);
                    return -1;
                }
            }

            audio_hw_params->fmt = AV_SAMPLE_FMT_S16;
            audio_hw_params->freq = spec.freq;
            audio_hw_params->channel_layout = wanted_channel_layout;
            audio_hw_params->channels =  spec.channels;
            audio_hw_params->frame_size = av_samples_get_buffer_size(NULL, audio_hw_params->channels, 1, audio_hw_params->fmt, 1);
            audio_hw_params->bytes_per_sec = av_samples_get_buffer_size(NULL, audio_hw_params->channels, audio_hw_params->freq, audio_hw_params->fmt, 1);
            if (audio_hw_params->bytes_per_sec <= 0 || audio_hw_params->frame_size <= 0) {
                av_log(NULL, AV_LOG_ERROR, "av_samples_get_buffer_size failed\n");
                return -1;
            }
            return spec.size;
        }

        /* open a given stream. Return 0 if OK */
        static int stream_component_open(MediaPlayer *is, int stream_index) {
            AVFormatContext *ic = is->ic;
            AVCodecContext *avctx;
            AVCodec *codec;
            const char *forced_codec_name = NULL;
            AVDictionary *opts = NULL;
            AVDictionaryEntry *t = NULL;
            int sample_rate, nb_channels;
            int64_t channel_layout;
            int ret = 0;
            int stream_lowres = lowres;

            if (stream_index < 0 || stream_index >= ic->nb_streams)
                return -1;

            avctx = avcodec_alloc_context3(NULL);
            if (!avctx)
                return AVERROR(ENOMEM);

            ret = avcodec_parameters_to_context(avctx, ic->streams[stream_index]->codecpar);
            if (ret < 0)
                goto fail;
            avctx->pkt_timebase = ic->streams[stream_index]->time_base;

            codec = avcodec_find_decoder(avctx->codec_id);

            switch(avctx->codec_type){
                case AVMEDIA_TYPE_AUDIO   : is->last_audio_stream    = stream_index; forced_codec_name =    audio_codec_name; break;
                case AVMEDIA_TYPE_VIDEO   : is->last_video_stream    = stream_index; forced_codec_name =    video_codec_name; break;
            }
            if (forced_codec_name)
                codec = avcodec_find_decoder_by_name(forced_codec_name);
            if (!codec) {
                if (forced_codec_name) av_log(NULL, AV_LOG_WARNING,
                                            "No codec could be found with name '%s'\n", forced_codec_name);
                else                   av_log(NULL, AV_LOG_WARNING,
                                            "No decoder could be found for codec %s\n", avcodec_get_name(avctx->codec_id));
                ret = AVERROR(EINVAL);
                goto fail;
            }

            avctx->codec_id = codec->id;
            if (stream_lowres > codec->max_lowres) {
                av_log(avctx, AV_LOG_WARNING, "The maximum value for lowres supported by the decoder is %d\n",
                        codec->max_lowres);
                stream_lowres = codec->max_lowres;
            }
            avctx->lowres = stream_lowres;

            if (fast)
                avctx->flags2 |= AV_CODEC_FLAG2_FAST;

            opts = filter_codec_opts(codec_opts, avctx->codec_id, ic, ic->streams[stream_index], codec);
            if (!av_dict_get(opts, "threads", NULL, 0))
                av_dict_set(&opts, "threads", "auto", 0);
            if (stream_lowres)
                av_dict_set_int(&opts, "lowres", stream_lowres, 0);
            if (avctx->codec_type == AVMEDIA_TYPE_VIDEO || avctx->codec_type == AVMEDIA_TYPE_AUDIO)
                av_dict_set(&opts, "refcounted_frames", "1", 0);
            if ((ret = avcodec_open2(avctx, codec, &opts)) < 0) {
                goto fail;
            }
            if ((t = av_dict_get(opts, "", NULL, AV_DICT_IGNORE_SUFFIX))) {
                av_log(NULL, AV_LOG_ERROR, "Option %s not found.\n", t->key);
                ret =  AVERROR_OPTION_NOT_FOUND;
                goto fail;
            }

            is->eof = 0;
            ic->streams[stream_index]->discard = AVDISCARD_DEFAULT;
            switch (avctx->codec_type) {
            case AVMEDIA_TYPE_AUDIO:

                sample_rate    = avctx->sample_rate;
                nb_channels    = avctx->channels;
                channel_layout = avctx->channel_layout;

                /* prepare audio output */
                if ((ret = audio_open(is, channel_layout, nb_channels, sample_rate, &is->audio_tgt)) < 0)
                    goto fail;
                is->audio_hw_buf_size = ret;
                is->audio_src = is->audio_tgt;
                is->audio_buf_size  = 0;
                is->audio_buf_index = 0;

                /* init averaging filter */
                is->audio_diff_avg_coef  = exp(log(0.01) / AUDIO_DIFF_AVG_NB);
                is->audio_diff_avg_count = 0;
                /* since we do not have a precise anough audio FIFO fullness,
                we correct audio sync only if larger than this threshold */
                is->audio_diff_threshold = (double)(is->audio_hw_buf_size) / is->audio_tgt.bytes_per_sec;

                is->audio_stream = stream_index;
                is->audio_st = ic->streams[stream_index];

                is->pAuddec = new Decoder(avctx, &is->audioq, is->continue_read_thread);
                if ((is->ic->iformat->flags & (AVFMT_NOBINSEARCH | AVFMT_NOGENSEARCH | AVFMT_NO_BYTE_SEEK)) && !is->ic->iformat->read_seek) {
                    is->pAuddec->start_pts = is->audio_st->start_time;
                    is->pAuddec->start_pts_tb = is->audio_st->time_base;
                }
                if ((ret = is->pAuddec->decoder_start(audio_thread, is)) < 0)
                    goto out;
                // SDL_PauseAudioDevice(audio_dev, 0);
                break;

            case AVMEDIA_TYPE_VIDEO:
                is->video_stream = stream_index;
                is->video_st = ic->streams[stream_index];

                is->pViddec =  new Decoder(avctx, &is->videoq, is->continue_read_thread);
                if ((ret = is->pViddec->decoder_start(video_thread, is)) < 0)
                    goto out;
                is->queue_attachments_req = 1;
                break;

            default:
                break;
            }

            goto out;

        fail:
            avcodec_free_context(&avctx);

        out:
            av_dict_free(&opts);

            return ret;
        }

        static int decode_interrupt_cb(void *ctx) {
            MediaPlayer *is = ctx;
            return is->abort_request;
        }

        static int stream_has_enough_packets(AVStream *st, int stream_id, PacketQueue *queue) {
            return stream_id < 0 ||
                queue->abort_request ||
                (st->disposition & AV_DISPOSITION_ATTACHED_PIC) ||
                queue->nb_packets > MIN_FRAMES && (!queue->duration || av_q2d(st->time_base) * queue->duration > 1.0);
        }

        static int is_realtime(AVFormatContext *s) {
            if(   !strcmp(s->iformat->name, "rtp")
            || !strcmp(s->iformat->name, "rtsp")
            || !strcmp(s->iformat->name, "sdp")
            )
                return 1;

            if(s->pb && (   !strncmp(s->url, "rtp:", 4)
                        || !strncmp(s->url, "udp:", 4)
                        )
            )
                return 1;
            return 0;
        }

        /* this thread gets the stream from the disk or the network */
        static int read_thread(MediaPlayer *is) {
            MediaPlyer *is = is;
            AVFormatContext *ic = NULL;
            int err, i, ret;
            int st_index[AVMEDIA_TYPE_NB];
            AVPacket pkt1, *pkt = &pkt1;
            int64_t stream_start_time;
            int pkt_in_play_range = 0;
            AVDictionaryEntry *t;
            std::mutex *wait_mutex;
            int scan_all_pmts_set = 0;
            int64_t pkt_ts;

            if (!wait_mutex) {
                av_log(NULL, AV_LOG_FATAL, "Could not create wait mutex in read thread");
                ret = AVERROR(ENOMEM);
                goto fail;
            }

            memset(st_index, -1, sizeof(st_index));
            is->last_video_stream = is->video_stream = -1;
            is->last_audio_stream = is->audio_stream = -1;
            is->last_subtitle_stream = is->subtitle_stream = -1;
            is->eof = 0;

            ic = avformat_alloc_context();
            if (!ic) {
                av_log(NULL, AV_LOG_FATAL, "Could not allocate context.\n");
                ret = AVERROR(ENOMEM);
                goto fail;
            }
            ic->interrupt_callback.callback = decode_interrupt_cb;
            ic->interrupt_callback.opaque = is;
            if (!av_dict_get(format_opts, "scan_all_pmts", NULL, AV_DICT_MATCH_CASE)) {
                av_dict_set(&format_opts, "scan_all_pmts", "1", AV_DICT_DONT_OVERWRITE);
                scan_all_pmts_set = 1;
            }
            err = avformat_open_input(&ic, is->filename, is->iformat, &format_opts);
            if (err < 0) {
                print_error(is->filename, err);
                ret = -1;
                goto fail;
            }
            if (scan_all_pmts_set)
                av_dict_set(&format_opts, "scan_all_pmts", NULL, AV_DICT_MATCH_CASE);

            if ((t = av_dict_get(format_opts, "", NULL, AV_DICT_IGNORE_SUFFIX))) {
                av_log(NULL, AV_LOG_ERROR, "Option %s not found.\n", t->key);
                ret = AVERROR_OPTION_NOT_FOUND;
                goto fail;
            }
            is->ic = ic;

            if (genpts)
                ic->flags |= AVFMT_FLAG_GENPTS;

            av_format_inject_global_side_data(ic);

            if (find_stream_info) {
                AVDictionary **opts = setup_find_stream_info_opts(ic, codec_opts);
                int orig_nb_streams = ic->nb_streams;

                err = avformat_find_stream_info(ic, opts);

                for (i = 0; i < orig_nb_streams; i++)
                    av_dict_free(&opts[i]);
                av_freep(&opts);

                if (err < 0) {
                    av_log(NULL, AV_LOG_WARNING,
                        "%s: could not find codec parameters\n", is->filename);
                    ret = -1;
                    goto fail;
                }
            }

            if (ic->pb)
                ic->pb->eof_reached = 0; // FIXME hack, ffplay maybe should not use avio_feof() to test for the end

            if (seek_by_bytes < 0)
                seek_by_bytes = !!(ic->iformat->flags & AVFMT_TS_DISCONT) && strcmp("ogg", ic->iformat->name);

            is->max_frame_duration = (ic->iformat->flags & AVFMT_TS_DISCONT) ? 10.0 : 3600.0;

            if (!window_title && (t = av_dict_get(ic->metadata, "title", NULL, 0)))
                window_title = av_asprintf("%s - %s", t->value, input_filename);

            /* if seeking requested, we execute it */
            if (start_time != AV_NOPTS_VALUE) {
                int64_t timestamp;

                timestamp = start_time;
                /* add the stream start time */
                if (ic->start_time != AV_NOPTS_VALUE)
                    timestamp += ic->start_time;
                ret = avformat_seek_file(ic, -1, INT64_MIN, timestamp, INT64_MAX, 0);
                if (ret < 0) {
                    av_log(NULL, AV_LOG_WARNING, "%s: could not seek to position %0.3f\n",
                            is->filename, (double)timestamp / AV_TIME_BASE);
                }
            }

            is->realtime = is_realtime(ic);

            if (show_status)
                av_dump_format(ic, 0, is->filename, 0);

            for (i = 0; i < ic->nb_streams; i++) {
                AVStream *st = ic->streams[i];
                enum AVMediaType type = st->codecpar->codec_type;
                st->discard = AVDISCARD_ALL;
                if (type >= 0 && wanted_stream_spec[type] && st_index[type] == -1)
                    if (avformat_match_stream_specifier(ic, st, wanted_stream_spec[type]) > 0)
                        st_index[type] = i;
            }

            for (i = 0; i < AVMEDIA_TYPE_NB; i++) {
                if (wanted_stream_spec[i] && st_index[i] == -1) {
                    av_log(NULL, AV_LOG_ERROR, "Stream specifier %s does not match any %s stream\n", wanted_stream_spec[i], av_get_media_type_string(i));
                    st_index[i] = INT_MAX;
                }
            }

            if (!video_disable)
                st_index[AVMEDIA_TYPE_VIDEO] =
                    av_find_best_stream(ic, AVMEDIA_TYPE_VIDEO,
                                        st_index[AVMEDIA_TYPE_VIDEO], -1, NULL, 0);
            if (!audio_disable)
                st_index[AVMEDIA_TYPE_AUDIO] =
                    av_find_best_stream(ic, AVMEDIA_TYPE_AUDIO,
                                        st_index[AVMEDIA_TYPE_AUDIO],
                                        st_index[AVMEDIA_TYPE_VIDEO],
                                        NULL, 0);

            is->show_mode = show_mode;

            if (st_index[AVMEDIA_TYPE_VIDEO] >= 0) {
                AVStream *st = ic->streams[st_index[AVMEDIA_TYPE_VIDEO]];
                AVCodecParameters *codecpar = st->codecpar;
                AVRational sar = av_guess_sample_aspect_ratio(ic, st, NULL);
                if (codecpar->width)
                    // set_default_window_size(codecpar->width, codecpar->height, sar);
            }

            /* open the streams */
            if (st_index[AVMEDIA_TYPE_AUDIO] >= 0) {
                stream_component_open(is, st_index[AVMEDIA_TYPE_AUDIO]);
            }

            ret = -1;
            if (st_index[AVMEDIA_TYPE_VIDEO] >= 0) {
                ret = stream_component_open(is, st_index[AVMEDIA_TYPE_VIDEO]);
            }
            if (is->show_mode == SHOW_MODE_NONE)
                is->show_mode = ret >= 0 ? SHOW_MODE_VIDEO : SHOW_MODE_RDFT;

            if (st_index[AVMEDIA_TYPE_SUBTITLE] >= 0) {
                stream_component_open(is, st_index[AVMEDIA_TYPE_SUBTITLE]);
            }

            if (is->video_stream < 0 && is->audio_stream < 0) {
                av_log(NULL, AV_LOG_FATAL, "Failed to open file '%s' or configure filtergraph\n",
                    is->filename);
                ret = -1;
                goto fail;
            }

            if (infinite_buffer < 0 && is->realtime)
                infinite_buffer = 1;

            for (;;) {
                if (is->abort_request)
                    break;
                if (is->paused != is->last_paused) {
                    is->last_paused = is->paused;
                    if (is->paused)
                        is->read_pause_return = av_read_pause(ic);
                    else
                        av_read_play(ic);
                }
                if (is->seek_req) {
                    int64_t seek_target = is->seek_pos;
                    int64_t seek_min    = is->seek_rel > 0 ? seek_target - is->seek_rel + 2: INT64_MIN;
                    int64_t seek_max    = is->seek_rel < 0 ? seek_target - is->seek_rel - 2: INT64_MAX;
        // FIXME the +-2 is due to rounding being not done in the correct direction in generation
        //      of the seek_pos/seek_rel variables

                    ret = avformat_seek_file(is->ic, -1, seek_min, seek_target, seek_max, is->seek_flags);
                    if (ret < 0) {
                        av_log(NULL, AV_LOG_ERROR,
                            "%s: error while seeking\n", is->ic->url);
                    } else {
                        if (is->audio_stream >= 0) {
                            PacketQueue::packet_queue_flush(&is->audioq);
                            PacketQueue::packet_queue_put(&is->audioq, &flush_pkt);
                        }
                        if (is->subtitle_stream >= 0) {
                            PacketQueue::packet_queue_flush(&is->subtitleq);
                            PacketQueue::packet_queue_put(&is->subtitleq, &flush_pkt);
                        }
                        if (is->video_stream >= 0) {
                            PacketQueue::packet_queue_flush(&is->videoq);
                            PacketQueue::packet_queue_put(&is->videoq, &flush_pkt);
                        }
                        if (is->seek_flags & AVSEEK_FLAG_BYTE) {
                            is->pExtclk->set_clock(NAN, 0);
                        } else {
                            &is->pExtclk->set_clock(seek_target / (double)AV_TIME_BASE, 0);
                        }
                    }
                    is->seek_req = 0;
                    is->queue_attachments_req = 1;
                    is->eof = 0;
                    if (is->paused)
                        step_to_next_frame(is);
                }
                if (is->queue_attachments_req) {
                    if (is->video_st && is->video_st->disposition & AV_DISPOSITION_ATTACHED_PIC) {
                        AVPacket copy = { 0 };
                        if ((ret = av_packet_ref(&copy, &is->video_st->attached_pic)) < 0)
                            goto fail;
                        PacketQueue::packet_queue_put(&is->videoq, &copy);
                        PacketQueue::packet_queue_put_nullpacket(&is->videoq, is->video_stream);
                    }
                    is->queue_attachments_req = 0;
                }

                /* if the queue are full, no need to read more */
                if (infinite_buffer<1 &&
                    (is->audioq.size + is->videoq.size + is->subtitleq.size > MAX_QUEUE_SIZE
                    || (stream_has_enough_packets(is->audio_st, is->audio_stream, &is->audioq) &&
                        stream_has_enough_packets(is->video_st, is->video_stream, &is->videoq) &&
                        stream_has_enough_packets(is->subtitle_st, is->subtitle_stream, &is->subtitleq)))) {
                    /* wait 10 ms */
                    std::unique_lock<std::mutex> locker(wait_mutex);
                    is->continue_read_thread->wait_until(wait_mutex, 10);
                    locker(wait_mutex)
                    continue;
                }
                if (!is->paused &&
                    (!is->audio_st || (is->pAuddec.finished == is->audioq.serial && FrameQueue::frame_queue_nb_remaining(&is->sampq) == 0)) &&
                    (!is->video_st || (is->pViddec.finished == is->videoq.serial && FrameQueue::frame_queue_nb_remaining(&is->pictq) == 0))) {
                    if (loop != 1 && (!loop || --loop)) {
                        stream_seek(is, start_time != AV_NOPTS_VALUE ? start_time : 0, 0, 0);
                    } else if (autoexit) {
                        ret = AVERROR_EOF;
                        goto fail;
                    }
                }
                ret = av_read_frame(ic, pkt);
                if (ret < 0) {
                    if ((ret == AVERROR_EOF || avio_feof(ic->pb)) && !is->eof) {
                        if (is->video_stream >= 0)
                            PacketQueue::packet_queue_put_nullpacket(&is->videoq, is->video_stream);
                        if (is->audio_stream >= 0)
                            PacketQueue::packet_queue_put_nullpacket(&is->audioq, is->audio_stream);
                        if (is->subtitle_stream >= 0)
                            PacketQueue::packet_queue_put_nullpacket(&is->subtitleq, is->subtitle_stream);
                        is->eof = 1;
                    }
                    if (ic->pb && ic->pb->error)
                        break;
                    std::unique_lock<std::mutex> locker(wait_mutex);
                    is->continue_read_thread->wait_until(wait_mutex, 10);
                    locker(wait_mutex)
                    continue;
                } else {
                    is->eof = 0;
                }
                /* check if packet is in play range specified by user, then queue, otherwise discard */
                stream_start_time = ic->streams[pkt->stream_index]->start_time;
                pkt_ts = pkt->pts == AV_NOPTS_VALUE ? pkt->dts : pkt->pts;
                pkt_in_play_range = duration == AV_NOPTS_VALUE ||
                        (pkt_ts - (stream_start_time != AV_NOPTS_VALUE ? stream_start_time : 0)) *
                        av_q2d(ic->streams[pkt->stream_index]->time_base) -
                        (double)(start_time != AV_NOPTS_VALUE ? start_time : 0) / 1000000
                        <= ((double)duration / 1000000);
                if (pkt->stream_index == is->audio_stream && pkt_in_play_range) {
                    PacketQueue::packet_queue_put(&is->audioq, pkt);
                } else if (pkt->stream_index == is->video_stream && pkt_in_play_range
                        && !(is->video_st->disposition & AV_DISPOSITION_ATTACHED_PIC)) {
                    PacketQueue::packet_queue_put(&is->videoq, pkt);
                } else if (pkt->stream_index == is->subtitle_stream && pkt_in_play_range) {
                    PacketQueue::packet_queue_put(&is->subtitleq, pkt);
                } else {
                    av_packet_unref(pkt);
                }
            }

            ret = 0;
        fail:
            if (ic && !is->ic)
                avformat_close_input(&ic);

            if (ret != 0) {
                SDL_Event event;

                event.type = FF_QUIT_EVENT;
                event.user.data1 = is;
                SDL_PushEvent(&event);
            }
            wait_mutex->destroy();
            return 0;
        }

        static int opt_sync(void *optctx, const char *opt, const char *arg) {
            if (!strcmp(arg, "audio"))
                av_sync_type = AV_SYNC_AUDIO_MASTER;
            else if (!strcmp(arg, "video"))
                av_sync_type = AV_SYNC_VIDEO_MASTER;
            else if (!strcmp(arg, "ext"))
                av_sync_type = AV_SYNC_EXTERNAL_CLOCK;
            else {
                av_log(NULL, AV_LOG_ERROR, "Unknown value for %s: %s\n", opt, arg);
                exit(1);
            }
            return 0;
        }

        static int opt_seek(void *optctx, const char *opt, const char *arg) {
            start_time = parse_time_or_die(opt, arg, 1);
            return 0;
        }

        static int get_master_sync_type() {
            if (av_sync_type == AV_SYNC_VIDEO_MASTER) {
                if (is->video_st)
                    return AV_SYNC_VIDEO_MASTER;
                else
                    return AV_SYNC_AUDIO_MASTER;
            } else if (av_sync_type == AV_SYNC_AUDIO_MASTER) {
                if (is->audio_st)
                    return AV_SYNC_AUDIO_MASTER;
                else
                    return AV_SYNC_EXTERNAL_CLOCK;
            } else {
                return AV_SYNC_EXTERNAL_CLOCK;
            }
        }

        /* get the current master clock value */
        static double get_master_clock() {
            double val;

            switch (get_master_sync_type()) {
                case AV_SYNC_VIDEO_MASTER:
                    val = is->pVidclk->get_clock();
                    break;
                case AV_SYNC_AUDIO_MASTER:
                    val = is->pAudclk->get_clock();
                    break;
                default:
                    val = is->pExtclk->get_clock();
                    break;
            }
            return val;
        }

        static void check_external_clock_speed() {
            if (is->video_stream >= 0 && is->videoq.nb_packets <= EXTERNAL_CLOCK_MIN_FRAMES ||
                is->audio_stream >= 0 && is->audioq.nb_packets <= EXTERNAL_CLOCK_MIN_FRAMES) {
                set_clock_speed(is->pExtclk, FFMAX(EXTERNAL_CLOCK_SPEED_MIN, is->pExtclk.speed - EXTERNAL_CLOCK_SPEED_STEP));
            } else if ((is->video_stream < 0 || is->videoq.nb_packets > EXTERNAL_CLOCK_MAX_FRAMES) &&
                        (is->audio_stream < 0 || is->audioq.nb_packets > EXTERNAL_CLOCK_MAX_FRAMES)) {
                set_clock_speed(is->pExtclk, FFMIN(EXTERNAL_CLOCK_SPEED_MAX, is->pExtclk.speed + EXTERNAL_CLOCK_SPEED_STEP));
            } else {
                double speed = is->pExtclk.speed;
                if (speed != 1.0)
                    set_clock_speed(is->pExtclk, speed + EXTERNAL_CLOCK_SPEED_STEP * (1.0 - speed) / fabs(1.0 - speed));
            }
        }

    public:
    unsigned sws_flags = SWS_BICUBIC;
    TextureFormatEntry sdl_texture_format_map[20] = {
            { AV_PIX_FMT_RGB8,           SDL_PIXELFORMAT_RGB332 },
            { AV_PIX_FMT_RGB444,         SDL_PIXELFORMAT_RGB444 },
            { AV_PIX_FMT_RGB555,         SDL_PIXELFORMAT_RGB555 },
            { AV_PIX_FMT_BGR555,         SDL_PIXELFORMAT_BGR555 },
            { AV_PIX_FMT_RGB565,         SDL_PIXELFORMAT_RGB565 },
            { AV_PIX_FMT_BGR565,         SDL_PIXELFORMAT_BGR565 },
            { AV_PIX_FMT_RGB24,          SDL_PIXELFORMAT_RGB24 },
            { AV_PIX_FMT_BGR24,          SDL_PIXELFORMAT_BGR24 },
            { AV_PIX_FMT_0RGB32,         SDL_PIXELFORMAT_RGB888 },
            { AV_PIX_FMT_0BGR32,         SDL_PIXELFORMAT_BGR888 },
            { AV_PIX_FMT_NE(RGB0, 0BGR), SDL_PIXELFORMAT_RGBX8888 },
            { AV_PIX_FMT_NE(BGR0, 0RGB), SDL_PIXELFORMAT_BGRX8888 },
            { AV_PIX_FMT_RGB32,          SDL_PIXELFORMAT_ARGB8888 },
            { AV_PIX_FMT_RGB32_1,        SDL_PIXELFORMAT_RGBA8888 },
            { AV_PIX_FMT_BGR32,          SDL_PIXELFORMAT_ABGR8888 },
            { AV_PIX_FMT_BGR32_1,        SDL_PIXELFORMAT_BGRA8888 },
            { AV_PIX_FMT_YUV420P,        SDL_PIXELFORMAT_IYUV },
            { AV_PIX_FMT_YUYV422,        SDL_PIXELFORMAT_YUY2 },
            { AV_PIX_FMT_UYVY422,        SDL_PIXELFORMAT_UYVY },
            { AV_PIX_FMT_NONE,           SDL_PIXELFORMAT_UNKNOWN },
        };
        MediaPlayer() :
            av_sync_type(AV_SYNC_AUDIO_MASTER),
            start_time(AV_NOPTS_VALUE),
            duration(AV_NOPTS_VALUE),
            show_mode(SHOW_MODE_NONE) {}

        virtual ~MediaPlayer() {
            delete pAudclk;
            delete pVidclk;
            delete pExtclk;
            delete pAuddec;
            delete pViddec;
        }

        static int getId() {
            return ++id;
        }

        /* seek in the stream */
        static void stream_seek(MediaPlayer *is, int64_t pos, int64_t rel, int seek_by_bytes)
        {
            if (!is->seek_req) {
                is->seek_pos = pos;
                is->seek_rel = rel;
                is->seek_flags &= ~AVSEEK_FLAG_BYTE;
                if (seek_by_bytes)
                    is->seek_flags |= AVSEEK_FLAG_BYTE;
                is->seek_req = 1;

                is->continue_read_thread->notify_all();
            }
        }

        static void toggle_pause(MediaPlayer *is)
        {
            stream_toggle_pause(is);
            is->step = 0;
        }

        static void toggle_mute(MediaPlayer *is)
        {
            is->muted = !is->muted;
        }

        static void update_volume(MediaPlayer *is, int sign, double step)
        {
            double volume_level = is->audio_volume ? (20 * log(is->audio_volume / (double)SDL_MIX_MAXVOLUME) / log(10)) : -1000.0;
            int new_volume = lrint(SDL_MIX_MAXVOLUME * pow(10.0, (volume_level + sign * step) / 20.0));
            is->audio_volume = av_clip(is->audio_volume == new_volume ? (is->audio_volume + sign) : new_volume, 0, SDL_MIX_MAXVOLUME);
        }

        static void step_to_next_frame(MediaPlayer *is)
        {
            /* if the stream is paused unpause it, then step */
            if (is->paused)
                stream_toggle_pause(is);
            is->step = 1;
        }

        static MediaPlayer *stream_open(const char *filename, AVInputFormat *iformat)
        {
            MediaPlayer *is;

            is = av_mallocz(sizeof(MediaPlayer));
            if (!is)
                return NULL;
            is->filename = av_strdup(filename);
            if (!is->filename)
                goto fail;
            is->iformat = iformat;
            is->ytop    = 0;
            is->xleft   = 0;

            /* start video display */
            if (FrameQueue::frame_queue_init(&is->pictq, &is->videoq, VIDEO_PICTURE_QUEUE_SIZE, 1) < 0)
                goto fail;
            if (FrameQueue::frame_queue_init(&is->sampq, &is->audioq, SAMPLE_QUEUE_SIZE, 1) < 0)
                goto fail;

            if (PacketQueue::packet_queue_init(&is->videoq) < 0 
                || PacketQueue::packet_queue_init(&is->audioq) < 0 )
                goto fail;

            //Might create a problem
            if (!(is->continue_read_thread = new std::condition_variable())) {
                av_log(NULL, AV_LOG_FATAL, "Cannot create read thread condition variable");
                goto fail;
            }

            is->pAudclk = new Clock(is->pAudclk.serial);
            is->pVidclk = new Clock(is->pVidclk.serial);
            is->pExtclk = new Clock(is->pExtclk.serial);

            // Clock::init_clock(&is->pVidclk, &is->videoq.serial);
            // Clock::init_clock(&is->pAudclk, &is->audioq.serial);
            // Clock::init_clock(&is->pExtclk, &is->pExtclk.serial);
            is->audio_clock_serial = -1;

            // if (startup_volume < 0)
            //     av_log(NULL, AV_LOG_WARNING, "-volume=%d < 0, setting to 0\n", startup_volume);
            // if (startup_volume > 100)
            //     av_log(NULL, AV_LOG_WARNING, "-volume=%d > 100, setting to 100\n", startup_volume);
            // startup_volume = av_clip(startup_volume, 0, 100);
            // startup_volume = av_clip(SDL_MIX_MAXVOLUME * startup_volume / 100, 0, SDL_MIX_MAXVOLUME);
            startup_volume = 100;
            is->audio_volume = startup_volume;
            is->muted = 0;
            is->av_sync_type = av_sync_type;

            is->read_tid = new std::thread(read_thread, is);
            if (!is->read_tid) {
                av_log(NULL, AV_LOG_FATAL, "Cannot create read thread\n");
                goto fail;
        fail:
                stream_close(is);
                return NULL;
            }
            return is;
        }

        static void stream_close(MediaPlayer *is)
        {
            /* XXX: use a special url_shutdown call to abort parse cleanly */
            is->abort_request = 1;
            SDL_WaitThread(is->read_tid, NULL);

            is->read_tid->join();

            /* close each stream */
            if (is->audio_stream >= 0)
                stream_component_close(is, is->audio_stream);
            if (is->video_stream >= 0)
                stream_component_close(is, is->video_stream);

            avformat_close_input(&is->ic);

            PacketQueue::packet_queue_destroy(&is->videoq);
            PacketQueue::packet_queue_destroy(&is->audioq);

            /* free all pictures */
            FrameQueue::frame_queue_destory(&is->pictq);
            FrameQueue::frame_queue_destory(&is->sampq);

            is->continue_read_thread->destroy();

            SDL_DestroyCond(is->continue_read_thread);  
            sws_freeContext(is->img_convert_ctx);
            sws_freeContext(is->sub_convert_ctx);
            av_free(is->filename);

            if (is->vis_texture)
                SDL_DestroyTexture(is->vis_texture);
            if (is->vid_texture)
                SDL_DestroyTexture(is->vid_texture);
            if (is->sub_texture)
                SDL_DestroyTexture(is->sub_texture);

            av_free(is);
        }

        static void do_exit(MediaPlayer *is)
        {
            if (is) {
                stream_close(is);
            }
            if (renderer)
                SDL_DestroyRenderer(renderer);
            if (window)
                SDL_DestroyWindow(window);
            uninit_opts();
        #if CONFIG_AVFILTER
            av_freep(&vfilters_list);
        #endif
            avformat_network_deinit();
            if (show_status)
                printf("\n");
            SDL_Quit();
            av_log(NULL, AV_LOG_QUIET, "%s", "");
            exit(0);
        }

        static void sigterm_handler(int sig)
        {
            exit(123);
        }

        static void set_default_window_size(int width, int height, AVRational sar)
        {
            SDL_Rect rect;
            calculate_display_rect(&rect, 0, 0, INT_MAX, height, width, height, sar);
            default_width  = rect.w;
            default_height = rect.h;
        }

        static int video_open(MediaPlayer *is)
        {
            int w,h;

            if (screen_width) {
                w = screen_width;
                h = screen_height;
            } else {
                w = default_width;
                h = default_height;
            }

            if (!window_title)
                window_title = input_filename;
            SDL_SetWindowTitle(window, window_title);

            SDL_SetWindowSize(window, w, h);
            SDL_SetWindowPosition(window, SDL_WINDOWPOS_CENTERED, SDL_WINDOWPOS_CENTERED);
            if (is_full_screen)
                SDL_SetWindowFullscreen(window, SDL_WINDOW_FULLSCREEN_DESKTOP);
            SDL_ShowWindow(window);

            is->width  = w;
            is->height = h;

            return 0;
        }

        /* display the current picture, if any */
        static void video_display(MediaPlayer *is)
        {
            if (!is->width)
                video_open(is);

            SDL_SetRenderDrawColor(renderer, 0, 0, 0, 255);
            SDL_RenderClear(renderer);
            if (is->audio_st && is->show_mode != SHOW_MODE_VIDEO)
                video_audio_display(is);
            else if (is->video_st)
                video_image_display(is);
            SDL_RenderPresent(renderer);
        }

        /* prepare a new audio buffer */
        static void sdl_audio_callback(void *opaque, Uint8 *stream, int len)
        {
            VideoState *is = opaque;
            int audio_size, len1;

            audio_callback_time = av_gettime_relative();

            while (len > 0) {
                if (is->audio_buf_index >= is->audio_buf_size) {
                audio_size = audio_decode_frame(is);
                if (audio_size < 0) {
                        /* if error, just output silence */
                    is->audio_buf = NULL;
                    is->audio_buf_size = SDL_AUDIO_MIN_BUFFER_SIZE / is->audio_tgt.frame_size * is->audio_tgt.frame_size;
                } else {
                    if (is->show_mode != SHOW_MODE_VIDEO)
                        update_sample_display(is, (int16_t *)is->audio_buf, audio_size);
                    is->audio_buf_size = audio_size;
                }
                is->audio_buf_index = 0;
                }
                len1 = is->audio_buf_size - is->audio_buf_index;
                if (len1 > len)
                    len1 = len;
                if (!is->muted && is->audio_buf && is->audio_volume == SDL_MIX_MAXVOLUME)
                    memcpy(stream, (uint8_t *)is->audio_buf + is->audio_buf_index, len1);
                else {
                    memset(stream, 0, len1);
                    if (!is->muted && is->audio_buf)
                        SDL_MixAudioFormat(stream, (uint8_t *)is->audio_buf + is->audio_buf_index, AUDIO_S16SYS, len1, is->audio_volume);
                }
                len -= len1;
                stream += len1;
                is->audio_buf_index += len1;
            }
            is->audio_write_buf_size = is->audio_buf_size - is->audio_buf_index;
            /* Let's assume the audio driver that is used by SDL has two periods. */
            if (!isnan(is->audio_clock)) {
                set_clock_at(&is->audclk, is->audio_clock - (double)(2 * is->audio_hw_buf_size + is->audio_write_buf_size) / is->audio_tgt.bytes_per_sec, is->audio_clock_serial, audio_callback_time / 1000000.0);
                sync_clock_to_slave(&is->extclk, &is->audclk);
            }
        }

        static void refresh_loop_wait_event(MediaPlayer *is, SDL_Event *event) {
            double remaining_time = 0.0;
            SDL_PumpEvents();
            while (!SDL_PeepEvents(event, 1, SDL_GETEVENT, SDL_FIRSTEVENT, SDL_LASTEVENT)) {
                if (!cursor_hidden && av_gettime_relative() - cursor_last_shown > CURSOR_HIDE_DELAY) {
                    SDL_ShowCursor(0);
                    cursor_hidden = 1;
                }
                if (remaining_time > 0.0)
                    av_usleep((int64_t)(remaining_time * 1000000.0));
                remaining_time = REFRESH_RATE;
                if (is->show_mode != SHOW_MODE_NONE && (!is->paused || is->force_refresh))
                    video_refresh(is, &remaining_time);
                SDL_PumpEvents();
            }
        }

        /* handle an event sent by the GUI */
        static void event_loop(MediaPlayer *cur_stream)
        {
            SDL_Event event;
            double incr, pos, frac;

            for (;;) {
                double x;
                refresh_loop_wait_event(cur_stream, &event);
                switch (event.type) {
                case SDL_KEYDOWN:
                    if (exit_on_keydown) {
                        do_exit(cur_stream);
                        break;
                    }
                    switch (event.key.keysym.sym) {
                    case SDLK_ESCAPE:
                    case SDLK_q:
                        do_exit(cur_stream);
                        break;
                    case SDLK_f:
                        toggle_full_screen(cur_stream);
                        cur_stream->force_refresh = 1;
                        break;
                    case SDLK_p:
                    case SDLK_SPACE:
                        toggle_pause(cur_stream);
                        break;
                    case SDLK_m:
                        toggle_mute(cur_stream);
                        break;
                    case SDLK_KP_MULTIPLY:
                    case SDLK_0:
                        update_volume(cur_stream, 1, SDL_VOLUME_STEP);
                        break;
                    case SDLK_KP_DIVIDE:
                    case SDLK_9:
                        update_volume(cur_stream, -1, SDL_VOLUME_STEP);
                        break;
                    case SDLK_s: // S: Step to next frame
                        step_to_next_frame(cur_stream);
                        break;
                    case SDLK_a:
                        stream_cycle_channel(cur_stream, AVMEDIA_TYPE_AUDIO);
                        break;
                    case SDLK_v:
                        stream_cycle_channel(cur_stream, AVMEDIA_TYPE_VIDEO);
                        break;
                    case SDLK_c:
                        stream_cycle_channel(cur_stream, AVMEDIA_TYPE_VIDEO);
                        stream_cycle_channel(cur_stream, AVMEDIA_TYPE_AUDIO);
                        stream_cycle_channel(cur_stream, AVMEDIA_TYPE_SUBTITLE);
                        break;
                    case SDLK_t:
                        stream_cycle_channel(cur_stream, AVMEDIA_TYPE_SUBTITLE);
                        break;
                    case SDLK_w:
        #if CONFIG_AVFILTER
                        if (cur_stream->show_mode == SHOW_MODE_VIDEO && cur_stream->vfilter_idx < nb_vfilters - 1) {
                            if (++cur_stream->vfilter_idx >= nb_vfilters)
                                cur_stream->vfilter_idx = 0;
                        } else {
                            cur_stream->vfilter_idx = 0;
                            toggle_audio_display(cur_stream);
                        }
        #else
                        toggle_audio_display(cur_stream);
        #endif
                        break;
                    case SDLK_PAGEUP:
                        if (cur_stream->ic->nb_chapters <= 1) {
                            incr = 600.0;
                            goto do_seek;
                        }
                        seek_chapter(cur_stream, 1);
                        break;
                    case SDLK_PAGEDOWN:
                        if (cur_stream->ic->nb_chapters <= 1) {
                            incr = -600.0;
                            goto do_seek;
                        }
                        seek_chapter(cur_stream, -1);
                        break;
                    case SDLK_LEFT:
                        incr = -10.0;
                        goto do_seek;
                    case SDLK_RIGHT:
                        incr = 10.0;
                        goto do_seek;
                    case SDLK_UP:
                        incr = 60.0;
                        goto do_seek;
                    case SDLK_DOWN:
                        incr = -60.0;
                    do_seek:
                            if (seek_by_bytes) {
                                pos = -1;
                                if (pos < 0 && cur_stream->video_stream >= 0)
                                    pos = frame_queue_last_pos(&cur_stream->pictq);
                                if (pos < 0 && cur_stream->audio_stream >= 0)
                                    pos = frame_queue_last_pos(&cur_stream->sampq);
                                if (pos < 0)
                                    pos = avio_tell(cur_stream->ic->pb);
                                if (cur_stream->ic->bit_rate)
                                    incr *= cur_stream->ic->bit_rate / 8.0;
                                else
                                    incr *= 180000.0;
                                pos += incr;
                                stream_seek(cur_stream, pos, incr, 1);
                            } else {
                                pos = get_master_clock(cur_stream);
                                if (isnan(pos))
                                    pos = (double)cur_stream->seek_pos / AV_TIME_BASE;
                                pos += incr;
                                if (cur_stream->ic->start_time != AV_NOPTS_VALUE && pos < cur_stream->ic->start_time / (double)AV_TIME_BASE)
                                    pos = cur_stream->ic->start_time / (double)AV_TIME_BASE;
                                stream_seek(cur_stream, (int64_t)(pos * AV_TIME_BASE), (int64_t)(incr * AV_TIME_BASE), 0);
                            }
                        break;
                    default:
                        break;
                    }
                    break;
                case SDL_MOUSEBUTTONDOWN:
                    if (exit_on_mousedown) {
                        do_exit(cur_stream);
                        break;
                    }
                    if (event.button.button == SDL_BUTTON_LEFT) {
                        static int64_t last_mouse_left_click = 0;
                        if (av_gettime_relative() - last_mouse_left_click <= 500000) {
                            toggle_full_screen(cur_stream);
                            cur_stream->force_refresh = 1;
                            last_mouse_left_click = 0;
                        } else {
                            last_mouse_left_click = av_gettime_relative();
                        }
                    }
                case SDL_MOUSEMOTION:
                    if (cursor_hidden) {
                        SDL_ShowCursor(1);
                        cursor_hidden = 0;
                    }
                    cursor_last_shown = av_gettime_relative();
                    if (event.type == SDL_MOUSEBUTTONDOWN) {
                        if (event.button.button != SDL_BUTTON_RIGHT)
                            break;
                        x = event.button.x;
                    } else {
                        if (!(event.motion.state & SDL_BUTTON_RMASK))
                            break;
                        x = event.motion.x;
                    }
                        if (seek_by_bytes || cur_stream->ic->duration <= 0) {
                            uint64_t size =  avio_size(cur_stream->ic->pb);
                            stream_seek(cur_stream, size*x/cur_stream->width, 0, 1);
                        } else {
                            int64_t ts;
                            int ns, hh, mm, ss;
                            int tns, thh, tmm, tss;
                            tns  = cur_stream->ic->duration / 1000000LL;
                            thh  = tns / 3600;
                            tmm  = (tns % 3600) / 60;
                            tss  = (tns % 60);
                            frac = x / cur_stream->width;
                            ns   = frac * tns;
                            hh   = ns / 3600;
                            mm   = (ns % 3600) / 60;
                            ss   = (ns % 60);
                            av_log(NULL, AV_LOG_INFO,
                                "Seek to %2.0f%% (%2d:%02d:%02d) of total duration (%2d:%02d:%02d)       \n", frac*100,
                                    hh, mm, ss, thh, tmm, tss);
                            ts = frac * cur_stream->ic->duration;
                            if (cur_stream->ic->start_time != AV_NOPTS_VALUE)
                                ts += cur_stream->ic->start_time;
                            stream_seek(cur_stream, ts, 0, 0);
                        }
                    break;
                case SDL_WINDOWEVENT:
                    switch (event.window.event) {
                        case SDL_WINDOWEVENT_RESIZED:
                            screen_width  = cur_stream->width  = event.window.data1;
                            screen_height = cur_stream->height = event.window.data2;
                            if (cur_stream->vis_texture) {
                                SDL_DestroyTexture(cur_stream->vis_texture);
                                cur_stream->vis_texture = NULL;
                            }
                        case SDL_WINDOWEVENT_EXPOSED:
                            cur_stream->force_refresh = 1;
                    }
                    break;
                case SDL_QUIT:
                case FF_QUIT_EVENT:
                    do_exit(cur_stream);
                    break;
                default:
                    break;
                }
            }
        }

        static void toggle_audio_display(MediaPlayer *is)
        {
            int next = is->show_mode;
            do {
                next = (next + 1) % SHOW_MODE_NB;
            } while (next != is->show_mode && (next == SHOW_MODE_VIDEO && !is->video_st || next != SHOW_MODE_VIDEO && !is->audio_st));
            if (is->show_mode != next) {
                is->force_refresh = 1;
                is->show_mode = next;
            }
        }
};

int MediaPlayer::id = -1;
int MediaPlayer::default_width = 640;
int MediaPlayer::default_height = 480;
int MediaPlayer::screen_width = 0;
int MediaPlayer::screen_height = 0;
int MediaPlayer::subtitle_disable = 1;
const char *MediaPlayer::wanted_stream_spec[AVMEDIA_TYPE_NB] = {0};
int MediaPlayer::seek_by_bytes = -1;
int MediaPlayer::startup_volume = 100;
int MediaPlayer::show_status = 1;
int MediaPlayer::fast = 0;
int MediaPlayer::genpts = 0;
int MediaPlayer::lowres = 0;
int MediaPlayer::decoder_reorder_pts = -1;
int MediaPlayer::loop = 1;
int MediaPlayer::framedrop = -1;
int MediaPlayer::infinite_buffer = -1;
int MediaPlayer::cursor_hidden = 0;
int MediaPlayer::autorotate = 1;
int MediaPlayer::find_stream_info = 1;

std::map<int, MediaPlayer*> idToMovieStream;

MediaPlayer* getMediaPlayer(int streamId) {
    return idToMovieStream.find(streamId) != idToMovieStream.end() ? idToMovieStream[streamId] : nullptr;
}

/* Called from the main */
int main(int argc, char **argv)
{
    int flags;
    VideoState *is;

    init_dynload();

    av_log_set_flags(AV_LOG_SKIP_REPEATED);
    parse_loglevel(argc, argv, options);

    /* register all codecs, demux and protocols */
    avformat_network_init();

    // init_opts();

    signal(SIGINT , sigterm_handler); /* Interrupt (ANSI).    */
    signal(SIGTERM, sigterm_handler); /* Termination (ANSI).  */

    show_banner(argc, argv, options);

    // parse_options(NULL, argc, argv, options, opt_input_file);

    const char *input_filename = "C:\\Users\DatavyuTests\\Documents\\Resources\\OPF Files\\DatavyuSample";
	const char *input_fileformat = ".mp4";
    

    if (!input_filename) {
        show_usage();
        av_log(NULL, AV_LOG_FATAL, "An input file must be specified\n");
        av_log(NULL, AV_LOG_FATAL,
               "Use -h to get full help or, even better, run 'man %s'\n", program_name);
        exit(1);
    }

    if (display_disable) {
        video_disable = 1;
    }
    flags = SDL_INIT_VIDEO | SDL_INIT_AUDIO | SDL_INIT_TIMER;
    if (audio_disable)
        flags &= ~SDL_INIT_AUDIO;
    else {
        /* Try to work around an occasional ALSA buffer underflow issue when the
         * period size is NPOT due to ALSA resampling by forcing the buffer size. */
        if (!SDL_getenv("SDL_AUDIO_ALSA_SET_BUFFER_SIZE"))
            SDL_setenv("SDL_AUDIO_ALSA_SET_BUFFER_SIZE","1", 1);
    }
    if (display_disable)
        flags &= ~SDL_INIT_VIDEO;
    if (SDL_Init (flags)) {
        av_log(NULL, AV_LOG_FATAL, "Could not initialize SDL - %s\n", SDL_GetError());
        av_log(NULL, AV_LOG_FATAL, "(Did you set the DISPLAY variable?)\n");
        exit(1);
    }

    SDL_EventState(SDL_SYSWMEVENT, SDL_IGNORE);
    SDL_EventState(SDL_USEREVENT, SDL_IGNORE);

    av_init_packet(&flush_pkt);
    flush_pkt.data = (uint8_t *)&flush_pkt;

    if (!display_disable) {
        int flags = SDL_WINDOW_HIDDEN;
        if (borderless)
            flags |= SDL_WINDOW_BORDERLESS;
        else
            flags |= SDL_WINDOW_RESIZABLE;
        window = SDL_CreateWindow(program_name, SDL_WINDOWPOS_UNDEFINED, SDL_WINDOWPOS_UNDEFINED, default_width, default_height, flags);
        SDL_SetHint(SDL_HINT_RENDER_SCALE_QUALITY, "linear");
        if (window) {
            renderer = SDL_CreateRenderer(window, -1, SDL_RENDERER_ACCELERATED | SDL_RENDERER_PRESENTVSYNC);
            if (!renderer) {
                av_log(NULL, AV_LOG_WARNING, "Failed to initialize a hardware accelerated renderer: %s\n", SDL_GetError());
                renderer = SDL_CreateRenderer(window, -1, 0);
            }
            if (renderer) {
                if (!SDL_GetRendererInfo(renderer, &renderer_info))
                    av_log(NULL, AV_LOG_VERBOSE, "Initialized %s renderer.\n", renderer_info.name);
            }
        }
        if (!window || !renderer || !renderer_info.num_texture_formats) {
            av_log(NULL, AV_LOG_FATAL, "Failed to create window or renderer: %s", SDL_GetError());
            do_exit(NULL);
        }
    }

    is = stream_open(input_filename, file_iformat);
    if (!is) {
        av_log(NULL, AV_LOG_FATAL, "Failed to initialize VideoState!\n");
        do_exit(NULL);
    }

    event_loop(is);

    /* never returns */

    return 0;
}

JNIEXPORT jintArray JNICALL Java_org_datavyu_plugins_ffmpegplayer_MediaPlayer0_open0(JNIEnv *env, jclass thisClass,
    jstring jFileName, jstring jFileFormat) {

    av_log_set_flags(AV_LOG_SKIP_REPEATED);
    av_log_set_level(AV_LOG_DEBUG);

    avformat_network_init();

	int errNo = 0;
    jintArray returnArray = env->NewIntArray(2);
    jint *returnValues = env->GetIntArrayElements(returnArray, NULL);

	const char *input_filename = env->GetStringUTFChars(jFileName, 0);
	const char *input_fileformat = env->GetStringUTFChars(jFileFormat, 0);

    if (!input_filename) {
        show_usage();
        av_log(NULL, AV_LOG_FATAL, "An input file must be specified\n");
        return -1;
    }

    if (!input_fileformat) {
        show_usage();
        av_log(NULL, AV_LOG_FATAL, "An input file format must be specified\n");
        return -1;
    }

    av_init_packet(&flush_pkt);
    flush_pkt.data = (uint8_t *)&flush_pkt;

    int streamId = MediaPlayer::getId();
    returnValues[0] = 0;
    returnValues[1] = streamId;
	
    std::string logFileName = std::string(fileName, strlen(fileName));
    logFileName = logFileName.substr(logFileName.find_last_of("/\\") + 1) + ".log";

    MediaPlayer* mediaPlayer = stream_open(input_filename, input_fileformat);
    if (!mediaPlayer){
        av_log(NULL, AV_LOG_FATAL, "Failed to initialize MediaPlayer!\n");
        return -1;
    }

	// Free strings
	env->ReleaseStringUTFChars(jFileName, fileName);
	env->ReleaseStringUTFChars(jVersion, version);

	// Add player to the map
	idToMovieStream[streamId] = mediaPlayer;

    env->ReleaseIntArrayElements(returnArray, returnValues, NULL);
    return returnArray;
}

JNIEXPORT jdouble JNICALL Java_org_datavyu_plugins_ffmpegplayer_MediaPlayer0_getAverageFrameRate0(JNIEnv *env,
    jclass thisClass, jint streamId) {
    MediaPlayer* mediaPlayer = getMediaPlayer(streamId);
    return 0;
}

JNIEXPORT jboolean JNICALL Java_org_datavyu_plugins_ffmpegplayer_MediaPlayer0_hasVideoStream0(JNIEnv *env,
    jclass thisClass, jint streamId) {
    MediaPlayer* mediaPlayer = getMediaPlayer(streamId);
    return 0;
}

JNIEXPORT jboolean JNICALL Java_org_datavyu_plugins_ffmpegplayer_MediaPlayer0_hasAudioStream0(JNIEnv *env,
    jclass thisClass, jint streamId) {
    MediaPlayer* mediaPlayer = getMediaPlayer(streamId);
    return 0;
}

JNIEXPORT jdouble JNICALL Java_org_datavyu_plugins_ffmpegplayer_MediaPlayer0_getStartTime0(JNIEnv *env,
    jclass thisClass, jint streamId) {
    MediaPlayer* mediaPlayer = getMediaPlayer(streamId);
    return 0;
}

JNIEXPORT jdouble JNICALL Java_org_datavyu_plugins_ffmpegplayer_MediaPlayer0_getEndTime0(JNIEnv *env,
    jclass thisClass, jint streamId) {
    MediaPlayer* mediaPlayer = getMediaPlayer(streamId);
    return 0;
}

JNIEXPORT jdouble JNICALL Java_org_datavyu_plugins_ffmpegplayer_MediaPlayer0_getDuration0(JNIEnv *env,
    jclass thisClass, jint streamId) {
    MediaPlayer* mediaPlayer = getMediaPlayer(streamId);
    return 0;
}

JNIEXPORT jdouble JNICALL Java_org_datavyu_plugins_ffmpegplayer_MediaPlayer0_getCurrentTime0(JNIEnv *env,
    jclass thisClass, jint streamId) {
    MediaPlayer* mediaPlayer = getMediaPlayer(streamId);
    return -1;
}

JNIEXPORT void JNICALL Java_org_datavyu_plugins_ffmpegplayer_MediaPlayer0_setAudioSyncDelay0(JNIEnv *env,
    jclass thisClass, jint streamId, jlong delay) {
    // TODO(fraudies): Wire this up
}

JNIEXPORT void JNICALL Java_org_datavyu_plugins_ffmpegplayer_MediaPlayer0_play0(JNIEnv *env, jclass thisClass,
       jint streamId) {
    // Nothing to do here for now
}

JNIEXPORT void JNICALL Java_org_datavyu_plugins_ffmpegplayer_MediaPlayer0_stop0(JNIEnv *env, jclass thisClass,
    jint streamId) {
    // Nothing to do here for now
}

JNIEXPORT void JNICALL Java_org_datavyu_plugins_ffmpegplayer_MediaPlayer0_pause0(JNIEnv *env, jclass thisClass,
    jint streamId) {
    MediaPlayer* mediaPlayer = getMediaPlayer(streamId);
    if (mediaPlayer != nullptr) {
        // mediaPlayer->pause();
    }
}

JNIEXPORT void JNICALL Java_org_datavyu_plugins_ffmpegplayer_MediaPlayer0_seek0(JNIEnv *env,
    jclass thisClass, jint streamId, jdouble jTime) {
    MediaPlayer* mediaPlayer = getMediaPlayer(streamId);
    if (mediaPlayer != nullptr) {
        // mediaPlayer->seek(jTime); // set current time limits range
    }
}

JNIEXPORT void JNICALL Java_org_datavyu_plugins_ffmpegplayer_MediaPlayer0_setSpeed0(JNIEnv *env,
    jclass thisClass, jint streamId, jfloat speed) {
    MediaPlayer* mediaPlayer = getMediaPlayer(streamId);
    if (mediaPlayer != nullptr) {
        // mediaPlayer->setSpeed(speed);
    }
}

JNIEXPORT void JNICALL Java_org_datavyu_plugins_ffmpegplayer_MediaPlayer0_reset0(JNIEnv *env,
    jclass thisClass, jint streamId) {
    // Nothing to do here
}

JNIEXPORT void JNICALL Java_org_datavyu_plugins_ffmpegplayer_MediaPlayer0_close0(JNIEnv *env, jclass thisClass,
    jint streamId) {
    MediaPlayer* mediaPlayer = getMediaPlayer(streamId);
    if (mediaPlayer != nullptr) {
        mediaPlayer->close();
        idToMovieStream.erase(streamId);
        delete mediaPlayer;
    }
}

JNIEXPORT jboolean JNICALL Java_org_datavyu_plugins_ffmpegplayer_MediaPlayer0_loadNextAudioData0(JNIEnv *env,
    jclass thisClass, jint streamId) {
    MediaPlayer* mediaPlayer = getMediaPlayer(streamId);
    return 0;
}

JNIEXPORT jobject JNICALL Java_org_datavyu_plugins_ffmpegplayer_MediaPlayer0_getAudioBuffer0(JNIEnv *env,
    jclass thisClass, jint streamId, jint nBytes) {
    MediaPlayer* mediaPlayer = getMediaPlayer(streamId);
    return 0;
}

JNIEXPORT jstring JNICALL Java_org_datavyu_plugins_ffmpegplayer_MediaPlayer0_getSampleFormat0(JNIEnv *env,
    jclass thisClass, jint streamId) {
    MediaPlayer* mediaPlayer = getMediaPlayer(streamId);
    return 0;
}

JNIEXPORT jstring JNICALL Java_org_datavyu_plugins_ffmpegplayer_MediaPlayer0_getCodecName0(JNIEnv *env,
    jclass thisClass, jint streamId) {
    MediaPlayer* mediaPlayer = getMediaPlayer(streamId);
    return 0;
}

JNIEXPORT jfloat JNICALL Java_org_datavyu_plugins_ffmpegplayer_MediaPlayer0_getSampleRate0(JNIEnv *env,
    jclass thisClass, jint streamId) {
    MediaPlayer* mediaPlayer = getMediaPlayer(streamId);
    return 0;
}

JNIEXPORT jint JNICALL Java_org_datavyu_plugins_ffmpegplayer_MediaPlayer0_getSampleSizeInBits0(JNIEnv *env,
    jclass thisClass, jint streamId) {
    MediaPlayer* mediaPlayer = getMediaPlayer(streamId);
    return 0;
}

JNIEXPORT jint JNICALL Java_org_datavyu_plugins_ffmpegplayer_MediaPlayer0_getNumberOfSoundChannels0(JNIEnv *env,
    jclass thisClass, jint streamId) {
    MediaPlayer* mediaPlayer = getMediaPlayer(streamId);
    return 0;
}

JNIEXPORT jint JNICALL Java_org_datavyu_plugins_ffmpegplayer_MediaPlayer0_getFrameSize0(JNIEnv *env,
    jclass thisClass, jint streamId) {
    MediaPlayer* mediaPlayer = getMediaPlayer(streamId);
    return 0;
}

JNIEXPORT jfloat JNICALL Java_org_datavyu_plugins_ffmpegplayer_MediaPlayer0_getFrameRate0(JNIEnv *env,
    jclass thisClass, jint streamId) {
    MediaPlayer* mediaPlayer = getMediaPlayer(streamId);
    return 0;
}

JNIEXPORT jboolean JNICALL Java_org_datavyu_plugins_ffmpegplayer_MediaPlayer0_bigEndian0(JNIEnv *env,
    jclass thisClass, jint streamId) {
    MediaPlayer* mediaPlayer = getMediaPlayer(streamId);
    return 0;
}

JNIEXPORT jint JNICALL Java_org_datavyu_plugins_ffmpegplayer_MediaPlayer0_getNumberOfColorChannels0(JNIEnv *env,
    jclass thisClass, jint streamId) {
    MediaPlayer* mediaPlayer = getMediaPlayer(streamId);
    return 0;
}

JNIEXPORT jint JNICALL Java_org_datavyu_plugins_ffmpegplayer_MediaPlayer0_getHeight0(JNIEnv *env, jclass thisClass,
    jint streamId) {
    MediaPlayer* mediaPlayer = getMediaPlayer(streamId);
    return 0;
}

JNIEXPORT jint JNICALL Java_org_datavyu_plugins_ffmpegplayer_MediaPlayer0_getWidth0(JNIEnv *env, jclass thisClass,
    jint streamId) {
    MediaPlayer* mediaPlayer = getMediaPlayer(streamId);
    return 0;
}

JNIEXPORT jobject JNICALL Java_org_datavyu_plugins_ffmpegplayer_MediaPlayer0_getImageBuffer0(JNIEnv *env,
    jclass thisClass, jint streamId) {
    MediaPlayer* mediaPlayer = getMediaPlayer(streamId);
    return 0;
}

JNIEXPORT jint JNICALL Java_org_datavyu_plugins_ffmpegplayer_MediaPlayer0_loadNextImageFrame0(JNIEnv *env,
    jclass thisClass, jint streamId) {
    MediaPlayer* mediaPlayer = getMediaPlayer(streamId);
    return 0;
}