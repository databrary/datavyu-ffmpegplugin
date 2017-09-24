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
#include "ImagePlayer.h"
#include "ImageBuffer.h"
#include <thread>
#include <chrono>
#include <algorithm>
#include <cstdlib>
#include <sstream>

// Florian Raudies, Mountain View, CA.
// vcvarsall.bat x64
// cl ImagePlayer.cpp /Fe"..\..\lib\ImagePlayer" /I"C:\Users\Florian\FFmpeg-release-3.3" /I"C:\Program Files\Java\jdk1.8.0_144\include" /I"C:\Program Files\Java\jdk1.8.0_144\include\win32" /showIncludes /MD /LD /link "C:\Program Files\Java\jdk1.8.0_144\lib\jawt.lib" "C:\Users\Florian\FFmpeg-release-3.3\libavcodec\avcodec.lib" "C:\Users\Florian\FFmpeg-release-3.3\libavformat\avformat.lib" "C:\Users\Florian\FFmpeg-release-3.3\libavutil\avutil.lib" "C:\Users\Florian\FFmpeg-release-3.3\libswscale\swscale.lib"

#define AV_SYNC_THRESHOLD 0.01
#define AV_NOSYNC_THRESHOLD 10.0
#define PTS_DELTA_THRESHOLD 3

// Basic information about the movie.
int					width			= 0; // Width of the image in pixels.
int					height			= 0; // Height of the image in pixels.
int					nChannel		= 3; // Number of color channels.
double				duration		= 0; // Duration of the video in seconds.

// Variables (most of them are pointers) to the video stream, video frames,...
AVFormatContext		*pFormatCtx		= nullptr;	// Audio and video format context.
int					iVideoStream	= -1;		// Index of the (1st) video stream.
AVStream			*pVideoStream	= nullptr;	// Video stream.
AVCodecContext		*pVideoCodecCtx	= nullptr;	// Video codec context.
AVCodec				*pVideoCodec	= nullptr;	// Video codec.
AVFrame				*pVideoFrame	= nullptr;	// Video frame read from stream.
AVFrame				*pVideoFrameShow= nullptr;	// Video frame displayed (wrapped by buffer).
AVDictionary		*pOptsDict		= nullptr;	// Dictionary is a key-value store.
struct SwsContext   *pSwsImageCtx	= nullptr;	// Scaling context from source to target image format and size.
std::thread			*decodeFrame	= nullptr;	// Decoding thread that decodes frames and is started when opening a video.
bool				quit			= false;	// Quit the decoding thread.
bool				loadedMovie		= false;	// Flag indicates that we loaded a movie. Used to protect uninitialized pointers.
ImageBuffer			*pImageBuffer	= nullptr;	// Pointer to the image buffer.
bool				eof				= false;	// The stream reached the end of the file.

// Variables to control playback speed and reverse playback.
bool				toggle			= false;
double				speed			= 1;
int64_t				lastPts			= 0; // on reset set to 0
int64_t				deltaPts		= 0;
int64_t				avgDeltaPts		= 1; // on reset set to 1 (used for reverse seek)
int64_t				lastWritePts	= 0; // on reset set to 0 
double				diff			= 0; // on reset set to 0
std::chrono::high_resolution_clock::time_point lastTime;

// Variables to control random seeking.
bool				seekReq			= false;
int64_t				seekPts			= 0;
int					seekFlags		= 0;
Logger				*pLogger		= nullptr;

// Parameters for viewing window.
int					widthView		= 0; // on reset 0
int					heightView		= 0; // on reset 0
int					x0View			= 0; // on reset 0
int					y0View			= 0; // on reset 0
bool				doView			= false; // on reset false

/**
 * True if writing reached the start of the file. This happens in reverse mode.
 * We are not at the end yet if we are in reverse (last condition).
 */
bool atStartForWrite() {
	return pImageBuffer->isReverse() 
		&& lastWritePts <= pVideoStream->start_time+avgDeltaPts 
		&& !pImageBuffer->inReverse();
}

/**
 * True if reading reached the start of the file. This happens in reverse mode.
 */
bool atStartForRead() {
	return atStartForWrite() && pImageBuffer->empty();
}

/**
 * True if writing reached the end of the file. This happens in forward mode.
 */
bool atEndForWrite() {
	// return lastWritePts-pVideoStream->start_time >= pVideoStream->duration;
	// Duration is just an estimate and usually larger than the acutal number of frames.
	// I measured 8 - 14 additional frames that duration specifies.
	return !pImageBuffer->isReverse() && eof;
}

/**
 * True if reading reached the end of the file. This happens in forward mode.
 */
bool atEndForRead() {
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

			pLogger->info("Seek frame for reverse playback with offset %d and "
							"min delta %d.", offset, delta);

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
				if(frameFinished) {

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

JNIEXPORT void JNICALL Java_ImagePlayer_setPlaybackSpeed
(JNIEnv *env, jobject thisObject, jfloat inSpeed) {
	if (loadedMovie) {
		pImageBuffer->setNMinImages(1);
		toggle = pImageBuffer->isReverse() != (inSpeed < 0);
		speed = fabs(inSpeed);
	}
}

JNIEXPORT void JNICALL Java_ImagePlayer_setTime
(JNIEnv *env, jobject thisObject, jdouble time) { // time in seconds
	if (loadedMovie) {
		lastWritePts = seekPts = ((int64_t)(time/(avgDeltaPts
												*av_q2d(pVideoStream->time_base))))
													*avgDeltaPts;
		seekReq = true;	
	}
}

JNIEXPORT jobject JNICALL Java_ImagePlayer_getFrameBuffer
(JNIEnv *env, jobject thisObject) {
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

JNIEXPORT jint JNICALL Java_ImagePlayer_loadNextFrame
(JNIEnv *env, jobject thisObject) {
	
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
		if(fabs(diff) < AV_NOSYNC_THRESHOLD) {

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
	return (jint) nFrame;
}

JNIEXPORT jint JNICALL Java_ImagePlayer_openMovie
(JNIEnv *env, jobject thisObject, jstring jFileName, jstring jVersion) {
	int errNo = 0;

	// Release resources first before loading another movie.
	if (loadedMovie) {
		Java_ImagePlayer_release(env, thisObject);
	}

	const char *fileName = env->GetStringUTFChars(jFileName, 0);
	const char *version = env->GetStringUTFChars(jVersion, 0);

	pLogger = new FileLogger("logger.txt");
	pLogger->info("Version: %s", version);
	//pLogger = new StreamLogger(&std::cerr);

	// Register all formats and codecs.
	av_register_all();

	// Open the video file.
	if ((errNo = avformat_open_input(&pFormatCtx, fileName, nullptr, nullptr)) != 0) {
		pLogger->error("Could not open file %s.", fileName);
		return (jint) errNo;
	}

	// Retrieve the stream information.
	if ((errNo = avformat_find_stream_info(pFormatCtx, nullptr)) < 0) {
		pLogger->error("Unable to find stream information for file %s.", 
						fileName);
		return (jint) errNo;
	}
  
	// Find the first video stream.
	iVideoStream = -1;
	for (int iStream = 0; iStream < pFormatCtx->nb_streams; ++iStream) {
		if (pFormatCtx->streams[iStream]->codec->codec_type == AVMEDIA_TYPE_VIDEO) {
			iVideoStream = iStream;
			break;
		}
	}

	if (iVideoStream == -1) {
		pLogger->error("Unable to find a video stream in file %s.", fileName);
		return (jint) AVERROR_INVALIDDATA;
	}

	// Get a poitner to the video stream.
	pVideoStream = pFormatCtx->streams[iVideoStream];

	// Get a pointer to the codec context for the video stream.
	pVideoCodecCtx = pVideoStream->codec;

	// Find the decoder for the video stream.
	pVideoCodec = avcodec_find_decoder(pVideoCodecCtx->codec_id);
	if (pVideoCodec == nullptr) {
		pLogger->error("Unsupported codec for file %s.", fileName);
		return (jint) AVERROR_DECODER_NOT_FOUND;
	}

	// Open codec.
	if ((errNo = avcodec_open2(pVideoCodecCtx, pVideoCodec, &pOptsDict)) < 0) {
		pLogger->error("Unable to open codec for file %s.", fileName);
		return (jint) errNo;
	}
	
	// Log that opened a file.
	pLogger->info("Opened file %s.", fileName);

	// Dump information about file onto standard error.
	std::string info = log_av_format(pFormatCtx, 0, fileName, 0);
	std::istringstream lines(info);
    std::string line;
    while (std::getline(lines, line)) {
		pLogger->info("%s", line.c_str());
    }

	// Allocate video frame.
	pVideoFrame = av_frame_alloc();

	// Initialize the color model conversion/rescaling context.
	pSwsImageCtx = sws_getContext
		(
			pVideoCodecCtx->width,
			pVideoCodecCtx->height,
			pVideoCodecCtx->pix_fmt,
			pVideoCodecCtx->width,
			pVideoCodecCtx->height,
			AV_PIX_FMT_RGB24,
			SWS_BILINEAR,
			nullptr,
			nullptr,
			nullptr
		);

	// Initialize the widht, height, and duration.
	width = pVideoCodecCtx->width;
	height = pVideoCodecCtx->height;
	duration = pVideoStream->duration*av_q2d(pVideoStream->time_base);
	pLogger->info("Duration of movie %d x %d pixels is %2.3f seconds, %I64d pts.", 
				  width, height, duration, pVideoStream->duration);
	pLogger->info("Time base %2.5f.", av_q2d(pVideoStream->time_base));

	// Initialize the delta pts using the average frame rate and the average pts.
	avgDeltaPts = deltaPts = (int64_t)(1.0/(av_q2d(pVideoStream->time_base)
										*av_q2d(pVideoStream->avg_frame_rate)));
	pLogger->info("Average delta %I64d pts.", avgDeltaPts);

	// Initialize the image buffer.
	pImageBuffer = new ImageBuffer(width, height, avgDeltaPts, pLogger);

	// Seek to the start of the file.
	lastWritePts = seekPts = pVideoStream->start_time;
	seekReq = true;

	// Start the decode thread.
	decodeFrame = new std::thread(readNextFrame);	
	pLogger->info("Started decoding thread!");

	// Set the value for loaded move true.
	loadedMovie = true;

	// Free strings.
	env->ReleaseStringUTFChars(jFileName, fileName);
	env->ReleaseStringUTFChars(jVersion, version);

	return (jint) 0; // No error.
}

JNIEXPORT jint JNICALL Java_ImagePlayer_getNumberOfChannels
(JNIEnv *env, jobject thisObject) {
	return (jint) nChannel;
}

JNIEXPORT jint JNICALL Java_ImagePlayer_getHeight
(JNIEnv *env, jobject thisObject) {
	return (jint) height;
}

JNIEXPORT jint JNICALL Java_ImagePlayer_getWidth
(JNIEnv *env, jobject thisObject) {
	return (jint) width;
}

JNIEXPORT jdouble JNICALL Java_ImagePlayer_getStartTime
(JNIEnv *, jobject) {
	return (jdouble) loadedMovie ? pVideoStream->start_time 
		* av_q2d(pVideoStream->time_base) : 0;
}

JNIEXPORT jdouble JNICALL Java_ImagePlayer_getEndTime
(JNIEnv *, jobject) {
	return (jdouble) loadedMovie ? (pVideoStream->duration 
		+ pVideoStream->start_time) * av_q2d(pVideoStream->time_base) : 0;
}

JNIEXPORT jdouble JNICALL Java_ImagePlayer_getDuration
(JNIEnv *env, jobject thisObject) {
	return (jdouble) duration;
}

JNIEXPORT jdouble JNICALL Java_ImagePlayer_getCurrentTime
(JNIEnv *env, jobject thisObject) {
	return loadedMovie ? pVideoFrameShow->pts*av_q2d(pVideoStream->time_base) : 0;
}

JNIEXPORT jboolean JNICALL Java_ImagePlayer_isForwardPlayback
(JNIEnv *env, jobject thisObject) {
	return loadedMovie ? (jboolean) !pImageBuffer->isReverse() : true;
}

JNIEXPORT void JNICALL Java_ImagePlayer_rewind
(JNIEnv *env, jobject thisObject) {
	if (loadedMovie) {
		if (pImageBuffer->isReverse()) {
			// Seek to the end of the file.
			lastWritePts = seekPts = pVideoStream->duration - 2*avgDeltaPts;
			seekReq = true;
			pLogger->info("Rewind to end %I64d pts, %I64d frames.", seekPts, 
				seekPts/avgDeltaPts);
		} else {
			// Seek to the start of the file.
			lastWritePts = seekPts = pVideoStream->start_time;
			seekReq = true;
			pLogger->info("Rewind to start %I64d pts, %I64d frames.", seekPts,
				seekPts/avgDeltaPts);
		}
	}
}

JNIEXPORT jboolean JNICALL Java_ImagePlayer_atStartForRead
(JNIEnv *env, jobject thisObject){
	return loadedMovie ? atStartForRead() : false;
}

JNIEXPORT jboolean JNICALL Java_ImagePlayer_atEndForRead
(JNIEnv *env, jobject thisObject) {
	return loadedMovie ? atEndForRead() : false;
}

JNIEXPORT jboolean JNICALL Java_ImagePlayer_hasNextFrame
(JNIEnv *env, jobject thisObject) {
	return !(Java_ImagePlayer_isForwardPlayback(env, thisObject) && Java_ImagePlayer_atEndForRead(env, thisObject) 
		|| !Java_ImagePlayer_isForwardPlayback(env, thisObject) && Java_ImagePlayer_atStartForRead(env, thisObject));
}

JNIEXPORT jboolean JNICALL Java_ImagePlayer_view
(JNIEnv *env, jobject thisObject, jint x0, jint y0, jint w, jint h) {
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

JNIEXPORT void JNICALL Java_ImagePlayer_release
(JNIEnv *env, jobject thisObject) {

	if (loadedMovie) {

		// Set the quit flag for the decoding thread.
		quit = true;

		// Flush the image buffer buffer (which unblocks all readers/writers).
		pImageBuffer->flush();

		// Join the decoding thread with this one.
		decodeFrame->join();

		// Free the decoding thread.
		delete decodeFrame;
		
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
	}

	if (pLogger) {
		pLogger->info("Closed video and released resources.");
		delete pLogger;
		pLogger = nullptr;
	}
}
