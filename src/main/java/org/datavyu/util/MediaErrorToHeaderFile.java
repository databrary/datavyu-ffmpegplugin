package org.datavyu.util;

import org.datavyu.plugins.ffmpeg.MediaError;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

/**
 * Generates the header file for the media error.
 *
 * This file is only used during the compile process.
 *
 * TODO(fraudies): Run this during the build with maven and add arguments for output path/file
 */
public class MediaErrorToHeaderFile {
    public static void main(String[] args) {
        String fileName = "FfmpegMediaErrors.h";
        try {
            PrintWriter writer = new PrintWriter(new File(fileName));
            writer.println("//DO NOT EDIT -- MACHINE GENERATED");
            writer.println("#ifndef _FFMPEG_MEDIA_ERRORS_H_");
            writer.println("#define _FFMPEG_MEDIA_ERRORS_H_");
            writer.println();
            for (MediaError mediaError : MediaError.values()) {
                writer.println("#define \t" + mediaError.name() + " \t" + mediaError.code());
            }
            writer.println();
            writer.println("#endif // _FFMPEG_MEDIA_ERRORS_H_");
            writer.close();
        } catch (FileNotFoundException noFile) {
            System.err.println(noFile.getMessage());
        }
        System.out.println("Generated header file for '" + MediaError.class.getName()
                + "' and wrote it to '" + fileName + "'");
    }
}
