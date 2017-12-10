# Datavyu ffmpegplugin

This plugin was developed by Florian Raudies.

This repo holds the native c++/java code that composes the ffmpeg plugin. The final files that produce the dll's are

    org_datavyu_plugins_ffmpegplayer_MovieStream.cpp
    org_datavyu_plugins_ffmpegplayer_MovieStream.h

The other source code files contain prototypes for video playback, audio playback, etc.

The compile instructions are in the cpp file.

The compile instructions for the ffmpeg native code are in doc/Libraries.txt.


## Native Code
The native code interfaces to the c API from ffmpeg using JNI. It implements buffers for audio and video data,
different playback speeds, especially fast backward playback through a sophisticate buffering strategy. It  also
implements a strategy to keep the audio and video in sync.  Notice, that at this point the audio playback is only
supported at 1x. Whenever, the caller plays video back at a different rate at 1x the sound playback will stop. The
caller is responsible for stopping the audio playback.

### Testing Native Code
To test the native code we use catch 2.0.1 that can be downloaded from here

    https://github.com/catchorg/Catch2/tree/v2.0.1.

Check out the examples folder in the release for more details on the framework.


## Java Code
The java code provides examples on how to interface with the move stream interface; especially on how to playback image
frames and audio frames through separate threads. The relevant java code resides in the package

    org.datavyu.plugins.ffmpegplayer