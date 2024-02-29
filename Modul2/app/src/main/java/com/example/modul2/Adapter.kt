package com.example.modul2

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.modul2.databinding.RvItemBinding

class Adapter(private var mangaList: ArrayList<Manga>):RecyclerView.Adapter<Adapter.MyViewHolder>() {
    private var onClickListener: OnClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(
            RvItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun getItemCount(): Int {
        return mangaList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentManga = mangaList[position]

        holder.apply {
            Glide.with(holder.itemView.context)
                .load(currentManga.image)
                .centerInside().into(ivManga)

            tvTitle.text = currentManga.title
            tvChapter.text = currentManga.chapter
            tvRating.text = currentManga.rating
        }

        holder.itemView.setOnClickListener {
            if(onClickListener != null){
                onClickListener!!.onClick(position, currentManga)
            }
        }
    }

    fun setOnClickListener(onClickListener: OnClickListener){
        this.onClickListener = onClickListener
    }

    interface OnClickListener{
        fun onClick(position: Int, model: Manga)
    }

    inner class MyViewHolder(binding: RvItemBinding):RecyclerView.ViewHolder(binding.root) {
        val ivManga = binding.ivManga
        val tvTitle = binding.tvTitle
        val tvRating = binding.tvRating
        val tvChapter = binding.tvChapters
    }
}