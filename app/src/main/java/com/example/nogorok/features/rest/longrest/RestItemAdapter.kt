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

        when (item.sourceType) {
            "short-recommend" -> {
                holder.card.setBackgroundResource(R.drawable.bg_schedule_short_rest)
                val textColor = Color.parseColor("#FFFBEA")
                holder.title.setTextColor(textColor)
                holder.time.setTextColor(textColor)
                holder.icon.setImageResource(R.drawable.comma)
            }

            "long-recommend" -> {
                holder.card.setBackgroundResource(R.drawable.bg_schedule_long_rest)
                val textColor = Color.parseColor("#73605A")
                holder.title.setTextColor(textColor)
                holder.time.setTextColor(textColor)
                holder.icon.setImageResource(R.drawable.comma)
            }

            else -> {
                holder.card.setBackgroundResource(R.drawable.bg_schedule_normal)
                val textColor = Color.parseColor("#6A4A38")
                holder.title.setTextColor(textColor)
                holder.time.setTextColor(textColor)
                holder.icon.setImageResource(R.drawable.pin)
            }
        }

        holder.icon.visibility = View.VISIBLE
    }


    override fun getItemCount(): Int = items.size

    fun updateItems(newItems: List<RestItem>) {
        items = newItems
        notifyDataSetChanged()
    }
}
