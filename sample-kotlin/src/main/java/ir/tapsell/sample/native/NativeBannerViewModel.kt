package ir.tapsell.sample.native

import android.util.Log
import android.view.View
import androidx.fragment.app.FragmentActivity
import ir.tapsell.mediation.Tapsell
import ir.tapsell.mediation.ad.AdStateListener
import ir.tapsell.mediation.ad.request.RequestResultListener
import ir.tapsell.mediation.ad.views.ntv.NativeAdView
import ir.tapsell.mediation.ad.views.ntv.NativeAdViewContainer
import ir.tapsell.sample.BaseViewModel
import ir.tapsell.sample.R

class NativeBannerViewModel : BaseViewModel() {

    companion object {
        private const val TAG = "NativeBannerViewModel"
    }

    var responseIds = hashSetOf<String>()
        private set

    fun requestAd(zoneId: String, count: Int = 1) {
        object : RequestResultListener {
            override fun onFailure() {
                log(TAG, "onFailure", Log.ERROR)
            }

            override fun onSuccess(adId: String) {
                responseIds.add(adId)
                log(TAG, "onSuccess: $adId")
            }
        }.let { listener ->
            if (count > 1) Tapsell.requestMultipleNativeAds(zoneId, count, listener)
            else Tapsell.requestNativeAd(zoneId, listener)
        }


    }

    fun showAd(activity: FragmentActivity, container: NativeAdViewContainer) {
        if (responseIds.isEmpty()) {
            log(TAG, "There is no adId to show", Log.ERROR)
            return
        }
        responseIds.random().let { id ->
            responseIds.remove(id)
            Tapsell.showNativeAd(
                id,
                NativeAdView.Builder(container)
                    .withMedia(container.findViewById(R.id.tapsell_native_ad_media))
                    .withTitle(container.findViewById(R.id.tapsell_native_ad_title))
                    .withDescription(container.findViewById(R.id.tapsell_native_ad_description))
                    .withLogo(container.findViewById(R.id.tapsell_native_ad_logo))
                    .withCtaButton(container.findViewById(R.id.tapsell_native_ad_cta))
                    .withSponsored(container.findViewById(R.id.tapsell_native_ad_sponsored))
                    .build(),                activity,
                object : AdStateListener.Native {
                    override fun onAdClicked() {
                        log(TAG, "onAdClicked")
                    }

                    override fun onAdFailed(message: String) {
                        log(TAG, "onAdFailed: $message", Log.ERROR)
                    }

                    override fun onAdImpression() {
                        log(TAG, "onAdImpression")
                    }
                })
        }
    }
}
