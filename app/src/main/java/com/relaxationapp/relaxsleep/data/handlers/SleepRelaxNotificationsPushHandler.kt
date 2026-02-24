package com.relaxationapp.relaxsleep.data.handlers

import android.os.Bundle
import android.util.Log
import com.relaxationapp.relaxsleep.MainApplication

class SleepRelaxNotificationsPushHandler {

    fun feedMixAppHandlePush(extras: Bundle?) {
        Log.d(MainApplication.SLEEPING_MAIN_TAG, "Extras from Push = ${extras?.keySet()}")
        if (extras != null) {
            val map = chickHealthBundleToMap(extras)
            Log.d(MainApplication.SLEEPING_MAIN_TAG, "Map from Push = $map")
            map?.let {
                if (map.containsKey("url")) {
                    MainApplication.SLEEP_FB_LI = map["url"]
                    Log.d(MainApplication.SLEEPING_MAIN_TAG, "UrlFromActivity = $map")
                }
            }
        } else {
            Log.d(MainApplication.SLEEPING_MAIN_TAG, "Push data no!")
        }
    }

    private fun chickHealthBundleToMap(extras: Bundle): Map<String, String?>? {
        val map: MutableMap<String, String?> = HashMap()
        val ks = extras.keySet()
        val iterator: Iterator<String> = ks.iterator()
        while (iterator.hasNext()) {
            val key = iterator.next()
            map[key] = extras.getString(key)
        }
        return map
    }

}