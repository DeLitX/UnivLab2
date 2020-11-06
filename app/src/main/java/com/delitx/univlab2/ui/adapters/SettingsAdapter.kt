package com.delitx.univlab2.ui.adapters

import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.delitx.univlab2.R

class SettingsAdapter :
    ListAdapter<SettingsAdapter.SettingsPair, SettingsAdapter.SettingsViewHolder>(object :
        DiffUtil.ItemCallback<SettingsPair>() {
        override fun areItemsTheSame(
            oldItem: SettingsPair,
            newItem: SettingsPair
        ): Boolean {
            return oldItem.key == newItem.key &&
                    oldItem.value == newItem.value
        }

        override fun areContentsTheSame(
            oldItem: SettingsPair,
            newItem: SettingsPair
        ): Boolean {
            return oldItem.key == newItem.key &&
                    oldItem.value == newItem.value
        }

    }),SettingsHelper {
    class SettingsViewHolder(private val v: View) : RecyclerView.ViewHolder(v) {
        private val mName: TextView = v.findViewById(R.id.parameter_key)
        private val mValue: EditText = v.findViewById(R.id.parameter_value)
        private var mItem: SettingsPair? = null

        init {
            mValue.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                }

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    mItem?.value = p0.toString()
                }

                override fun afterTextChanged(p0: Editable?) {

                }

            })
        }

        fun bind(item: SettingsPair) {
            mItem = item
            mName.text = item.key+":"
            mValue.setText(item.value)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SettingsViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.settings_item_layout, parent,false)
        return SettingsViewHolder(view)
    }

    override fun onBindViewHolder(holder: SettingsViewHolder, position: Int) {
        holder.bind(currentList[position])
    }

    data class SettingsPair(var key: String, var value: String)

    override fun changeSetting(condition: SettingsPair) {
        val list=currentList
        list[list.indexOfFirst {it.key== condition.key }]=condition
        submitList(list)
    }
}
interface SettingsHelper{
    fun changeSetting(condition:SettingsAdapter.SettingsPair)
}