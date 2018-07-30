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

#include "Pipeline.h"
#include "JavaPlayerEventDispatcher.h"
#include "FfmpegMediaErrors.h"

//*************************************************************************************************
//********** class CPipeline
//*************************************************************************************************
CPipeline::CPipeline(CPipelineOptions* pOptions)
	: m_pEventDispatcher(NULL),
	m_pOptions(pOptions),
	m_PlayerState(Unknown),
	m_PlayerPendingState(Unknown)
{
}

CPipeline::~CPipeline()
{
	if (NULL != m_pOptions)
		delete m_pOptions;

	Dispose();

	if (NULL != m_pEventDispatcher)
		delete m_pEventDispatcher;
}

void CPipeline::SetEventDispatcher(CJavaPlayerEventDispatcher* pEventDispatcher)
{
	m_pEventDispatcher = pEventDispatcher;
}

uint32_t CPipeline::Init(const char * filename)
{
	return ERROR_NONE;
}

void CPipeline::Dispose()
{
}

uint32_t CPipeline::Play()
{
	return ERROR_NONE;
}

uint32_t CPipeline::Stop()
{
	return ERROR_NONE;
}

uint32_t CPipeline::Pause()
{
	return ERROR_NONE;
}

uint32_t CPipeline::Finish()
{
	return ERROR_NONE;
}

uint32_t CPipeline::Seek(double dSeekTime)
{
	return ERROR_NONE;
}

uint32_t CPipeline::GetDuration(double *pdDuration)
{
	if (NULL == pdDuration)
		return ERROR_FUNCTION_PARAM_NULL;

	*pdDuration = 0.0;

	return ERROR_NONE;
}

uint32_t CPipeline::GetStreamTime(double* pdStreamTime)
{
	if (NULL == pdStreamTime)
		return ERROR_FUNCTION_PARAM_NULL;

	*pdStreamTime = 0.0;

	return ERROR_NONE;
}

uint32_t CPipeline::SetRate(float fRate)
{
	return ERROR_NONE;
}

uint32_t CPipeline::GetRate(float* pfRate)
{
	if (NULL == pfRate)
		return ERROR_FUNCTION_PARAM_NULL;

	*pfRate = 0.0F;

	return ERROR_NONE;
}

uint32_t CPipeline::SetVolume(float fVolume)
{
	return ERROR_NONE;
}

uint32_t CPipeline::GetVolume(float* pfVolume)
{
	if (NULL == pfVolume)
		return ERROR_FUNCTION_PARAM_NULL;

	*pfVolume = 0.5F;

	return ERROR_NONE;
}

uint32_t CPipeline::SetBalance(float fBalance)
{
	return ERROR_NONE;
}

uint32_t CPipeline::GetBalance(float* pfBalance)
{
	if (NULL == pfBalance)
		return ERROR_FUNCTION_PARAM_NULL;

	*pfBalance = 0.0F;

	return ERROR_NONE;
}

uint32_t CPipeline::SetAudioSyncDelay(long lMillis) {
	return ERROR_NONE;
}

uint32_t CPipeline::GetAudioSyncDelay(long* plMillis) {
	if (NULL == plMillis)
		return ERROR_FUNCTION_PARAM_NULL;

	*plMillis = 0L;

	return ERROR_NONE;
}

uint32_t CPipeline::HasAudioData(bool* bAudioData) const {
	if (NULL == bAudioData)
		return ERROR_FUNCTION_PARAM_NULL;

	*bAudioData = false;

	return ERROR_NONE;
}

uint32_t CPipeline::HasImageData(bool* bImageData) const {
	if (NULL == bImageData)
		return ERROR_FUNCTION_PARAM_NULL;

	*bImageData = false;

	return ERROR_NONE;
}

uint32_t CPipeline::GetImageWidth(int* iWidth) const {
	if (NULL == iWidth)
		return ERROR_FUNCTION_PARAM_NULL;

	*iWidth = -1;

	return ERROR_NONE;
}

uint32_t CPipeline::GetImageHeight(int* iHeight) const {
	if (NULL == iHeight)
		return ERROR_FUNCTION_PARAM_NULL;

	*iHeight = -1;

	return ERROR_NONE;
}

uint32_t CPipeline::GetAudioFormat(AudioFormat* pAudioFormat) const {
	if (NULL == pAudioFormat)
		return ERROR_FUNCTION_PARAM_NULL;

	*pAudioFormat = AudioFormat();

	return ERROR_NONE;
}

uint32_t CPipeline::GetPixelFormat(PixelFormat* pPixelFormat) const {
	if (NULL == pPixelFormat)
		return ERROR_FUNCTION_PARAM_NULL;

	*pPixelFormat = PixelFormat();

	return ERROR_NONE;
}

uint32_t CPipeline::UpdateImageBuffer(uint8_t* pImageData, const long len) {
	if (NULL == pImageData || len == 0)
		return ERROR_FUNCTION_PARAM_NULL;

	pImageData = nullptr;

	return ERROR_NONE;
}

uint32_t CPipeline::UpdateAudioBuffer(uint8_t* pAudioData, const long len) {
	if (NULL == pAudioData || len == 0)
		return ERROR_FUNCTION_PARAM_NULL;

	pAudioData = nullptr;

	return ERROR_NONE;
}

void CPipeline::UpdatePlayerState(PlayerState newState) {
	PlayerState newPlayerState = m_PlayerState;	// If we assign the same state again
	bool bSilent = false;

	switch (m_PlayerState)
	{
	case Unknown:
		if (Ready == newState)
		{
			newPlayerState = Ready;
		}
		break;

	case Ready:
		if (Playing == newState)
		{
			newPlayerState = Playing;
		}
		break;

	case Playing:
		if (Stalled == newState || Paused == newState || Stopped == newState || Finished == newState) {
			newPlayerState = newState;
		}
		break;

	case Paused:
		if (Stopped == newState || Playing == newState)
		{
			newPlayerState = newState;
		}
		break;

	case Stopped:
		if (Playing == newState || Paused == newState)
		{
			newPlayerState = newState;
		}
		break;

	case Stalled:
	{
		if (Stopped == newState || Paused == newState || Playing == newState) {
			newPlayerState = newState;
		}
		break;
	}

	case Finished:
		if (Playing == newState) {
			// We can go from Finished to Playing only when seek happens (or repeat)
			// This state change should be silent.
			newPlayerState = Playing;
			bSilent = true;
		}
		if (Stopped == newState) {
			newPlayerState = Stopped;
		}

		break;

	case Error:
		break;
	}

	// The same thread can acquire the same lock several times
	SetPlayerState(newPlayerState, bSilent);
}


void CPipeline::SetPlayerState(PlayerState newPlayerState, bool bSilent) {

	// Determine if we need to send an event out
	bool updateState = newPlayerState != m_PlayerState;
	if (updateState)
	{
		if (NULL != m_pEventDispatcher && !bSilent)
		{
			m_PlayerState = newPlayerState;

			if (!m_pEventDispatcher->SendPlayerStateEvent(newPlayerState, 0))
			{
				m_pEventDispatcher->SendPlayerMediaErrorEvent(ERROR_JNI_SEND_PLAYER_STATE_EVENT);
			}
		}
		else
		{
			m_PlayerState = newPlayerState;
		}
	}

	if (updateState && newPlayerState == Stalled) { // Try to play
		Play();
	}
}

CPipelineOptions* CPipeline::GetCPipelineOptions() {
	return m_pOptions;
}
