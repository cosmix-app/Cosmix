package com.cosmix.app.utils

import com.cosmix.app.InternalAPI
import kotlin.concurrent.Volatile

@InternalAPI
object AppDebug {
    @Volatile
    var isDebug: Boolean = false
}
