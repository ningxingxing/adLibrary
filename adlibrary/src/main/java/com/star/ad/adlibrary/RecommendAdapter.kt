package com.star.ad.adlibrary

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.RecyclerView
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.utils.DensityUtil
import com.star.ad.adlibrary.utils.ScreenUtils
import com.star.ad.adlibrary.model.RecommendData
import com.star.ad.adlibrary.utils.DensityUtil


/**
 * @author：luck
 * @date：2016-12-11 17:02
 * @describe：PictureAlbumDirectoryAdapter
 */
class RecommendAdapter(private val context: Context) :
    RecyclerView.Adapter<RecommendAdapter.ViewHolder?>() {

    private var albumList: MutableList<RecommendData> = ArrayList()
    private var isEditMode = false
    private var mItemWidth = 100
    private var mSize = 100

    companion object {
        private const val TAG = "GalleryAllAdapter"
    }

    init {
        mItemWidth = ScreenUtils.getScreenWidth(context) / 4
        mSize = DensityUtil.dip2px(context, 50f)
        Log.i(TAG, "mItemWidth=$mItemWidth")
    }

    fun bindAllData(albumList: MutableList<RecommendData>?) {
        this.albumList = ArrayList(albumList)
        notifyDataSetChanged()
    }

    fun setShowMode(isEditMode: Boolean) {
        this.isEditMode = isEditMode
        notifyDataSetChanged()
    }

    fun updateSelect(position: Int, localMedia: RecommendData) {

        notifyItemChanged(position)
    }


    fun getShowMode(): Boolean {
        return isEditMode
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.adapter_recommend, parent, false)
        return ViewHolder(itemView)
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val localMedia = albumList[position]



//
//        Glide.with(context)
//            .asBitmap()
//            .load(R.drawable.ic_cover)
//            .transform(CenterCrop(), RoundedCorners(8))
//            .into(holder.ivAllCover)


    }

    override fun getItemCount(): Int {
        return if (albumList.isEmpty()) {
            0
        } else {
            albumList.size
        }
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var ivAllImage: AppCompatImageView
        var ivEdit: AppCompatImageView
        var ivAllCover: AppCompatImageView


        init {
            ivAllImage = itemView.findViewById(R.id.iv_adapter_all_image)
            ivEdit = itemView.findViewById(R.id.iv_edit)
            ivAllCover = itemView.findViewById(R.id.iv_all_cover)
            itemView.setOnClickListener {
                onAllItemClickListener?.onItemClick(layoutPosition, albumList)
            }

            itemView.setOnLongClickListener {
                onAllItemClickListener?.onLongClick(layoutPosition, albumList)

                false
            }
            setViewSize(ivAllImage)
            setViewSize(ivAllCover)
        }

        private fun setViewSize(view: View) {
            val layoutParams = view.layoutParams as RelativeLayout.LayoutParams
            layoutParams.width = mItemWidth
            layoutParams.height = mItemWidth
            view.layoutParams = layoutParams
        }
    }

    private var onAllItemClickListener: OnAllItemClickListener? = null

    /**
     * 专辑列表桥接类
     *
     * @param listener
     */
    fun setOnIBridgeAllListener(listener: OnAllItemClickListener?) {
        onAllItemClickListener = listener
    }

    interface OnAllItemClickListener {
        fun onItemClick(position: Int, list: MutableList<LocalMedia>)

        fun onLongClick(position: Int, list: MutableList<LocalMedia>)
    }
}