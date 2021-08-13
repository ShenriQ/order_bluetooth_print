package com.ecbyheart.admin.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ecbyheart.admin.R

class AdminHomeAdapter(
    var items: ArrayList<String>,
    val resources: Int,
    val listener: OnItemClickListener?
) : RecyclerView.Adapter<AdminHomeAdapter.ViewHolder>() {

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }

//    val items: List<String>? = items

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v =
            LayoutInflater.from(parent.context).inflate(resources, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items, position, listener)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    class ViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        private val name: TextView
        fun bind(items: ArrayList<String>, position: Int, listener: OnItemClickListener?) {
            name.setText(items.get(position));
            itemView.setOnClickListener { listener?.onItemClick(position) }
        }

        init {
            name = itemView.findViewById<View>(R.id.name) as TextView
        }
    }

    fun addAll(collection: Collection<String>) {
        items.addAll(collection)
        notifyDataSetChanged()
    }

    fun setData(collection: Collection<String>) {
        clear()
        addAll(collection)
    }

    fun clear() {
        items.clear()
        notifyDataSetChanged()
    }
}