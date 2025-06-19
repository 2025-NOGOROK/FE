package com.example.nogorok.features.rest.longrest

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.nogorok.R

class RestItemAdapter(private var items: List<RestItem>) :
    RecyclerView.Adapter<RestItemAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val card: CardView = view.findViewById(R.id.cardRestItem)
        val icon: ImageView = view.findViewById(R.id.ivSymbol)
        val title: TextView = view.findViewById(R.id.itemTitle)
        val time: TextView = view.findViewById(R.id.itemTime)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_rest, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.title.text = item.title
        holder.time.text = "${item.startTime} - ${item.endTime}"

        val isRecommend = item.sourceType == "short-recommend" || item.sourceType == "long-recommend"

        // 배경 색상 적용
        holder.card.setBackgroundResource(
            if (isRecommend) R.drawable.bg_schedule_short_rest
            else R.drawable.bg_schedule_normal
        )

        // 텍스트 색상 적용
        val textColor = if (isRecommend) Color.parseColor("#FFFBEA") else Color.parseColor("#6A4A38")
        holder.title.setTextColor(textColor)
        holder.time.setTextColor(textColor)

        // 아이콘 설정
        holder.icon.visibility = View.VISIBLE
        holder.icon.setImageResource(if (isRecommend) R.drawable.comma else R.drawable.pin)
    }

    override fun getItemCount(): Int = items.size

    fun updateItems(newItems: List<RestItem>) {
        items = newItems
        notifyDataSetChanged()
    }
}
