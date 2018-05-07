#include "org_datavyu_plugins_ffmpegplayer_MoviePlayer0.h"
#include "FrameBuffer.hpp"
#include "AudioBuffer.h"
#include "AudioFormatHelper.hpp"
#include "Logger.h"
#include "AVLogger.h"
#include "Clock.hpp"
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

extern "C" {
	#include <libavcodec/avcodec.h> // codecs
	#include <libavformat/avformat.h> // formats
	#include <libswscale/swscale.h> // sampling of images
	#include <libswresample/swresample.h> // resampling of audio
	#include <libavutil/error.h> // error codes
	#include <libavutil/time.h> // timer
}

// Florian Raudies, Mountain View, CA.
// vcvarsall.bat x64

// To create the header file run 'javah -d ../cpp org.datavyu.plugins.ffmpegplayer.MoviePlayer0'
// from the directory 'src/main/java'

/*
cl org_datavyu_plugins_ffmpegplayer_MoviePlayer0.cpp /Fe"..\..\..\MoviePlayer0" /I"C:\Users\Florian\FFmpeg\FFmpeg-n3.4" /I"C:\Program Files\Java\jdk1.8.0_151\include" /I"C:\Program Files\Java\jdk1.8.0_151\include\win32" /showIncludes /MD /LD /link "C:\Program Files\Java\jdk1.8.0_151\lib\jawt.lib" "C:\Users\Florian\FFmpeg\FFmpeg-n3.4\libavcodec\avcodec.lib" "C:\Users\Florian\FFmpeg\FFmpeg-n3.4\libavformat\avformat.lib" "C:\Users\Florian\FFmpeg\FFmpeg-n3.4\libavutil\avutil.lib" "C:\Users\Florian\FFmpeg\FFmpeg-n3.4\libswscale\swscale.lib" "C:\Users\Florian\FFmpeg\FFmpeg-n3.4\libswresample\swresample.lib"
*/

// Add flag /MDd for debugging information and flag /DEBUG:FULL to add all symbols to the PDB file
// On debug flag for cl; see https://docs.microsoft.com/en-us/cpp/build/reference/debug-generate-debug-info

// use dumpbin /ALL MoviePlayer.lib to list the function symbols
// Make sure that visual studio's 'vc' folder is in the path

/* no AV sync correction is done if below the minimum AV sync threshold */
#define AV_SYNC_THRESHOLD_MIN 0.04
/* AV sync correction is done if above the maximum AV sync threshold */
#define AV_SYNC_THRESHOLD_MAX 0.1
/* If a frame duration is longer than this, it will not be duplicated to compensate AV sync */
#define AV_SYNC_FRAMEDUP_THRESHOLD 0.1
/* no AV correction is done if too big error */
#define AV_NOSYNC_THRESHOLD 10.0

#define PTS_DELTA_THRESHOLD 3
#define MAX_AUDIO_FRAME_SIZE 192000
#define SAMPLE_CORRECTION_PERCENT_MAX 10
#define AUDIO_DIFF_AVG_NB 20
#define AUDIO_AVG_COEF exp(log(0.01 / AUDIO_DIFF_AVG_NB))
#define WAIT_TIMEOUT_IN_MSEC 250

enum {
    AV_SYNC_AUDIO_MASTER, /* default choice */
    AV_SYNC_VIDEO_MASTER,
    AV_SYNC_EXTERNAL_CLOCK, /* synchronize to an external clock */
};


/*****************************************************************************************
 * Movie player class that encapsulates functionality for the playback of a movie
 ****************************************************************************************/
class MoviePlayer {

public:
    /** Clocks for synchronization for video, audio, external */
    Clock audioClock;
    Clock videoClock;
    Clock externalClock;
    int avSyncType;
    bool paused;
    double frameTimer;
    double maxFrameDuration; // maximum duration of a frame - above this, we consider the jump a timestamp discontinuity
    double remainingTime; // accumulates remaining time for frame display

    /** Id counter for the streams that are opened */
    static int id;

    /** Basic Information */

    /** Width of the image in pixels */
    int width;

    /** Height of the image in pixels */
    int height;

    /** Number of color channels */
    int	nChannel;

    /** Duration of the video in seconds */
    double duration;


    /**  Variables to playback the image stream within the video file */

    /** Audio and video format context */
    AVFormatContext	*pFormatCtx;

    /** Index of the 1st video stream */
    int	iImageStream;

    /** Image stream */
    AVStream *pImageStream;

    /** Image codec context */
    AVCodecContext *pImageCodecCtx;

    /** Image codec */
    AVCodec *pImageCodec;

    /** Image frame displayed (wrapped by buffer) */
    AVFrame *pAVFrameShow;

    /** Dictionary is a key-value store */
    AVDictionary *pOptsDict;

    /** Scaling context from source to target image format and size */
    struct SwsContext *pSwsImageCtx;

    /** Pointer to the image buffer */
    FrameBuffer *pFrameBuffer;


    /** Status in the movie file. */

    /** Quit the decoding thread */
    bool quit;

    /** Flag indicates that we loaded a movie */
    bool loadedMovie;

    /** Last written time stamp. Set to 0 on reset */
    int64_t	lastWritePts;

    /** Average of time stamp intervals. Set to 1 on reset. Used for reverse seek */
    int64_t	avgDeltaPts;

    /** Decoding thread that decodes frames and is started when opening a video */
    std::thread	*pDecodeFrame;


    /** Variables to playback the audio stream. */
    /** Index of the 1st audio stream */
    int	iAudioStream;

    /** The audio input codec context (before transcoding) */
    AVCodecContext *pAudioInCodecCtx;

    /** The audio output codec context (after transcoding) */
    AVCodecContext *pAudioOutCodecCtx;

    /** Context for resampling of the audio signal */
    SwrContext *pResampleCtx;

    /** Audio stream */
    AVStream *pAudioStream;

    /** Buffer for the audio packet data */
    AudioBuffer	*pAudioBuffer;

    /** The audio buffer data. This pointer is shared with the java buffer */
    uint8_t	*pAudioBufferData;

    /** Number of bytes in the audio buffer. Set to 0 on reset */
    unsigned int nAudioBuffer;

    /** Number of data in the audio buffer. Set to 0 on reset */
    unsigned int nAudioData;

    /** Index of audio data in bytes. Set to 0 on reset */
    unsigned int iAudioData;

    /** Used to compute the average difference for the audio */
    double audioDiffCum;

    /** Holds the average coefficient for teh audio difference */
    double audioDiffAvgCoef;

    /** Holds the audio difference threshold. Set when opening the audio buffer */
    double audioDiffThreshold;

    /** Holds the average audio difference count */
    int audioDiffAvgCount;

    /** Used to keep time in the audio decoding */
    double audioTime;

    /** This boolean is set if we do not play sound */
    std::atomic<bool> playSound;


    /**  Variables that control random seeking. */

    /** Flag to initiate a random seek request */
    bool seekReq;

    /** The time stamp that should be present after a random seek */
    int64_t seekPts;

    /** Logger */
    Logger *pLogger;

    /** Parameters for methods converting the audio signal. These DO NOT NEED TO BE INITIALIZED OR FREED */

    /** Packet for 'audioDecodeFrame' method */
    AVPacket *pAudioDecodeFramePkt;

    /** Pointer to data for the 'audioDecodeFrame' method */
    uint8_t *pAudioDecodePktData;

    /** Size of the audio decode packet used in the 'audioDecodeFrame' method */
    int audioDecodePktSize;

    /** AVFrame used in the 'audioDecodeFrame' method */
    AVFrame *pAudioDecodeAVFrame;

    /** size of the audio buffer used in the 'loadNextAudioData' method */
    int nLoadAudioByteBuffer;

    /** audio buffer used in the 'loadNextAudioData' method */
    uint8_t *pLoadAudioByteBuffer;

    MoviePlayer() :
        avSyncType(AV_SYNC_AUDIO_MASTER),
        paused(false),
        width(0),
        height(0),
        nChannel(3),
        duration(0),
        pFormatCtx(nullptr),
        iImageStream(-1),
        pImageStream(nullptr),
        pImageCodecCtx(nullptr),
        pImageCodec(nullptr),
        pAVFrameShow(nullptr),
        pOptsDict(nullptr),
        pSwsImageCtx(nullptr),
        pFrameBuffer(nullptr),
        quit(false),
        loadedMovie(false),
        pDecodeFrame(nullptr),
        iAudioStream(-1),
        pAudioInCodecCtx(nullptr),
        pAudioOutCodecCtx(nullptr),
        pResampleCtx(nullptr),
        pAudioStream(nullptr),
        pAudioBuffer(nullptr),
        pAudioBufferData(nullptr),
        nAudioBuffer(0),
        nAudioData(0),
        iAudioData(0),
        audioTime(0),
        playSound(false),
        seekPts(0),
        seekReq(false),
        pLogger(nullptr),
        pAudioDecodeFramePkt(nullptr),
        pAudioDecodePktData(nullptr),
        audioDecodePktSize(0),
        pAudioDecodeAVFrame(nullptr) {

        // Initialize the pkt and frame and buffer for methods
        pAudioDecodeFramePkt = av_packet_alloc();
        pAudioDecodeAVFrame = av_frame_alloc();
        nLoadAudioByteBuffer = ((MAX_AUDIO_FRAME_SIZE * 3) / 2) * sizeof(uint8_t);
        pLoadAudioByteBuffer = (uint8_t*) malloc(nLoadAudioByteBuffer);
    }

    virtual ~MoviePlayer() {
        // Clean up memory
        free(pLoadAudioByteBuffer);
        av_packet_free(&pAudioDecodeFramePkt);
        av_frame_free(&pAudioDecodeAVFrame);
    }

    static int getId() {
        return ++id;
    }

    /* pause or resume the video */
    void pause() {
        if (paused) {
            frameTimer += av_gettime_relative() / 1000000.0 - videoClock.getLastUpdated();
            videoClock.setTime(videoClock.getTime());
        }
        externalClock.setTime(externalClock.getTime());
        // toggle paused
        paused = !paused;
        externalClock.setPaused(paused);
        videoClock.setPaused(paused);
        audioClock.setPaused(paused);
    }

    double computeTargetDelay(double delay) {
        double syncThreshold, diff = 0;

        /* update delay to follow master synchronisation source */
        if (getMasterSyncType() != AV_SYNC_VIDEO_MASTER) {
            /* if video is slave, we try to correct big delays by
               duplicating or deleting a frame */
            diff = videoClock.getTime() - getMasterClockTime();

            /* skip or repeat frame. We take into account the
               delay to compute the threshold. I still don't know
               if it is the best guess */
            syncThreshold = FFMAX(AV_SYNC_THRESHOLD_MIN, FFMIN(AV_SYNC_THRESHOLD_MAX, delay));
            if (!isnan(diff) && fabs(diff) < maxFrameDuration) {
                if (diff <= -syncThreshold)
                    delay = FFMAX(0, delay + diff);
                else if (diff >= syncThreshold && delay > AV_SYNC_FRAMEDUP_THRESHOLD)
                    delay = delay + diff;
                else if (diff >= syncThreshold)
                    delay = 2 * delay;
            }
        }

        pLogger->info("video: delay=%0.3f A-V=%f", delay, -diff);

        return delay;
    }

    double getDurationBetweenFrames(Frame *pCurrentFrame, Frame *pNextFrame) {
        double duration = pNextFrame->pts - pCurrentFrame->pts;
        if (isnan(duration) || duration <= 0 || duration > maxFrameDuration)
            return pCurrentFrame->duration;
        else
            return duration;
    }

    inline int64_t limitToRange(int64_t target) {
        // TODO: Fixme sometime. Here we make sure not to jump to the end but it depends on the accuracy of the duration.
        int64_t after = std::min(std::max(target, pImageStream->start_time),
                                         pImageStream->start_time + pImageStream->duration - 5*avgDeltaPts);
        // Not to jump all the way to the end is necessary because we need at least one frame after the current one to
        // reverse
        pLogger->info("Seek: before limit %I64d, after limit: %I64d, start %I64d, end %I64d.",
                      target/avgDeltaPts,
                      after/avgDeltaPts,
                      pImageStream->start_time/avgDeltaPts,
                      (pImageStream->start_time + pImageStream->duration)/avgDeltaPts);
        return after;
    }

    /*
    // TODO: Remove this?
    int synchronize_audio(VideoState *is, int nb_samples) {
        int wanted_nb_samples = nb_samples;

        // if not master, then we try to remove or add samples to correct the clock
        if (getMasterSyncType(is) != AV_SYNC_AUDIO_MASTER) {
            double diff, avgDiff;
            int min_nb_samples, max_nb_samples;

            diff = audioClock->getTime() - getMasterClockTime();

            if (!isnan(diff) && fabs(diff) < AV_NOSYNC_THRESHOLD) {
                audioDiffCum = diff + audioDiffAvgCoef * audioDiffCum;
                if (is->audio_diff_avg_count < AUDIO_DIFF_AVG_NB) {
                    // not enough measures to have a correct estimate
                    is->audio_diff_avg_count++;
                } else {
                    // estimate the A-V difference
                    avgDiff = audioDiffCum * (1.0 - audioDiffAvgCoef);

                    if (fabs(avgDiff) >= audioDiffThreshold) {
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
                // too big difference : may be initial PTS errors, so reset A-V filter
                is->audio_diff_avg_count = 0;
                is->audio_diff_cum       = 0;
            }
        }
        return wanted_nb_samples;
    }
    */

    /**
     * Synchronize the audio to the system clock by adding (repeating last sample)
     * or removing (truncating last samples) from the audio buffer.
     */
    int synchronizeAudio(short *samples, int nSamples) {
        double diffPts, avgDiffPts;
        int nWanted, nMin, nMax;
        int nBytePerSample = 2 * pAudioOutCodecCtx->channels;

        // Get the time difference
        diffPts = audioClock.getTime() - getMasterClockTime();
        if (diffPts < AV_NOSYNC_THRESHOLD) {

            // Accumulate the difference
            audioDiffCum = diffPts + audioDiffAvgCoef * audioDiffCum;
            if (audioDiffAvgCount < AUDIO_DIFF_AVG_NB) {
                audioDiffAvgCount++;
            } else {
                avgDiffPts = audioDiffCum * (1.0 - audioDiffAvgCoef);
                if (fabs(avgDiffPts) >= audioDiffThreshold) {
                    nWanted = nSamples + ((int)(diffPts * pAudioOutCodecCtx->sample_rate) * nBytePerSample);
                    nMin = nSamples * ((100 - SAMPLE_CORRECTION_PERCENT_MAX) / 100);
                    nMax = nSamples * ((100 + SAMPLE_CORRECTION_PERCENT_MAX) / 100);
                    // Clip nWanted to the range [nMin, nMax]
                    if (nWanted < nMin) {
                        nWanted = nMin;
                    } else if (nWanted > nMax) {
                        nWanted = nMax;
                    }
                    // Remove samples from the end
                    if (nWanted < nSamples) {
                        nSamples = nWanted;
                        pLogger->info("Removing %d samples from the audio.", nSamples);
                    // Repeat the final sample
                    } else if (nWanted > nSamples) {
                        uint8_t *samples_end, *q;
                        int nb = (nSamples - nWanted);
                        pLogger->info("Repeat the last sample %d times.", nb);
                        samples_end = (uint8_t *)samples + nSamples - nBytePerSample;
                        q = samples_end + nBytePerSample;
                        while (nb > 0) {
                            memcpy(q, samples_end, nBytePerSample);
                            q += nBytePerSample;
                            nb -= nBytePerSample;
                        }
                        nSamples = nWanted;
                    }
                }
            }
        } else {
            pLogger->info("Reset audio buffer.");
            // The difference is TOO big, reset
            audioDiffAvgCount = 0;
            audioDiffCum = 0;
        }
        return nSamples;
    }

    /**
     * Initialize the re-sampler context for the audio signal.  This re-sampler is transcoding the audio signals.
     */
    static int initResampler(AVCodecContext *inCodecCtx, AVCodecContext *outCodecCtx, SwrContext **pResampleCtx,
                             Logger* pLogger) {
        int errNo;
        // Create a re-sampler context by setting the conversion parameters
        *pResampleCtx = swr_alloc_set_opts(NULL,
                                           av_get_default_channel_layout(outCodecCtx->channels),
                                           outCodecCtx->sample_fmt,
                                           outCodecCtx->sample_rate,
                                           av_get_default_channel_layout(inCodecCtx->channels),
                                           inCodecCtx->sample_fmt,
                                           inCodecCtx->sample_rate,
                                           0, NULL);
        if (!*pResampleCtx) {
            pLogger->error("Could not allocate resample context");
            return AVERROR(ENOMEM);
        }

        // Only work if the sample rates are th same; otherwise we need to use filters:
        // https://github.com/FFmpeg/FFmpeg/blob/master/doc/examples/filtering_audio.c
        assert(outCodecCtx->sample_rate == inCodecCtx->sample_rate);

        // Open the re-sampler
        if ((errNo = swr_init(*pResampleCtx)) < 0) {
            pLogger->error("Unable to open resample context. Error: '%s'.", getErrorText(errNo));
            swr_free(pResampleCtx);
            return errNo;
        }

        return 0;
    }

    /**
     * Initialize a temporary storage for the specified number of audio samples.
     * The conversion requires temporary storage due to the different formats.
     * The number of audio samples to be allocated is specified in frameSize
     */
    static int initConvertedSamples(uint8_t ***convertedInSamples, AVCodecContext *outCodecCtx, int frameSize,
                                    Logger* pLogger) {
        int errNo;

        // Allocate a pointer per audio channel. Each pointer points to the audio
        // samples of the corresponding channels. It may point to NULL for
        // interleaved formats
        if (!(*convertedInSamples = (uint8_t**)calloc(outCodecCtx->channels, sizeof(**convertedInSamples)))) {
            pLogger->error("Could not allocate converted input sample pointers.");
            return 1;
        }

        // Allocate memory for the samples of all channels in one consecutive block
        // for convenience
        if ((errNo = av_samples_alloc(*convertedInSamples, NULL,
                                      outCodecCtx->channels,
                                      frameSize,
                                      outCodecCtx->sample_fmt, 0)) < 0) {
            pLogger->error("Could not allocate converted input samples. Error: '%s'.", getErrorText(errNo));
            av_freep(&(*convertedInSamples)[0]);
            free(*convertedInSamples);
            return errNo;
        }
        return 0;
    }

    /**
     * Convert the input audio samples into the output sample format.
     * This conversion happens per frame. The size is specified by frameSize.
     */
    static int convertSamples(const uint8_t **inData, uint8_t **convertedData, const int frameSize,
                              SwrContext *pResampleCtx, Logger* pLogger) {
        int errNo;
        // Convert samples using the resampler context.
        if ((errNo = swr_convert(pResampleCtx, convertedData, frameSize, inData, frameSize)) < 0) {
            pLogger->error("Could not convert input samples. Error '%s'.", getErrorText(errNo));
            return errNo;
        }
        return 0;
    }

    /**
     * Decodes packet from queue into the audioBuffer.
     */
    int audioDecodeFrame(AVCodecContext *pAudioInCodecCtx, uint8_t *audioBuffer, int bufferSize) {
        uint8_t **convertedInSamples = nullptr;
        int dataLen, dataSize = 0;
        int errNo;

        for (;;) {
            while (audioDecodePktSize > 0) {
                int gotFrame = 0;
                dataLen = avcodec_decode_audio4(pAudioInCodecCtx, pAudioDecodeAVFrame, &gotFrame, pAudioDecodeFramePkt);

                // If an error occurred skip the frame
                if (dataLen < 0) {
                    audioDecodePktSize = 0;
                    break;
                }

                pAudioDecodePktData += dataLen;
                audioDecodePktSize -= dataLen;
                dataSize = 0;

                if (gotFrame) {
                    dataSize = av_samples_get_buffer_size(NULL,
                                    pAudioOutCodecCtx->channels,
                                    pAudioDecodeAVFrame->nb_samples,
                                    pAudioOutCodecCtx->sample_fmt,
                                    1);

                    assert(dataSize <= bufferSize);

                    if ((errNo = MoviePlayer::initConvertedSamples(&convertedInSamples,
                                                                   pAudioOutCodecCtx,
                                                                   pAudioDecodeAVFrame->nb_samples,
                                                                   pLogger)) < 0) {
                        // clean-up
                        if (convertedInSamples) {
                            av_freep(&convertedInSamples[0]);
                            free(convertedInSamples);
                        }
                        return errNo;
                    }

                    if ((errNo = MoviePlayer::convertSamples((const uint8_t**)pAudioDecodeAVFrame->extended_data,
                                                             convertedInSamples,
                                                             pAudioDecodeAVFrame->nb_samples, pResampleCtx,
                                                             pLogger)) < 0) {
                        // clean-up
                        if (convertedInSamples) {
                            av_freep(&convertedInSamples[0]);
                            free(convertedInSamples);
                        }
                        return errNo;
                    }

                    memcpy(audioBuffer, convertedInSamples[0], dataSize);

                    av_freep(&convertedInSamples[0]);
                    free(convertedInSamples);
                }

                // No data yet. Get more frames
                if (dataSize <= 0) {
                    continue;
                }

                audioTime += (double)dataSize / (double)(2 * pAudioOutCodecCtx->channels * pAudioOutCodecCtx->sample_rate);

                // We have data, return it and come back for more later
                return dataSize;
            }
            if (pAudioDecodeFramePkt->data) {
                av_free_packet(pAudioDecodeFramePkt);
            }

            if (quit) {
                return -1;
            }
            if (pAudioBuffer->empty()) {
                return -1;
            }
            if (pAudioBuffer->get(pAudioDecodeFramePkt) < 0) {
                return -1;
            }
            pAudioDecodePktData = pAudioDecodeFramePkt->data;
            audioDecodePktSize = pAudioDecodeFramePkt->size;

            if (pAudioDecodeFramePkt->pts != AV_NOPTS_VALUE) {
                audioTime = av_q2d(pAudioStream->time_base)* pAudioDecodeFramePkt->pts;
                Clock::syncMasterToSlave(&externalClock, &audioClock, AV_NOSYNC_THRESHOLD);
            }
        }
    }

    /**
     * True if the current video file has an image stream; otherwise false
     */
    bool hasVideoStream() const { return iImageStream > -1; }

    /**
     * True if the current video file has an audio stream; otherwise false
     */
    bool hasAudioStream() const { return iAudioStream > -1; }

    /**
     * Reads the next frame from the video stream and supports:
     * - Random seek through the variable seekReq.
     * - Toggling the direction through the variable toggle.
     * - Automatically fills the buffer for backward play.
     *
     * For a seek this jumps to the next earlier keyframe than the current frame.
     * This method drops as many frames as there are between the keyframe and the
     * requested seek frame.
     *
     * Decodes multiple AVPacket into an AVFrame and writes this one to the buffer.
     *
     */
    void readNextFrame() {
        int frameFinished;
        bool reverseRefresh = true;
        AVPacket packet;
        int delta = 0;
        AVFrame *pSrcAVFrame = av_frame_alloc();
        AVRational frameRate = av_guess_frame_rate(pFormatCtx, pImageStream, NULL);
        AVRational timeBase = pImageStream->time_base;
        double pts;
        double duration;

        while (!quit) {

            // Random seek
            if (seekReq) {
                seekReq = false;
                //pFrameBuffer->block();
                int64_t target = seekPts;
                int64_t delta = seekPts - lastWritePts;
                int64_t min_ = delta > 0 ? target - delta + 2: INT64_MIN;
                int64_t max_ = delta < 0 ? target - delta - 2: INT64_MAX;
                int seekFlags = 0;

                if (avformat_seek_file(pFormatCtx, iImageStream, min_, target, max_, seekFlags) < 0) {
                    pLogger->info("Failed seek to frame %I64d.", target/avgDeltaPts);
                } else {
                    lastWritePts = target;
                    pLogger->info("Succeeded seek to frame %I64d with min %I64d frames and max %I64d frames for diff %I64d frames.",
                                  target/avgDeltaPts, min_/avgDeltaPts, max_/avgDeltaPts, delta/avgDeltaPts);
                    if (hasVideoStream()) {
                        pFrameBuffer->flush();
                        avcodec_flush_buffers(pImageCodecCtx);
                    }
                    if (hasAudioStream()) {
                        pAudioBuffer->flush();
                        avcodec_flush_buffers(pAudioInCodecCtx);
                        avcodec_flush_buffers(pAudioOutCodecCtx);
                    }
                    pLogger->info("Status after seek.");
                    pFrameBuffer->log(*pLogger, avgDeltaPts);
                    externalClock.setTime(target/ (double)AV_TIME_BASE);
                }
            }

            // Read frame
            int ret = av_read_frame(pFormatCtx, &packet);

            if (ret == AVERROR_EOF) {
                pLogger->debug("Reached the end of the file.");
                std::this_thread::sleep_for(std::chrono::milliseconds(WAIT_TIMEOUT_IN_MSEC));
                continue;
            }

            if (ret < 0) {
                pLogger->error("Error:  %c, %c, %c, %c.\n",
                    static_cast<char>((-ret >> 0) & 0xFF),
                    static_cast<char>((-ret >> 8) & 0xFF),
                    static_cast<char>((-ret >> 16) & 0xFF),
                    static_cast<char>((-ret >> 24) & 0xFF));
                std::this_thread::sleep_for(std::chrono::milliseconds(WAIT_TIMEOUT_IN_MSEC));
                continue;
            }

            // Is this a packet from the video stream?
            if (hasVideoStream() && packet.stream_index == iImageStream) {

                // Decode the video frame
                avcodec_decode_video2(pImageCodecCtx, pSrcAVFrame, &frameFinished, &packet);

                // Did we get a full video frame?
                if (frameFinished) {

                    // Set the presentation time stamp (PTS)
                    int64_t readPts = pSrcAVFrame->pkt_pts;

                    // Skip frames if seek before we may need to skip frames until we are at seekPts
                    if (readPts >= seekPts) {

                        // Get the next writable buffer. This may block and can be unblocked by flushing
                        Frame* pFrame;

                        pFrameBuffer->writeRequest(&pFrame);

                        // Did we get a frame buffer?
                        if (pFrame) {

                            // Convert the image from its native color format into the RGB color format
                            // TODO: Add resize here
                            sws_scale(
                                pSwsImageCtx,
                                (uint8_t const * const *)pSrcAVFrame->data,
                                pSrcAVFrame->linesize,
                                0,
                                pImageCodecCtx->height,
                                pFrame->frame->data, // TODO: Check me, but should be fine
                                pFrame->frame->linesize);
                            pts = (pFrame->pts == AV_NOPTS_VALUE) ? NAN : pFrame->pts * av_q2d(timeBase);
                            duration = (frameRate.num && frameRate.den ? av_q2d(struct AVRational {frameRate.den, frameRate.num}) : 0);

                            // Assign fields to the pFrame
                            pFrame->frame->repeat_pict = pSrcAVFrame->repeat_pict;
                            pFrame->pts = lastWritePts = pts;
                            pFrame->duration = duration;
                            pFrame->width = pSrcAVFrame->width;
                            pFrame->height = pSrcAVFrame->height;
                            av_frame_move_ref(pFrame->frame, pSrcAVFrame);

                            // Complete the write
                            pFrameBuffer->writeComplete();

                            pLogger->info("Wrote frame %I64d.", lastWritePts/avgDeltaPts);


                        } else {
                            pLogger->info("Write request aborted.");
                        }
                    } else {
                        pLogger->info("Skipped frame %ld.", readPts/avgDeltaPts);
                    }

                    // Reset frame container to initial state
                    av_frame_unref(pSrcAVFrame);
                }

                // Free the packet that was allocated by av_read_frame
                av_free_packet(&packet);

            } else if (hasAudioStream() && playSound && packet.stream_index == iAudioStream) {

                // Decode packet from audio stream
                pAudioBuffer->put(&packet); // packet is freed when consumed
            } else {
                av_free_packet(&packet);
            }
        }
        av_frame_free(&pSrcAVFrame);
    }

    double getStartTime() const {
    	return hasVideoStream() ? pImageStream->start_time * av_q2d(pImageStream->time_base)
    	                        : pAudioStream->start_time * av_q2d(pAudioStream->time_base);
    }

    double getEndTime() const {
        return hasVideoStream() ? (pImageStream->duration + pImageStream->start_time) * av_q2d(pImageStream->time_base)
                                : (pAudioStream->duration + pAudioStream->start_time) * av_q2d(pAudioStream->time_base);
    }

    double getDuration() const {
        return duration;
    }

    double getCurrentTime() { return externalClock.getTime() * av_q2d(pImageStream->time_base); }

    void seek(double time) {
        if (hasVideoStream() || hasAudioStream()) {
            lastWritePts = seekPts = limitToRange( ((int64_t)(time/(avgDeltaPts * av_q2d(pImageStream->time_base))))*avgDeltaPts );
            seekReq = true;
        }
    }

    // assumes inSpeed is positive
    void setSpeed(float speed) {

        if (hasVideoStream() || hasAudioStream()) {

            // Set speed for clock
            externalClock.setSpeed(speed);

            // If we have audio need to turn off at playback other than 1x
            if (hasAudioStream()) {

                // Do we have speedOne?
                bool isSpeedOne = fabs(speed-1.0) <= std::numeric_limits<float>::epsilon();

                // If we switch from playing sound to not play sound or vice versa
                // TODO: Disable/enable sound decoding
                if (!playSound && isSpeedOne || playSound && !isSpeedOne) {
                    pAudioBuffer->flush();
                    avcodec_flush_buffers(pAudioInCodecCtx);
                    avcodec_flush_buffers(pAudioOutCodecCtx);
                }

                // If we have speed one than we play sound (otherwise not)
                playSound = isSpeedOne;
            }
        }
    }

    int loadNextImageFrame() {
        // If there is no image stream or if the image buffer is empty, then return -1
        // The latter condition avoids blocking when reaching the start/end of the stream
        if (!hasVideoStream() || pFrameBuffer->empty()) return -1;

        // Counts the number of frames that this method requested (could be 0, 1, 2)
        int nFrame = 0;
        Frame *pCurrentFrame, *pLastFrame;
        double lastDuration;
        double delay;
        double time;

        for (int iFrameDrop = ceil(getMasterClockSpeed()); iFrameDrop > 0 && !pFrameBuffer->empty(); --iFrameDrop) {
            pFrameBuffer->peekLast(&pLastFrame);
            pFrameBuffer->peekCurrent(&pCurrentFrame);

            lastDuration = getDurationBetweenFrames(pLastFrame, pCurrentFrame);
            delay = computeTargetDelay(lastDuration);
            time = av_gettime_relative()/1000000.0;

            if (paused) {
                break;
            }

            if (time < frameTimer + delay) {
                remainingTime = FFMIN(frameTimer + delay - time, remainingTime);
                break;
            }

            frameTimer += delay;
            if (delay > 0 && time - frameTimer > AV_SYNC_THRESHOLD_MAX)
                frameTimer = time;

            if (!isnan(pCurrentFrame->pts)) {
                videoClock.setTime(pCurrentFrame->pts);
                Clock::syncMasterToSlave(&externalClock, &videoClock, AV_NOSYNC_THRESHOLD);
            }

            if (!pFrameBuffer->empty()) {
                Frame *pNextFrame;
                pFrameBuffer->peekNext(&pNextFrame);
                duration = getDurationBetweenFrames(pCurrentFrame, pNextFrame);

                if (getMasterSyncType() != AV_SYNC_VIDEO_MASTER && time > frameTimer + duration) {
                    pFrameBuffer->next();
                } else {
                    break;
                }
            }
        }

        if (pCurrentFrame == nullptr) {
            return nFrame;
        }

        // Delay read to keep the desired frame rate.
        if (delay > 0) {
            pLogger->info("Frame %I64d waits for %lf seconds.", pCurrentFrame->pts/avgDeltaPts, delay);
            std::this_thread::sleep_for(std::chrono::milliseconds((int)(delay*1000+0.5)));
        }

        // Free the previously shown av frame (if it does exist)
        // TODO: Check this for any memory problems
        av_frame_unref(pAVFrameShow);

        pAVFrameShow = pCurrentFrame->frame;
        av_frame_move_ref(pAVFrameShow, pCurrentFrame->frame);

        // We displayed a frame
        pLogger->info("Return frame: %I64d.", pCurrentFrame->pts/avgDeltaPts);

        // Return the number of read frames; the number may be larger than 1
        return nFrame;
    }

    bool loadNextAudioData() {

        // If we do not have an audio stream, we are done decoding, or the audio
        // buffer is empty, return false to indicate that we did not load data
        if (!hasAudioStream() || pAudioBuffer->empty()) {
            return false;
        }

        int len = nAudioBuffer; // get length of buffer
        uint8_t *data = pAudioBufferData; // get a write pointer.
        int decodeLen, audioSize;

        while (len > 0) {
            // We still need to read len bytes
            if (iAudioData >= nAudioData) {
                // We already sent all our data; get more
                audioSize = audioDecodeFrame(pAudioInCodecCtx, pLoadAudioByteBuffer, nLoadAudioByteBuffer);


                if (audioSize < 0) {
                    // If error, output silence
                    nAudioData = 1024; // arbitrary?
                    memset(pLoadAudioByteBuffer, 0, nAudioData); // set silence for the rest
                } else {
                    nAudioData = audioSize = synchronizeAudio((int16_t *)pLoadAudioByteBuffer, audioSize);
                }

                iAudioData = 0;
            }
            decodeLen = nAudioData - iAudioData;

            if (decodeLen > len) {
                decodeLen = len;
            }

            memcpy(data, (uint8_t *)pLoadAudioByteBuffer + iAudioData, decodeLen);
            len -= decodeLen;
            data += decodeLen;
            iAudioData += decodeLen;
        }
        return !quit;
    }

    void close() {

        pLogger->info("Closing.");

    	// If we have a video or an audio stream
    	if (hasVideoStream() || hasAudioStream()) {

    		// Set the quit flag for the decoding thread
    		quit = true;

    		// Drain the audio buffer that means no packets can be get or put
    		if (hasAudioStream()) {
    			pAudioBuffer->flush();
    		    pLogger->info("Flushed audio stream.");
    		}

    		// Flush the image buffer buffer which unblocks all readers/writers
    		if (hasVideoStream()) {
    			pFrameBuffer->flush();
    			pLogger->info("Flushed video stream.");
    		}

    		// Join and free the decoding thread
    		pDecodeFrame->join();
    		delete pDecodeFrame;
    		pLogger->info("Joined decoding thread");

    		// If we have a video stream
    		if (hasVideoStream()) {
    			// Free the image buffer buffer and reset pointer
    			delete pFrameBuffer;
    			pFrameBuffer = nullptr;

    			// Free the dictionary and reset pointer
    			av_dict_free(&pOptsDict);
    			pOptsDict = nullptr;

    			// Flush the buffers
    			avcodec_flush_buffers(pImageCodecCtx);

    			// Close codec context and reset pointer
    			avcodec_close(pImageCodecCtx);
    			pImageCodecCtx = nullptr;

    			// Free scaling context and reset pointer
    			sws_freeContext(pSwsImageCtx);
    			pSwsImageCtx = nullptr;

    			// Free the YUV frame and reset pointers
    			pAVFrameShow = nullptr;

    			pLogger->info("Freed image stream resources.");
    		}

    		// If we have an audio stream
    		if (hasAudioStream()) {
    			// Delete the audio buffer
    			delete pAudioBuffer;
    			avcodec_flush_buffers(pAudioInCodecCtx);
    			avcodec_flush_buffers(pAudioOutCodecCtx);
    			avcodec_close(pAudioInCodecCtx);
    			avcodec_close(pAudioOutCodecCtx);
    			swr_free(&pResampleCtx);
    			free(pAudioBufferData);

    			pAudioInCodecCtx = nullptr;
    			pAudioOutCodecCtx = nullptr;
    			pResampleCtx = nullptr;
    			pAudioBufferData = nullptr;

    			pLogger->info("Freed audio stream resources.");
    		}

    		// Close the video file AFTER closing all codecs!!!
    		avformat_close_input(&pFormatCtx);
    		pFormatCtx = nullptr;

    		// Set default values for movie information.
    		loadedMovie = false;
    		width = 0;
    		height = 0;
    		nChannel = 3;
    		duration = 0;
    		lastWritePts = 0;

    		// Set default values for playback speed.
    		avgDeltaPts = 1;
    		lastWritePts = 0;

    		// Reset value for seek and backward request.
    		seekReq = false;
    		seekPts = 0;

    		// Set default values for the audio buffer.
    		nAudioData = 0;
    		iAudioData = 0;
    		audioDiffCum = 0;
    		audioDiffAvgCoef = AUDIO_AVG_COEF;
    		audioDiffThreshold = 0;
    		audioDiffAvgCount = 0;
    		audioTime = 0;
    		playSound = false;

    		quit = false;

    		pLogger->info("Reset state for this movie stream.");
    	}

    	if (pLogger) {
    		pLogger->info("Closed video and released resources.");
    		delete pLogger;
    		pLogger = nullptr;
    	}
    }

    jobject getAudioBuffer(JNIEnv *env, jint jSize) {
        if (!hasAudioStream()) { return 0; } // null
        nAudioBuffer = jSize;
        audioDiffThreshold = 2.0 * nAudioBuffer / pAudioOutCodecCtx->sample_rate;
        pAudioBufferData = (uint8_t*) malloc(jSize);
        if (!pAudioBufferData) {
            pLogger->error("Failed to allocate audio buffer.");
            return 0; // null
        }
        return env->NewDirectByteBuffer((void*) pAudioBufferData, jSize*sizeof(uint8_t));
    }

    jstring getSampleFormat(JNIEnv* env) const {
        if (!hasAudioStream()) { return 0; } // null
        // sample formats http://ffmpeg.org/doxygen/trunk/group__lavu__sampfmts.html#gaf9a51ca15301871723577c730b5865c5
        AVSampleFormat sampleFormat = pAudioOutCodecCtx->sample_fmt;
        const char* name = av_get_sample_fmt_name(sampleFormat);
        return env->NewStringUTF(name);
    }

    jstring getCodecName(JNIEnv* env) const {
    	if (!hasAudioStream()) { return 0; }
    	const char* name = pAudioOutCodecCtx->codec->name;
    	return env->NewStringUTF(name);
    }

    double getAverageFrameRate() const {
        if (!hasVideoStream()) { return 0; }
        return av_q2d(pImageStream->avg_frame_rate);
    }

    float getSampleRate() const {
        return hasAudioStream() ? pAudioOutCodecCtx->sample_rate : 0;
    }

    int getSampleSizeInBits() const {
        return hasAudioStream() ? pAudioOutCodecCtx->bits_per_coded_sample : 0;
    }

    int getNumberOfSoundChannels() const {
    	return hasAudioStream() ? pAudioOutCodecCtx->channels : 0;
    }

    int getFrameSize() const {
    	AVSampleFormat sampleFormat = pAudioOutCodecCtx->sample_fmt;
    	return hasAudioStream() ? av_get_bytes_per_sample(sampleFormat) : 0;
    }

    float getFrameRate() const {
    	return hasAudioStream() ? pAudioOutCodecCtx->sample_rate : 0;
    }

    bool bigEndian() {
        short int number = 0x1;
        char *numPtr = (char*)&number;
    	return hasAudioStream() ? (numPtr[0] != 1) : false;
    }

    bool setPlaySound(bool play) {
    	bool original = playSound;
    	playSound = play;
    	return original;
    }

    int getNumberOfColorChannels() const { return nChannel; }

    int getHeight() const { return height; }

    int getWidth() const { return width; }

    jobject getFrameBuffer(JNIEnv* env) {
        // No movie was loaded return nullptr.
        if (!hasVideoStream()) return 0;

        // Construct a new direct byte buffer pointing to data from pAVFrameShow.
        return env->NewDirectByteBuffer((void*) pAVFrameShow->data[0],
                                        width*height*nChannel*sizeof(uint8_t));
    }

    int getMasterSyncType() {
        if (avSyncType == AV_SYNC_VIDEO_MASTER) {
            if (hasVideoStream())
                return AV_SYNC_VIDEO_MASTER;
            else
                return AV_SYNC_AUDIO_MASTER;
        } else if (avSyncType == AV_SYNC_AUDIO_MASTER) {
            if (hasAudioStream())
                return AV_SYNC_AUDIO_MASTER;
            else
                return AV_SYNC_EXTERNAL_CLOCK;
        } else {
            return AV_SYNC_EXTERNAL_CLOCK;
        }
    }

    void getMasterClock(Clock* master) {
        switch (getMasterSyncType()) {
            case AV_SYNC_VIDEO_MASTER:
                master = &videoClock;
                break;
            case AV_SYNC_AUDIO_MASTER:
                master = &audioClock;
                break;
            default:
                master = &externalClock;
                break;
        }
    }

    double getMasterClockTime() {
        Clock* master;
        getMasterClock(master);
        return master->getTime();
    }

    double getMasterClockSpeed() {
        Clock* master;
        getMasterClock(master);
        return master->getSpeed();
    }
};

int MoviePlayer::id = -1;
std::map<int, MoviePlayer*> idToMovieStream;

MoviePlayer* getMoviePlayer(int streamId) {
    return idToMovieStream.find(streamId) != idToMovieStream.end() ? idToMovieStream[streamId] : nullptr;
}

JNIEXPORT jintArray JNICALL Java_org_datavyu_plugins_ffmpegplayer_MoviePlayer0_open0(JNIEnv *env, jclass thisClass,
    jstring jFileName, jstring jVersion, jobject jAudioFormat) {

	int errNo = 0;
    jintArray returnArray = env->NewIntArray(2);
    jint *returnValues = env->GetIntArrayElements(returnArray, NULL);

	const char *fileName = env->GetStringUTFChars(jFileName, 0);
	const char *version = env->GetStringUTFChars(jVersion, 0);

    int streamId = MoviePlayer::getId();
    returnValues[0] = 0;
    returnValues[1] = streamId;
	MoviePlayer* moviePlayer = new MoviePlayer();
    std::string logFileName = std::string(fileName, strlen(fileName));
    logFileName = logFileName.substr(logFileName.find_last_of("/\\") + 1) + ".log";
    moviePlayer->pLogger = new FileLogger(logFileName);
    //moviePlayer->pLogger = new StreamLogger(&std::cerr);
	moviePlayer->pLogger->info("Version: %s", version);

	// Register all formats and codecs
	av_register_all();

	// Open the video file.
	if ((errNo = avformat_open_input(&moviePlayer->pFormatCtx, fileName, nullptr, nullptr)) != 0) {
		moviePlayer->pLogger->error("Could not open file %s.", fileName);
		returnValues[0] = errNo;
		env->ReleaseIntArrayElements(returnArray, returnValues, NULL);
		return returnArray;
	}

	// Retrieve the stream information.
	if ((errNo = avformat_find_stream_info(moviePlayer->pFormatCtx, nullptr)) < 0) {
		moviePlayer->pLogger->error("Unable to find stream information for file %s.", fileName);
		returnValues[0] = errNo;
		env->ReleaseIntArrayElements(returnArray, returnValues, NULL);
		return returnArray;
	}

	// Log that opened a file.
	moviePlayer->pLogger->info("Opened file %s.", fileName);

	// Dump information about file onto standard error.
	std::string info = log_av_format(moviePlayer->pFormatCtx, 0, fileName, 0);
	std::istringstream lines(info);
    std::string line;
    while (std::getline(lines, line)) {
		moviePlayer->pLogger->info("%s", line.c_str());
    }

	// Find the first video stream.
	moviePlayer->iImageStream = -1;
	for (int iStream = 0; iStream < moviePlayer->pFormatCtx->nb_streams; ++iStream) {
		if (moviePlayer->pFormatCtx->streams[iStream]->codec->codec_type == AVMEDIA_TYPE_VIDEO) {
			moviePlayer->iImageStream = iStream;
			break;
		}
	}

	if (moviePlayer->hasVideoStream()) {
		moviePlayer->pLogger->info("Found image stream with id %d.", moviePlayer->iImageStream);
	} else {
		moviePlayer->pLogger->info("Could not find an image stream.");
	}

	// Find the first audio stream
	moviePlayer->iAudioStream = -1;
	for (int iStream = 0; iStream < moviePlayer->pFormatCtx->nb_streams; iStream++) {
		if (moviePlayer->pFormatCtx->streams[iStream]->codec->codec_type == AVMEDIA_TYPE_AUDIO) {
			moviePlayer->iAudioStream = iStream;
			break;
		}
	}

	if (moviePlayer->hasAudioStream()) {
		moviePlayer->pLogger->info("Found audio stream with id %d.", moviePlayer->iAudioStream);
	} else {
		moviePlayer->pLogger->info("Could not find an audio stream.");
	}

	if (!moviePlayer->hasVideoStream() && !moviePlayer->hasAudioStream()) {
		moviePlayer->pLogger->error("Could not find an image stream or audio stream.");
		avformat_close_input(&moviePlayer->pFormatCtx);
		returnValues[0] = AVERROR_INVALIDDATA;
		env->ReleaseIntArrayElements(returnArray, returnValues, NULL);
		return returnArray;
	}

	if (moviePlayer->hasVideoStream()) {

        // Set the max frame duration
	    moviePlayer->maxFrameDuration = (moviePlayer->pFormatCtx->iformat->flags & AVFMT_TS_DISCONT) ? 10.0 : 3600.0;

		// Get a pointer to the video stream.
		moviePlayer->pImageStream = moviePlayer->pFormatCtx->streams[moviePlayer->iImageStream];

		// Get a pointer to the codec context for the video stream.
		moviePlayer->pImageCodecCtx = moviePlayer->pImageStream->codec;

		// Find the decoder for the video stream.
		moviePlayer->pImageCodec = avcodec_find_decoder(moviePlayer->pImageCodecCtx->codec_id);
		if (moviePlayer->pImageCodec == nullptr) {
			moviePlayer->pLogger->error("Unsupported codec for file %s.", fileName);
			returnValues[0] = AVERROR_DECODER_NOT_FOUND;
            env->ReleaseIntArrayElements(returnArray, returnValues, NULL);
            return returnArray;
		}

		// Open codec.
		if ((errNo = avcodec_open2(moviePlayer->pImageCodecCtx, moviePlayer->pImageCodec,
		                           &moviePlayer->pOptsDict)) < 0) {
			moviePlayer->pLogger->error("Unable to open codec for file %s.", fileName);
			returnValues[0] = errNo;
            env->ReleaseIntArrayElements(returnArray, returnValues, NULL);
            return returnArray;
		}

		// Initialize the color model conversion/rescaling context.
		moviePlayer->pSwsImageCtx = sws_getContext
			(
				moviePlayer->pImageCodecCtx->width,
				moviePlayer->pImageCodecCtx->height,
				moviePlayer->pImageCodecCtx->pix_fmt,
				moviePlayer->pImageCodecCtx->width,
				moviePlayer->pImageCodecCtx->height,
				AV_PIX_FMT_RGB24,
				SWS_BILINEAR,
				nullptr,
				nullptr,
				nullptr
			);

		// Initialize the width, height, and duration.
		moviePlayer->width = moviePlayer->pImageCodecCtx->width;
		moviePlayer->height = moviePlayer->pImageCodecCtx->height;
		moviePlayer->duration = moviePlayer->pImageStream->duration * av_q2d(moviePlayer->pImageStream->time_base);
		moviePlayer->pLogger->info("Duration of movie %d x %d pixels is %2.3f seconds, %I64d pts.",
					               moviePlayer->width, moviePlayer->height, moviePlayer->duration,
					               moviePlayer->pImageStream->duration);
		moviePlayer->pLogger->info("Time base %2.5f.", av_q2d(moviePlayer->pImageStream->time_base));

		// Initialize the delta pts using the average frame rate and the average pts.
		moviePlayer->avgDeltaPts = (int64_t)(1.0/
		        (av_q2d(moviePlayer->pImageStream->time_base)*av_q2d(moviePlayer->pImageStream->avg_frame_rate)));
		moviePlayer->pLogger->info("Average delta %I64d pts.", moviePlayer->avgDeltaPts);

		// Initialize the image buffer.

		//int width, int height, long firstItem, int nItem, int nMaxReverse
		moviePlayer->pFrameBuffer = new FrameBuffer(moviePlayer->width,
		                                            moviePlayer->height,
		                                            moviePlayer->pImageStream->start_time/moviePlayer->avgDeltaPts);
	}

	// *************************************************************************
	// Work on audio
	// *************************************************************************
	if (moviePlayer->hasAudioStream()) {

		AVCodec *aOutCodec = nullptr;
		AVCodec *aInCodec = nullptr;

		struct AudioFormat audioFormat;
		if ((errNo = getAudioFormat(env, &audioFormat, jAudioFormat, moviePlayer->pLogger)) < 0) {
			returnValues[0] = errNo;
            env->ReleaseIntArrayElements(returnArray, returnValues, NULL);
            return returnArray;
		}

		AVSampleFormat sampleFormat;
		if (strcmp("PCM_UNSIGNED", audioFormat.encodingName.c_str()) == 0) {
			sampleFormat = AV_SAMPLE_FMT_U8;
		} else if (strcmp("PCM_SIGNED", audioFormat.encodingName.c_str()) == 0) {
			sampleFormat = AV_SAMPLE_FMT_S16;
		} else {
			moviePlayer->pLogger->error("Encoding %s is not supported.\n", audioFormat.encodingName.c_str());
			returnValues[0] = AVERROR_INVALIDDATA;
            env->ReleaseIntArrayElements(returnArray, returnValues, NULL);
            return returnArray;
		}

		// Supported codecs are: AV_CODEC_ID_PCM_U8 and AV_CODEC_ID_PCM_S16LE.
		AVCodecID codecId = av_get_pcm_codec(sampleFormat, audioFormat.bigEndian);

		moviePlayer->pAudioStream = moviePlayer->pFormatCtx->streams[moviePlayer->iAudioStream];

		moviePlayer->pAudioInCodecCtx = moviePlayer->pAudioStream->codec;

		if (!(aInCodec = avcodec_find_decoder(moviePlayer->pAudioInCodecCtx->codec_id))) {
			moviePlayer->pLogger->error("Could not find input codec!\n");
			avformat_close_input(&moviePlayer->pFormatCtx);
			returnValues[0] = AVERROR_EXIT;
            env->ReleaseIntArrayElements(returnArray, returnValues, NULL);
            return returnArray;
		}

		if ((errNo = avcodec_open2(moviePlayer->pAudioInCodecCtx, aInCodec, NULL)) < 0) {
			moviePlayer->pLogger->error("Could not open audio codec. Error message: '%s'.\n", getErrorText(errNo));
			avformat_close_input(&moviePlayer->pFormatCtx);
			returnValues[0] = errNo;
            env->ReleaseIntArrayElements(returnArray, returnValues, NULL);
            return returnArray;
		}
		moviePlayer->pAudioBuffer = new AudioBuffer();

		// create the output codec (alternative is stero: AV_CODEC_ID_PCM_U8)
		if (!(aOutCodec = avcodec_find_encoder(codecId))) {
			moviePlayer->pLogger->error("Could not create output codec.");
			avformat_close_input(&moviePlayer->pFormatCtx);
			returnValues[0] = AVERROR_EXIT;
			env->ReleaseIntArrayElements(returnArray, returnValues, NULL);
			return returnArray;
		}

		// Allocate the output codec context
		if (!(moviePlayer->pAudioOutCodecCtx = avcodec_alloc_context3(aOutCodec))) {
			moviePlayer->pLogger->error("Could not allocate an encoding output context.\n");
			avformat_close_input(&moviePlayer->pFormatCtx);
			returnValues[0] = AVERROR_EXIT;
			env->ReleaseIntArrayElements(returnArray, returnValues, NULL);
			return returnArray;
		}

		// Set the sample format
		moviePlayer->pAudioOutCodecCtx->sample_fmt = sampleFormat;

		// Set channels, either from input jAudioFormat or from input codec
		if (audioFormat.channels != 0) {
			moviePlayer->pAudioOutCodecCtx->channels = audioFormat.channels;
			moviePlayer->pAudioOutCodecCtx->channel_layout = av_get_default_channel_layout(audioFormat.channels);
		} else {
			moviePlayer->pAudioOutCodecCtx->channels = moviePlayer->pAudioInCodecCtx->channels;
			moviePlayer->pAudioOutCodecCtx->channel_layout = moviePlayer->pAudioInCodecCtx->channel_layout;
			//audioFormat.channels = moviePlayer->pAudioInCodecCtx->channels;
		}

		// Set sample rate, either from input jAudioFormat or from input codec
		if (audioFormat.sampleRate != 0) {
			moviePlayer->pAudioOutCodecCtx->sample_rate = (int) audioFormat.sampleRate;
		} else {
			moviePlayer->pAudioOutCodecCtx->sample_rate = moviePlayer->pAudioInCodecCtx->sample_rate;
			//audioFormat.sampleRate = moviePlayer->pAudioInCodecCtx->sample_rate;
		}

		// Set bit rate
		if (audioFormat.frameRate != 0) {
			moviePlayer->pAudioOutCodecCtx->bit_rate = (int) audioFormat.frameRate;
		} else {
			moviePlayer->pAudioOutCodecCtx->bit_rate = moviePlayer->pAudioInCodecCtx->bit_rate;
			//audioFormat.sampleRate = moviePlayer->pAudioInCodecCtx->bit_rate;
		}

		// Set the frame size
		//audioFormat.frameSize = av_get_bytes_per_sample(sampleFormat);

		// Open the encoder for the audio stream to use it later.
		if ((errNo = avcodec_open2(moviePlayer->pAudioOutCodecCtx, aOutCodec, NULL)) < 0) {
			moviePlayer->pLogger->error("Could not open output codec. Error message: '%s'.\n", getErrorText(errNo));
			avformat_close_input(&moviePlayer->pFormatCtx);
			returnValues[0] = errNo;
			env->ReleaseIntArrayElements(returnArray, returnValues, NULL);
			return returnArray;
		}

		// Initialize the re-sampler to be able to convert audio sample formats.
		if ((errNo = MoviePlayer::initResampler(moviePlayer->pAudioInCodecCtx,
		                                        moviePlayer->pAudioOutCodecCtx,
		                                        &moviePlayer->pResampleCtx,
		                                        moviePlayer->pLogger)) < 0) {
			avformat_close_input(&moviePlayer->pFormatCtx);
			returnValues[0] = errNo;
			env->ReleaseIntArrayElements(returnArray, returnValues, NULL);
			return returnArray;
		}
	}

	moviePlayer->playSound = moviePlayer->hasAudioStream();

	// Seek to the start of the file (must have an image or audio stream).
	moviePlayer->lastWritePts = moviePlayer->seekPts = moviePlayer->hasVideoStream()
	                                                        ? moviePlayer->pImageStream->start_time : 0;
	                                                        // assume 0 is the start time for the audio stream
	moviePlayer->seekReq = true;

	// Start the decode thread
	moviePlayer->pDecodeFrame = new std::thread(&MoviePlayer::readNextFrame, moviePlayer);
	moviePlayer->pLogger->info("Started decoding thread!");

	// Set the value for loaded movie true
	moviePlayer->loadedMovie = true;

	// Free strings
	env->ReleaseStringUTFChars(jFileName, fileName);
	env->ReleaseStringUTFChars(jVersion, version);

	// Add player to the map
	idToMovieStream[streamId] = moviePlayer;

    env->ReleaseIntArrayElements(returnArray, returnValues, NULL);
    return returnArray;
}

JNIEXPORT jdouble JNICALL Java_org_datavyu_plugins_ffmpegplayer_MoviePlayer0_getAverageFrameRate0(JNIEnv *env,
    jclass thisClass, jint streamId) {
    MoviePlayer* moviePlayer = getMoviePlayer(streamId);
    return moviePlayer != nullptr ? moviePlayer->getAverageFrameRate() : 0;
}

JNIEXPORT jboolean JNICALL Java_org_datavyu_plugins_ffmpegplayer_MoviePlayer0_hasVideoStream0(JNIEnv *env,
    jclass thisClass, jint streamId) {
    MoviePlayer* moviePlayer = getMoviePlayer(streamId);
    return moviePlayer != nullptr && moviePlayer->hasVideoStream();
}

JNIEXPORT jboolean JNICALL Java_org_datavyu_plugins_ffmpegplayer_MoviePlayer0_hasAudioStream0(JNIEnv *env,
    jclass thisClass, jint streamId) {
    MoviePlayer* moviePlayer = getMoviePlayer(streamId);
    return moviePlayer != nullptr && moviePlayer->hasAudioStream();
}

JNIEXPORT jdouble JNICALL Java_org_datavyu_plugins_ffmpegplayer_MoviePlayer0_getStartTime0(JNIEnv *env,
    jclass thisClass, jint streamId) {
    MoviePlayer* moviePlayer = getMoviePlayer(streamId);
    return moviePlayer != nullptr ? moviePlayer->getStartTime() : 0;
}

JNIEXPORT jdouble JNICALL Java_org_datavyu_plugins_ffmpegplayer_MoviePlayer0_getEndTime0(JNIEnv *env,
    jclass thisClass, jint streamId) {
    MoviePlayer* moviePlayer = getMoviePlayer(streamId);
    return moviePlayer != nullptr ? moviePlayer->getEndTime() : 0;
}

JNIEXPORT jdouble JNICALL Java_org_datavyu_plugins_ffmpegplayer_MoviePlayer0_getDuration0(JNIEnv *env,
    jclass thisClass, jint streamId) {
    MoviePlayer* moviePlayer = getMoviePlayer(streamId);
    return moviePlayer != nullptr ? moviePlayer->getDuration() : 0;
}

JNIEXPORT jdouble JNICALL Java_org_datavyu_plugins_ffmpegplayer_MoviePlayer0_getCurrentTime0(JNIEnv *env,
    jclass thisClass, jint streamId) {
    MoviePlayer* moviePlayer = getMoviePlayer(streamId);
    return moviePlayer != nullptr ? moviePlayer->getCurrentTime() : -1;
}

JNIEXPORT void JNICALL Java_org_datavyu_plugins_ffmpegplayer_MoviePlayer0_setAudioSyncDelay0(JNIEnv *env,
    jclass thisClass, jint streamId, jlong delay) {
    // TODO: Wire this up properly
}

JNIEXPORT void JNICALL Java_org_datavyu_plugins_ffmpegplayer_MoviePlayer0_start0(JNIEnv *env, jclass thisClass,
    jint streamId) {
    // Nothing to do here for now
}

JNIEXPORT void JNICALL Java_org_datavyu_plugins_ffmpegplayer_MoviePlayer0_stop0(JNIEnv *env, jclass thisClass,
    jint streamId) {
    // Nothing to do here for now
}

JNIEXPORT void JNICALL Java_org_datavyu_plugins_ffmpegplayer_MoviePlayer0_pause0(JNIEnv *env, jclass thisClass,
    jint streamId) {
    MoviePlayer* moviePlayer = getMoviePlayer(streamId);
    if (moviePlayer != nullptr) {
        moviePlayer->pause();
    }
}

JNIEXPORT void JNICALL Java_org_datavyu_plugins_ffmpegplayer_MoviePlayer0_seek0(JNIEnv *env,
    jclass thisClass, jint streamId, jdouble jTime) {
    MoviePlayer* moviePlayer = getMoviePlayer(streamId);
    if (moviePlayer != nullptr) {
        moviePlayer->seek(jTime); // set current time limits range
    }
}

JNIEXPORT void JNICALL Java_org_datavyu_plugins_ffmpegplayer_MoviePlayer0_setPlaybackSpeed0(JNIEnv *env,
    jclass thisClass, jint streamId, jfloat speed) {
    MoviePlayer* moviePlayer = getMoviePlayer(streamId);
    if (moviePlayer != nullptr) {
        moviePlayer->setSpeed(speed);
    }
}

JNIEXPORT void JNICALL Java_org_datavyu_plugins_ffmpegplayer_MoviePlayer0_reset0(JNIEnv *env,
    jclass thisClass, jint streamId) {
    // Nothing to do here
}

JNIEXPORT void JNICALL Java_org_datavyu_plugins_ffmpegplayer_MoviePlayer0_close0(JNIEnv *env, jclass thisClass,
    jint streamId) {
    MoviePlayer* moviePlayer = getMoviePlayer(streamId);
    if (moviePlayer != nullptr) {
        moviePlayer->close();
        idToMovieStream.erase(streamId);
        delete moviePlayer;
    }
}

JNIEXPORT jboolean JNICALL Java_org_datavyu_plugins_ffmpegplayer_MoviePlayer0_loadNextAudioData0(JNIEnv *env,
    jclass thisClass, jint streamId) {
    MoviePlayer* moviePlayer = getMoviePlayer(streamId);
    return moviePlayer != nullptr && moviePlayer->loadNextAudioData();
}

JNIEXPORT jobject JNICALL Java_org_datavyu_plugins_ffmpegplayer_MoviePlayer0_getAudioBuffer0(JNIEnv *env,
    jclass thisClass, jint streamId, jint nBytes) {
    MoviePlayer* moviePlayer = getMoviePlayer(streamId);
    return moviePlayer != nullptr ? moviePlayer->getAudioBuffer(env, nBytes) : 0;
}

JNIEXPORT jstring JNICALL Java_org_datavyu_plugins_ffmpegplayer_MoviePlayer0_getSampleFormat0(JNIEnv *env,
    jclass thisClass, jint streamId) {
    MoviePlayer* moviePlayer = getMoviePlayer(streamId);
    return moviePlayer != nullptr ? moviePlayer->getSampleFormat(env) : 0;
}

JNIEXPORT jstring JNICALL Java_org_datavyu_plugins_ffmpegplayer_MoviePlayer0_getCodecName0(JNIEnv *env,
    jclass thisClass, jint streamId) {
    MoviePlayer* moviePlayer = getMoviePlayer(streamId);
    return moviePlayer != nullptr ? moviePlayer->getCodecName(env) : 0;
}

JNIEXPORT jfloat JNICALL Java_org_datavyu_plugins_ffmpegplayer_MoviePlayer0_getSampleRate0(JNIEnv *env,
    jclass thisClass, jint streamId) {
    MoviePlayer* moviePlayer = getMoviePlayer(streamId);
    return moviePlayer != nullptr ? moviePlayer->getSampleRate() : 0;
}

JNIEXPORT jint JNICALL Java_org_datavyu_plugins_ffmpegplayer_MoviePlayer0_getSampleSizeInBits0(JNIEnv *env,
    jclass thisClass, jint streamId) {
    MoviePlayer* moviePlayer = getMoviePlayer(streamId);
    return moviePlayer != nullptr ? moviePlayer->getSampleSizeInBits() : 0;
}

JNIEXPORT jint JNICALL Java_org_datavyu_plugins_ffmpegplayer_MoviePlayer0_getNumberOfSoundChannels0(JNIEnv *env,
    jclass thisClass, jint streamId) {
    MoviePlayer* moviePlayer = getMoviePlayer(streamId);
    return moviePlayer != nullptr ? moviePlayer->getNumberOfSoundChannels() : 0;
}

JNIEXPORT jint JNICALL Java_org_datavyu_plugins_ffmpegplayer_MoviePlayer0_getFrameSize0(JNIEnv *env,
    jclass thisClass, jint streamId) {
    MoviePlayer* moviePlayer = getMoviePlayer(streamId);
    return moviePlayer != nullptr ? moviePlayer->getFrameSize() : 0;
}

JNIEXPORT jfloat JNICALL Java_org_datavyu_plugins_ffmpegplayer_MoviePlayer0_getFrameRate0(JNIEnv *env,
    jclass thisClass, jint streamId) {
    MoviePlayer* moviePlayer = getMoviePlayer(streamId);
    return moviePlayer != nullptr ? moviePlayer->getFrameRate() : 0;
}

JNIEXPORT jboolean JNICALL Java_org_datavyu_plugins_ffmpegplayer_MoviePlayer0_bigEndian0(JNIEnv *env,
    jclass thisClass, jint streamId) {
    MoviePlayer* moviePlayer = getMoviePlayer(streamId);
    return moviePlayer != nullptr && moviePlayer->bigEndian();
}

JNIEXPORT jint JNICALL Java_org_datavyu_plugins_ffmpegplayer_MoviePlayer0_getNumberOfColorChannels0(JNIEnv *env,
    jclass thisClass, jint streamId) {
    MoviePlayer* moviePlayer = getMoviePlayer(streamId);
    return moviePlayer != nullptr ? moviePlayer->getNumberOfColorChannels() : 0;
}

JNIEXPORT jint JNICALL Java_org_datavyu_plugins_ffmpegplayer_MoviePlayer0_getHeight0(JNIEnv *env, jclass thisClass,
    jint streamId) {
    MoviePlayer* moviePlayer = getMoviePlayer(streamId);
    return moviePlayer != nullptr ? moviePlayer->getHeight() : 0;
}

JNIEXPORT jint JNICALL Java_org_datavyu_plugins_ffmpegplayer_MoviePlayer0_getWidth0(JNIEnv *env, jclass thisClass,
    jint streamId) {
    MoviePlayer* moviePlayer = getMoviePlayer(streamId);
    return moviePlayer != nullptr ? moviePlayer->getWidth() : 0;
}

JNIEXPORT jobject JNICALL Java_org_datavyu_plugins_ffmpegplayer_MoviePlayer0_getFrameBuffer0(JNIEnv *env,
    jclass thisClass, jint streamId) {
    MoviePlayer* moviePlayer = getMoviePlayer(streamId);
    return moviePlayer != nullptr ? moviePlayer->getFrameBuffer(env) : 0;
}

JNIEXPORT jint JNICALL Java_org_datavyu_plugins_ffmpegplayer_MoviePlayer0_loadNextImageFrame0(JNIEnv *env,
    jclass thisClass, jint streamId) {
    MoviePlayer* moviePlayer = getMoviePlayer(streamId);
    return moviePlayer != nullptr ? moviePlayer->loadNextImageFrame() : 0;
}