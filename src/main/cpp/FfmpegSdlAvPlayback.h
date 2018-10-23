#ifndef FFMPEGSDLAVPLAYBACK_H_
#define FFMPEGSDLAVPLAYBACK_H_

#include <atomic>
#include "VideoState.h"
#include "FfmpegAVPlayback.h"

extern "C" {
	#include <SDL2/SDL.h>
	#include <SDL2/SDL_thread.h>
}

#define FF_QUIT_EVENT (SDL_USEREVENT + 2)

class FfmpegSdlAvPlayback : public FfmpegAvPlayback {
private:
	SDL_Window* window;
	SDL_Renderer* renderer;
	SDL_AudioDeviceID audio_dev = 0;
	int ytop, xleft;
	int xpos;

	struct SwsContext *img_convert_ctx;
	struct SwsContext *sub_convert_ctx;

	SDL_Texture *vis_texture;
	SDL_Texture *sub_texture;
	SDL_Texture *vid_texture;

	int last_i_start;

	int screen_width;
	int screen_height;
	int is_full_screen;

	int audio_volume;

	int64_t cursor_last_shown;
	int cursor_hidden;
	char *window_title;
	SDL_RendererInfo renderer_info;

	inline int compute_mod(int a, int b);

	inline void fill_rectangle(int x, int y, int w, int h);

	static void calculate_display_rect(SDL_Rect *rect,
		int scr_xleft, int scr_ytop, int scr_width, int scr_height,
		int pic_width, int pic_height, AVRational pic_sar);

	std::atomic<bool> stopped = false;
	std::thread* display_tid = nullptr;

	void InitSdl(); // This is private because it has to be called on the same thread as the looping
	int video_open(const char* filename);
	void closeAudioDevice();
	void video_image_display();
	void stop_display_loop();

	static int kDefaultWidth;
	static int kDefaultHeight;
	static unsigned kSwsFlags;
	static const char* kDefaultWindowTitle; // assumed if the file can't be opened
	static int kWindowResizable;
	static int kAudioMinBufferSize;
	static int kAudioMaxCallbackPerSec;
	static double kVolumeStepInDecibel;
	static double kRefreshRate;
	static int kCursorHideDelayInMillis;

	struct TextureFormatEntry {
		enum AVPixelFormat format;
		int texture_fmt;
	};
	
	static const TextureFormatEntry kTextureFormatMap[];

public:
	FfmpegSdlAvPlayback(int startup_volume = SDL_MIX_MAXVOLUME);
	~FfmpegSdlAvPlayback();

	int Init(const char *filename, AVInputFormat *iformat);

	VideoState* get_VideoState();

	int audio_open(int64_t wanted_channel_layout, int wanted_nb_channels, int wanted_sample_rate,
		struct AudioParams *audio_hw_params);

	static void set_default_window_size(int width, int height, AVRational sar);

	void pauseAudioDevice();

	/* copy samples for viewing in editor window */
	static void update_sample_display(short *samples, int samples_size);

	void toggle_full_screen();

	int upload_texture(SDL_Texture **tex, AVFrame *frame, struct SwsContext **img_convert_ctx);

	static void get_sdl_pix_fmt_and_blendmode(int format, Uint32 *sdl_pix_fmt, SDL_BlendMode *sdl_blendmode);

	int realloc_texture(SDL_Texture **texture, Uint32 new_format, int new_width, 
						int new_height, SDL_BlendMode blendmode, int init_texture);

	/* display the current picture, if any */
	void video_display();

	int get_audio_volume() const;

	void update_volume(int sign, double step);

	// Function Called from the event loop
	void refresh_loop_wait_event(SDL_Event *event);

	/* called to display each frame */
	void video_refresh(double *remaining_time);

	void destroy();

	void init_and_event_loop();

	int init_and_start_display_loop();
};

static void sdl_audio_callback_bridge(void* vs, Uint8 *stream, int len) {
	FfmpegSdlAvPlayback* pFfmpegSdlAvPlayback = static_cast<FfmpegSdlAvPlayback*>(vs);
	VideoState* pVideoState = pFfmpegSdlAvPlayback->get_VideoState();
	pVideoState->audio_callback(stream, len);

	// Note, the mixer can work inplace using the same stream as src and dest, see source code here
	// https://github.com/davidsiaw/SDL2/blob/c315c99d46f89ef8dbb1b4eeab0fe38ea8a8b6c5/src/audio/SDL_mixer.c
	if (!pVideoState->get_muted() && stream)
		SDL_MixAudioFormat(stream, stream, AUDIO_S16SYS, len, pFfmpegSdlAvPlayback->get_audio_volume());
}

#endif FFMPEGSDLAVPLAYBACK_H_
