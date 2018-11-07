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

#ifndef _JAVA_PLAYER_EVENT_DISPATCHER_H_
#define _JAVA_PLAYER_EVENT_DISPATCHER_H_

#include <jni.h>

#include "Media.h"
#include "Pipeline.h"

using namespace std;

class CJavaPlayerEventDispatcher {
public:
  CJavaPlayerEventDispatcher();
  ~CJavaPlayerEventDispatcher();

  void Init(JNIEnv *env, jobject PlayerInstance, CMedia *pMedia);
  void Dispose();

  virtual bool SendPlayerMediaErrorEvent(int errorCode);
  virtual bool SendPlayerStateEvent(int newState, double presentTime);

private:
  JavaVM *p_player_vm_;
  jobject player_instance_;
  jlong media_reference_; // FIXME: Nuke this field, it's completely unused

  static jmethodID send_warning_method_;

  static jmethodID send_player_media_error_event_method_;
  static jmethodID send_player_state_event_method_;

  static jobject CreateObject(JNIEnv *env, jmethodID *cid,
                              const char *class_name, const char *signature,
                              jvalue *value);
  static jobject CreateBoolean(JNIEnv *env, jboolean boolean_value);
  static jobject CreateInteger(JNIEnv *env, jint int_value);
  static jobject CreateLong(JNIEnv *env, jlong long_value);
  static jobject CreateDouble(JNIEnv *env, jdouble double_value);
  static jobject CreateDuration(JNIEnv *env, jlong duration);
};

#endif // _JAVA_PLAYER_EVENT_DISPATCHER_H_
