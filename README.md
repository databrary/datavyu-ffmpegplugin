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

## Compiling Native Code (Windows) in Visual Studio
1. Download and install Microsoft Visual Studio Community Edition (https://visualstudio.microsoft.com/vs/community/)
1. Download the "Dev" 64-bit version of FFmpeg (https://ffmpeg.zeranoe.com/builds/)
1. Download and install the Java 8 JDK (http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html)
1. Download the SDL2 development libraries for Visual C++ 32/64-bit (https://www.libsdl.org/download-2.0.php) and unzip them to a directory (we'll use `C:\SDL2`)
1. Go to the `include` folder in `C:\SDL2`. Create a new directory called `SDL2` inside of the `include` folder and then move all of the files in the `include` folder into the `SDL2` folder (this is for compatibility due to SDL packaging the Visual Studio distribution differently than the rest of their distributions).
1. Unzip the Dev version of ffmpeg to a directory (we will use C:\FFmpeg-dev as an example)
1. Clone this repository to a directory of your choosing.
1. In Visual Studio, open MediaPlayer.sln using `File -> Open -> Project/Solution` and navigating to `src\main\cpp` in the folder where you cloned this repository.
1. Once the solution is open, we have to tell Visual Studio where to find the FFmpeg, Java, and SDL headers and libraries.
	1. Click on "Solution Explorer" in the bottom of the left pane.
	1. Right click the `FfmpegMediaPlayer` project under `Solution 'MediaPlayer'` and click `Properties`.
	1. In the left pane under `Configuration Properties` click `VC++ Directories`.
		1. Add a directory to `Include Directories` that points to `C:\FFmpeg-dev\include`.
		1. Add a directory to `Include Directories` that points to `C:\SDL2\include` (not the SDL2 folder you created).
		1. Add a directory to `Include Directories` that points to `C:\Program Files\Java\jdk1.8.0_YOURVERSION\include`
		1. Add a directory to `Include Directories` that points to `C:\Program Files\Java\jdk1.8.0_YOURVERSION\include\win32`
		1. Add a directory to `Library Directories` that points to `C:\FFmpeg-dev\lib`.
		1. Add a directory to `Library Directories` that points to `C:\Program Files\Java\jdk1.8.0_YOURVERSION\lib`.
		1. Add a directory to `Library Directories` that points to `C:\SDL2\lib\x64`.
1. Right click on the `FfmpegMediaPlayer` project and hit build. The build should be a success and create `src\main\cpp\x64\Debug\FfmpegMediaPlayer.dll`.

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