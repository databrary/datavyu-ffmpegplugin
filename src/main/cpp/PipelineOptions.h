/*
 * Copyright (c) 2010, 2013, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */

#ifndef _PIPELINE_OPTIONS_H_
#define _PIPELINE_OPTIONS_H_

#include <string.h>
#include <list>
#include <string>

#include "FfmpegJniUtils.h"

using namespace std;
typedef list<string> ContentTypesList;

class CPipelineOptions
{
public:
public:
	// TODO(fraudies): Add the audio and video format here
    CPipelineOptions(
		AudioFormat audioFormat = AudioFormat(),
		PixelFormat pixelFormat = PixelFormat(),
		int audioBufferSizeInBy = 0)
		: m_audioFormat(audioFormat),
		m_pixelFormat(pixelFormat),
		m_audioBufferSizeInBy(audioBufferSizeInBy)
    {}

    virtual ~CPipelineOptions() {}
	inline const AudioFormat* GetAudioFormat() const { return &m_audioFormat; }
	inline const PixelFormat* GetPixelFormat() const { return &m_pixelFormat; }
	inline const int GetAudioBufferSizeInBy() const { return m_audioBufferSizeInBy;  }
private:
	AudioFormat m_audioFormat;
	PixelFormat m_pixelFormat;
	int			m_audioBufferSizeInBy;
};

#endif  //_PIPELINE_OPTIONS_H_
