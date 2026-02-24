package com.relaxationapp.relaxsleep.data.domain.usecases

import android.util.Log
import com.relaxationapp.relaxsleep.MainApplication
import com.relaxationapp.relaxsleep.data.domain.data.SleepingRelaxPushTokenUseCase
import com.relaxationapp.relaxsleep.data.domain.data.SleepRelaxRepositoryImpl
import com.relaxationapp.relaxsleep.data.domain.data.SleepingRelaxingChSystemServiceI
import com.relaxationapp.relaxsleep.data.domain.model.FeedMIxEntity
import com.relaxationapp.relaxsleep.data.domain.model.FeedMixParam

class SleepingRelaxingGetAllUseCaseInApp(
    private val sleepRelaxRepositoryImpl: SleepRelaxRepositoryImpl,
    private val sleepingRelaxingChSystemServiceI: SleepingRelaxingChSystemServiceI,
    private val sleepingRelaxPushTokenUseCase: SleepingRelaxPushTokenUseCase,
) {
    suspend operator fun invoke(conversion: MutableMap<String, Any>?): FeedMIxEntity? {
        val params = FeedMixParam(
            feedMixLocale = sleepingRelaxingChSystemServiceI.getLocaleOfUserFeedMix(),
            feedMixPushToken = sleepingRelaxPushTokenUseCase.eggLabelGetToken(),
            feedMixAfId = sleepingRelaxingChSystemServiceI.getAppsflyerIdForApp()
        )
        Log.d(MainApplication.SLEEPING_MAIN_TAG, "Params for request: $params")
        return sleepRelaxRepositoryImpl.feedMixAppGetClient(params, conversion)
    }


}