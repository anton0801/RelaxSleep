package com.relaxationapp.relaxsleep.views.pres.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.relaxationapp.relaxsleep.MainActivity
import com.relaxationapp.relaxsleep.R
import com.relaxationapp.relaxsleep.data.handlers.SleepRelaxLocalStorageManager
import com.relaxationapp.relaxsleep.databinding.FragmentLoadFeedMixBinding
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel


class FeedMixLoadFragment : Fragment(R.layout.fragment_load_feed_mix) {
    private lateinit var chickHealthLoadBinding: FragmentLoadFeedMixBinding

    private val sleepRelaxingLoadingViewModel by viewModel<SleepRelaxingLoadingViewModel>()

    private val sleepRelaxLocalStorageManager by inject<SleepRelaxLocalStorageManager>()

    private var eggLabelUrl = ""

    private val chickHealthRequestNotificationPermission = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            eggLabelNavigateToSuccess(eggLabelUrl)
        } else {
            if (shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {
                sleepRelaxLocalStorageManager.feedMixNotificationRequest =
                    (System.currentTimeMillis() / 1000) + 2592000000
                eggLabelNavigateToSuccess(eggLabelUrl)
            } else {
                eggLabelNavigateToSuccess(eggLabelUrl)
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == 999 && grantResults.isNotEmpty() &&
            grantResults[0] == PackageManager.PERMISSION_GRANTED
        ) {
            eggLabelNavigateToSuccess(eggLabelUrl)
        } else {
            // твой код на отказ
            eggLabelNavigateToSuccess(eggLabelUrl)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        chickHealthLoadBinding = FragmentLoadFeedMixBinding.bind(view)

        chickHealthLoadBinding.feedMixGrandButton.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                val eggLabelPermission = Manifest.permission.POST_NOTIFICATIONS
                chickHealthRequestNotificationPermission.launch(eggLabelPermission)
                sleepRelaxLocalStorageManager.feedMixNotificationRequestedBefore = true
            } else {
                eggLabelNavigateToSuccess(eggLabelUrl)
                sleepRelaxLocalStorageManager.feedMixNotificationRequestedBefore = true
            }
        }

        chickHealthLoadBinding.feedMixSkipButton.setOnClickListener {
            sleepRelaxLocalStorageManager.feedMixNotificationRequest =
                (System.currentTimeMillis() / 1000) + 259200
            eggLabelNavigateToSuccess(eggLabelUrl)
        }

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                sleepRelaxingLoadingViewModel.chickHealthHomeScreenState.collect {
                    when (it) {
                        is SleepRelaxingLoadingViewModel.FeedMixHomeScreenState.FeedMixLoading -> {
                        }

                        is SleepRelaxingLoadingViewModel.FeedMixHomeScreenState.FeedMixError -> {
                            requireActivity().startActivity(
                                Intent(
                                    requireContext(),
                                    MainActivity::class.java
                                )
                            )
                            requireActivity().finish()
                        }

                        is SleepRelaxingLoadingViewModel.FeedMixHomeScreenState.FeedMixSuccess -> {
                            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.S_V2) {
                                val eggLabelPermission = Manifest.permission.POST_NOTIFICATIONS
                                val eggLabelPermissionRequestedBefore =
                                    sleepRelaxLocalStorageManager.feedMixNotificationRequestedBefore

                                if (ContextCompat.checkSelfPermission(
                                        requireContext(),
                                        eggLabelPermission
                                    ) == PackageManager.PERMISSION_GRANTED
                                ) {
                                    eggLabelNavigateToSuccess(it.data)
                                } else if (!eggLabelPermissionRequestedBefore && (System.currentTimeMillis() / 1000 > sleepRelaxLocalStorageManager.feedMixNotificationRequest)) {
                                    // первый раз — показываем UI для запроса
                                    chickHealthLoadBinding.feedMixNotiGroup.visibility =
                                        View.VISIBLE
                                    chickHealthLoadBinding.feedMixLoadingGroup.visibility =
                                        View.GONE
                                    eggLabelUrl = it.data
                                } else if (shouldShowRequestPermissionRationale(eggLabelPermission)) {
                                    // временный отказ — через 3 дня можно показать
                                    if (System.currentTimeMillis() / 1000 > sleepRelaxLocalStorageManager.feedMixNotificationRequest) {
                                        chickHealthLoadBinding.feedMixNotiGroup.visibility =
                                            View.VISIBLE
                                        chickHealthLoadBinding.feedMixLoadingGroup.visibility =
                                            View.GONE
                                        eggLabelUrl = it.data
                                    } else {
                                        eggLabelNavigateToSuccess(it.data)
                                    }
                                } else {
                                    eggLabelNavigateToSuccess(it.data)
                                }
                            } else {
                                eggLabelNavigateToSuccess(it.data)
                            }
                        }

                        SleepRelaxingLoadingViewModel.FeedMixHomeScreenState.FeedMixNotInternet -> {
                            chickHealthLoadBinding.feedMixStateGroup.visibility = View.VISIBLE
                            chickHealthLoadBinding.feedMixLoadingGroup.visibility = View.GONE
                        }
                    }
                }
            }
        }
    }


    private fun eggLabelNavigateToSuccess(data: String) {
        findNavController().navigate(
            R.id.action_feedMixLoadFragment_to_feedMixV,
            bundleOf(FEED_MIX_D to data)
        )
    }


    companion object {
        const val FEED_MIX_D = "eggLabelData"
    }
}