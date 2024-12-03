package com.star.ad.adlibrary

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.star.ad.adlibrary.utils.ScreenUtils
import com.star.ad.adlibrary.model.RecommendData
import com.star.ad.adlibrary.utils.DensityUtil


class RecommendAdapter(private val context: Context) :
    RecyclerView.Adapter<RecommendAdapter.ViewHolder?>() {

    private var recommendList: MutableList<RecommendData> = ArrayList()
    private var isEditMode = false
    private var mItemWidth = 100

    companion object {
        private const val TAG = "RecommendAdapter"
    }

    init {
        mItemWidth = ScreenUtils.getScreenWidth(context) / 3
        Log.i(TAG, "mItemWidth=$mItemWidth")
    }

    fun bindAllData(data: MutableList<RecommendData>) {
        this.recommendList = data
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

        val recommendData = recommendList[position]

        holder.tvTitle.setText(recommendData.title)
        Glide.with(context)
            .asBitmap()
            .load(recommendData.icon)
            .into(holder.ivIcon)

    }

    override fun getItemCount(): Int {
        return if (recommendList.isEmpty()) {
            0
        } else {
            recommendList.size
        }
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tvTitle: AppCompatTextView
        var ivIcon: AppCompatImageView
        var llRecommend: LinearLayoutCompat


        init {
            tvTitle = itemView.findViewById(R.id.tv_adapter_ads_title)
            ivIcon = itemView.findViewById(R.id.iv_adapter_ads_icon)
            llRecommend = itemView.findViewById(R.id.ll_adapter_recommend)
            itemView.setOnClickListener {
                onAllItemClickListener?.onItemClick(layoutPosition, recommendList[adapterPosition])
            }

            itemView.setOnLongClickListener {
                onAllItemClickListener?.onLongClick(layoutPosition, recommendList)

                false
            }
           // setViewSize(llRecommend)
        }

        private fun setViewSize(view: View) {
            val layoutParams = view.layoutParams as ConstraintLayout.LayoutParams
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
        fun onItemClick(position: Int, recommendData: RecommendData)

        fun onLongClick(position: Int, list: MutableList<RecommendData>)
    }
}