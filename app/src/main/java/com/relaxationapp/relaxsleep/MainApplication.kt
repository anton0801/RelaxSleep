package com.relaxationapp.relaxsleep

import android.app.Application
import android.util.Log
import android.view.WindowManager
import com.appsflyer.AppsFlyerConversionListener
import com.appsflyer.AppsFlyerLib
import com.appsflyer.attribution.AppsFlyerRequestListener
import com.appsflyer.deeplink.DeepLink
import com.appsflyer.deeplink.DeepLinkResult
import com.relaxationapp.relaxsleep.data.domain.data.SleepingRelaxingChSystemServiceI
import com.relaxationapp.relaxsleep.data.domain.data.SleepingRelaxPushTokenUseCase
import com.relaxationapp.relaxsleep.data.domain.data.SleepRelaxRepositoryImpl
import com.relaxationapp.relaxsleep.data.domain.usecases.SleepingRelaxingGetAllUseCaseInApp
import com.relaxationapp.relaxsleep.data.handlers.SleepRelaxLocalStorageManager
import com.relaxationapp.relaxsleep.data.handlers.SleepRelaxNotificationsPushHandler
import com.relaxationapp.relaxsleep.views.pres.views.SleepingVsdFunViFun
import com.relaxationapp.relaxsleep.views.pres.ui.SleepRelaxingLoadingViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.awaitResponse
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query
import kotlin.collections.iterator


val feedMixModule = module {
    factory {
        SleepRelaxNotificationsPushHandler()
    }
    single {
        SleepRelaxRepositoryImpl()
    }
    single {
        SleepRelaxLocalStorageManager(get())
    }
    factory {
        SleepingRelaxPushTokenUseCase()
    }
    factory {
        SleepingRelaxingChSystemServiceI(get())
    }
    factory {
        SleepingRelaxingGetAllUseCaseInApp(
            get(), get(), get()
        )
    }
    factory {
        SleepingVsdFunViFun(get())
    }
    viewModel {
        SleepRelaxingLoadingViewModel(get(), get(), get())
    }
}

class MainApplication : Application() {

    private var sleppingChIsResumed = false
    private var feedMIxConvTimeoutJob: Job? = null
    private var feedMixxxChickkDeepLinksMap: MutableMap<String, Any>? = null

    override fun onCreate() {
        super.onCreate()

        val appsflyer = AppsFlyerLib.getInstance()
        feedMixDebugLoggerMode(appsflyer)
        feedMix(appsflyer)

        AppsFlyerLib.getInstance().subscribeForDeepLink { p0 ->
            when (p0.status) {
                DeepLinkResult.Status.FOUND -> {
                    feedMixDDExtractDeepLinksData(p0.deepLink)

                }

                DeepLinkResult.Status.NOT_FOUND -> {
                }

                DeepLinkResult.Status.ERROR -> {
                }
            }
        }

        appsflyer.init(
            SLEEP_APPSFLYER_DEV,
            object : AppsFlyerConversionListener {
                override fun onConversionDataSuccess(p0: MutableMap<String, Any>?) {
                    feedMIxConvTimeoutJob?.cancel()

                    val afStatus = p0?.get("af_status")?.toString() ?: "null"
                    if (afStatus == "Organic") {
                        CoroutineScope(Dispatchers.IO).launch {
                            try {
                                delay(5000)
                                val api = feedMixGetApiMethodsForAppsflyer(
                                    "https://gcdsdk.appsflyer.com/install_data/v4.0/",
                                    null
                                )
                                val response = api.eggLabelGetClient(
                                    devkey = SLEEP_APPSFLYER_DEV,
                                    deviceId = feedMixGetAppserId()
                                ).awaitResponse()

                                val resp = response.body()
                                if (resp?.get("af_status") == "Organic") {
                                    feedMixChickResume(SleepingRelaxingAppsFlyerState.SleepingRelaxingError)
                                } else {
                                    feedMixChickResume(
                                        SleepingRelaxingAppsFlyerState.SleepingRelaxingSuccess(resp)
                                    )
                                }
                            } catch (d: Exception) {
                                feedMixChickResume(SleepingRelaxingAppsFlyerState.SleepingRelaxingError)
                            }
                        }
                    } else {
                        feedMixChickResume(SleepingRelaxingAppsFlyerState.SleepingRelaxingSuccess(p0))
                    }
                }

                override fun onConversionDataFail(p0: String?) {
                    feedMIxConvTimeoutJob?.cancel()
                    feedMixChickResume(SleepingRelaxingAppsFlyerState.SleepingRelaxingError)
                }

                override fun onAppOpenAttribution(p0: MutableMap<String, String>?) {
                }

                override fun onAttributionFailure(p0: String?) {
                }
            },
            this
        )

        appsflyer.start(this, SLEEP_APPSFLYER_DEV, object :
            AppsFlyerRequestListener {
            override fun onSuccess() {
            }

            override fun onError(p0: Int, p1: String) {
                feedMixChickResume(SleepingRelaxingAppsFlyerState.SleepingRelaxingError)
            }
        })
        feedMixStartConvTimeot()
        startKoin {
            androidLogger(Level.DEBUG)
            androidContext(this@MainApplication)
            modules(
                listOf(
                    feedMixModule
                )
            )
        }
    }

    private fun feedMixDebugLoggerMode(appsflyer: AppsFlyerLib) {
        appsflyer.setDebugLog(true)
    }

    private fun feedMixDDExtractDeepLinksData(dl: DeepLink) {
        val map = mutableMapOf<String, Any>()
        dl.deepLinkValue?.let { map["deep_link_value"] = it }
        dl.mediaSource?.let { map["media_source"] = it }
        dl.campaign?.let { map["campaign"] = it }
        dl.campaignId?.let { map["campaign_id"] = it }
        dl.afSub1?.let { map["af_sub1"] = it }
        dl.afSub2?.let { map["af_sub2"] = it }
        dl.afSub3?.let { map["af_sub3"] = it }
        dl.afSub4?.let { map["af_sub4"] = it }
        dl.afSub5?.let { map["af_sub5"] = it }
        dl.matchType?.let { map["match_type"] = it }
        dl.clickHttpReferrer?.let { map["click_http_referrer"] = it }
        dl.getStringValue("timestamp")?.let { map["timestamp"] = it }
        dl.isDeferred?.let { map["is_deferred"] = it }
        for (i in 1..10) {
            val key = "deep_link_sub$i"
            dl.getStringValue(key)?.let {
                if (!map.containsKey(key)) {
                    map[key] = it
                }
            }
        }
        Log.d(SLEEPING_MAIN_TAG, "Extracted DeepLink data: $map")
        feedMixxxChickkDeepLinksMap = map
    }

    private fun feedMixGetAppserId(): String =
        AppsFlyerLib.getInstance().getAppsFlyerUID(this) ?: ""


    companion object {
        var sleepingMInputMode = WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN
        val sleepingMMConversionFlow: MutableStateFlow<SleepingRelaxingAppsFlyerState> = MutableStateFlow(
            SleepingRelaxingAppsFlyerState.SleepingRelaxingDefault
        )
        var SLEEP_FB_LI: String? = null
        const val SLEEPING_MAIN_TAG = "SLEEP_RELAX_MainTag"
    }

    private fun feedMixGetApiMethodsForAppsflyer(
        url: String,
        client: OkHttpClient?
    ): SleepMixAppsApi {
        val retrofit = Retrofit.Builder()
            .baseUrl(url)
            .client(client ?: OkHttpClient.Builder().build())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        return retrofit.create()
    }

    private fun feedMixStartConvTimeot() {
        feedMIxConvTimeoutJob = CoroutineScope(Dispatchers.Main).launch {
            delay(30000)
            if (!sleppingChIsResumed) {
                Log.d(SLEEPING_MAIN_TAG, "TIMEOUT: No conversion data received in 30s")
                feedMixChickResume(SleepingRelaxingAppsFlyerState.SleepingRelaxingError)
            }
        }
    }

    private fun feedMixChickResume(state: SleepingRelaxingAppsFlyerState) {
        feedMIxConvTimeoutJob?.cancel()
        if (state is SleepingRelaxingAppsFlyerState.SleepingRelaxingSuccess) {
            val convData = state.feedMixxChickkData ?: mutableMapOf()
            val deepData = feedMixxxChickkDeepLinksMap ?: mutableMapOf()
            val merged = mutableMapOf<String, Any>().apply {
                putAll(convData)
                for ((key, value) in deepData) {
                    if (!containsKey(key)) {
                        put(key, value)
                    }
                }
            }
            if (!sleppingChIsResumed) {
                sleppingChIsResumed = true
                sleepingMMConversionFlow.value = SleepingRelaxingAppsFlyerState.SleepingRelaxingSuccess(merged)
            }
        } else {
            if (!sleppingChIsResumed) {
                sleppingChIsResumed = true
                sleepingMMConversionFlow.value = state
            }
        }
    }

    private fun feedMix(appsflyer: AppsFlyerLib) {
        appsflyer.setMinTimeBetweenSessions(0)
    }
}

sealed interface SleepingRelaxingAppsFlyerState {
    data object SleepingRelaxingDefault : SleepingRelaxingAppsFlyerState
    data class SleepingRelaxingSuccess(val feedMixxChickkData: MutableMap<String, Any>?) :
        SleepingRelaxingAppsFlyerState

    data object SleepingRelaxingError : SleepingRelaxingAppsFlyerState
}

interface SleepMixAppsApi {
    @Headers("Content-Type: application/json")
    @GET(SLEEP_MIX_LIN)
    fun eggLabelGetClient(
        @Query("devkey") devkey: String,
        @Query("device_id") deviceId: String,
    ): Call<MutableMap<String, Any>?>
}

private const val SLEEP_APPSFLYER_DEV = "oqnqZTRTczx6YahZdMfpF6"
private const val SLEEP_MIX_LIN = "com.relaxationapp.relaxsleep"
