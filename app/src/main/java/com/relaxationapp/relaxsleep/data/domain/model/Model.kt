package com.relaxationapp.relaxsleep.data.domain.model

import com.google.gson.annotations.SerializedName

private const val FEED_MIX_A = "com.relaxationapp.relaxsleep"
private const val FEED_MIX_B = "relaxsleep-10c5a"


data class FeedMixParam (
    @SerializedName("af_id")
    val feedMixAfId: String,
    @SerializedName("bundle_id")
    val feedMixBundleId: String = FEED_MIX_A,
    @SerializedName("os")
    val feedMixOs: String = "Android",
    @SerializedName("store_id")
    val feedMixStoreId: String = FEED_MIX_A,
    @SerializedName("locale")
    val feedMixLocale: String,
    @SerializedName("push_token")
    val feedMixPushToken: String,
    @SerializedName("firebase_project_id")
    val feedMixFirebaseProjectId: String = FEED_MIX_B,
)
data class FeedMIxEntity (
    @SerializedName("ok")
    val feedMixOk: String,
    @SerializedName("url")
    val feedMixUrl: String,
    @SerializedName("expires")
    val feedMixExpires: Long,
)