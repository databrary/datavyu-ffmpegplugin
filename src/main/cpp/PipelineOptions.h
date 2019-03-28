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

#include <list>
#include <string.h>
#include <string>

#include "FfmpegJniUtils.h"

using namespace std;

class CPipelineOptions {
public:
  CPipelineOptions(AudioFormat audioFormat = AudioFormat(),
                   PixelFormat pixelFormat = PixelFormat(),
                   int audioBufferSizeInBy = 0)
      : audio_format_(audioFormat), pixel_format_(pixelFormat),
        audio_buffer_size_in_by_(audioBufferSizeInBy) {}

  virtual ~CPipelineOptions() {}
  inline const AudioFormat *GetAudioFormat() const { return &audio_format_; }
  inline const PixelFormat *GetPixelFormat() const { return &pixel_format_; }
  inline const int GetAudioBufferSizeInBy() const {
    return audio_buffer_size_in_by_;
  }

private:
  AudioFormat audio_format_;
  PixelFormat pixel_format_;
  int audio_buffer_size_in_by_;
};

#endif //_PIPELINE_OPTIONS_H_
