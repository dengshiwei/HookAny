package com.explore.hook

object HookLogger {
    @JvmStatic
    var ENABLE_HOOK_LOG = true

    @JvmStatic
    fun log(tag: String, msg: String) {
        if (ENABLE_HOOK_LOG) {
            System.err.println("[$tag]  $msg")
        }
    }
}
