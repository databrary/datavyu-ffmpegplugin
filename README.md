# Datavyu ffmpegplugin [![License: GPL v3](https://img.shields.io/badge/License-GPL%20v3-blue.svg)](https://www.gnu.org/licenses/gpl-3.0)

## Overview
The Datavyu ffmpegplugin is a Java Media Player interfacing with ffmpeg [FFmpeg](https://github.com/FFmpeg/FFmpeg) 
engine and [MPV Player](https://github.com/mpv-player/mpv) through Java Native Interface (JNI). It supports a 
wide variety of video file formats, audio and video codecs for Windows Platform. Datavyu ffmpegplugin is primarily 
used with [Datavyu](http://www.datavyu.org/) coding tool but could be embedded in any Java application.

To learn how to use the plugin, please refer to the [Examples](##Examples) section below as well as the [Java](src/main/java/org/datavyu/plugins/ffmpeg/examples) programs. You may also find it useful to refer to the wiki pages to set up a development environment and contribute to the project.

## System Requirements

- An implementation of Java SE 8 or newer; [OpenJDK](http://openjdk.java.net/install/) or
[Oracle JDK](http://www.oracle.com/technetwork/java/javase/downloads/).
- Windows 7 or later.
- A somewhat capable CPU.
- Only for the MPV player: A not too crappy GPU. mpv is not intended to be used with bad GPUs. 

## Downloads
The latest version of the datavyu-ffmpegplugin could be downloaded using the following Maven dependency (inside the pom.xml file):

``` xml  
    <dependency>
        <groupId>datavyu</groupId>
        <artifactId>ffmpeg-plugin</artifactId>
        <version>0.18</version>
    </dependency>
```

## Examples
With the Datavyu-ffmpegplugin you can lunch and control multiple instance of one the provided media player from your java application. Creating an instantiating a Media Player is a matter of passing a file path and an AWT Container to the MediaPlayer interface.

### JAVA Player
The Java Player is using FFmpeg API's to decode and read the stream, the player will pull both Image and Audio Buffers from the native side and display the video in a Java container.

We provide a Maven dependency for the FFmpeg 4.0.2 version to be added to your `pom.xml` file
``` xml  
    <dependency>
        <groupId>datavyu</groupId>
        <artifactId>ffmpeg-libs</artifactId>
        <version>0.5</version>
    </dependency>
```

Here is a simple example on how to create and initialize the Java Player, all what you have to do is to be creative and create your own Java controller for the player

``` java
    import org.datavyu.plugins.ffmpeg.*;

    import javax.swing.*;
    import java.io.File;
    import java.net.URI;

    public class SimpleJavaMediaPlayer {

        public static void main(String[] args) {
            // Define the media file, add your file path here !
            URI mediaPath = new File("PATH/TO/MOVIE/FILE").toURI();

            // Create the media player using the constructor with URI and a Java Container
            MediaPlayerData mediaPlayer = new FfmpegJavaMediaPlayer(mediaPath, new JDialog());

            // Initialize the player
            mediaPlayer.init();

            // Start Playing
            mediaPlayer.play();
        }
    }
```
Note that The Java Player is using the MediaPLayerData Interface in order to access the buffers sent through the JNI interface.

### MPV Player
The MPV Player is a fully functional media player providing an [API](https://github.com/mpv-player/mpv/blob/master/libmpv/client.h) to embed MPV in a window, in this repo we are providing and java bridge to control an MPV instance from Java. 

We provide a Maven dependency for the MPV 0.29.1 version to be added to your `pom.xml` file in addition to the FFmpeg dependency mentioned above.
``` xml  
    <dependency>
        <groupId>datavyu</groupId>
        <artifactId>mpv-libs</artifactId>
        <version>0.1</version>
    </dependency>
```
Here is a simple example on how to create and initialize the [MPV Player](https://github.com/mpv-player/mpv), all what you have to do is to be creative and build your own Java controller for the player

``` java
    import org.datavyu.plugins.ffmpeg.MediaPlayer;
    import org.datavyu.plugins.ffmpeg.MpvMediaPlayer;

    import javax.swing.*;
    import java.io.File;
    import java.net.URI;

    public class SimpleMpvMediaPlayer {

        public static void main(String[] args) {
            // Define the media file, add your file path here !
            URI mediaPath = new File("PATH/TO/MOVIE/FILE").toURI();

            // Create the media player using the constructor with URI
            MediaPlayer mediaPlayer = new MpvMediaPlayer(mediaPath, new JDialog());

            // Initialize the player
            mediaPlayer.init();

            // Start Playing
            mediaPlayer.play();
        }
    }
```
Note that the MPV player will embed a native window in the Java Container passed as an argument to the `MpvMediaPlayer` constructor.

### SDL Player
The SDL player is relying on FFmpeg engine as the Java player does, but is using [Simple DirectMedia Layer SDL2 Framework](https://www.libsdl.org/) to Display Images and Play Audio natively.

We provide a Maven dependency for the SDL2.0.8 version to be added to your `pom.xml` file in addition to the FFmpeg dependency mentioned above

``` xml  
    <dependency>
        <groupId>datavyu</groupId>
        <artifactId>sdl-libs</artifactId>
        <version>0.1</version>
    </dependency>
```

Here is a simple example on how to create and initialize an SDL Player, all what you have to do is to be creative and create your own Java controller for the player

``` java
    import org.datavyu.plugins.ffmpeg.*;

    import java.io.File;
    import java.net.URI;

    public class SimpleSdlMediaPlayer {
        public static void main(String[] args) {
            // Define the media file, add your file path here !
            URI mediaPath = new File("PATH/TO/MOVIE/FILE").toURI();

            // Create the media player using the constructor with URI
            MediaPlayer mediaPlayer = new FfmpegSdlMediaPlayer(mediaPath);

            // Initialize the player
            mediaPlayer.init();

            // Start Playing
            mediaPlayer.play();
        }
    }
```

A simple video controller example is used [here](src/main/java/org/datavyu/plugins/ffmpeg/examples/JMediaPlayerControlFrame.java) to control media players through key binding, and a more sophisticated controller is provided in [Datavyu](https://github.com/databrary/datavyu/blob/master/src/main/java/org/datavyu/views/VideoController.java).

## Bug reports
Please use the [issue tracker](https://github.com/databrary/datavyu-ffmpegplugin/issues) provided by GitHub to send us bug reports or feature requests. Follow the template's instructions or the issue will likely be ignored or closed as invalid.

Using the bug tracker as place for simple questions is recommended.

## Contributing
Please refer to the [wiki](https://github.com/databrary/datavyu-ffmpegplugin/wiki) and read on how you could hep us to
improve this tool.

You can check the wiki or the issue tracker for ideas on what you could contribute with.

## Authors
* Florian Raudies
* Reda Nezzar
* Jesse Lingeman