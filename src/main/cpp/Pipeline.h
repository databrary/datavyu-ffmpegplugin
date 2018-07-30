/*
 * Copyright (c) 2010, 2015, Oracle and/or its affiliates. All rights reserved.
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

#ifndef _PIPELINE_H_
#define _PIPELINE_H_

#include <stdint.h>
#include "PipelineOptions.h"
#include "AudioVideoFormats.h"


class CMedia;
class CJavaPlayerEventDispatcher;

/**
 * class CPipeline
 *
 * Underlying object that interfaces the JNI layer to the actual media engine (e.g. GStreamer or PacketVideo).
 */
class CPipeline
{
public:
    enum PlayerState
    {
        Unknown = 0,
        Ready = 1,
        Playing = 2,
        Paused = 3,
        Stopped = 4,
        Stalled = 5,
        Finished = 6,
        Error = 7
    };

public:
    CPipeline(CPipelineOptions* pOptions=NULL);
    virtual ~CPipeline();

    void                    SetEventDispatcher(CJavaPlayerEventDispatcher* pEventDispatcher);

    virtual uint32_t        Init(const char * filename);
    virtual void            Dispose();

    virtual uint32_t        Play()=0;
    virtual uint32_t        Stop();
    virtual uint32_t        Pause();
    virtual uint32_t        Finish();

    virtual uint32_t        Seek(double dSeekTime);

    virtual uint32_t        GetDuration(double* pdDuration);
    virtual uint32_t        GetStreamTime(double* pdStreamTime);

    virtual uint32_t        SetRate(float fRate);
    virtual uint32_t        GetRate(float* pfRate);

    virtual uint32_t        SetVolume(float fVolume);
    virtual uint32_t        GetVolume(float* pfVolume);

    virtual uint32_t        SetBalance(float fBalance);
    virtual uint32_t        GetBalance(float* pfBalance);

    virtual uint32_t        SetAudioSyncDelay(long lMillis);
    virtual uint32_t        GetAudioSyncDelay(long* plMillis);

	virtual uint32_t		HasAudioData(bool* bAudioData) const;
	virtual uint32_t		HasImageData(bool* bImageData) const;
	virtual uint32_t		GetImageWidth(int* iWidth) const;
	virtual uint32_t		GetImageHeight(int* iHeight) const;
	virtual uint32_t		GetAudioFormat(AudioFormat* pAudioFormat) const;
	virtual uint32_t		GetPixelFormat(PixelFormat* pPixelFormat) const;
	virtual uint32_t		UpdateImageBuffer(uint8_t* pImageBuffer, const long len);
	virtual uint32_t		UpdateAudioBuffer(uint8_t* pAudioBuffer, const long len);

	// TODO(fraudies): Clean this up... currently used to free memory
	CPipelineOptions* GetCPipelineOptions();



protected:
	CJavaPlayerEventDispatcher* m_pEventDispatcher;
	CPipelineOptions*			m_pOptions;
    PlayerState					m_PlayerState;
	PlayerState					m_PlayerPendingState; // This is necessary to get from stalled into the next correct state

	void UpdatePlayerState(PlayerState newState);
	void SetPlayerState(PlayerState newPlayerState, bool bSilent);
};

#endif  //_PIPELINE_H_
