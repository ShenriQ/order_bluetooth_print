package core.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.app.fooddeliverysystem.R
import core.Models.CartItem
import core.Utils.AppUtils
import java.util.*

class OrderProductsAdapter(
    private var items: ArrayList<CartItem>,
    var resources: Int,
    private val listener: OnItemClickListener?
) :
    RecyclerView.Adapter<OrderProductsAdapter.ViewHolder>() {
    var backup: ArrayList<CartItem> = ArrayList<CartItem>(items)

    interface OnItemClickListener {
        fun onItemClick(position: Int)
        fun onItemDelete(position: Int)
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
        private val price: TextView = itemView.findViewById(R.id.price)
        private val subp_title: TextView = itemView.findViewById(R.id.subp_title)
        private val quantity: TextView = itemView.findViewById(R.id.quantity)
        fun bind(
            position: Int,
            listener: OnItemClickListener?
        ) {
            val cartItem = items[position]
            val product = cartItem.product!!
            title.text = product.title

            quantity.text = "${cartItem.quantity} x "
            price.text = if (product.isDiscounted()) {
                "$${AppUtils.formatNumber(product.discountedPrice())}"
            } else {
                "$${AppUtils.formatNumber(product.price) }"
            }
//            subp_title.text = cartItem.subProduct!!.catId!!.split("=@=")[0] + " / " + cartItem.subProduct!!.title
            subp_title.text = cartItem.subProduct!!.title
        }

    }

    fun addAll(collection: Array<CartItem>) {
        items.addAll(collection)
        backup.addAll(collection)
        notifyDataSetChanged()
    }

    fun addAll(collection: Collection<CartItem>) {
        items.addAll(collection)
        backup.addAll(collection)
        notifyDataSetChanged()
    }

    fun setData(collection: Collection<CartItem>) {
        clear()
        addAll(collection)
    }

    fun delete(position: Int) {
        items.removeAt(position)
//        notifyItemRemoved(position)
        notifyDataSetChanged()
    }

    fun clear() {
        items.clear()
        backup.clear()
        notifyDataSetChanged()
    }

    /*fun filter(charText: String) {
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
    }*/
}