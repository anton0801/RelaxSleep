package com.relaxationapp.relaxsleep.views.pres.views

import android.content.DialogInterface
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.CookieManager
import android.webkit.ValueCallback
import android.widget.FrameLayout
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.relaxationapp.relaxsleep.MainApplication
import com.relaxationapp.relaxsleep.views.pres.ui.FeedMixLoadFragment
import org.koin.android.ext.android.inject

class FeedMixV : Fragment() {

    private lateinit var eggLabelPhoto: Uri
    private var eggLabelFilePathFromChrome: ValueCallback<Array<Uri>>? = null

    private val eggLabelTakeFile: ActivityResultLauncher<PickVisualMediaRequest> =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) {
            eggLabelFilePathFromChrome?.onReceiveValue(arrayOf(it ?: Uri.EMPTY))
            eggLabelFilePathFromChrome = null
        }

    private val eggLabelTakePhoto: ActivityResultLauncher<Uri> =
        registerForActivityResult(ActivityResultContracts.TakePicture()) {
            if (it) {
                eggLabelFilePathFromChrome?.onReceiveValue(arrayOf(eggLabelPhoto))
                eggLabelFilePathFromChrome = null
            } else {
                eggLabelFilePathFromChrome?.onReceiveValue(null)
                eggLabelFilePathFromChrome = null
            }
        }

    private val feedMixDataStore by activityViewModels<FeedMixDataStore>()


    private val sleepingVsdFunViFun by inject<SleepingVsdFunViFun>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(MainApplication.SLEEPING_MAIN_TAG, "Fragment onCreate")
        CookieManager.getInstance().setAcceptCookie(true)
        requireActivity().onBackPressedDispatcher.addCallback(
            this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (feedMixDataStore.feedMixView.canGoBack()) {
                        feedMixDataStore.feedMixView.goBack()
                    } else if (feedMixDataStore.feedMixViList.size > 1) {
                        feedMixDataStore.feedMixViList.removeAt(feedMixDataStore.feedMixViList.lastIndex)
                        feedMixDataStore.feedMixView.destroy()
                        val previousWebView = feedMixDataStore.feedMixViList.last()
                        eggLabelAttachWebViewToContainer(previousWebView)
                        feedMixDataStore.feedMixView = previousWebView
                    }
                }

            })
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        if (feedMixDataStore.feedMixIsFirstCreate) {
            feedMixDataStore.feedMixIsFirstCreate = false
            feedMixDataStore.feedMixContainerView = FrameLayout(requireContext()).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                id = View.generateViewId()
            }
            return feedMixDataStore.feedMixContainerView
        } else {
            return feedMixDataStore.feedMixContainerView
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (feedMixDataStore.feedMixViList.isEmpty()) {
            feedMixDataStore.feedMixView = FeedMixVi(requireContext(), object :
                FeedMixCallBack {
                override fun feedMixHandleCreateWebWindowRequest(feedMixVi: FeedMixVi) {
                    feedMixDataStore.feedMixViList.add(feedMixVi)
                    feedMixDataStore.feedMixView = feedMixVi
                    feedMixVi.eggLabelSetFileChooserHandler { callback ->
                        eggLabelHandleFileChooser(callback)
                    }
                    eggLabelAttachWebViewToContainer(feedMixVi)
                }

            }, eggLabelWindow = requireActivity().window).apply {
                eggLabelSetFileChooserHandler { callback ->
                    eggLabelHandleFileChooser(callback)
                }
            }
            feedMixDataStore.feedMixView.eggLabelFLoad(
                arguments?.getString(FeedMixLoadFragment.FEED_MIX_D) ?: ""
            )
            feedMixDataStore.feedMixViList.add(feedMixDataStore.feedMixView)
            eggLabelAttachWebViewToContainer(feedMixDataStore.feedMixView)
        } else {
            feedMixDataStore.feedMixViList.forEach { webView ->
                webView.eggLabelSetFileChooserHandler { callback ->
                    eggLabelHandleFileChooser(callback)
                }
            }
            feedMixDataStore.feedMixView = feedMixDataStore.feedMixViList.last()

            eggLabelAttachWebViewToContainer(feedMixDataStore.feedMixView)
        }
    }

    private fun eggLabelHandleFileChooser(callback: ValueCallback<Array<Uri>>?) {
        eggLabelFilePathFromChrome = callback

        val listItems: Array<out String> = arrayOf("Select from file", "To make a photo")
        val listener = DialogInterface.OnClickListener { _, which ->
            when (which) {
                0 -> {
                    eggLabelTakeFile.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                }

                1 -> {
                    eggLabelPhoto = sleepingVsdFunViFun.eggLabelSavePhoto()
                    eggLabelTakePhoto.launch(eggLabelPhoto)
                }
            }
        }

        AlertDialog.Builder(requireContext())
            .setTitle("Choose a method")
            .setItems(listItems, listener)
            .setCancelable(true)
            .setOnCancelListener {
                callback?.onReceiveValue(null)
                eggLabelFilePathFromChrome = null
            }
            .create()
            .show()
    }

    private fun eggLabelAttachWebViewToContainer(w: FeedMixVi) {
        feedMixDataStore.feedMixContainerView.post {
            (w.parent as? ViewGroup)?.removeView(w)
            feedMixDataStore.feedMixContainerView.removeAllViews()
            feedMixDataStore.feedMixContainerView.addView(w)
        }
    }


}