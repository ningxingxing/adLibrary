package com.star.ad.adlibrary

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowInsetsControllerCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.star.ad.adlibrary.constants.KEY_PACKAGE_NAME
import com.star.ad.adlibrary.coroutine.launchIO
import com.star.ad.adlibrary.coroutine.launchMain
import com.star.ad.adlibrary.databinding.ActivityRecommendBinding
import com.star.ad.adlibrary.helper.RecommendHelper
import com.star.ad.adlibrary.model.RecommendData
import com.star.ad.adlibrary.utils.WindowInsetsUtils


class RecommendActivity : AppCompatActivity(), View.OnClickListener,
    RecommendAdapter.OnAllItemClickListener {

    companion object {
        private const val TAG = "RecommendActivity"
    }

    private val binding: ActivityRecommendBinding by lazy {
        ActivityRecommendBinding.inflate(LayoutInflater.from(this))
    }

    private var mRecommendAdapter: RecommendAdapter? = null
    private var recommendList: MutableList<RecommendData> = ArrayList()
    private var mPackageName: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        enableEdgeToEdge()
        WindowInsetsUtils.setOnApplyWindowInsetsListener(
            binding.root,
            WindowInsetsUtils.SET_WINDOW_DEFAULT
        )
        WindowInsetsControllerCompat(window, window.decorView).apply {
            isAppearanceLightStatusBars = true // 设置状态栏图标为深色
        }

        initData()
    }

    private fun initData() = launchMain {
        mPackageName = intent.getStringExtra(KEY_PACKAGE_NAME)

        binding.ivAdsBack.setOnClickListener(this@RecommendActivity)
        val gridLayout = GridLayoutManager(this@RecommendActivity, 2, RecyclerView.VERTICAL, false)
        binding.recycler.layoutManager = gridLayout
        mRecommendAdapter = RecommendAdapter(this@RecommendActivity)
        binding.recycler.adapter = mRecommendAdapter
        mRecommendAdapter?.setOnIBridgeAllListener(this@RecommendActivity)
        loadData()
        mRecommendAdapter?.bindAllData(recommendList)
        Log.i(TAG, "recommendList=${recommendList.size} mPackageName=$mPackageName")

    }

    private suspend fun loadData() = launchIO {
        val packageNames = arrayOf(
            resources.getString(R.string.eq_package_name),
            resources.getString(R.string.music_package_name),
            resources.getString(R.string.gallery_package_name),
            resources.getString(R.string.wallpaper_live_package_name),
            resources.getString(R.string.heart_theme_wallpaper_package_name)
        )

        for (i in packageNames.indices) {
            if (mPackageName != packageNames[i]) {
                recommendList.add(
                    RecommendData(
                        RecommendHelper.titles[i],
                        RecommendHelper.icons[i],
                        true,
                        resources.getString(RecommendHelper.urls[i])
                    )
                )
            }
        }

    }

    private fun openApp(url: String) {
        try {
            val uri =
                Uri.parse(url)
            val intent = Intent(Intent.ACTION_VIEW, uri)
            startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            Log.e(TAG, "share =${e.message}")
        }
    }

    override fun onClick(v: View) {
        when (v.id) {

            R.id.iv_ads_back -> {

                finish()
            }

        }
    }

    override fun onItemClick(position: Int, recommendData: RecommendData) {
        openApp(recommendData.url)
    }

    override fun onLongClick(position: Int, list: MutableList<RecommendData>) {

    }

}