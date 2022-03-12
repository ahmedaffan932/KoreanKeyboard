package com.example.koreankeyboard.classes

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.koreankeyboard.R
import com.example.koreankeyboard.interfaces.InterfaceOnClickSuggestion

class SuggestionsAdapter (
    private val arr: List<String>,
    private val callBack: InterfaceOnClickSuggestion
) : RecyclerView.Adapter<SuggestionsAdapter.ItemHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.suggestion_text_list_item, parent, false)
        return ItemHolder(view)
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onBindViewHolder(holder: ItemHolder, position: Int) {
        holder.tv.text = arr[position]
        holder.tv.setOnClickListener {
            callBack.onClick(arr[position])
        }
    }

    override fun getItemCount(): Int {
        return arr.size
    }


    override fun getItemViewType(position: Int): Int {
        return position
    }

    open class ItemHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tv = itemView.findViewById<TextView>(R.id.tvSuggestions)
    }
}