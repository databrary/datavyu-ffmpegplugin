package org.datavyu.plugins.ffmpegplayer;

import com.sun.media.jfxmedia.locator.Locator;
import com.sun.media.jfxmediaimpl.NativeMedia;
import com.sun.media.jfxmediaimpl.platform.Platform;

public class FfmpegNativeMedia extends NativeMedia {

    FfmpegNativeMedia(Locator locator) {
        super(locator);

    }

    @Override
    public Platform getPlatform() {
        return null;
    }

    @Override
    public void dispose() {

    }
}
