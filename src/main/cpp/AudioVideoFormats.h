#ifndef _AUDIO_VIDEO_FORMATS_H_
#define _AUDIO_VIDEO_FORMATS_H_
#include <string>

typedef struct AudioFormat {
	std::string encoding;
	float sampleRate;
	int sampleSizeInBits;
	int channels;
	int frameSize;
	float frameRate;
	bool bigEndian;
};

// These types are taken from java.awt.color.ColorSpace
enum PixelFormatType {
	TYPE_XYZ = 0,
	TYPE_Lab = 1,
	TYPE_Luv = 2,
	TYPE_YCbCr = 3,
	TYPE_Yxy = 4,
	TYPE_RGB = 5,
	TYPE_GRAY = 6,
	TYPE_HSV = 7,
	TYPE_HLS = 8,
	TYPE_CMYK = 9,
	TYPE_CMY = 11
};

typedef struct PixelFormat {
	PixelFormatType type;
};

#endif // _AUDIO_VIDEO_FORMATS_H_

