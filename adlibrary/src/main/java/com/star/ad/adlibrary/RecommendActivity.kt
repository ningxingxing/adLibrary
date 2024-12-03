package com.star.ad.adlibrary

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.star.ad.adlibrary.databinding.ActivityRecommendBinding

class RecommendActivity : AppCompatActivity(), View.OnClickListener {

    private val binding: ActivityRecommendBinding by lazy {
        ActivityRecommendBinding.inflate(LayoutInflater.from(this))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        initData()
    }

    private fun initData() {
        binding.ivAdsBack.setOnClickListener(this)


    }

    override fun onClick(v: View) {
        when (v.id) {

            R.id.iv_ads_back -> {

                finish()
            }

        }
    }

}