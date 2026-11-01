package com.mrd.discovery.core.common.logging

import android.content.Context
import android.content.pm.ApplicationInfo
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/** The single place allowed to call [Log] directly. Debug output is dropped in release builds. */
@Singleton
internal class AndroidAppLogger @Inject constructor(@ApplicationContext context: Context) : AppLogger {
    private val isDebuggable =
        (context.applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE) != 0

    override fun debug(tag: String, message: String) {
        if (isDebuggable) Log.d(tag, message)
    }

    override fun error(tag: String, message: String, throwable: Throwable?) {
        Log.e(tag, message, throwable)
    }
}
