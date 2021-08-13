package com.ecbyheart.admin.Adapters

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.api.load
import coil.transform.RoundedCornersTransformation
import com.ecbyheart.admin.R
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import core.Models.Order
import core.Models.Product
import core.UI.BaseActivity
import core.Utils.AppUtils
import core.Utils.CoreConstants
import java.util.*


class OrdersListAdapter(
    var activity: BaseActivity,
    private var items: ArrayList<Order>,
    var resources: Int,
    private val listener: OnItemClickListener
) :
    RecyclerView.Adapter<OrdersListAdapter.ViewHolder>() {
    var backup: ArrayList<Order> = ArrayList<Order>(items)

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
        private val name: TextView = itemView.findViewById(R.id.title)
        private val price: TextView = itemView.findViewById(R.id.price)
        private val time: TextView = itemView.findViewById(R.id.time)
        private val address: TextView? = itemView.findViewById(R.id.address)
        private val image: ImageView? = itemView.findViewById(R.id.image)

        //        private val call: ImageView = itemView.findViewById(R.id.call)
//        private val msg: ImageView = itemView.findViewById(R.id.msg)
        fun bind(
            position: Int,
            listener: OnItemClickListener?
        ) {
            val order = items[position]
//            var title = ""
//            for ((index, product) in order.products.withIndex()) {
//                title += product.product!!.title + ","
//                if (index == order.products.size)
//                    title.removeSuffix(",")
//            }
//            name.text = title
            name.text = order.no
            price.text = "$${AppUtils.formatNumber(order.total)}"
            if(order.order_date != null)
            {
                time.text = AppUtils.formatDate(CoreConstants.DATE_TIME, Date(order.order_date!!))
            }
            else {
                time.text = ""
            }

            address?.text = /*${order.customer?.region}, */"${order.customer?.address}"
//            image?.load(order.products.first().product!!.image) {
//                transformations(RoundedCornersTransformation(25f))
//            }
            if (image != null)
                Glide.with(image.context).load(order.products.first().product!!.image)
                    .transform(
                        CenterCrop(),
                        RoundedCorners(25)
                    ).apply(
                        RequestOptions()
                            .placeholder(R.drawable.placeholder)
                            .error(R.drawable.placeholder)
                    ).into(image)
            /*when (order.status) {
                CoreConstants.NEW_ORDER -> {
                    call.visibility = View.GONE
                    msg.visibility = View.GONE
                }
                CoreConstants.INPROGRESS_ORDER -> {
                    call.visibility = View.VISIBLE
                    msg.visibility = View.VISIBLE
                    //Need to implement call msg functionality
                    call.setOnClickListener {
                        val intent = Intent(Intent.ACTION_DIAL)
                        intent.data = Uri.parse("tel:" + order.driver?.phone)
                        activity.startActivity(intent)
                    }
                    msg.setOnClickListener {
                        val sms_uri: Uri = Uri.parse("smsto:${order.driver?.phone}")
                        val sms_intent = Intent(Intent.ACTION_SENDTO, sms_uri)
//                        sms_intent.putExtra("sms_body", "Good Morning ! how r U ?")
                        activity.startActivity(sms_intent)
                    }
                }
                CoreConstants.COMPLETED_ORDER -> {
                    call.visibility = View.GONE
                    msg.visibility = View.GONE
                }
            }*/
            itemView.setOnClickListener { listener?.onItemClick(position) }
        }

    }

    fun addAll(collection: Array<Order>) {
        items.addAll(collection)
        backup.addAll(collection)
        notifyDataSetChanged()
    }

    fun addAll(collection: Collection<Order>) {
        items.addAll(collection)
        backup.addAll(collection)
        notifyDataSetChanged()
    }

    fun setData(collection: Collection<Order>) {
        clear()
        addAll(collection)
    }

    fun clear() {
        items.clear()
        backup.clear()
        notifyDataSetChanged()
    }

    fun filter(charText: String, datestr : String) {
        var charText = charText
        charText = charText.toLowerCase()
        items.clear()
        if (charText.isEmpty() && datestr.isEmpty()) {
            items.addAll(backup)
        }
        else if (charText.isNotEmpty() && datestr.isEmpty()) {
            for (item in backup) {
                if (item.no != null)
                {
                    if (item.no!!.toLowerCase().contains(charText)) {
                        items.add(item)
                    }
                }
            }
        }
        else if (charText.isEmpty() && datestr.isNotEmpty()) {
            for (item in backup) {
                if(item.order_date != null)
                {
                    val order_date_str = AppUtils.formatDate(CoreConstants.DATE, Date(item.order_date!!))
                    if (order_date_str == datestr) {
                        items.add(item)
                    }
                }
            }
        }
        else {
            for (item in backup) {
                if(item.no != null && item.order_date != null)
                {
                    val order_date_str = AppUtils.formatDate(CoreConstants.DATE, Date(item.order_date!!))
                    if ((order_date_str == datestr) && (item.no!!.toLowerCase().contains(charText))) {
                        items.add(item)
                    }
                }
            }
        }
        notifyDataSetChanged()
    }
}