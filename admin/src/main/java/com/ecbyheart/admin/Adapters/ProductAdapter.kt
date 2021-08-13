package com.ecbyheart.admin.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.api.load
import coil.transform.RoundedCornersTransformation
import com.ecbyheart.admin.R
import core.Models.Product
import core.Utils.AppUtils
import java.util.*

class ProductAdapter(
    private var items: ArrayList<Product>,
    var resources: Int,
    var showSalesCnt : Boolean,
    private val listener: OnItemClickListener
) :
    RecyclerView.Adapter<ProductAdapter.ViewHolder>() {
    var backup: ArrayList<Product> = ArrayList<Product>(items)

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val v =
            LayoutInflater.from(parent.context).inflate(resources, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {
        holder.bind(position, listener)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    inner class ViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        private val title: TextView = itemView.findViewById(R.id.title)
        private val desc: TextView = itemView.findViewById(R.id.desc)
        private val price: TextView = itemView.findViewById(R.id.price)
        private val image: ImageView = itemView.findViewById(R.id.image)
        private val salescnt: TextView = itemView.findViewById(R.id.salescnt)
        fun bind(
            position: Int,
            listener: OnItemClickListener?
        ) {
            val product = items[position]
            title.text = product.title
            desc.text = product.desc
            price.text = "$${AppUtils.formatNumber(product.price)}"

            if(showSalesCnt) {
                salescnt.visibility = View.VISIBLE
                var sales_cnt = 0
                if (product.salesCnt != null) {
                    sales_cnt =  product.salesCnt!!
                }
                salescnt.text = "${sales_cnt}"
            }
            else {
                salescnt.visibility = View.GONE
            }

            var product_image = ""
            if(product.photos != null && product.photos!!.size > 0) {
                product_image = product.photos!!.first()
            }
            image.load(product_image) {
                transformations(RoundedCornersTransformation(25f))
            }
            itemView.setOnClickListener { listener?.onItemClick(position) }
        }

    }

    fun addAll(collection: Array<Product>) {
        items.addAll(collection)
        backup.addAll(collection)
        notifyDataSetChanged()
    }

    fun addAll(collection: Collection<Product>) {
        items.addAll(collection)
        backup.addAll(collection)
        notifyDataSetChanged()
    }

    fun setData(collection: Collection<Product>) {
        clear()
        addAll(collection)
    }

    fun clear() {
        items.clear()
        backup.clear()
        notifyDataSetChanged()
    }

    fun filter(charText: String) {
        var charText = charText
        charText = charText.toLowerCase()
        items.clear()
        if (charText.isEmpty()) {
            items.addAll(backup)
        } else {
            for (item in backup) {
                if (item.title!!.toLowerCase().contains(charText)) {
                    items.add(item)
                }
            }
        }
        notifyDataSetChanged()
    }
}