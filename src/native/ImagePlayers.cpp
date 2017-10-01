#include <jni.h>
#include <cstdio>
#include <cmath>
#include <cassert>
#include <vector>

extern "C" {
	#include <libavcodec/avcodec.h>
	#include <libavformat/avformat.h>
	#include <libswscale/swscale.h>
	#include <libavutil/error.h> // error codes
}

#include "Logger.h"
#include "AVLogger.h"
#include "ImagePlayers.h"
#include "ImageBuffer.h"
#include <thread>
#include <chrono>
#include <algorithm>
#include <cstdlib>
#include <sstream>
#include <map>

// Florian Raudies, Mountain View, CA.
// vcvarsall.bat x64
// cl ImagePlayers.cpp /Fe"..\..\lib\ImagePlayers" /I"C:\Users\Florian\FFmpeg-release-3.3" /I"C:\Program Files\Java\jdk1.8.0_144\include" /I"C:\Program Files\Java\jdk1.8.0_144\include\win32" /showIncludes /MD /LD /link "C:\Program Files\Java\jdk1.8.0_144\lib\jawt.lib" "C:\Users\Florian\FFmpeg-release-3.3\libavcodec\avcodec.lib" "C:\Users\Florian\FFmpeg-release-3.3\libavformat\avformat.lib" "C:\Users\Florian\FFmpeg-release-3.3\libavutil\avutil.lib" "C:\Users\Florian\FFmpeg-release-3.3\libswscale\swscale.lib"

#define AV_SYNC_THRESHOLD 0.01
#define AV_NOSYNC_THRESHOLD 10.0
#define PTS_DELTA_THRESHOLD 3

class ImagePlayer {

    // TODO: Control visibility
public:
    // Basic information about the movie.
    static int id;

    // Width of the image in pixels
    int width;

    // Height of the image in pixels
    int height;

    // Number of color channels
    int	nChannel;

    // Duration of the video in seconds
    double duration;

    // Audio and video format context
    AVFormatContext	*pFormatCtx;

    // Index of the (1st) video stream
    int	iVideoStream;

    // Video stream
    AVStream *pVideoStream;

    // Video codec context
    AVCodecContext *pVideoCodecCtx;

    // Video codec
    AVCodec	*pVideoCodec;

    // Video frame read from stream
    AVFrame	*pVideoFrame;

    // Video frame displayed (wrapped by buffer)
    AVFrame	*pVideoFrameShow;

    // Dictionary is a key-value store
    AVDictionary *pOptsDict;

    // Scaling context from source to target image format and size
    struct SwsContext *pSwsImageCtx;

    // Decoding thread that decodes frames and is started when opening a video
    std::thread *pDecodeFrame;

    // Quit the decoding thread
    bool quit;

    // Flag indicates that we loaded a movie. Used to protect uninitialized pointers
    bool loadedMovie;

    // Pointer to the image buffer
    ImageBuffer	*pImageBuffer;

    // The stream reached the end of the file
    bool eof;

    // Toggle the direction of playback
    bool toggle;

    // The speed of the playback, e.g. 0.5 plays at half speed
    double speed;

    // Last presentation time stamp -- set to 0 on reset
    int64_t lastPts;

    // Difference between two successive presentation time stamps
    int64_t	deltaPts;

    // Average difference of presentation time stamps -- set to 1 on reset
    int64_t	avgDeltaPts;

    // Last written presentation time stamp -- set to 0 on reset
    int64_t	lastWritePts;

    // The difference between times -- set to 0 on reset
    double diff;

    // The last time
    std::chrono::high_resolution_clock::time_point lastTime;

    // Seek request
    bool seekReq;

    // See presentation time stamp
    int64_t	seekPts;

    // Seek flags
    int seekFlags;

    // Width of viewing window -- set to 0 on reset
    int widthView;

    // Height of viewing window -- set to 0 on reset
    int	heightView;

    // Horizontal start position -- set to 0 on reset
    int x0View;

    // Vertical start position -- set to 0 on reset
    int y0View;

    // True if we use a viewing window -- set to false on reset
    bool doView;

    // Logger per video file use the filename for the log file -- flush and empty on rest
    Logger *pLogger;


    /**
     * Constructor for an image player set the defaults
     */
    ImagePlayer() :
        width(0), 
        height(0), 
        nChannel(3), 
        duration(0), 
        pFormatCtx(nullptr),
        iVideoStream(-1),
        pVideoStream(nullptr),
        pVideoCodecCtx(nullptr),
        pVideoCodec(nullptr),
        pVideoFrame(nullptr),
        pVideoFrameShow(nullptr),
        pOptsDict(nullptr),
        pSwsImageCtx(nullptr),
        pDecodeFrame(nullptr),
        quit(false),
        loadedMovie(false),
        pImageBuffer(nullptr),
        eof(false),
        toggle(false),
        speed(1),
        lastPts(0),
        deltaPts(0),
        avgDeltaPts(1),
        lastWritePts(0),
        diff(0),
        seekReq(false),
        seekPts(0),
        seekFlags(0),
        widthView(0),
        heightView(0),
        x0View(0),
        y0View(0),
        doView(false),
        pLogger(nullptr) { /* nothing to do here */ }

    static int getId() {
        return ++id;
    }

    /**
     * True if writing reached the start of the file. This happens in reverse mode.
     * We are not at the end yet if we are in reverse (last condition).
     */
    bool atStartForWrite() const {
        return pImageBuffer->isReverse()
            && lastWritePts <= pVideoStream->start_time+avgDeltaPts
            && !pImageBuffer->inReverse();
    }

    /**
     * True if reading reached the start of the file. This happens in reverse mode.
     */
    bool atStartForRead() const {
        return atStartForWrite() && pImageBuffer->empty();
    }

    /**
     * True if writing reached the end of the file. This happens in forward mode.
     */
    bool atEndForWrite() const {
        // return lastWritePts-pVideoStream->start_time >= pVideoStream->duration;
        // Duration is just an estimate and usually larger than the acutal number of frames.
        // I measured 8 - 14 additional frames that duration specifies.
        return !pImageBuffer->isReverse() && eof;
    }

    /**
     * True if reading reached the end of the file. This happens in forward mode.
     */
    bool atEndForRead() const {
        // The duration is not a reliable estimate for the end a video file.
        return !pImageBuffer->isReverse() && eof && pImageBuffer->empty();
    }

    /**
     * Reads the next frame from the video stream and supports:
     * - Random seek through the variable seekReq.
     * - Toggeling the direction through the variable toggle.
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
        while (!quit) {

            // Random seek.
            if (seekReq) {
                seekFlags |= AVSEEK_FLAG_BACKWARD;
                if (av_seek_frame(pFormatCtx, iVideoStream, seekPts, seekFlags) < 0) {
                    pLogger->error("Random seek of %I64d pts, %I64d frames unsuccessful.",
                        seekPts, seekPts/avgDeltaPts);
                } else {
                    pLogger->info("Random seek of %I64d pts, %I64d frames successful.",
                        seekPts, seekPts/avgDeltaPts);
                    pImageBuffer->flush();
                    avcodec_flush_buffers(pVideoCodecCtx);
                    lastWritePts = seekPts;
                }
                seekReq = false;
            }

            // Switch direction of playback.
            if (toggle) {
                std::pair<int,int> offsetDelta = pImageBuffer->toggle();
                int offset = offsetDelta.first;
                int delta = offsetDelta.second;
                int nShift = 0;

                pLogger->info("Toggle with offset %d and delta %d.", offset, delta);

                // Even if we may not seek backward it is safest to get the prior keyframe.
                seekFlags |= AVSEEK_FLAG_BACKWARD;
                if (pImageBuffer->isReverse()) {
                    int maxDelta = (-pVideoStream->start_time+lastWritePts)/avgDeltaPts;
                    delta = std::min(offset+delta, maxDelta) - offset;
                    pImageBuffer->setBackwardAfterToggle(delta);
                    nShift = -(offset + delta) + 1;
                } else {
                    nShift = offset;
                }

                lastWritePts = seekPts = lastWritePts + nShift*avgDeltaPts;

                if (av_seek_frame(pFormatCtx, iVideoStream, seekPts, seekFlags) < 0) {
                    pLogger->error("Toggle seek of %I64d pts, %I64d frames unsuccessful.",
                                    seekPts, seekPts/avgDeltaPts);
                } else {
                    pLogger->info("Toggle seek of %I64d pts, %I64d frames successful.",
                                    seekPts, seekPts/avgDeltaPts);
                    avcodec_flush_buffers(pVideoCodecCtx);
                }
                toggle = false;
            }

            // Check start or end before issuing seek request!
            if (atStartForWrite() || atEndForWrite()) {
                pLogger->info("Reached the start or end with seek %I64d pts, ",
                    "%I64d frames and last write %I64d pts, %I64d frames.",
                    seekPts, seekPts/avgDeltaPts, lastWritePts,
                    lastWritePts/avgDeltaPts);
                std::this_thread::sleep_for(std::chrono::milliseconds(500));
                continue;
            }

            // Find next frame in reverse playback.
            if (pImageBuffer->seekReq()) {

                // Find the number of frames that can still be read
                int maxDelta = (-pVideoStream->start_time+lastWritePts)/avgDeltaPts;

                std::pair<int, int> offsetDelta = pImageBuffer->seekBackward();

                int offset = offsetDelta.first;
                int delta = offsetDelta.second;

                delta = std::min(offset+delta, maxDelta) - offset;

                pLogger->info("Seek frame for reverse playback with offset %d and min delta %d.", offset, delta);

                pImageBuffer->setBackwardAfterSeek(delta);

                seekFlags |= AVSEEK_FLAG_BACKWARD;
                int nShift = -(offset + delta) + 1;

                lastWritePts = seekPts = lastWritePts + nShift*avgDeltaPts;

                if (av_seek_frame(pFormatCtx, iVideoStream, seekPts, seekFlags) < 0) {
                    pLogger->error("Reverse seek of %I64d pts, %I64d frames unsuccessful.",
                                    seekPts, seekPts/avgDeltaPts);
                } else {
                    pLogger->info("Reverse seek of %I64d pts, %I64d frames successful.",
                                    seekPts, seekPts/avgDeltaPts);
                    avcodec_flush_buffers(pVideoCodecCtx);
                }
            }

            // Read frame.
            int ret = av_read_frame(pFormatCtx, &packet);

            // Set eof for end of file.
            eof = ret == AVERROR_EOF;

            // Any error that is not eof.
            if (ret < 0 && !eof) {
                pLogger->error("Error:  %c, %c, %c, %c.\n",
                    static_cast<char>((-ret >> 0) & 0xFF),
                    static_cast<char>((-ret >> 8) & 0xFF),
                    static_cast<char>((-ret >> 16) & 0xFF),
                    static_cast<char>((-ret >> 24) & 0xFF));
                std::this_thread::sleep_for(std::chrono::milliseconds(500));

            // We got a frame! Let's decode it.
            } else {

                // Is this a packet from the video stream?
                if (packet.stream_index == iVideoStream) {

                    // Decode the video frame.
                    avcodec_decode_video2(pVideoCodecCtx, pVideoFrame, &frameFinished, &packet);

                    // Did we get a full video frame?
                    if (frameFinished) {

                        // Set the presentation time stamp.
                        int64_t readPts = pVideoFrame->pkt_pts;

                        // Skip frames until we are at or beyond of the seekPts time stamp.
                        if (readPts >= seekPts) {

                            // Get the next writeable buffer.
                            // This may block and can be unblocked with a flush.
                            AVFrame* pFrameBuffer = pImageBuffer->requestPutPtr();

                            // Did we get a frame buffer?
                            if (pFrameBuffer) {

                                // Convert the image from its native format into RGB.
                                sws_scale
                                (
                                    pSwsImageCtx,
                                    (uint8_t const * const *)pVideoFrame->data,
                                    pVideoFrame->linesize,
                                    0,
                                    pVideoCodecCtx->height,
                                    pFrameBuffer->data,
                                    pFrameBuffer->linesize
                                );
                                pFrameBuffer->repeat_pict = pVideoFrame->repeat_pict;
                                pFrameBuffer->pts = lastWritePts = readPts;
                                pImageBuffer->completePutPtr();
                                pLogger->info("Wrote %I64d pts, %I64d frames.",
                                                lastWritePts,
                                                lastWritePts/avgDeltaPts);
                                pImageBuffer->printLog();
                            }
                        }

                        // Reset frame container to initial state.
                        av_frame_unref(pVideoFrame);
                    }
                }
                // Free the packet that was allocated by av_read_frame.
                av_free_packet(&packet);
            }
        }
    }
    void setPlaybackSpeed(float inSpeed) {
        if (loadedMovie) {
            pImageBuffer->setNMinImages(1);
            toggle = pImageBuffer->isReverse() != (inSpeed < 0);
            speed = fabs(inSpeed);
        }
    }
    void setTime(double time) {
        if (loadedMovie) {
            lastWritePts = seekPts = ((int64_t)(time/(avgDeltaPts
                                                    *av_q2d(pVideoStream->time_base))))
                                                        *avgDeltaPts;
            seekReq = true;
        }
    }
    jobject getFrameBuffer(JNIEnv *env) {
        // No movie was loaded return nullptr.
        if (!loadedMovie) return 0;

        // We have a viewing window that needs to be supported.
        if (doView) {
            for (int iRow = 0; iRow < heightView; ++iRow) {
                for (int iCol = 0; iCol < widthView; ++iCol) {
                    for (int iChannel = 0; iChannel < nChannel; ++iChannel) {
                        int iSrc = ((y0View+iRow)*width + x0View+iCol)*nChannel + iChannel;
                        int iDst = (iRow*widthView + iCol)*nChannel + iChannel;
                        pVideoFrameShow->data[0][iDst] = pVideoFrameShow->data[0][iSrc];
                    }
                }
            }
            return env->NewDirectByteBuffer((void*) pVideoFrameShow->data[0],
                                        widthView*heightView*nChannel*sizeof(uint8_t));
        }

        // Construct a new direct byte buffer pointing to data from pVideoFrameShow.
        return env->NewDirectByteBuffer((void*) pVideoFrameShow->data[0],
                                        width*height*nChannel*sizeof(uint8_t));
    }

    int loadNextFrame() {

        // No movie was loaded return -1.
        if (!loadedMovie) return -1;

        // Counts the number of frames that this method requested (could be 0, 1, 2).
        int nFrame = 0;

        // Get the next read pointer.
        AVFrame *pVideoFrameTmp = pImageBuffer->getGetPtr();

        // We received a frame (no flushing).
        if (pVideoFrameTmp) {

            // Retrieve the presentation time for this first frame.
            uint64_t firstPts = pVideoFrameTmp->pts;

            // Increase the number of read frames by one.
            nFrame++;

            // Initialize if the pts difference is above threshold as a result of a seek.
            bool init = std::labs(firstPts - lastPts) > PTS_DELTA_THRESHOLD*deltaPts;

            // Compute the difference for the presentation time stamps.
            double diffPts = init ? 0 : std::labs(firstPts - lastPts)
                                            /speed*av_q2d(pVideoStream->time_base);

            // Get the current time.
            auto time = std::chrono::high_resolution_clock::now();

            // Compute the time difference.
            double timeDiff = init ? 0
                : std::chrono::duration_cast<std::chrono::microseconds>(time-lastTime).count()/1000000.0;

            // Compute the difference between times and pts.
            diff = init ? 0 : (diff + diffPts - timeDiff);

            // Calculate the delay that this display thread is required to wait.
            double delay = diffPts;

            // If the frame is repeated split this delay in half.
            if (pVideoFrameTmp->repeat_pict) {
                delay += delay/2;
            }

            // Compute the synchronization threshold (see ffplay.c)
            double syncThreshold = (delay > AV_SYNC_THRESHOLD) ? delay : AV_SYNC_THRESHOLD;

            // The time difference is within the no sync threshold.
            if (fabs(diff) < AV_NOSYNC_THRESHOLD) {

                // If our time difference is lower than the sync threshold, then skip a frame.
                if (diff <= -syncThreshold) {
                    AVFrame *pVideoFrameTmp2 = pImageBuffer->getGetPtr();
                    if (pVideoFrameTmp2) {
                        pVideoFrameTmp = pVideoFrameTmp2;
                        nFrame++;
                    }

                // If the time difference is within -syncThreshold ... +0 then show frame instantly.
                } else if (diff < 0) {
                    delay = 0;

                // If the time difference is greater than the syncThreshold increase the delay.
                } else if (diff >= syncThreshold) {
                    delay *= 2;
                }
            }

            // Save values for next call.
            deltaPts = init ? (int64_t)(1.0/(av_q2d(pVideoStream->time_base)
                                    *av_q2d(pVideoStream->avg_frame_rate)))
                                    : std::labs(firstPts - lastPts);
            lastPts = firstPts; // Need to use the first pts.
            lastTime = time;

            // Delay read to keep the desired frame rate.
            if (delay > 0) {
                std::this_thread::sleep_for(std::chrono::milliseconds((int)(delay*1000+0.5)));
            }

            // Update the pointer for the show frame.
            pVideoFrameShow = pVideoFrameTmp;

            // Log that we displayed a frame.
            pLogger->info("Display pts %I64d.", pVideoFrameShow->pts/avgDeltaPts);
        }

        // Return the number of read frames (not neccesarily all are displayed).
        return nFrame;
    }
    int getNumberOfChannels() const { return nChannel; }

    int getHeight() const { return height; }

    int getWidth() const { return width; }

    double getStartTime() const { return loadedMovie ? pVideoStream->start_time * av_q2d(pVideoStream->time_base) : 0; }

    double getEndTime() const { return loadedMovie ?
                        (pVideoStream->duration + pVideoStream->start_time) * av_q2d(pVideoStream->time_base) : 0; }

    double getDuration() const { return duration; }

    double getCurrentTime() const { return loadedMovie ? pVideoFrameShow->pts*av_q2d(pVideoStream->time_base) : 0; }

    bool isForwardPlayback() const { return loadedMovie ? (jboolean) !pImageBuffer->isReverse() : true; }

    void rewind() {
        if (loadedMovie) {
            if (pImageBuffer->isReverse()) {
                // Seek to the end of the file.
                lastWritePts = seekPts = pVideoStream->duration - 2*avgDeltaPts;
                seekReq = true;
                pLogger->info("Rewind to end %I64d pts, %I64d frames.", seekPts, seekPts/avgDeltaPts);
            } else {
                // Seek to the start of the file.
                lastWritePts = seekPts = pVideoStream->start_time;
                seekReq = true;
                pLogger->info("Rewind to start %I64d pts, %I64d frames.", seekPts, seekPts/avgDeltaPts);
            }
        }
    }

    bool hasNextFrame() const { return !(isForwardPlayback() && atEndForRead()
                                     || !isForwardPlayback() && atStartForRead()); }

    bool setView(int x0, int y0, int w, int h) {
        // Done if we did not load any movie.
        if (!loadedMovie) {
            return (jboolean) false;
        }

        // Error if the start coordinates are out of range.
        if (x0 < 0 || y0 < 0 || x0 >= width || y0 >= height) {
            pLogger->error("Start position (x0, y0) = (%d, %d) pixels is out of range ",
                "(%d, %d) ... (%d, %d) pixels.", x0, y0, 0, 0, width, height);
            return (jboolean) false;
        }

        // Error if the width or height is too large.
        if ((x0+w) > width || (y0+h) > height) {
            pLogger->error("Width %d pixels > %d pixels or height %d pixels > %d pixels.",
                w, h, width, height);
            return (jboolean) false;
        }

        // We need to restrict the view if we do not use the original window of
        // (0, 0) ... (width, height).
        doView = x0 > 0 || y0 > 0 || w < width || h < height;

        // Set the view variables.
        x0View = x0;
        y0View = y0;
        widthView = w;
        heightView = h;

        // Log the new viewing window.
        pLogger->info("Set view to (%d, %d) to (%d, %d).", x0, y0, x0+w, y0+h);

        // Return true to indicate that the window has been adjusted.
        return (jboolean) true;
    }

    void release() {
        if (loadedMovie) {

            // Set the quit flag for the decoding thread.
            quit = true;

            // Flush the image buffer buffer (which unblocks all readers/writers).
            pImageBuffer->flush();

            // Join the decoding thread with this one.
            pDecodeFrame->join();

            // Free the decoding thread.
            delete pDecodeFrame;

            // Free the image buffer buffer.
            delete pImageBuffer;
            pImageBuffer = nullptr;

            // Free the dictionary.
            av_dict_free(&pOptsDict);
            pOptsDict = nullptr;

            // Flush the buffers
            avcodec_flush_buffers(pVideoCodecCtx);

            // Close codec context
            avcodec_close(pVideoCodecCtx);
            pVideoCodecCtx = nullptr;

            // Free scaling context.
            sws_freeContext(pSwsImageCtx);
            pSwsImageCtx = nullptr;

            // Free the YUV frame
            av_free(pVideoFrame);
            pVideoFrame = nullptr;
            pVideoFrameShow = nullptr;

            // Close the video file
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
            toggle = false;
            speed = 1;
            lastPts = 0;
            deltaPts = 0;
            avgDeltaPts = 1;
            lastWritePts = 0;
            diff = 0;
            eof = false;

            // Reset value for seek request.
            seekReq = false;
            seekPts = 0;
            seekFlags = 0;

            // Reset variables from viewing window.
            widthView = 0;
            heightView = 0;
            x0View = 0;
            y0View = 0;
            doView = false;

            quit = false;

            if (pLogger) {
                pLogger->info("Closed logger.");
                delete pLogger;
                pLogger = nullptr;
            }
        }
    }
};

int ImagePlayer::id = -1;
std::map<int, ImagePlayer*> idToPlayer;

ImagePlayer* getPlayer(int playerId) {
    return idToPlayer.find(playerId) != idToPlayer.end() ? idToPlayer[playerId] : nullptr;
}

JNIEXPORT jintArray JNICALL Java_ImagePlayers_openMovie0(JNIEnv *env, jclass thisClass, jstring jFileName,
                                                         jstring jVersion) {
	int errNo = 0;
    jintArray returnArray = env->NewIntArray(2);
    jint *returnValues = env->GetIntArrayElements(returnArray, NULL);

	const char *fileName = env->GetStringUTFChars(jFileName, 0);
	const char *version = env->GetStringUTFChars(jVersion, 0);

    // We won't close any player in this case -- to allow opening the same video multiple times
    int playerId = ImagePlayer::getId();
    returnValues[0] = 0;
    returnValues[1] = playerId;
	ImagePlayer* player = new ImagePlayer();
    std::string logFileName = std::string(fileName, strlen(fileName));
    logFileName = logFileName.substr(logFileName.find_last_of("/\\") + 1) + ".log";
    player->pLogger = new FileLogger(logFileName);
    //player->pLogger = new StreamLogger(&std::cerr);
	player->pLogger->info("Version: %s", version);

	// Register all formats and codecs
	av_register_all();

	// Open the video file.
	if ((errNo = avformat_open_input(&player->pFormatCtx, fileName, nullptr, nullptr)) != 0) {
        player->pLogger->error("Could not open file %s.", fileName);
	    returnValues[0] = errNo;
	    env->ReleaseIntArrayElements(returnArray, returnValues, NULL);
		return returnArray;
	}

	// Retrieve the stream information.
	if ((errNo = avformat_find_stream_info(player->pFormatCtx, nullptr)) < 0) {
        player->pLogger->error("Unable to find stream information for file %s.", fileName);
	    returnValues[0] = errNo;
	    env->ReleaseIntArrayElements(returnArray, returnValues, NULL);
		return returnArray;
	}

	// Find the first video stream.
	int iVideoStream = -1;
	for (int iStream = 0; iStream < player->pFormatCtx->nb_streams; ++iStream) {
		if (player->pFormatCtx->streams[iStream]->codec->codec_type == AVMEDIA_TYPE_VIDEO) {
			iVideoStream = iStream;
			break;
		}
	}
    player->iVideoStream = iVideoStream;


	if (iVideoStream == -1) {
        player->pLogger->error("Unable to find a video stream in file %s.", fileName);
	    returnValues[0] = AVERROR_INVALIDDATA;
	    env->ReleaseIntArrayElements(returnArray, returnValues, NULL);
		return returnArray;
	}

	// Get a poitner to the video stream.
	player->pVideoStream = player->pFormatCtx->streams[iVideoStream];

	// Get a pointer to the codec context for the video stream.
	player->pVideoCodecCtx = player->pVideoStream->codec;

	// Find the decoder for the video stream.
	player->pVideoCodec = avcodec_find_decoder(player->pVideoCodecCtx->codec_id);
	if (player->pVideoCodec == nullptr) {
        player->pLogger->error("Unsupported codec for file %s.", fileName);
	    returnValues[0] = AVERROR_DECODER_NOT_FOUND;
	    env->ReleaseIntArrayElements(returnArray, returnValues, NULL);
		return returnArray;
	}

	// Open codec.
	if ((errNo = avcodec_open2(player->pVideoCodecCtx, player->pVideoCodec, &player->pOptsDict)) < 0) {
        player->pLogger->error("Unable to open codec for file %s.", fileName);
	    returnValues[0] = errNo;
	    env->ReleaseIntArrayElements(returnArray, returnValues, NULL);
		return returnArray;
	}

    // Log that opened a file.
    player->pLogger->info("Opened file %s.", fileName);

    // Dump information about file onto standard error.
    std::string info = log_av_format(player->pFormatCtx, 0, fileName, 0);
    std::istringstream lines(info);
    std::string line;
    while (std::getline(lines, line)) {
        player->pLogger->info("%s", line.c_str());
    }

	// Allocate video frame.
	player->pVideoFrame = av_frame_alloc();

	// Initialize the color model conversion/rescaling context.
	player->pSwsImageCtx = sws_getContext
		(
			player->pVideoCodecCtx->width,
			player->pVideoCodecCtx->height,
			player->pVideoCodecCtx->pix_fmt,
			player->pVideoCodecCtx->width,
			player->pVideoCodecCtx->height,
			AV_PIX_FMT_RGB24,
			SWS_BILINEAR,
			nullptr,
			nullptr,
			nullptr
		);

	// Initialize the widht, height, and duration.
	player->width = player->pVideoCodecCtx->width;
	player->height = player->pVideoCodecCtx->height;
	player->duration = player->pVideoStream->duration*av_q2d(player->pVideoStream->time_base);
    player->pLogger->info("Duration of movie %d x %d pixels is %2.3f seconds, %I64d pts.",
                          player->width, player->height, player->duration, player->pVideoStream->duration);
    player->pLogger->info("Time base %2.5f.", av_q2d(player->pVideoStream->time_base));

	// Initialize the delta pts using the average frame rate and the average pts.
	player->avgDeltaPts = player->deltaPts = (int64_t)(1.0/(av_q2d(player->pVideoStream->time_base)
										                    *av_q2d(player->pVideoStream->avg_frame_rate)));
    player->pLogger->info("Average delta %I64d pts.", player->avgDeltaPts);

	// Initialize the image buffer.
	player->pImageBuffer = new ImageBuffer(player->width, player->height, player->avgDeltaPts, player->pLogger);

	// Seek to the start of the file.
	player->lastWritePts = player->seekPts = player->pVideoStream->start_time;
	player->seekReq = true;

	// Start the decode thread.
	player->pDecodeFrame = new std::thread(&ImagePlayer::readNextFrame, *player);
    player->pLogger->info("Started decoding thread!");

	// Set the value for loaded move true.
	player->loadedMovie = true;

	// Free strings.
	env->ReleaseStringUTFChars(jFileName, fileName);
	env->ReleaseStringUTFChars(jVersion, version);

	// Add player to the map
	idToPlayer[playerId] = player;

	// Free array of return values (this also triggers the write-back to the jintArray)
    env->ReleaseIntArrayElements(returnArray, returnValues, NULL);

	return returnArray; // No error
}

JNIEXPORT jobject JNICALL Java_ImagePlayers_getFrameBuffer0(JNIEnv *env, jclass thisClass, jint playerId) {
    ImagePlayer* player = getPlayer(playerId);
    return player != nullptr ? player->getFrameBuffer(env) : nullptr;
}

JNIEXPORT jint JNICALL Java_ImagePlayers_loadNextFrame0(JNIEnv *env, jclass thisClass, jint playerId) {
    ImagePlayer* player = getPlayer(playerId);
    return player != nullptr ? player->loadNextFrame() : 0;
}

JNIEXPORT jint JNICALL Java_ImagePlayers_getNumberOfChannels0(JNIEnv *env, jclass thisClass, jint playerId) {
    ImagePlayer* player = getPlayer(playerId);
    return player != nullptr ? player->getNumberOfChannels() : 0;
}

JNIEXPORT jint JNICALL Java_ImagePlayers_getHeight0(JNIEnv *env, jclass thisClass, jint playerId) {
    ImagePlayer* player = getPlayer(playerId);
    return player != nullptr ? player->getHeight() : 0;
}

JNIEXPORT jint JNICALL Java_ImagePlayers_getWidth0(JNIEnv *env, jclass thisClass, jint playerId) {
    ImagePlayer* player = getPlayer(playerId);
    return player != nullptr ? player->getWidth() : 0;
}

JNIEXPORT jdouble JNICALL Java_ImagePlayers_getStartTime0(JNIEnv *env, jclass thisClass, jint playerId) {
    ImagePlayer* player = getPlayer(playerId);
    return player != nullptr ? player->getStartTime() : 0;
}

JNIEXPORT jdouble JNICALL Java_ImagePlayers_getEndTime0(JNIEnv *env, jclass thisClass, jint playerId) {
    ImagePlayer* player = getPlayer(playerId);
    return player != nullptr ? player->getEndTime() : 0;
}

JNIEXPORT jdouble JNICALL Java_ImagePlayers_getDuration0(JNIEnv *env, jclass thisClass, jint playerId) {
    ImagePlayer* player = getPlayer(playerId);
    return player != nullptr ? player->getDuration() : 0;
}

JNIEXPORT jdouble JNICALL Java_ImagePlayers_getCurrentTime0(JNIEnv *env, jclass thisClass, jint playerId) {
    ImagePlayer* player = getPlayer(playerId);
    return player != nullptr ? player->getCurrentTime() : -1;
}

JNIEXPORT void JNICALL Java_ImagePlayers_rewind0(JNIEnv *env, jclass thisClass, jint playerId) {
    ImagePlayer* player = getPlayer(playerId);
    if (player != nullptr) {
        player->rewind();
    }
}

JNIEXPORT jboolean JNICALL Java_ImagePlayers_isForwardPlayback0(JNIEnv *env, jclass thisClass, jint playerId) {
    ImagePlayer* player = getPlayer(playerId);
    return player != nullptr && player->isForwardPlayback();
}

JNIEXPORT jboolean JNICALL Java_ImagePlayers_atStartForRead0(JNIEnv *env, jclass thisClass, jint playerId) {
    ImagePlayer* player = getPlayer(playerId);
    return player != nullptr && player->atStartForRead();
}

JNIEXPORT jboolean JNICALL Java_ImagePlayers_atEndForRead0(JNIEnv *env, jclass thisClass, jint playerId) {
    ImagePlayer* player = getPlayer(playerId);
    return player != nullptr && player->atEndForRead();
}

JNIEXPORT void JNICALL Java_ImagePlayers_release0(JNIEnv *env, jclass thisClass, jint playerId) {
    ImagePlayer* player = getPlayer(playerId);
    if (player != nullptr) {
        player->release();
        idToPlayer.erase(playerId);
        delete player;
    }
}

JNIEXPORT void JNICALL Java_ImagePlayers_setPlaybackSpeed0(JNIEnv *env, jclass thisClass, jint playerId, jfloat speed) {
    ImagePlayer* player = getPlayer(playerId);
    if (player != nullptr) {
        player->setPlaybackSpeed(speed);
    }
}

JNIEXPORT void JNICALL Java_ImagePlayers_setTime0(JNIEnv *env, jclass thisClass, jint playerId, jdouble time) {
    ImagePlayer* player = getPlayer(playerId);
    if (player != nullptr) {
        player->setTime(time);
    }
}

JNIEXPORT jboolean JNICALL Java_ImagePlayers_hasNextFrame0(JNIEnv *env, jclass thisClass, jint playerId) {
    ImagePlayer* player = getPlayer(playerId);
    return player != nullptr && player->hasNextFrame();
}

JNIEXPORT jboolean JNICALL Java_ImagePlayers_setView0(JNIEnv *env, jclass thisClass, jint playerId, jint x0, jint y0,
    jint w, jint h) {
    ImagePlayer* player = getPlayer(playerId);
    return player != nullptr ? player->setView(x0, y0, w, h) : false;
}
