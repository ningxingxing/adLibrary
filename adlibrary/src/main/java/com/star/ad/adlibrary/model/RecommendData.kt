package com.star.ad.adlibrary.model

data class RecommendData(
    val title: Int,
    val icon: Int,
    val isHot: Boolean,
    val url:String = ""
)
