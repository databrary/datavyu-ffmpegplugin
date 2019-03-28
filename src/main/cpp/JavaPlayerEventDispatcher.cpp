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

jmethodID CJavaPlayerEventDispatcher::send_player_media_error_event_method_ = 0;
jmethodID CJavaPlayerEventDispatcher::send_player_state_event_method_ = 0;

CJavaPlayerEventDispatcher::CJavaPlayerEventDispatcher()
    : p_player_vm_(NULL), player_instance_(NULL), media_reference_(0L) {}

CJavaPlayerEventDispatcher::~CJavaPlayerEventDispatcher() { Dispose(); }

void CJavaPlayerEventDispatcher::Init(JNIEnv *env, jobject PlayerInstance,
                                      CMedia *pMedia) {
  if (env->GetJavaVM(&p_player_vm_) != JNI_OK) {
    if (env->ExceptionCheck()) {
      env->ExceptionClear();
    }
    return;
  }
  player_instance_ = env->NewGlobalRef(PlayerInstance);
  media_reference_ = (jlong)ptr_to_jlong(pMedia);

  // Initialize jmethodID data members. These are derived from the class of
  // the object and not its instance. No, this particular implementation is
  // not thread-safe, but the worst that can happen is that the jmethodIDs are
  // initialized more than once which is still better than once per player.
  if (false == areJMethodIDsInitialized) {
    CJavaEnvironment javaEnv(env);
    bool hasException = false;
    jclass klass = env->GetObjectClass(player_instance_);

    send_player_media_error_event_method_ =
        env->GetMethodID(klass, "sendPlayerMediaErrorEvent", "(I)V");
    hasException = javaEnv.ReportException();

    if (!hasException) {
      send_player_state_event_method_ =
          env->GetMethodID(klass, "sendPlayerStateEvent", "(ID)V");
      hasException = javaEnv.ReportException();
    }

    env->DeleteLocalRef(klass);

    areJMethodIDsInitialized = !hasException;
  }
}

void CJavaPlayerEventDispatcher::Dispose() {
  CJavaEnvironment jenv(p_player_vm_);
  JNIEnv *pEnv = jenv.GetEnvironment();
  if (pEnv) {
    pEnv->DeleteGlobalRef(player_instance_);
    player_instance_ = nullptr; // prevent further calls to this object
  }
}

bool CJavaPlayerEventDispatcher::SendPlayerMediaErrorEvent(int errorCode) {
  bool bSucceeded = false;
  CJavaEnvironment jenv(p_player_vm_);
  JNIEnv *pEnv = jenv.GetEnvironment();
  if (pEnv) {
    jobject localPlayer = pEnv->NewLocalRef(player_instance_);
    if (localPlayer) {
      pEnv->CallVoidMethod(localPlayer, send_player_media_error_event_method_,
                           errorCode);
      pEnv->DeleteLocalRef(localPlayer);

      bSucceeded = !jenv.ReportException();
    }
  }

  return bSucceeded;
}

bool CJavaPlayerEventDispatcher::SendPlayerStateEvent(int newState,
                                                      double presentTime) {
  long newJavaState;

  switch (newState) {
  case CPipeline::Unknown:
    newJavaState =
        org_datavyu_plugins_ffmpeg_NativeMediaPlayer_eventPlayerUnknown;
    break;
  case CPipeline::Ready:
    newJavaState =
        org_datavyu_plugins_ffmpeg_NativeMediaPlayer_eventPlayerReady;
    break;
  case CPipeline::Playing:
    newJavaState =
        org_datavyu_plugins_ffmpeg_NativeMediaPlayer_eventPlayerPlaying;
    break;
  case CPipeline::Paused:
    newJavaState =
        org_datavyu_plugins_ffmpeg_NativeMediaPlayer_eventPlayerPaused;
    break;
  case CPipeline::Stopped:
    newJavaState =
        org_datavyu_plugins_ffmpeg_NativeMediaPlayer_eventPlayerStopped;
    break;
  case CPipeline::Stalled:
    newJavaState =
        org_datavyu_plugins_ffmpeg_NativeMediaPlayer_eventPlayerStalled;
    break;
  case CPipeline::Finished:
    newJavaState =
        org_datavyu_plugins_ffmpeg_NativeMediaPlayer_eventPlayerFinished;
    break;
  case CPipeline::Error:
    newJavaState =
        org_datavyu_plugins_ffmpeg_NativeMediaPlayer_eventPlayerError;
    break;
  default:
    return false;
  }

  bool bSucceeded = false;
  CJavaEnvironment jenv(p_player_vm_);
  JNIEnv *pEnv = jenv.GetEnvironment();
  if (pEnv) {
    jobject localPlayer = pEnv->NewLocalRef(player_instance_);
    if (localPlayer) {
      pEnv->CallVoidMethod(localPlayer, send_player_state_event_method_,
                           newJavaState, presentTime);
      pEnv->DeleteLocalRef(localPlayer);

      bSucceeded = !jenv.ReportException();
    }
  }

  return bSucceeded;
}
