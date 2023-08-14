package com.star.ad.adlibrary.helper

import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import com.google.android.gms.ads.nativead.MediaView
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdView
import com.star.ad.adlibrary.R

object NativeAdHelper {

    fun populateNativeAdView(nativeAd: NativeAd, nativeAdView: NativeAdView) {
        val adMedia: MediaView = nativeAdView.findViewById(R.id.ad_media)
        val adHeadline: TextView = nativeAdView.findViewById(R.id.ad_headline)
        val adBody: TextView = nativeAdView.findViewById(R.id.ad_body)
        val adCallToAction: Button = nativeAdView.findViewById(R.id.ad_call_to_action)
        val adAppIcon: ImageView = nativeAdView.findViewById(R.id.ad_app_icon)
        val adPrice: TextView = nativeAdView.findViewById(R.id.ad_price)
        val adStore: TextView = nativeAdView.findViewById(R.id.ad_store)
        val adStars: RatingBar = nativeAdView.findViewById(R.id.ad_stars)
        val adAdvertiser: TextView = nativeAdView.findViewById(R.id.ad_advertiser)

        // Set the media view.
        nativeAdView.mediaView = adMedia
        // Set other ad assets.
        nativeAdView.headlineView = adHeadline
        nativeAdView.bodyView = adBody
        nativeAdView.callToActionView = adCallToAction
        nativeAdView.iconView = adAppIcon
        nativeAdView.priceView = adPrice
        nativeAdView.starRatingView = adStars
        nativeAdView.storeView = adStore
        nativeAdView.advertiserView = adAdvertiser

        // The headline and media content are guaranteed to be in every UnifiedNativeAd.
        adHeadline.text = nativeAd.headline
        nativeAd.mediaContent?.let { adMedia.mediaContent = it }

        // These assets aren't guaranteed to be in every UnifiedNativeAd, so it's important to
        // check before trying to display them.
        if (nativeAd.body == null) {
            adBody.visibility = View.INVISIBLE
        } else {
            adBody.visibility = View.VISIBLE
            adBody.text = nativeAd.body
        }

        if (nativeAd.callToAction == null) {
            adCallToAction.visibility = View.INVISIBLE
        } else {
            adCallToAction.visibility = View.VISIBLE
            adCallToAction.text = nativeAd.callToAction
        }

        if (nativeAd.icon == null) {
            adAppIcon.visibility = View.GONE
        } else {
            adAppIcon.setImageDrawable(nativeAd.icon?.drawable)
            adAppIcon.visibility = View.VISIBLE
        }

        if (nativeAd.price == null) {
            adPrice.visibility = View.INVISIBLE
        } else {
            adPrice.visibility = View.VISIBLE
            adPrice.text = nativeAd.price
        }

        if (nativeAd.store == null) {
            adStore.visibility = View.INVISIBLE
        } else {
            adStore.visibility = View.VISIBLE
            adStore.text = nativeAd.store
        }

        if (nativeAd.starRating == null) {
            adStars.visibility = View.INVISIBLE
        } else {
            adStars.rating = nativeAd.starRating!!.toFloat()
            adStars.visibility = View.VISIBLE
        }

        if (nativeAd.advertiser == null) {
            adAdvertiser.visibility = View.INVISIBLE
        } else {
            adAdvertiser.text = nativeAd.advertiser
            adAdvertiser.visibility = View.VISIBLE
        }

        // This method tells the Google Mobile Ads SDK that you have finished populating your
        // native ad view with this native ad.
        nativeAdView.setNativeAd(nativeAd)

        // Get the video controller for the ad. One will always be provided, even if the ad doesn't
        // have a video asset.
//        val mediaContent = nativeAd.mediaContent
//        val vc = mediaContent?.videoController

        // Updates the UI to say whether or not this ad has a video asset.
//        if (vc != null && mediaContent.hasVideoContent()) {
//            // Create a new VideoLifecycleCallbacks object and pass it to the VideoController. The
//            // VideoController will call methods on this object when events occur in the video
//            // lifecycle.
//            vc.videoLifecycleCallbacks =
//                object : VideoController.VideoLifecycleCallbacks() {
//                    override fun onVideoEnd() {
//
//                        super.onVideoEnd()
//                    }
//                }
//        } else {
//
//        }
    }


    fun populateNativeAdSmallView(nativeAd: NativeAd, nativeAdView: NativeAdView) {
        val adAppIcon: ImageView = nativeAdView.findViewById(R.id.ad_app_icon)
        val adHeadline: TextView = nativeAdView.findViewById(R.id.ad_headline)
        val adStars: RatingBar = nativeAdView.findViewById(R.id.ad_stars)
        val adCallToAction: Button = nativeAdView.findViewById(R.id.ad_call_to_action)

        nativeAdView.callToActionView = adCallToAction
        nativeAdView.iconView = adAppIcon
        adHeadline.text = nativeAd.headline


        if (nativeAd.callToAction == null) {
            adCallToAction.visibility = View.INVISIBLE
        } else {
            adCallToAction.visibility = View.VISIBLE
            adCallToAction.text = nativeAd.callToAction
        }

        if (nativeAd.icon == null) {
            adAppIcon.visibility = View.GONE
        } else {
            adAppIcon.setImageDrawable(nativeAd.icon?.drawable)
            adAppIcon.visibility = View.VISIBLE
        }

        if (nativeAd.starRating == null) {
            adStars.visibility = View.INVISIBLE
        } else {
            adStars.rating = nativeAd.starRating!!.toFloat()
            adStars.visibility = View.VISIBLE
        }

//        if (nativeAd.advertiser == null) {
//            adAdvertiser.visibility = View.INVISIBLE
//        } else {
//            adAdvertiser.text = nativeAd.advertiser
//            adAdvertiser.visibility = View.VISIBLE
//        }

        nativeAdView.setNativeAd(nativeAd)
    }
}