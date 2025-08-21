package com.example.nogorok.features.home.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.nogorok.databinding.ItemStressCardBinding
import com.example.nogorok.features.home.StressWebActivity
import com.example.nogorok.features.home.model.StressCard

class StressCardAdapter(
    private val items: List<StressCard>
) : RecyclerView.Adapter<StressCardAdapter.StressCardViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StressCardViewHolder {
        val binding = ItemStressCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return StressCardViewHolder(binding)
    }

    override fun onBindViewHolder(holder: StressCardViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    inner class StressCardViewHolder(private val binding: ItemStressCardBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: StressCard) {
            binding.txtTitle.text = item.title
            Glide.with(binding.imgThumbnail.context)
                .load(item.imageUrl)
                .into(binding.imgThumbnail)

            // 카드 클릭 시 웹뷰로 이동
            binding.root.setOnClickListener {
                val context = binding.root.context
                val intent = Intent(context, StressWebActivity::class.java)
                intent.putExtra("linkUrl", item.linkUrl)
                context.startActivity(intent)
            }
        }
    }
}
