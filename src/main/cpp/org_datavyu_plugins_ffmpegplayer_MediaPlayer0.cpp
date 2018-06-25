#include "org_datavyu_plugins_ffmpegplayer_MediaPlayer0.h"
#include "AVStreamer.hpp"
#include "AVDecoder.h"
#include "AVClock.hpp"
#include "AVFrameQueue.hpp"
#include "AVPacketQueue.hpp"
#include "AudioFormatHelper.hpp"
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

// Florian Raudies, Mountain View, CA.
// vcvarsall.bat x64

// To create the header file run 'javah -d ../cpp org.datavyu.plugins.ffmpegplayer.MediaPlayer0'
// from the directory 'src/main/java'

// Helpful source code in ffmpeg to look at
// frame.h/frame.c in libavutil
// avformat.h/avformat.c in libavformat

/*
cl org_datavyu_plugins_ffmpegplayer_MediaPlayer0.cpp /Fe"..\..\..\MediaPlayer0" /I"C:\Users\Florian\FFmpeg\FFmpeg-n3.4" /I"C:\Program Files\Java\jdk1.8.0_151\include" /I"C:\Program Files\Java\jdk1.8.0_151\include\win32" /showIncludes /MD /LD /link "C:\Program Files\Java\jdk1.8.0_151\lib\jawt.lib" "C:\Users\Florian\FFmpeg\FFmpeg-n3.4\libavcodec\avcodec.lib" "C:\Users\Florian\FFmpeg\FFmpeg-n3.4\libavformat\avformat.lib" "C:\Users\Florian\FFmpeg\FFmpeg-n3.4\libavutil\avutil.lib" "C:\Users\Florian\FFmpeg\FFmpeg-n3.4\libswscale\swscale.lib" "C:\Users\Florian\FFmpeg\FFmpeg-n3.4\libswresample\swresample.lib"
*/

// Add flag /MDd for debugging information and flag /DEBUG:FULL to add all symbols to the PDB file
// On debug flag for cl; see https://docs.microsoft.com/en-us/cpp/build/reference/debug-generate-debug-info

// use dumpbin /ALL MediaPlayer0.lib to list the function symbols
// Make sure that visual studio's 'vc' folder is in the path

int streamId = -1;
std::map<int, VideoState*> idToVideoState;

VideoState* getVideoState(int streamId) {
    return idToVideoState.find(streamId) != idToVideoState.end() ? idToVideoState[streamId] : nullptr;
}

static int opt_format(void *optctx, const char *opt, const char *arg)
{
    file_iformat = av_find_input_format(arg);
    if (!file_iformat) {
        av_log(NULL, AV_LOG_FATAL, "Unknown input format: %s\n", arg);
        return AVERROR(EINVAL);
    }
    return 0;
}

JNIEXPORT jintArray JNICALL Java_org_datavyu_plugins_ffmpegplayer_MediaPlayer0_open0(JNIEnv *env, jclass thisClass,
    jstring jFileName, jstring jVersion, jobject jAudioFormat) {

    jintArray returnArray = env->NewIntArray(2);
    jint *returnValues = env->GetIntArrayElements(returnArray, NULL);

	const char *fileName = env->GetStringUTFChars(jFileName, 0);
	const char *version = env->GetStringUTFChars(jVersion, 0);

	// Register all formats and codecs
	// TODO: Add init method
	av_register_all();
    avformat_network_init();
    av_init_packet(&flush_pkt);
    flush_pkt.data = (uint8_t *)&flush_pkt;

    // TODO: Check if this is correct to use the default
    AVInputFormat *file_iformat = av_find_input_format("none");
    if (!file_iformat) {
        av_log(NULL, AV_LOG_FATAL, "Unknown input format: %s\n", arg);
        returnValues[0] = AVERROR(EINVAL);
        env->ReleaseStringUTFChars(jFileName, fileName);
        env->ReleaseStringUTFChars(jVersion, version);
        env->ReleaseIntArrayElements(returnArray, returnValues, NULL);
        return returnArray;
    }

    VideoState* is = stream_open(fileName, file_iformat);
    if (!is) {
        av_log(NULL, AV_LOG_FATAL, "Failed to initialize VideoState!\n");
        returnValues[0] = AVERROR(ESRCH);
        env->ReleaseStringUTFChars(jFileName, fileName);
        env->ReleaseStringUTFChars(jVersion, version);
        env->ReleaseIntArrayElements(returnArray, returnValues, NULL);
        return returnArray;
    }

	// Add video state to the map
	idToVideoState[streamId] = is;
    returnValues[0] = 0; // no error
    returnValues[1] = ++streamId;

	// Free
	env->ReleaseStringUTFChars(jFileName, fileName);
	env->ReleaseStringUTFChars(jVersion, version);
    env->ReleaseIntArrayElements(returnArray, returnValues, NULL);

    return returnArray;
}

JNIEXPORT jdouble JNICALL Java_org_datavyu_plugins_ffmpegplayer_MediaPlayer0_getAverageFrameRate0(JNIEnv *env,
    jclass thisClass, jint streamId) {
    VideoState* is = getVideoState(streamId);
    return is && is->video_st ? av_q2d(is->video_st->avg_frame_rate) : 0;
}

JNIEXPORT jboolean JNICALL Java_org_datavyu_plugins_ffmpegplayer_MediaPlayer0_hasVideoStream0(JNIEnv *env,
    jclass thisClass, jint streamId) {
    VideoState* is = getVideoState(streamId);
    return is && is->video_st;
}

JNIEXPORT jboolean JNICALL Java_org_datavyu_plugins_ffmpegplayer_MediaPlayer0_hasAudioStream0(JNIEnv *env,
    jclass thisClass, jint streamId) {
    VideoState* is = getVideoState(streamId);
    return is && is->audio_st;
}

JNIEXPORT jdouble JNICALL Java_org_datavyu_plugins_ffmpegplayer_MediaPlayer0_getStartTime0(JNIEnv *env,
    jclass thisClass, jint streamId) {
    VideoState* is = getVideoState(streamId);
    return is && is->ic ? is->ic->start_time / (double)AV_TIME_BASE : 0;
}

JNIEXPORT jdouble JNICALL Java_org_datavyu_plugins_ffmpegplayer_MediaPlayer0_getEndTime0(JNIEnv *env,
    jclass thisClass, jint streamId) {
    VideoState* is = getVideoState(streamId);
    return is && is->ic ? (is->ic->start_time + is->ic->duration) / (double)AV_TIME_BASE : 0;
}

JNIEXPORT jdouble JNICALL Java_org_datavyu_plugins_ffmpegplayer_MediaPlayer0_getDuration0(JNIEnv *env,
    jclass thisClass, jint streamId) {
    VideoState* is = getVideoState(streamId);
    return is && is->ic ? is->ic->duration / (double)AV_TIME_BASE : 0;
}

JNIEXPORT jdouble JNICALL Java_org_datavyu_plugins_ffmpegplayer_MediaPlayer0_getCurrentTime0(JNIEnv *env,
    jclass thisClass, jint streamId) {
    VideoState* is = getVideoState(streamId);
    return is ? get_master_clock(is) : -1;
}

JNIEXPORT void JNICALL Java_org_datavyu_plugins_ffmpegplayer_MediaPlayer0_setAudioSyncDelay0(JNIEnv *env,
    jclass thisClass, jint streamId, jlong delay) {
    // TODO(fraudies): Wire this up
}

JNIEXPORT void JNICALL Java_org_datavyu_plugins_ffmpegplayer_MediaPlayer0_play0(JNIEnv *env, jclass thisClass,
       jint streamId) {
    VideoState* is = getVideoState(streamId);
    if (is) {
        stream_play(is);
    }
}

JNIEXPORT void JNICALL Java_org_datavyu_plugins_ffmpegplayer_MediaPlayer0_stop0(JNIEnv *env, jclass thisClass,
    jint streamId) {
    // Nothing to do here for now
}

JNIEXPORT void JNICALL Java_org_datavyu_plugins_ffmpegplayer_MediaPlayer0_pause0(JNIEnv *env, jclass thisClass,
    jint streamId) {
    VideoState* is = getVideoState(streamId);
    if (is) {
        stream_pause(is);
    }
}

JNIEXPORT void JNICALL Java_org_datavyu_plugins_ffmpegplayer_MediaPlayer0_seek0(JNIEnv *env,
    jclass thisClass, jint streamId, jdouble jTime) {
    VideoState* is = getVideoState(streamId);
    if (is) {
        double pos = get_master_clock(is);
        if (isnan(pos))
            pos = (double)is->seek_pos / AV_TIME_BASE;
        double incr = jTime - pos;
        // Get the current time get the new time
        if (is->seek_by_bytes) {
            pos = -1;
            if (pos < 0 && is->video_stream >= 0)
                pos = frame_queue_last_pos(&is->pictq);
            if (pos < 0 && is->audio_stream >= 0)
                pos = frame_queue_last_pos(&is->sampq);
            if (pos < 0)
                pos = avio_tell(is->ic->pb);
            if (cur_stream->ic->bit_rate)
                incr *= cur_stream->ic->bit_rate / 8.0;
            else
                incr *= 180000.0;
            pos += incr;
            stream_seek(is, pos, incr);
        } else {
            pos += incr;
            if (is->ic->start_time != AV_NOPTS_VALUE && pos < is->ic->start_time / (double)AV_TIME_BASE)
                pos = is->ic->start_time / (double)AV_TIME_BASE;
            stream_seek(is, (int64_t)(pos * AV_TIME_BASE), (int64_t)(incr * AV_TIME_BASE));
        }
    }
}

JNIEXPORT void JNICALL Java_org_datavyu_plugins_ffmpegplayer_MediaPlayer0_setPlaybackSpeed0(JNIEnv *env,
    jclass thisClass, jint streamId, jfloat speed) {
    // set speed on clock
    // TODO: Need to implement this; it requires some changes to the design
}

JNIEXPORT void JNICALL Java_org_datavyu_plugins_ffmpegplayer_MediaPlayer0_close0(JNIEnv *env, jclass thisClass,
    jint streamId) {
    VideoState* is = getVideoState(streamId);
    if (is) {
        stream_close(is);
        idToMovieStream.erase(streamId);
    }
}

JNIEXPORT jboolean JNICALL Java_org_datavyu_plugins_ffmpegplayer_MediaPlayer0_loadNextAudioData0(JNIEnv *env,
    jclass thisClass, jint streamId) {
    VideoState* is = getVideoState(streamId);
    if (is) {
        int audio_size, len1;
        int64_t audio_callback_time = av_gettime_relative();
        int len = is->audio_play_buf_size;
        while (len > 0) {
            if (is->audio_buf_index >= is->audio_buf_size) {
               audio_size = audio_decode_frame(is);
               if (audio_size < 0) {
                    /* if error, just output silence */
                   is->audio_buf = NULL;
                   is->audio_buf_size = SDL_AUDIO_MIN_BUFFER_SIZE / is->audio_tgt.frame_size * is->audio_tgt.frame_size;
               } else {
                   is->audio_buf_size = audio_size;
               }
               is->audio_buf_index = 0;
            }
            len1 = is->audio_buf_size - is->audio_buf_index;
            if (len1 > len)
                len1 = len;
            if (!is->muted && is->audio_buf && is->audio_volume == SDL_MIX_MAXVOLUME)
                memcpy(is->audio_play_buf, (uint8_t *)is->audio_buf + is->audio_buf_index, len1);
            else {
                memset(is->audio_play_buf, 0, len1);
            }
            len -= len1;
            stream += len1;
            is->audio_buf_index += len1;
        }
        is->audio_write_buf_size = is->audio_buf_size - is->audio_buf_index;
        /* Let's assume the audio driver that is used by SDL has two periods. */
        if (!isnan(is->audio_clock)) {
            // TODO: Check the sync here
            set_clock_at(&is->audclk,
                is->audio_clock - (double)(2 * is->audio_hw_buf_size + is->audio_write_buf_size)
                / is->audio_tgt.bytes_per_sec, is->audio_clock_serial,
                audio_callback_time / 1000000.0
            );
            sync_clock_to_slave(&is->extclk, &is->audclk);
        }
        return true;
    } else {
        return false;
    }
}

JNIEXPORT jobject JNICALL Java_org_datavyu_plugins_ffmpegplayer_MediaPlayer0_getAudioBuffer0(JNIEnv *env,
    jclass thisClass, jint streamId, jint nBytes) {
    VideoState* is = getVideoState(streamId);
    if (is) {
        is->audio_play_buf = av_malloc(nBytes);
        is->audio_play_buf_size = nBytes;
        return env->NewDirectByteBuffer((void*) is->audio_play_buf, nBytes*sizeof(uint8_t));
    }
    return nullptr;
}

JNIEXPORT jstring JNICALL Java_org_datavyu_plugins_ffmpegplayer_MediaPlayer0_getSampleFormat0(JNIEnv *env,
    jclass thisClass, jint streamId) {
    VideoState* is = getVideoState(streamId);
    if (is) {
        const char* name = av_get_sample_fmt_name(is->audio_tgt.fmt);
        return env->NewStringUTF(name);
    }
    return nullptr;
}

JNIEXPORT jstring JNICALL Java_org_datavyu_plugins_ffmpegplayer_MediaPlayer0_getCodecName0(JNIEnv *env,
    jclass thisClass, jint streamId) {
    VideoState* is = getVideoState(streamId);
    if (is) {
        const char* name = is->avctx->name;
        return env->NewStringUTF(name);
    }
    return nullptr;
}

JNIEXPORT jfloat JNICALL Java_org_datavyu_plugins_ffmpegplayer_MediaPlayer0_getSampleRate0(JNIEnv *env,
    jclass thisClass, jint streamId) {
    VideoState* is = getVideoState(streamId);
    return is ? is->audio_tgt.freq : 0;
}

JNIEXPORT jint JNICALL Java_org_datavyu_plugins_ffmpegplayer_MediaPlayer0_getSampleSizeInBits0(JNIEnv *env,
    jclass thisClass, jint streamId) {
    VideoState* is = getVideoState(streamId);
    return is ? is->audio_tgt.frame_size*8 : 0;
}

JNIEXPORT jint JNICALL Java_org_datavyu_plugins_ffmpegplayer_MediaPlayer0_getNumberOfSoundChannels0(JNIEnv *env,
    jclass thisClass, jint streamId) {
    VideoState* is = getVideoState(streamId);
    return is : is->audio_tgt.channels : 0;
}

JNIEXPORT jint JNICALL Java_org_datavyu_plugins_ffmpegplayer_MediaPlayer0_getFrameSize0(JNIEnv *env,
    jclass thisClass, jint streamId) {
    VideoState* is = getVideoState(streamId);
    return is ? is->audio_tgt.frame_size : 0;
}

JNIEXPORT jfloat JNICALL Java_org_datavyu_plugins_ffmpegplayer_MediaPlayer0_getFrameRate0(JNIEnv *env,
    jclass thisClass, jint streamId) {
    VideoState* is = getVideoState(streamId);
    return is ? is->audio_tgt.bytes_per_sec : 0;
}

JNIEXPORT jboolean JNICALL Java_org_datavyu_plugins_ffmpegplayer_MediaPlayer0_bigEndian0(JNIEnv *env,
    jclass thisClass, jint streamId) {
    // Check for big endian
    short int number = 0x1;
    char *numPtr = (char*)&number;
    return (numPtr[0] != 1);
}

JNIEXPORT jint JNICALL Java_org_datavyu_plugins_ffmpegplayer_MediaPlayer0_getNumberOfColorChannels0(JNIEnv *env,
    jclass thisClass, jint streamId) {
    // TODO: Fix me to the true number
    VideoState* is = getVideoState(streamId);
    return is ? 3 : 0;
}

JNIEXPORT jint JNICALL Java_org_datavyu_plugins_ffmpegplayer_MediaPlayer0_getHeight0(JNIEnv *env, jclass thisClass,
    jint streamId) {
    VideoState* is = getVideoState(streamId);
    return is ? is && is->height : 0;
}

JNIEXPORT jint JNICALL Java_org_datavyu_plugins_ffmpegplayer_MediaPlayer0_getWidth0(JNIEnv *env, jclass thisClass,
    jint streamId) {
    VideoState* is = getVideoState(streamId);
    return is ? is && is->width : 0;
}

JNIEXPORT jint JNICALL Java_org_datavyu_plugins_ffmpegplayer_MediaPlayer0_getMaxImageBufferSizeInBytes(JNIEnv *env,
    jclass thisClass, jint streamId) {
    // TODO: Note, the video_st may not be loaded yet at the time of the call!
    VideoState* is = getVideoState(streamId);
    return is ? av_image_get_buffer_size(AV_PIX_FMT_RGB24, is->width, is->height, 1) : 0;
}

JNIEXPORT jobject JNICALL Java_org_datavyu_plugins_ffmpegplayer_MediaPlayer0_getImageBuffer0(JNIEnv *env,
    jclass thisClass, jint streamId) {
    VideoState* is = getVideoState(streamId);
    if (is) {
        Frame* vp = is->pictq->frame_queue_peek_last();
        return env->NewDirectByteBuffer((void*) vp->frame->data[0],
                                                vp->width*vp->height*3*sizeof(uint8_t));
    }
    return nullptr;
}

JNIEXPORT jint JNICALL Java_org_datavyu_plugins_ffmpegplayer_MediaPlayer0_loadNextImageFrame0(JNIEnv *env,
    jclass thisClass, jint streamId) {
    VideoState* is = getVideoState(streamId);
    return is ? mediaPlayer->loadNextImageFrame() : 0;
}