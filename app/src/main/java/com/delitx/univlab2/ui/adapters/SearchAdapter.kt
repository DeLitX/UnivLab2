package com.delitx.univlab2.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.delitx.univlab2.R
import com.delitx.univlab2.data.Item
import java.lang.Exception

class SearchAdapter : ListAdapter<Item, SearchAdapter.SearchViewHolder>(object :
    DiffUtil.ItemCallback<Item>() {
    override fun areItemsTheSame(oldItem: Item, newItem: Item): Boolean {
        if (oldItem.values.size != newItem.values.size) {
            return false
        }
        try {
            for (i in oldItem.values) {
                if (i.value != newItem.values[i.key]) {
                    return false
                }
            }
        } catch (e: Exception) {
            return false
        }
        return true
    }

    override fun areContentsTheSame(oldItem: Item, newItem: Item): Boolean {
        return areItemsTheSame(oldItem, newItem)
    }

}) {
    class SearchViewHolder(private val v: View) : RecyclerView.ViewHolder(v) {
        private val text: TextView = v.findViewById(R.id.item_text)
        fun bind(item: Item) {
            text.text = item.toString()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchViewHolder {
        return SearchViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.search_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: SearchViewHolder, position: Int) {
        holder.bind(currentList[position])
    }
}