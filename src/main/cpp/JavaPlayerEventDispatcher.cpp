/*
 * Copyright (c) 2010, 2016, Oracle and/or its affiliates. All rights reserved.
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

#include "JavaPlayerEventDispatcher.h"
#include "JniUtils.h"
#include "org_datavyu_plugins_ffmpeg_NativeMediaPlayer.h"

static bool areJMethodIDsInitialized = false;

jmethodID CJavaPlayerEventDispatcher::m_SendPlayerMediaErrorEventMethod = 0;
jmethodID CJavaPlayerEventDispatcher::m_SendPlayerStateEventMethod = 0;

CJavaPlayerEventDispatcher::CJavaPlayerEventDispatcher()
: m_PlayerVM(NULL),
  m_PlayerInstance(NULL),
  m_MediaReference(0L)
{
}

CJavaPlayerEventDispatcher::~CJavaPlayerEventDispatcher()
{
    Dispose();
}

void CJavaPlayerEventDispatcher::Init(JNIEnv *env, jobject PlayerInstance, CMedia* pMedia)
{
    if (env->GetJavaVM(&m_PlayerVM) != JNI_OK) {
        if (env->ExceptionCheck()) {
            env->ExceptionClear();
        }
        return;
    }
    m_PlayerInstance = env->NewGlobalRef(PlayerInstance);
    m_MediaReference = (jlong) ptr_to_jlong(pMedia);

    // Initialize jmethodID data members. These are derived from the class of
    // the object and not its instance. No, this particular implementation is
    // not thread-safe, but the worst that can happen is that the jmethodIDs are
    // initialized more than once which is still better than once per player.
    if (false == areJMethodIDsInitialized)
    {
        CJavaEnvironment javaEnv(env);
        bool hasException = false;
        jclass klass = env->GetObjectClass(m_PlayerInstance);

        m_SendPlayerMediaErrorEventMethod = env->GetMethodID(klass, "sendPlayerMediaErrorEvent", "(I)V");
        hasException = javaEnv.reportException();

        if (!hasException)
        {
            m_SendPlayerStateEventMethod = env->GetMethodID(klass, "sendPlayerStateEvent", "(ID)V");
            hasException = javaEnv.reportException();
        }

        env->DeleteLocalRef(klass);

        areJMethodIDsInitialized = !hasException;
    }

}

void CJavaPlayerEventDispatcher::Dispose()
{
    CJavaEnvironment jenv(m_PlayerVM);
    JNIEnv *pEnv = jenv.getEnvironment();
    if (pEnv) {
        pEnv->DeleteGlobalRef(m_PlayerInstance);
        m_PlayerInstance = NULL; // prevent further calls to this object
    }
}

bool CJavaPlayerEventDispatcher::SendPlayerMediaErrorEvent(int errorCode)
{
    bool bSucceeded = false;
    CJavaEnvironment jenv(m_PlayerVM);
    JNIEnv *pEnv = jenv.getEnvironment();
    if (pEnv) {
        jobject localPlayer = pEnv->NewLocalRef(m_PlayerInstance);
        if (localPlayer) {
            pEnv->CallVoidMethod(localPlayer, m_SendPlayerMediaErrorEventMethod, errorCode);
            pEnv->DeleteLocalRef(localPlayer);

            bSucceeded = !jenv.reportException();
        }
    }

    return bSucceeded;
}

bool CJavaPlayerEventDispatcher::SendPlayerStateEvent(int newState, double presentTime)
{
    long newJavaState;

    switch(newState) {
    case CPipeline::Unknown:
        newJavaState = org_datavyu_plugins_ffmpeg_NativeMediaPlayer_eventPlayerUnknown;
        break;
    case CPipeline::Ready:
        newJavaState = org_datavyu_plugins_ffmpeg_NativeMediaPlayer_eventPlayerReady;
        break;
    case CPipeline::Playing:
        newJavaState = org_datavyu_plugins_ffmpeg_NativeMediaPlayer_eventPlayerPlaying;
        break;
    case CPipeline::Paused:
        newJavaState = org_datavyu_plugins_ffmpeg_NativeMediaPlayer_eventPlayerPaused;
        break;
    case CPipeline::Stopped:
        newJavaState = org_datavyu_plugins_ffmpeg_NativeMediaPlayer_eventPlayerStopped;
        break;
    case CPipeline::Stalled:
        newJavaState = org_datavyu_plugins_ffmpeg_NativeMediaPlayer_eventPlayerStalled;
        break;
    case CPipeline::Finished:
        newJavaState = org_datavyu_plugins_ffmpeg_NativeMediaPlayer_eventPlayerFinished;
        break;
    case CPipeline::Error:
        newJavaState = org_datavyu_plugins_ffmpeg_NativeMediaPlayer_eventPlayerError;
        break;
    default:
        return false;
    }

    bool bSucceeded = false;
    CJavaEnvironment jenv(m_PlayerVM);
    JNIEnv *pEnv = jenv.getEnvironment();
    if (pEnv) {
        jobject localPlayer = pEnv->NewLocalRef(m_PlayerInstance);
        if (localPlayer) {
            pEnv->CallVoidMethod(localPlayer, m_SendPlayerStateEventMethod, newJavaState, presentTime);
            pEnv->DeleteLocalRef(localPlayer);

            bSucceeded = !jenv.reportException();
        }
    }

    return bSucceeded;
}
