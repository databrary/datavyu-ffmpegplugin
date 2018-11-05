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

#include "JniUtils.h"

JNIEnv *GetJavaEnvironment(JavaVM *jvm, jboolean &didAttach) {
  JNIEnv *env = NULL;
  didAttach = false;
  if (jvm) {
    if (jvm->GetEnv((void **)&env, JNI_VERSION_1_4) != JNI_OK) {
      didAttach = true;
      jvm->AttachCurrentThreadAsDaemon((void **)&env, NULL);
    }
  }
  return env;
}

bool CJavaEnvironment::HasException() {
  return (p_environment ? (bool)p_environment->ExceptionCheck() : false);
}

bool CJavaEnvironment::ClearException() {
  if (p_environment ? p_environment->ExceptionCheck() : false) {
    p_environment->ExceptionClear();
    return true;
  }
  return false;
}

/**
 * Check whether there is a pending exception and if so, log its string version
 * and return true, otherwise, i.e., if there is no exception, return false.
 */
bool CJavaEnvironment::ReportException() {
  if (p_environment) {
    jthrowable exc = p_environment->ExceptionOccurred();
    if (exc) {
      p_environment->ExceptionClear(); // Clear current exception
      jclass cid = p_environment->FindClass("java/lang/Throwable");
      if (!ClearException()) {
        jmethodID mid =
            p_environment->GetMethodID(cid, "toString", "()Ljava/lang/String;");
        if (!ClearException()) {
          jstring jmsg = (jstring)p_environment->CallObjectMethod(exc, mid);
          if (!ClearException()) {
            char *pmsg = (char *)p_environment->GetStringUTFChars(jmsg, NULL);
            p_environment->ReleaseStringUTFChars(jmsg, pmsg);
          }
        }
        p_environment->DeleteLocalRef(cid);
      }
      p_environment->DeleteLocalRef(exc);
      return true;
    }
  }
  return false;
}

CJavaEnvironment::CJavaEnvironment(JavaVM *jvm)
    : attached(false), p_environment(NULL) {
  if (jvm) {
    p_environment = GetJavaEnvironment(jvm, attached);
  }
}

CJavaEnvironment::CJavaEnvironment(JNIEnv *env) : attached(false) {
  p_environment = env;
}

CJavaEnvironment::~CJavaEnvironment() {
  if (attached && p_environment) {
    JavaVM *jvm;
    if (p_environment->GetJavaVM(&jvm) == JNI_OK) {
      jvm->DetachCurrentThread();
    }
  }
}

JNIEnv *CJavaEnvironment::GetEnvironment() { return p_environment; }
