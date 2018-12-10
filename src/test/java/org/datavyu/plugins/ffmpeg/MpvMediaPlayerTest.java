package org.datavyu.plugins.ffmpeg;

import javafx.application.Application;
import javafx.stage.Stage;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import javax.swing.*;
import java.net.URI;

public class MpvMediaPlayerTest extends MediaPlayerTest {

    private static class MpvJxApplication extends Application {
        private URI mediaPath;
        private MediaPlayer mediaPlayer;

        public MpvJxApplication(URI mediaPath) {
            this.mediaPath = mediaPath;
        }

        @Override
        public void start(Stage primaryStage) {
            mediaPlayer = new MpvFxMediaPlayer(mediaPath, primaryStage);
            mediaPlayer.init();
        }

        public MediaPlayer getMediaPlayer() {
            return mediaPlayer;
        }
    }

    @DataProvider(name = "mpvPlayers")
    public Object[][] createPlayerWithMedia() {
        // TODO(fraudies): Work with all video files
        MediaInformation mediaInformation = MEDIA.stream().findFirst().get();
        //MediaPlayer mediaPlayer = new MpvFxMediaPlayer(mediaInformation.getLocalPath(), new Stage());
        MediaPlayerSync mediaPlayerSync = MediaPlayerSync.createMediaPlayerSync(
                new MpvJxApplication(mediaInformation.getLocalPath()).getMediaPlayer());
        return new Object[][] {{ mediaPlayerSync, mediaInformation}};
    }

    @Test(dataProvider = "mpvPlayers")
    public void testReadyState(MediaPlayerSync player, MediaInformation mediaInformation) {
        super.testReadyState(player, mediaInformation);
    }

}
