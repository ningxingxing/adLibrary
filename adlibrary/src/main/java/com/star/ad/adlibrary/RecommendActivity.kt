package com.star.ad.adlibrary

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.star.ad.adlibrary.constants.KEY_PACKAGE_NAME
import com.star.ad.adlibrary.databinding.ActivityRecommendBinding
import com.star.ad.adlibrary.helper.RecommendHelper
import com.star.ad.adlibrary.model.RecommendData


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
        val window = window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = ContextCompat.getColor(this, R.color.ads_bg_color)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR

        setContentView(binding.root)


        initData()
    }

    private fun initData() {
        mPackageName = intent.getStringExtra(KEY_PACKAGE_NAME)

        binding.ivAdsBack.setOnClickListener(this)
        val gridLayout = GridLayoutManager(this, 2, RecyclerView.VERTICAL, false)
        binding.recycler.layoutManager = gridLayout
        mRecommendAdapter = RecommendAdapter(this)
        binding.recycler.adapter = mRecommendAdapter
        mRecommendAdapter?.setOnIBridgeAllListener(this)
        loadData()
        mRecommendAdapter?.bindAllData(recommendList)
        Log.i(TAG, "recommendList=${recommendList.size} mPackageName=$mPackageName")

    }

    private fun loadData() {
        val packageNames = arrayOf(
            resources.getString(R.string.eq_package_name),
            resources.getString(R.string.music_package_name),
            resources.getString(R.string.gallery_package_name)
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