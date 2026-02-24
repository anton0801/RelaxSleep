package com.relaxationapp.relaxsleep.views.pres.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.relaxationapp.relaxsleep.SleepingRelaxingAppsFlyerState
import com.relaxationapp.relaxsleep.MainApplication
import com.relaxationapp.relaxsleep.data.handlers.SleepRelaxLocalStorageManager
import com.relaxationapp.relaxsleep.data.domain.data.SleepingRelaxingChSystemServiceI
import com.relaxationapp.relaxsleep.data.domain.usecases.SleepingRelaxingGetAllUseCaseInApp
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SleepRelaxingLoadingViewModel(
    private val sleepingRelaxingGetAllUseCaseInApp: SleepingRelaxingGetAllUseCaseInApp,
    private val sleepRelaxLocalStorageManager: SleepRelaxLocalStorageManager,
    private val sleepingRelaxingChSystemServiceI: SleepingRelaxingChSystemServiceI
) : ViewModel() {

    private val _chickHealthHomeScreenState: MutableStateFlow<FeedMixHomeScreenState> =
        MutableStateFlow(FeedMixHomeScreenState.FeedMixLoading)
    val chickHealthHomeScreenState = _chickHealthHomeScreenState.asStateFlow()

    private var eggLabelGetApps = false

    init {
        viewModelScope.launch {
            when (sleepRelaxLocalStorageManager.feedMixAppState) {
                0 -> {
                    if (sleepingRelaxingChSystemServiceI.feedMixCheckInternetConnection()) {
                        MainApplication.sleepingMMConversionFlow.collect {
                            when (it) {
                                SleepingRelaxingAppsFlyerState.SleepingRelaxingDefault -> {}
                                SleepingRelaxingAppsFlyerState.SleepingRelaxingError -> {
                                    sleepRelaxLocalStorageManager.feedMixAppState = 2
                                    _chickHealthHomeScreenState.value =
                                        FeedMixHomeScreenState.FeedMixError
                                    eggLabelGetApps = true
                                }

                                is SleepingRelaxingAppsFlyerState.SleepingRelaxingSuccess -> {
                                    if (!eggLabelGetApps) {
                                        feedMixGetData(it.feedMixxChickkData)
                                        eggLabelGetApps = true
                                    }
                                }
                            }
                        }
                    } else {
                        _chickHealthHomeScreenState.value =
                            FeedMixHomeScreenState.FeedMixNotInternet
                    }
                }

                1 -> {
                    if (sleepingRelaxingChSystemServiceI.feedMixCheckInternetConnection()) {
                        if (MainApplication.SLEEP_FB_LI != null) {
                            _chickHealthHomeScreenState.value =
                                FeedMixHomeScreenState.FeedMixSuccess(
                                    MainApplication.SLEEP_FB_LI.toString()
                                )
                        } else if (System.currentTimeMillis() / 1000 > sleepRelaxLocalStorageManager.feedMixExpired) {
                            Log.d(
                                MainApplication.SLEEPING_MAIN_TAG,
                                "Current time more then expired, repeat request"
                            )
                            MainApplication.sleepingMMConversionFlow.collect {
                                when (it) {
                                    SleepingRelaxingAppsFlyerState.SleepingRelaxingDefault -> {}
                                    SleepingRelaxingAppsFlyerState.SleepingRelaxingError -> {
                                        _chickHealthHomeScreenState.value =
                                            FeedMixHomeScreenState.FeedMixSuccess(
                                                sleepRelaxLocalStorageManager.feedMixSavedUrl
                                            )
                                        eggLabelGetApps = true
                                    }

                                    is SleepingRelaxingAppsFlyerState.SleepingRelaxingSuccess -> {
                                        if (!eggLabelGetApps) {
                                            feedMixGetData(it.feedMixxChickkData)
                                            eggLabelGetApps = true
                                        }
                                    }
                                }
                            }
                        } else {
                            Log.d(
                                MainApplication.SLEEPING_MAIN_TAG,
                                "Current time less then expired, use saved url"
                            )
                            _chickHealthHomeScreenState.value =
                                FeedMixHomeScreenState.FeedMixSuccess(
                                    sleepRelaxLocalStorageManager.feedMixSavedUrl
                                )
                        }
                    } else {
                        _chickHealthHomeScreenState.value =
                            FeedMixHomeScreenState.FeedMixNotInternet
                    }
                }

                2 -> {
                    _chickHealthHomeScreenState.value =
                        FeedMixHomeScreenState.FeedMixError
                }
            }
        }
    }


    private suspend fun feedMixGetData(conversation: MutableMap<String, Any>?) {
        val eggLabelData = sleepingRelaxingGetAllUseCaseInApp.invoke(conversation)
        if (sleepRelaxLocalStorageManager.feedMixAppState == 0) {
            if (eggLabelData == null) {
                sleepRelaxLocalStorageManager.feedMixAppState = 2
                _chickHealthHomeScreenState.value =
                    FeedMixHomeScreenState.FeedMixError
            } else {
                sleepRelaxLocalStorageManager.feedMixAppState = 1
                sleepRelaxLocalStorageManager.apply {
                    feedMixExpired = eggLabelData.feedMixExpires
                    feedMixSavedUrl = eggLabelData.feedMixUrl
                }
                _chickHealthHomeScreenState.value =
                    FeedMixHomeScreenState.FeedMixSuccess(eggLabelData.feedMixUrl)
            }
        } else {
            if (eggLabelData == null) {
                _chickHealthHomeScreenState.value =
                    FeedMixHomeScreenState.FeedMixSuccess(sleepRelaxLocalStorageManager.feedMixSavedUrl)
            } else {
                sleepRelaxLocalStorageManager.apply {
                    feedMixExpired = eggLabelData.feedMixExpires
                    feedMixSavedUrl = eggLabelData.feedMixUrl
                }
                _chickHealthHomeScreenState.value =
                    FeedMixHomeScreenState.FeedMixSuccess(eggLabelData.feedMixUrl)
            }
        }
    }


    sealed class FeedMixHomeScreenState {
        data object FeedMixLoading : FeedMixHomeScreenState()
        data object FeedMixError : FeedMixHomeScreenState()
        data class FeedMixSuccess(val data: String) : FeedMixHomeScreenState()
        data object FeedMixNotInternet : FeedMixHomeScreenState()
    }
}