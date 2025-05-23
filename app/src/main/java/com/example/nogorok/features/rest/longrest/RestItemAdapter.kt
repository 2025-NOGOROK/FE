package com.example.nogorok.features.rest.longrest

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.nogorok.R
import com.example.nogorok.features.rest.longrest.RestItem

class RestItemAdapter(private val items: List<RestItem>) :
    RecyclerView.Adapter<RestItemAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val icon: TextView = view.findViewById(R.id.itemIcon)
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
        holder.icon.text = item.icon
        holder.title.text = item.title
        holder.time.text = item.time
    }

    override fun getItemCount(): Int = items.size
}
