#include "org_datavyu_plugins_ffmpegplayer_MovieStream.h"
#include "ImageBuffer.hpp"
#include "AudioBuffer.h"
#include "AudioFormatHelper.hpp"
#include "Logger.h"
#include "AVLogger.h"
#include <jni.h>
#include <cstdio>
#include <cstdlib>
#include <ctime>
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
}

// Florian Raudies, Mountain View, CA.
// vcvarsall.bat x64

// To create the header file run 'javah -d native org.datavyu.plugins.ffmpegplayer.MovieStream' from the directory 'src'

/*
cl org_datavyu_plugins_ffmpegplayer_MovieStream.cpp /Fe"..\..\MovieStream" /I"C:\Users\Florian\FFmpeg\FFmpeg-n3.4" /I"C:\Program Files\Java\jdk1.8.0_151\include" /I"C:\Program Files\Java\jdk1.8.0_151\include\win32" /showIncludes /MD /LD /link "C:\Program Files\Java\jdk1.8.0_151\lib\jawt.lib" "C:\Users\Florian\FFmpeg\FFmpeg-n3.4\libavcodec\avcodec.lib" "C:\Users\Florian\FFmpeg\FFmpeg-n3.4\libavformat\avformat.lib" "C:\Users\Florian\FFmpeg\FFmpeg-n3.4\libavutil\avutil.lib" "C:\Users\Florian\FFmpeg\FFmpeg-n3.4\libswscale\swscale.lib" "C:\Users\Florian\FFmpeg\FFmpeg-n3.4\libswresample\swresample.lib"
copy ..\..\MovieStream.* ..\..\lib\MovieStream.*
*/

// Add flag /MDd for debugging information and flag /DEBUG:FULL to add all symbols to the PDB file
// On debug flag for cl; see https://docs.microsoft.com/en-us/cpp/build/reference/debug-generate-debug-info

// use dumpbin /ALL MovieStream.lib to list the function symbols
// Make sure that visual studio's 'vc' folder is in the path

/* no AV sync correction is done if below the minimum AV sync threshold */
#define AV_SYNC_THRESHOLD_MIN 0.04
/* AV sync correction is done if above the maximum AV sync threshold */
#define AV_SYNC_THRESHOLD_MAX 0.1

#define AV_NOSYNC_THRESHOLD 2.0
#define PTS_DELTA_THRESHOLD 3
#define MAX_AUDIO_FRAME_SIZE 192000
#define SAMPLE_CORRECTION_PERCENT_MAX 10
#define AUDIO_DIFF_AVG_NB 20
#define AUDIO_AVG_COEF exp(log(0.01 / AUDIO_DIFF_AVG_NB))
#define WAIT_TIMEOUT_IN_MSEC 250

/*****************************************************************************************
 * Movie stream class that encapsulates functionality for the playback of a movie stream.
 ****************************************************************************************/
class MovieStream {

public:
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

    /** Image frame read from stream */
    AVFrame *pImageFrame;

    /** Image frame displayed (wrapped by buffer) */
    AVFrame *pAVFrameShow;

    /** Dictionary is a key-value store */
    AVDictionary *pOptsDict;

    /** Scaling context from source to target image format and size */
    struct SwsContext *pSwsImageCtx;

    /** Pointer to the image buffer */
    ImageBuffer *pImageBuffer;

    /** Last present time stamp. Set to 0 on reset */
    int64_t imageLastPts;

    /** Difference between time stamps. Set to 0 on reset */
    int64_t	imageDeltaPts;

    /** Accumulated difference. Set to 0 on reset */
    double imageDiffCum;

    /** The system clock's last time */
    std::chrono::high_resolution_clock::time_point lastDisplayTime;

    /** The current presentation time stamp for the video */
    double videoTime;

    /** Means that we need to initialize the next image frame */
    std::atomic<bool> resetClock;


    /** Status in the movie file. */

    /** Quit the decoding thread */
    bool quit;

    /** Flag indicates that we loaded a movie */
    bool loadedMovie;

    /** The stream reached the end of the file */
    std::atomic<bool> endOrStart;

    /** Toggles the direction of playback */
    std::atomic<bool> toggle;

    /** Controls the speed of playback as factor of the original playback speed */
    double speed;

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

    /** Time as progressed in the audio stream; measured through packets */
    double audioTime;

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

    /** This boolean is set if we do not play sound */
    std::atomic<bool> playSound;


    /**  Variables that control random seeking. */

    /** Flag to initiate a random seek request */
    bool seekReq;

    /** The time stamp that should be present after a random seek */
    int64_t seekPts;

    /** Logger */
    Logger *pLogger;


    /** Parameters for the viewing window. */

    /** Width of the viewing window. Set to 0 on reset */
    int	widthView;

    /** Height of the viewing window. Set to 0 on reset */
    int	heightView;

    /** Horizontal starting position of the viewing window. Set to 0 on reset */
    int	x0View;

    /** Vertical starting position of the viewing window. Set to 0 on reset */
    int	y0View;

    /** True if a viewing window is set, otherwise false. Set to false on reset */
    bool doView;

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

    MovieStream() :
        width(0),
        height(0),
        nChannel(3),
        duration(0),
        pFormatCtx(nullptr),
        iImageStream(-1),
        pImageStream(nullptr),
        pImageCodecCtx(nullptr),
        pImageCodec(nullptr),
        pImageFrame(nullptr),
        pAVFrameShow(nullptr),
        pOptsDict(nullptr),
        pSwsImageCtx(nullptr),
        pImageBuffer(nullptr),
        imageLastPts(0),
        imageDeltaPts(0),
        imageDiffCum(0),
        videoTime(0),
        resetClock(false),
        quit(false),
        loadedMovie(false),
        endOrStart(false),
        toggle(false),
        speed(1),
        lastWritePts(0),
        avgDeltaPts(1),
        pDecodeFrame(nullptr),
        iAudioStream(-1),
        pAudioInCodecCtx(nullptr),
        pAudioOutCodecCtx(nullptr),
        pResampleCtx(nullptr),
        pAudioStream(nullptr),
        audioTime(0),
        pAudioBuffer(nullptr),
        pAudioBufferData(nullptr),
        nAudioBuffer(0),
        nAudioData(0),
        iAudioData(0),
        audioDiffCum(0),
        audioDiffAvgCoef(AUDIO_AVG_COEF),
        audioDiffThreshold(0),
        audioDiffAvgCount(0),
        playSound(false),
        seekPts(0),
        seekReq(false),
        pLogger(nullptr),
        widthView(0),
        heightView(0),
        x0View(0),
        y0View(0),
        doView(false),
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

    virtual ~MovieStream() {
        // Clean up memory
        free(pLoadAudioByteBuffer);
        av_packet_free(&pAudioDecodeFramePkt);
        av_frame_free(&pAudioDecodeAVFrame);
    }

    static int getId() {
        return ++id;
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

    /**
     * Get the time passed from the audio.
     */
    double getAudioTime() const {
        double pts = audioTime; // Maintained by the audio thread
        int hw_buf_size = nAudioData - iAudioData;
        int bytes_per_sec = pAudioOutCodecCtx->sample_rate * pAudioOutCodecCtx->channels * 2;
        if (bytes_per_sec) {
            pts -= (double)hw_buf_size / bytes_per_sec;
        }
        return pts;
    }

    /**
     * Synchronize the audio to the system clock by adding (repeating last sample)
     * or removing (truncating last samples) from the audio buffer.
     */
    int synchronizeAudio(short *samples, int nSamples) {
        double diffPts, avgDiffPts;
        int nWanted, nMin, nMax;
        int nBytePerSample = 2 * pAudioOutCodecCtx->channels;

        // Get the time difference
        diffPts = getAudioTime() - (double)std::chrono::system_clock::to_time_t(std::chrono::system_clock::now());
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

                    if ((errNo = MovieStream::initConvertedSamples(&convertedInSamples,
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

                    if ((errNo = MovieStream::convertSamples((const uint8_t**)pAudioDecodeAVFrame->extended_data,
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
     * True if we are in forward playback
     */
    bool isForwardPlayback() const {
        return loadedMovie ? !pImageBuffer->isBackward() : true;
    }

    /**
     * ALWAYS seeks before the current target. Use this method for precise location.  It may take longer though.
     */
    void doSeekBefore(int64_t target) {
        // make sure the target is in the range
        if (av_seek_frame(pFormatCtx, iImageStream, target, AVSEEK_FLAG_BACKWARD) < 0) {
            pLogger->error("Failed seek before to frame %I64d.", target/avgDeltaPts);
        } else {
            lastWritePts = target;
            pLogger->info("Succeeded seek before to frame %I64d.", target/avgDeltaPts);
            if (hasVideoStream()) {
                avcodec_flush_buffers(pImageCodecCtx);
            }
            if (hasAudioStream()) {
                avcodec_flush_buffers(pAudioInCodecCtx);
                avcodec_flush_buffers(pAudioOutCodecCtx);
            }
        }
    }

    /**
     * Seek approximately to the target frame. Use this for random seeks.
     */
    void doSeek(int64_t target, int64_t delta, bool flush) {
        pLogger->info("Seeking to frame %I64d with delta %I64d.", target/avgDeltaPts, delta/avgDeltaPts);
        if (delta != 0) {
            int64_t min_ = delta > 0 ? target - delta + 2: INT64_MIN;
            int64_t max_ = delta < 0 ? target - delta - 2: INT64_MAX;
            int seekFlags = delta < 0 ? AVSEEK_FLAG_BACKWARD : 0;
            if (avformat_seek_file(pFormatCtx, iImageStream, min_, target, max_, seekFlags) < 0) {
                pLogger->error("Failed seek to frame %I64d.", target/avgDeltaPts);
            } else {
                lastWritePts = target;
                pLogger->info("Succeeded seek to frame %I64d with min %I64d and max %I64d for delta %I64d.",
                              target/avgDeltaPts, min_/avgDeltaPts, max_/avgDeltaPts, delta/avgDeltaPts);
                if (hasVideoStream()) {
                    if (flush) {
                        pImageBuffer->flush();
                    }
                    avcodec_flush_buffers(pImageCodecCtx);
                }
                if (hasAudioStream()) {
                    if (flush) {
                        pAudioBuffer->flush();
                    }
                    avcodec_flush_buffers(pAudioInCodecCtx);
                    avcodec_flush_buffers(pAudioOutCodecCtx);
                }
            }
        }
    }

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
        bool initClock = true; // initial state

        while (!quit) {

            // Random seek
            if (seekReq) {
                endOrStart = seekReq = false;
                doSeek(seekPts, seekPts-lastWritePts, true);
                pLogger->info("Status after seek.");
                pImageBuffer->log(*pLogger, avgDeltaPts);
                initClock = true; // Will set the next frame's to init
            }

            // Toggle direction
            if (toggle) {
                toggle = endOrStart = false;
                pImageBuffer->block(); // Block the buffer again; unblocked for toggle
                int delta_ = pImageBuffer->toggle(lastWritePts/avgDeltaPts);

                // Initialize frame after toggle
                AVFrame* pFrameTmp;
                pImageBuffer->peek(&pFrameTmp);
                if (pFrameTmp) {
                    static_cast<AVFrameMetaData*>(pFrameTmp->opaque)->initClock = true;
                }
                // Never jump beyond the start/end of the stream
                seekPts = limitToRange(lastWritePts + (1+delta_)*avgDeltaPts);
                pLogger->info("Toggle to frame %I64d by %d frames.", seekPts/avgDeltaPts, delta_);
                doSeekBefore(seekPts); // no flushing
            }

            // Find next frame in reverse playback
            if (delta) {
                seekPts = limitToRange(lastWritePts + (1+delta)*avgDeltaPts);
                endOrStart = false;
                pLogger->info("Jump to frame %I64d by %d frames.", seekPts/avgDeltaPts, delta);
                doSeekBefore(seekPts); // no flushing
                delta = 0;
            }

            // Read frame
            int ret = av_read_frame(pFormatCtx, &packet);

            // Set end or start of file
            endOrStart = ret == AVERROR_EOF || pImageBuffer->atStart();

            if (endOrStart) {
                pLogger->info("Reached the end or start of the file.");
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
                avcodec_decode_video2(pImageCodecCtx, pImageFrame, &frameFinished, &packet);

                // Did we get a full video frame?
                if (frameFinished) {

                    // Set the presentation time stamp (PTS)
                    int64_t readPts = pImageFrame->pkt_pts;

                    // Skip frames until we are at or beyond seekPts
                    if (readPts >= seekPts) {

                        // Get the next writable buffer. This may block and can be unblocked by flushing
                        AVFrame* pFrameBuffer;
                        long currentFrame = readPts/avgDeltaPts;

                        pLogger->info("Write frame %I64d.", currentFrame);

                        pImageBuffer->writeRequest(&pFrameBuffer, currentFrame);

                        // Did we get a frame buffer?
                        if (pFrameBuffer) {

                            // Convert the image from its native color format into the RGB color format
                            sws_scale(
                                pSwsImageCtx,
                                (uint8_t const * const *)pImageFrame->data,
                                pImageFrame->linesize,
                                0,
                                pImageCodecCtx->height,
                                pFrameBuffer->data,
                                pFrameBuffer->linesize);
                            pFrameBuffer->repeat_pict = pImageFrame->repeat_pict;
                            pFrameBuffer->pts = lastWritePts = readPts;
                            static_cast<AVFrameMetaData*>(pFrameBuffer->opaque)->initClock = initClock;
                            delta = pImageBuffer->writeComplete(currentFrame);
                            pLogger->info("Wrote frame %I64d with init %d.", lastWritePts/avgDeltaPts, initClock);
                            pImageBuffer->log(*pLogger, avgDeltaPts);

                            initClock = false;
                        } else {
                            pLogger->info("Write request aborted.");
                        }
                    } else {
                        pLogger->info("Skipped frame %ld.", readPts/avgDeltaPts);
                    }

                    // Reset frame container to initial state
                    av_frame_unref(pImageFrame);
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

    double getCurrentTime() const { return hasVideoStream() ? videoTime : audioTime; }

    void setTime(double time) {
        if (hasVideoStream() || hasAudioStream()) {
            lastWritePts = seekPts = limitToRange( ((int64_t)(time/(avgDeltaPts * av_q2d(pImageStream->time_base))))*avgDeltaPts );
            seekReq = true;
        }
    }

    void setPlaybackSpeed(float inSpeed) {

        if (hasVideoStream() || hasAudioStream()) {

            toggle = pImageBuffer->isBackward() != (inSpeed < 0);

            if (toggle) {
                // Unblock the buffer to allow for the toggle and write
                pImageBuffer->unblock();
            }

            speed = fabs(inSpeed);

            // If we have audio need to turn off at playback other than 1x
            if (hasAudioStream()) {

                // Do we have speedOne?
                bool speedOne = fabs(inSpeed-1.0) <= std::numeric_limits<float>::epsilon();

                // If we switch from playing sound to not play sound or vice versa
                if (!playSound && speedOne || playSound && !speedOne) {
                    pAudioBuffer->flush();
                    avcodec_flush_buffers(pAudioInCodecCtx);
                    avcodec_flush_buffers(pAudioOutCodecCtx);
                }

                // If we have speed one than we play sound (otherwise not)
                playSound = speedOne;
            }
        }
    }

    void reset() {
    	// If we have a video or an audio stream
    	if (hasVideoStream() || hasAudioStream()) {

    		if (pImageBuffer->isBackward()) {
    			// Seek to the end of the file
    			lastWritePts = seekPts = pImageStream->start_time + pImageStream->duration - 2*avgDeltaPts;
    			seekReq = true;
    			pLogger->info("Rewind to end %I64d pts, %I64d frames.", seekPts, seekPts/avgDeltaPts);

    		} else {
    			// Seek to the start of the file
    			lastWritePts = seekPts = pImageStream->start_time;
    			seekReq = true;
    			pLogger->info("Rewind to start %I64d pts, %I64d frames.", seekPts, seekPts/avgDeltaPts);
    		}
    	}
    }

    int loadNextImageFrame() {

        // If there is no image stream or if the image buffer is empty, then return -1
        // The latter condition avoids blocking when reaching the start/end of the stream
        if (!hasVideoStream() || pImageBuffer->empty()) return -1;

        // Counts the number of frames that this method requested (could be 0, 1, 2)
        int nFrame = 0;

        // Get the next read pointer
        AVFrame *pAVFrame;
        uint64_t latestPts;

    // TODO: Remove this ugly label/goto
    retry:
        pImageBuffer->read(&pAVFrame);

        // We received a frame -- no flushing
        if (pAVFrame) {

            latestPts = pAVFrame->pts;

            // Increase the number of read frames by one
            nFrame++;

            // Reset differences
            bool init = static_cast<AVFrameMetaData*>(pAVFrame->opaque)->initClock || resetClock;

            // Compute the difference for the presentation time stamps in seconds
            double diffStream = init ? 0 : std::labs(latestPts - imageLastPts)/speed*av_q2d(pImageStream->time_base);

            // Get the current time
            auto time = std::chrono::high_resolution_clock::now();

            // Compute the time difference in seconds
            double diffClock = init ? 0
                : std::chrono::duration_cast<std::chrono::microseconds>(time-lastDisplayTime).count()/1000000.0;

            // Compute the difference between stream and clock times
            double diffCumNew = init ? 0 : (imageDiffCum + diffStream - diffClock);

            // Calculate the delay for wait
            double delay = diffStream;

            // If the frame is repeated split the delay in half
            if (pAVFrame->repeat_pict) {
                delay += delay/2;
            }

            // Compute the synchronization threshold (see ffplay.c)
            double syncThreshold = std::max(AV_SYNC_THRESHOLD_MIN, std::min(delay, AV_SYNC_THRESHOLD_MAX));

            pLogger->info("Pts difference: %lf; time difference: %lf sec; cum diff %lf; sync %lf; initialize %d.",
                          diffStream, diffClock, diffCumNew, syncThreshold, init);

            // The time difference is within the no sync threshold
            if (fabs(diffCumNew) < AV_NOSYNC_THRESHOLD) {

                // If our time difference is lower than the sync threshold, then skip frames
                if (diffCumNew <= -syncThreshold) {
                    delay = std::max(0.0, delay + diffCumNew);
                    //imageLastPts = latestPts;

                    if (!pImageBuffer->empty()) {
                        goto retry;
                    }

                // If the time difference is within -syncThreshold ... +0 then show frame instantly
                } else if (diffCumNew < 0) {
                    delay = 0;

                // If the time difference is greater than the syncThreshold increase the delay
                } else if (diffCumNew >= syncThreshold) {
                    delay *= 2;
                }

                // Delay read to keep the desired frame rate.
                if (delay > 0) {
                    pLogger->info("Image waiting for %lf seconds.\n", delay);
                    std::this_thread::sleep_for(std::chrono::milliseconds((int)(delay*1000+0.5)));
                }

                imageDeltaPts = init ? (int64_t)(1.0/(av_q2d(pImageStream->time_base)*av_q2d(pImageStream->avg_frame_rate)))
                                     : std::labs(latestPts - imageLastPts);

            } else {
                if (!resetClock) {
                    uint64_t diffPts = (-diffCumNew)*speed/av_q2d(pImageStream->time_base);
                    int diffFrames = diffPts/avgDeltaPts;
                    nFrame += diffFrames;
                    pLogger->info("Correct frames %d.", diffFrames);
                    seekReq = true;
                    latestPts = seekPts = limitToRange(imageLastPts + diffPts);
                } else {
                    pLogger->info("Reset clock in progress issue seek later.");
                }
            }

            // Update values for next call
            imageLastPts = latestPts;
            imageDiffCum = diffCumNew;
            lastDisplayTime = time;

            // Show the frame by updating the "show" pointer
            pAVFrameShow = pAVFrame;
            videoTime = pAVFrameShow->pts * av_q2d(pImageStream->time_base);

            // Reset 'reset clock'
            resetClock = false;

            // We displayed a frame
            pLogger->info("Display frame %I64d.", pAVFrameShow->pts/avgDeltaPts);
        }

        // Return the number of read frames; the number may be larger than 1
        return nFrame;
    }

    bool loadNextAudioData() {

        // If we do not have an audio stream, we are done decoding, or the audio
        // buffer is empty, return false to indicate that we did not load data
        if (!hasAudioStream() || endOrStart || pAudioBuffer->empty()) {
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

    	// If we have a video or an audio stream
    	if (hasVideoStream() || hasAudioStream()) {

    		// Set the quit flag for the decoding thread
    		quit = true;

    		// Drain the audio buffer that means no packets can be get or put
    		if (hasAudioStream()) {
    			pAudioBuffer->flush();
    		}

    		// Flush the image buffer buffer which unblocks all readers/writers
    		if (hasVideoStream()) {
    			pImageBuffer->flush();
    		}

    		// Join and free the decoding thread
    		pDecodeFrame->join();
    		delete pDecodeFrame;

    		// If we have a video stream
    		if (hasVideoStream()) {
    			// Free the image buffer buffer and reset pointer
    			delete pImageBuffer;
    			pImageBuffer = nullptr;

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
    			av_free(pImageFrame);
    			pImageFrame = nullptr;
    			pAVFrameShow = nullptr;
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
    		videoTime = 0;
    		resetClock = false;

    		// Set default values for playback speed.
    		toggle = false;
    		speed = 1;
    		imageLastPts = 0;
    		imageDeltaPts = 0;
    		avgDeltaPts = 1;
    		lastWritePts = 0;
    		imageDiffCum = 0;
    		endOrStart = false;

    		// Reset value for seek and backward request.
    		seekReq = false;
    		seekPts = 0;

    		// Reset variables from viewing window.
    		widthView = 0;
    		heightView = 0;
    		x0View = 0;
    		y0View = 0;
    		doView = false;

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

    bool view(int x0, int y0, int inWidth, int inHeight) {
        // Done if we did not load any movie.
        if (!hasVideoStream()) {
            return false;
        }

        // Error if the start coordinates are out of range.
        if (x0 < 0 || y0 < 0 || x0 >= width || y0 >= height) {
            pLogger->error("Start position (x0, y0) = (%d, %d) pixels is out of range ",
                "(%d, %d) ... (%d, %d) pixels.", x0, y0, 0, 0, width, height);
            return false;
        }

        // Error if the width or height is too large.
        if ((x0+inWidth) > width || (y0+inHeight) > height) {
            pLogger->error("x0 + width %d pixels > %d pixels or y0 + height %d pixels > %d pixels.",
                inWidth, width, inHeight, height);
            return false;
        }

        // We need to restrict the view if we do not use the original window of
        // (0, 0) ... (width, height).
        doView = x0 > 0 || y0 > 0 || (x0+inWidth) < width || (y0+inHeight) < height;

        // Set the view variables.
        x0View = x0;
        y0View = y0;
        widthView = inWidth;
        heightView = inHeight;

        // Log the new viewing window.
        pLogger->info("Set view to (%d, %d) to (%d, %d).", x0, y0, x0+inWidth, y0+inHeight);

        // Return true to indicate that the window has been adjusted.
        return true;
    }

    jobject getFrameBuffer(JNIEnv* env) {
        // No movie was loaded return nullptr.
        if (!hasVideoStream()) return 0;

        // We have a viewing window that needs to be supported.
        if (doView) {
            for (int iRow = 0; iRow < heightView; ++iRow) {
                for (int iCol = 0; iCol < widthView; ++iCol) {
                    for (int iChannel = 0; iChannel < nChannel; ++iChannel) {
                        int iSrc = ((y0View+iRow)*width + x0View+iCol)*nChannel + iChannel;
                        int iDst = (iRow*widthView + iCol)*nChannel + iChannel;
                        pAVFrameShow->data[0][iDst] = pAVFrameShow->data[0][iSrc];
                    }
                }
            }
            return env->NewDirectByteBuffer((void*) pAVFrameShow->data[0],
                                        widthView*heightView*nChannel*sizeof(uint8_t));
        }

        // Construct a new direct byte buffer pointing to data from pAVFrameShow.
        return env->NewDirectByteBuffer((void*) pAVFrameShow->data[0],
                                        width*height*nChannel*sizeof(uint8_t));
    }

};

int MovieStream::id = -1;
std::map<int, MovieStream*> idToMovieStream;

MovieStream* getMovieStream(int streamId) {
    return idToMovieStream.find(streamId) != idToMovieStream.end() ? idToMovieStream[streamId] : nullptr;
}

JNIEXPORT jintArray JNICALL Java_org_datavyu_plugins_ffmpegplayer_MovieStream_open0(JNIEnv *env, jclass thisClass,
    jstring jFileName, jstring jVersion, jobject jAudioFormat) {

	int errNo = 0;
    jintArray returnArray = env->NewIntArray(2);
    jint *returnValues = env->GetIntArrayElements(returnArray, NULL);

	const char *fileName = env->GetStringUTFChars(jFileName, 0);
	const char *version = env->GetStringUTFChars(jVersion, 0);

    int streamId = MovieStream::getId();
    returnValues[0] = 0;
    returnValues[1] = streamId;
	MovieStream* movieStream = new MovieStream();
    std::string logFileName = std::string(fileName, strlen(fileName));
    logFileName = logFileName.substr(logFileName.find_last_of("/\\") + 1) + ".log";
    //movieStream->pLogger = new FileLogger(logFileName);
    movieStream->pLogger = new StreamLogger(&std::cerr);
	movieStream->pLogger->info("Version: %s", version);

	// Register all formats and codecs
	av_register_all();

	// Open the video file.
	if ((errNo = avformat_open_input(&movieStream->pFormatCtx, fileName, nullptr, nullptr)) != 0) {
		movieStream->pLogger->error("Could not open file %s.", fileName);
		returnValues[0] = errNo;
		env->ReleaseIntArrayElements(returnArray, returnValues, NULL);
		return returnArray;
	}

	// Retrieve the stream information.
	if ((errNo = avformat_find_stream_info(movieStream->pFormatCtx, nullptr)) < 0) {
		movieStream->pLogger->error("Unable to find stream information for file %s.", fileName);
		returnValues[0] = errNo;
		env->ReleaseIntArrayElements(returnArray, returnValues, NULL);
		return returnArray;
	}

	// Log that opened a file.
	movieStream->pLogger->info("Opened file %s.", fileName);

	// Dump information about file onto standard error.
	std::string info = log_av_format(movieStream->pFormatCtx, 0, fileName, 0);
	std::istringstream lines(info);
    std::string line;
    while (std::getline(lines, line)) {
		movieStream->pLogger->info("%s", line.c_str());
    }

	// Find the first video stream.
	movieStream->iImageStream = -1;
	for (int iStream = 0; iStream < movieStream->pFormatCtx->nb_streams; ++iStream) {
		if (movieStream->pFormatCtx->streams[iStream]->codec->codec_type == AVMEDIA_TYPE_VIDEO) {
			movieStream->iImageStream = iStream;
			break;
		}
	}

	if (movieStream->hasVideoStream()) {
		movieStream->pLogger->info("Found image stream with id %d.", movieStream->iImageStream);
	} else {
		movieStream->pLogger->info("Could not find an image stream.");
	}

	// Find the first audio stream
	movieStream->iAudioStream = -1;
	for (int iStream = 0; iStream < movieStream->pFormatCtx->nb_streams; iStream++) {
		if (movieStream->pFormatCtx->streams[iStream]->codec->codec_type == AVMEDIA_TYPE_AUDIO) {
			movieStream->iAudioStream = iStream;
			break;
		}
	}

	if (movieStream->hasAudioStream()) {
		movieStream->pLogger->info("Found audio stream with id %d.", movieStream->iAudioStream);
	} else {
		movieStream->pLogger->info("Could not find an audio stream.");
	}

	if (!movieStream->hasVideoStream() && !movieStream->hasAudioStream()) {
		movieStream->pLogger->error("Could not find an image stream or audio stream.");
		avformat_close_input(&movieStream->pFormatCtx);
		returnValues[0] = AVERROR_INVALIDDATA;
		env->ReleaseIntArrayElements(returnArray, returnValues, NULL);
		return returnArray;
	}

	if (movieStream->hasVideoStream()) {
		// Get a pointer to the video stream.
		movieStream->pImageStream = movieStream->pFormatCtx->streams[movieStream->iImageStream];

		// Get a pointer to the codec context for the video stream.
		movieStream->pImageCodecCtx = movieStream->pImageStream->codec;

		// Find the decoder for the video stream.
		movieStream->pImageCodec = avcodec_find_decoder(movieStream->pImageCodecCtx->codec_id);
		if (movieStream->pImageCodec == nullptr) {
			movieStream->pLogger->error("Unsupported codec for file %s.", fileName);
			returnValues[0] = AVERROR_DECODER_NOT_FOUND;
            env->ReleaseIntArrayElements(returnArray, returnValues, NULL);
            return returnArray;
		}

		// Open codec.
		if ((errNo = avcodec_open2(movieStream->pImageCodecCtx, movieStream->pImageCodec,
		                           &movieStream->pOptsDict)) < 0) {
			movieStream->pLogger->error("Unable to open codec for file %s.", fileName);
			returnValues[0] = errNo;
            env->ReleaseIntArrayElements(returnArray, returnValues, NULL);
            return returnArray;
		}

		// Allocate video frame.
		movieStream->pImageFrame = av_frame_alloc();

		// Initialize the color model conversion/rescaling context.
		movieStream->pSwsImageCtx = sws_getContext
			(
				movieStream->pImageCodecCtx->width,
				movieStream->pImageCodecCtx->height,
				movieStream->pImageCodecCtx->pix_fmt,
				movieStream->pImageCodecCtx->width,
				movieStream->pImageCodecCtx->height,
				AV_PIX_FMT_RGB24,
				SWS_BILINEAR,
				nullptr,
				nullptr,
				nullptr
			);

		// Initialize the width, height, and duration.
		movieStream->width = movieStream->pImageCodecCtx->width;
		movieStream->height = movieStream->pImageCodecCtx->height;
		movieStream->duration = movieStream->pImageStream->duration * av_q2d(movieStream->pImageStream->time_base);
		movieStream->pLogger->info("Duration of movie %d x %d pixels is %2.3f seconds, %I64d pts.",
					               movieStream->width, movieStream->height, movieStream->duration,
					               movieStream->pImageStream->duration);
		movieStream->pLogger->info("Time base %2.5f.", av_q2d(movieStream->pImageStream->time_base));

		// Initialize the delta pts using the average frame rate and the average pts.
		movieStream->avgDeltaPts = movieStream->imageDeltaPts = (int64_t)(1.0/
		        (av_q2d(movieStream->pImageStream->time_base)*av_q2d(movieStream->pImageStream->avg_frame_rate)));
		movieStream->pLogger->info("Average delta %I64d pts.", movieStream->avgDeltaPts);

		// Initialize the image buffer.

		//int width, int height, long firstItem, int nItem, int nMaxReverse
		movieStream->pImageBuffer = new ImageBuffer(movieStream->width,
		                                            movieStream->height,
		                                            movieStream->pImageStream->start_time/movieStream->avgDeltaPts);
	}

	// *************************************************************************
	// Work on audio
	// *************************************************************************
	if (movieStream->hasAudioStream()) {
		AVCodec *aOutCodec = nullptr;
		AVCodec *aInCodec = nullptr;

		struct AudioFormat audioFormat;
		if ((errNo = getAudioFormat(env, &audioFormat, jAudioFormat, movieStream->pLogger)) < 0) {
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
			movieStream->pLogger->error("Encoding %s is not supported.\n", audioFormat.encodingName.c_str());
			returnValues[0] = AVERROR_INVALIDDATA;
            env->ReleaseIntArrayElements(returnArray, returnValues, NULL);
            return returnArray;
		}

		// Supported codecs are: AV_CODEC_ID_PCM_U8 and AV_CODEC_ID_PCM_S16LE.
		AVCodecID codecId = av_get_pcm_codec(sampleFormat, audioFormat.bigEndian);

		movieStream->pAudioStream = movieStream->pFormatCtx->streams[movieStream->iAudioStream];

		movieStream->pAudioInCodecCtx = movieStream->pAudioStream->codec;

		if (!(aInCodec = avcodec_find_decoder(movieStream->pAudioInCodecCtx->codec_id))) {
			movieStream->pLogger->error("Could not find input codec!\n");
			avformat_close_input(&movieStream->pFormatCtx);
			returnValues[0] = AVERROR_EXIT;
            env->ReleaseIntArrayElements(returnArray, returnValues, NULL);
            return returnArray;
		}

		if ((errNo = avcodec_open2(movieStream->pAudioInCodecCtx, aInCodec, NULL)) < 0) {
			movieStream->pLogger->error("Could not open audio codec. Error message: '%s'.\n", getErrorText(errNo));
			avformat_close_input(&movieStream->pFormatCtx);
			returnValues[0] = errNo;
            env->ReleaseIntArrayElements(returnArray, returnValues, NULL);
            return returnArray;
		}
		movieStream->pAudioBuffer = new AudioBuffer();

		// create the output codec (alternative is stero: AV_CODEC_ID_PCM_U8)
		if (!(aOutCodec = avcodec_find_encoder(codecId))) {
			movieStream->pLogger->error("Could not create output codec.");
			avformat_close_input(&movieStream->pFormatCtx);
			returnValues[0] = AVERROR_EXIT;
			env->ReleaseIntArrayElements(returnArray, returnValues, NULL);
			return returnArray;
		}

		// Allocate the output codec context
		if (!(movieStream->pAudioOutCodecCtx = avcodec_alloc_context3(aOutCodec))) {
			movieStream->pLogger->error("Could not allocate an encoding output context.\n");
			avformat_close_input(&movieStream->pFormatCtx);
			returnValues[0] = AVERROR_EXIT;
			env->ReleaseIntArrayElements(returnArray, returnValues, NULL);
			return returnArray;
		}

		// Set the sample format
		movieStream->pAudioOutCodecCtx->sample_fmt = sampleFormat;

		// Set channels, either from input jAudioFormat or from input codec
		if (audioFormat.channels != 0) {
			movieStream->pAudioOutCodecCtx->channels = audioFormat.channels;
			movieStream->pAudioOutCodecCtx->channel_layout = av_get_default_channel_layout(audioFormat.channels);
		} else {
			movieStream->pAudioOutCodecCtx->channels = movieStream->pAudioInCodecCtx->channels;
			movieStream->pAudioOutCodecCtx->channel_layout = movieStream->pAudioInCodecCtx->channel_layout;
			audioFormat.channels = movieStream->pAudioInCodecCtx->channels;
		}

		// Set sample rate, either from input jAudioFormat or from input codec
		if (audioFormat.sampleRate != 0) {
			movieStream->pAudioOutCodecCtx->sample_rate = (int) audioFormat.sampleRate;
		} else {
			movieStream->pAudioOutCodecCtx->sample_rate = movieStream->pAudioInCodecCtx->sample_rate;
			audioFormat.sampleRate = movieStream->pAudioInCodecCtx->sample_rate;
		}

		// Set bit rate
		if (audioFormat.frameRate != 0) {
			movieStream->pAudioOutCodecCtx->bit_rate = (int) audioFormat.frameRate;
		} else {
			movieStream->pAudioOutCodecCtx->bit_rate = movieStream->pAudioInCodecCtx->bit_rate;
			audioFormat.sampleRate = movieStream->pAudioInCodecCtx->bit_rate;
		}

		// Set the frame size
		audioFormat.frameSize = av_get_bytes_per_sample(sampleFormat);

		// Open the encoder for the audio stream to use it later.
		if ((errNo = avcodec_open2(movieStream->pAudioOutCodecCtx, aOutCodec, NULL)) < 0) {
			movieStream->pLogger->error("Could not open output codec. Error message: '%s'.\n", getErrorText(errNo));
			avformat_close_input(&movieStream->pFormatCtx);
			returnValues[0] = errNo;
			env->ReleaseIntArrayElements(returnArray, returnValues, NULL);
			return returnArray;
		}

		// Initialize the re-sampler to be able to convert audio sample formats.
		if ((errNo = MovieStream::initResampler(movieStream->pAudioInCodecCtx,
		                                        movieStream->pAudioOutCodecCtx,
		                                        &movieStream->pResampleCtx,
		                                        movieStream->pLogger)) < 0) {
			avformat_close_input(&movieStream->pFormatCtx);
			returnValues[0] = errNo;
			env->ReleaseIntArrayElements(returnArray, returnValues, NULL);
			return returnArray;
		}

		// bits_per_coded_sample is only set after opening the audio codec context
		audioFormat.sampleSizeInBits = movieStream->pAudioOutCodecCtx->bits_per_coded_sample;

		if ((errNo = setAudioFormat(env, jAudioFormat, audioFormat, movieStream->pLogger)) < 0) {
		    // TODO: Add error message
			returnValues[0] = errNo;
			env->ReleaseIntArrayElements(returnArray, returnValues, NULL);
			return returnArray;
		}
	}

	movieStream->playSound = movieStream->hasAudioStream();

	// Seek to the start of the file (must have an image or audio stream).
	movieStream->lastWritePts = movieStream->seekPts = movieStream->hasVideoStream()
	                                                        ? movieStream->pImageStream->start_time : 0;
	                                                        // assume 0 is the start time for the audio stream
	movieStream->seekReq = true;

	// Start the decode thread
	movieStream->pDecodeFrame = new std::thread(&MovieStream::readNextFrame, movieStream);
	movieStream->pLogger->info("Started decoding thread!");

	// Set the value for loaded movie true
	movieStream->loadedMovie = true;

	// Free strings
	env->ReleaseStringUTFChars(jFileName, fileName);
	env->ReleaseStringUTFChars(jVersion, version);

	// Add player to the map
	idToMovieStream[streamId] = movieStream;

    env->ReleaseIntArrayElements(returnArray, returnValues, NULL);
    return returnArray;
}

JNIEXPORT jdouble JNICALL Java_org_datavyu_plugins_ffmpegplayer_MovieStream_getAverageFrameRate0(JNIEnv *env,
    jclass thisClass, jint streamId) {
    MovieStream* movieStream = getMovieStream(streamId);
    return movieStream != nullptr ? movieStream->getAverageFrameRate() : 0;
}

JNIEXPORT jboolean JNICALL Java_org_datavyu_plugins_ffmpegplayer_MovieStream_hasVideoStream0(JNIEnv *env,
    jclass thisClass, jint streamId) {
    MovieStream* movieStream = getMovieStream(streamId);
    return movieStream != nullptr && movieStream->hasVideoStream();
}

JNIEXPORT jboolean JNICALL Java_org_datavyu_plugins_ffmpegplayer_MovieStream_hasAudioStream0(JNIEnv *env,
    jclass thisClass, jint streamId) {
    MovieStream* movieStream = getMovieStream(streamId);
    return movieStream != nullptr && movieStream->hasAudioStream();
}

JNIEXPORT jdouble JNICALL Java_org_datavyu_plugins_ffmpegplayer_MovieStream_getStartTime0(JNIEnv *env,
    jclass thisClass, jint streamId) {
    MovieStream* movieStream = getMovieStream(streamId);
    return movieStream != nullptr ? movieStream->getStartTime() : 0;
}

JNIEXPORT jdouble JNICALL Java_org_datavyu_plugins_ffmpegplayer_MovieStream_getEndTime0(JNIEnv *env,
    jclass thisClass, jint streamId) {
    MovieStream* movieStream = getMovieStream(streamId);
    return movieStream != nullptr ? movieStream->getEndTime() : 0;
}

JNIEXPORT jdouble JNICALL Java_org_datavyu_plugins_ffmpegplayer_MovieStream_getDuration0(JNIEnv *env,
    jclass thisClass, jint streamId) {
    MovieStream* movieStream = getMovieStream(streamId);
    return movieStream != nullptr ? movieStream->getDuration() : 0;
}

JNIEXPORT jdouble JNICALL Java_org_datavyu_plugins_ffmpegplayer_MovieStream_getCurrentTime0(JNIEnv *env,
    jclass thisClass, jint streamId) {
    MovieStream* movieStream = getMovieStream(streamId);
    return movieStream != nullptr ? movieStream->getCurrentTime() : -1;
}

JNIEXPORT void JNICALL Java_org_datavyu_plugins_ffmpegplayer_MovieStream_start0(JNIEnv *env, jclass thisClass,
    jint streamId) {
    MovieStream* movieStream = getMovieStream(streamId);
    if (movieStream != nullptr) {
        movieStream->resetClock = true;
    }
}

JNIEXPORT void JNICALL Java_org_datavyu_plugins_ffmpegplayer_MovieStream_stop0(JNIEnv *env, jclass thisClass,
    jint streamId) {
    // Nothing to do here for now
}

JNIEXPORT void JNICALL Java_org_datavyu_plugins_ffmpegplayer_MovieStream_step0(JNIEnv *env, jclass thisClass,
    jint streamId) {
    MovieStream* movieStream = getMovieStream(streamId);
    if (movieStream != nullptr) {
        movieStream->resetClock = true;
    }
}

JNIEXPORT void JNICALL Java_org_datavyu_plugins_ffmpegplayer_MovieStream_setTime0(JNIEnv *env,
    jclass thisClass, jint streamId, jdouble jTime) {
    MovieStream* movieStream = getMovieStream(streamId);
    if (movieStream != nullptr) {
        movieStream->setTime(jTime);
    }
}

JNIEXPORT void JNICALL Java_org_datavyu_plugins_ffmpegplayer_MovieStream_setPlaybackSpeed0(JNIEnv *env,
    jclass thisClass, jint streamId, jfloat speed) {
    MovieStream* movieStream = getMovieStream(streamId);
    if (movieStream != nullptr) {
        movieStream->setPlaybackSpeed(speed);
    }
}

JNIEXPORT void JNICALL Java_org_datavyu_plugins_ffmpegplayer_MovieStream_reset0(JNIEnv *env,
    jclass thisClass, jint streamId) {
    MovieStream* movieStream = getMovieStream(streamId);
    if (movieStream != nullptr) {
        movieStream->reset();
    }
}

JNIEXPORT void JNICALL Java_org_datavyu_plugins_ffmpegplayer_MovieStream_close0(JNIEnv *env, jclass thisClass,
    jint streamId) {
    MovieStream* movieStream = getMovieStream(streamId);
    if (movieStream != nullptr) {
        movieStream->close();
        idToMovieStream.erase(streamId);
        delete movieStream;
    }
}

JNIEXPORT jboolean JNICALL Java_org_datavyu_plugins_ffmpegplayer_MovieStream_loadNextAudioData0(JNIEnv *env,
    jclass thisClass, jint streamId) {
    MovieStream* movieStream = getMovieStream(streamId);
    return movieStream != nullptr && movieStream->loadNextAudioData();
}

JNIEXPORT jobject JNICALL Java_org_datavyu_plugins_ffmpegplayer_MovieStream_getAudioBuffer0(JNIEnv *env,
    jclass thisClass, jint streamId, jint nBytes) {
    MovieStream* movieStream = getMovieStream(streamId);
    return movieStream != nullptr ? movieStream->getAudioBuffer(env, nBytes) : 0;
}

JNIEXPORT jstring JNICALL Java_org_datavyu_plugins_ffmpegplayer_MovieStream_getSampleFormat0(JNIEnv *env,
    jclass thisClass, jint streamId) {
    MovieStream* movieStream = getMovieStream(streamId);
    return movieStream != nullptr ? movieStream->getSampleFormat(env) : 0;
}

JNIEXPORT jstring JNICALL Java_org_datavyu_plugins_ffmpegplayer_MovieStream_getCodecName0(JNIEnv *env,
    jclass thisClass, jint streamId) {
    MovieStream* movieStream = getMovieStream(streamId);
    return movieStream != nullptr ? movieStream->getCodecName(env) : 0;
}

JNIEXPORT jfloat JNICALL Java_org_datavyu_plugins_ffmpegplayer_MovieStream_getSampleRate0(JNIEnv *env,
    jclass thisClass, jint streamId) {
    MovieStream* movieStream = getMovieStream(streamId);
    return movieStream != nullptr ? movieStream->getSampleRate() : 0;
}

JNIEXPORT jint JNICALL Java_org_datavyu_plugins_ffmpegplayer_MovieStream_getSampleSizeInBits0(JNIEnv *env,
    jclass thisClass, jint streamId) {
    MovieStream* movieStream = getMovieStream(streamId);
    return movieStream != nullptr ? movieStream->getSampleSizeInBits() : 0;
}

JNIEXPORT jint JNICALL Java_org_datavyu_plugins_ffmpegplayer_MovieStream_getNumberOfSoundChannels0(JNIEnv *env,
    jclass thisClass, jint streamId) {
    MovieStream* movieStream = getMovieStream(streamId);
    return movieStream != nullptr ? movieStream->getNumberOfSoundChannels() : 0;
}

JNIEXPORT jint JNICALL Java_org_datavyu_plugins_ffmpegplayer_MovieStream_getFrameSize0(JNIEnv *env,
    jclass thisClass, jint streamId) {
    MovieStream* movieStream = getMovieStream(streamId);
    return movieStream != nullptr ? movieStream->getFrameSize() : 0;
}

JNIEXPORT jfloat JNICALL Java_org_datavyu_plugins_ffmpegplayer_MovieStream_getFrameRate0(JNIEnv *env,
    jclass thisClass, jint streamId) {
    MovieStream* movieStream = getMovieStream(streamId);
    return movieStream != nullptr ? movieStream->getFrameRate() : 0;
}

JNIEXPORT jboolean JNICALL Java_org_datavyu_plugins_ffmpegplayer_MovieStream_bigEndian0(JNIEnv *env,
    jclass thisClass, jint streamId) {
    MovieStream* movieStream = getMovieStream(streamId);
    return movieStream != nullptr && movieStream->bigEndian();
}

JNIEXPORT jboolean JNICALL Java_org_datavyu_plugins_ffmpegplayer_MovieStream_setPlaySound0(JNIEnv *env,
    jclass thisCLass, jint streamId, jboolean playSound) {
    MovieStream* movieStream = getMovieStream(streamId);
    return movieStream != nullptr && movieStream->setPlaySound(playSound);
}

JNIEXPORT jint JNICALL Java_org_datavyu_plugins_ffmpegplayer_MovieStream_getNumberOfColorChannels0(JNIEnv *env,
    jclass thisClass, jint streamId) {
    MovieStream* movieStream = getMovieStream(streamId);
    return movieStream != nullptr ? movieStream->getNumberOfColorChannels() : 0;
}

JNIEXPORT jint JNICALL Java_org_datavyu_plugins_ffmpegplayer_MovieStream_getHeight0(JNIEnv *env, jclass thisClass,
    jint streamId) {
    MovieStream* movieStream = getMovieStream(streamId);
    return movieStream != nullptr ? movieStream->getHeight() : 0;
}

JNIEXPORT jint JNICALL Java_org_datavyu_plugins_ffmpegplayer_MovieStream_getWidth0(JNIEnv *env, jclass thisClass,
    jint streamId) {
    MovieStream* movieStream = getMovieStream(streamId);
    return movieStream != nullptr ? movieStream->getWidth() : 0;
}

JNIEXPORT jboolean JNICALL Java_org_datavyu_plugins_ffmpegplayer_MovieStream_view0(JNIEnv *env, jclass thisClass,
    jint streamId, jint x0, jint y0, jint width, jint height) {
    MovieStream* movieStream = getMovieStream(streamId);
    return movieStream != nullptr && movieStream->view(x0, y0, width, height);
}

JNIEXPORT jobject JNICALL Java_org_datavyu_plugins_ffmpegplayer_MovieStream_getFrameBuffer0(JNIEnv *env,
    jclass thisClass, jint streamId) {
    MovieStream* movieStream = getMovieStream(streamId);
    return movieStream != nullptr ? movieStream->getFrameBuffer(env) : 0;
}

JNIEXPORT jint JNICALL Java_org_datavyu_plugins_ffmpegplayer_MovieStream_loadNextImageFrame0(JNIEnv *env,
    jclass thisClass, jint streamId) {
    MovieStream* movieStream = getMovieStream(streamId);
    return movieStream != nullptr ? movieStream->loadNextImageFrame() : 0;
}