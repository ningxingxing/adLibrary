package com.star.ad.adlibrary.helper

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import com.star.ad.adlibrary.R
import com.star.ad.adlibrary.RecommendActivity

object RecommendHelper {

    private const val TAG = "RecommendHelper"

    val icons = arrayOf(R.drawable.eq,R.drawable.music,R.drawable.photo,R.drawable.wallpaper_live,R.drawable.heart_theme_wallpaper)
    val titles = arrayOf(R.string.eq_name,R.string.music_name,R.string.photo_name,R.string.wallpaper_live_name,R.string.heart_theme_wallpaper_name)
            val urls = arrayOf(R.string.eq_url,R.string.music_url,R.string.photo_url,R.string.wallpaper_live_url,R.string.heart_theme_wallpaper_url)


    fun openApp(context: Context,url: String) {
        try {
            val uri =
                Uri.parse(url)
            val intent = Intent(Intent.ACTION_VIEW, uri)
            context.startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            Log.e(TAG, "share =${e.message}")
        }
    }
}