# Datavyu ffmpegplugin

This plugin was developed by Florian Raudies.

This repo holds the native c++/java code that composes the ffmpeg plugin. The final files that produce the dll's are

    org_datavyu_plugins_ffmpegplayer_MovieStream.cpp
    org_datavyu_plugins_ffmpegplayer_MovieStream.h

The other source code files contain org.datavyu.plugins.ffmpegplayer.prototypes for video playback, audio playback, etc.

The compile instructions for the plugin are in the cpp file.

The compile instructions for the ffmpeg code are in doc/Libraries.txt.


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

To compile the test code each Test[...].cpp file has the compilation command atop for the compile under Windows.
Before executing that command run the 

    vcvarsall.bat x86 

script to setup the compile environment.

## Java Code
The java code provides examples on how to interface with the movie stream interface; especially on how to playback image
frames and audio frames through separate threads. The relevant java code resides in the package

    org.datavyu.plugins.ffmpegplayer
    
## Deployment
To build process we use maven. Dependencies are described in a pom file. The package is build with

    mvn package
    
and deployed with 

    mvn deploy
    
Notice, the latter command deploys to the local cache '.m2' and a maven server if setup. At the moment, we do not have a 
maven server setup and that step of the deploy fails. Instead, we manually copy files '*.jar' and '*.pom' unto the 
'wwww' server.