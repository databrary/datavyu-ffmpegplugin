#include <jni.h>
#include <stdio.h>
#include "DisplayImageFromJNI.h"

// Florian Raudies, 05/30/2016, Mountain View, CA.
// vcvarsall.bat x64
/*
cl DisplayImageFromJNI.cpp /Fe"..\..\lib\DisplayImageFromJNI"^
 /I"C:\Program Files\Java\jdk1.8.0_144\include"^
 /I"C:\Program Files\Java\jdk1.8.0_144\include\win32"^
 /showIncludes -MD -LD /link "C:\Program Files\Java\jdk1.8.0_144\lib\jawt.lib"
*/

typedef unsigned char BYTE;

JNIEXPORT jobject JNICALL Java_DisplayImageFromJNI_getByteBuffer
	(JNIEnv *env, jobject thisObj, jint width, jint height, jint nChannel) {
	int blockSize = 50;
	BYTE *data = new BYTE[width*height*nChannel]; // jint maps to long
	// Fill the data.
	for (int iRow = 0; iRow < height; ++iRow) {
		for (int iCol = 0; iCol < width; ++iCol) {
			for (int iChannel = 0; iChannel < nChannel; ++iChannel) {
				data[(iRow*width+iCol)*nChannel+iChannel] = (iRow/blockSize % 2) && (iCol/blockSize % 2) ? 0xFF : 0x00;
			}
		}
	}
	return env->NewDirectByteBuffer((void*) data, width*height*nChannel*sizeof(BYTE));
}
