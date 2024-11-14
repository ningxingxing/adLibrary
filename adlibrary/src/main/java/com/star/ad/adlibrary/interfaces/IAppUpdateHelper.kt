package com.star.ad.adlibrary.interfaces

interface IAppUpdateHelper {

    fun onUpdateState(updateAvailability: Int) {}

    fun onUpdateProgress(progress: Long, maxSize: Long) {}

    fun onDownloadFinish() {}
}