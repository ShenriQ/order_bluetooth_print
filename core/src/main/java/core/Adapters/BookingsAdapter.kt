package core.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.app.fooddeliverysystem.R
import java.util.*

import core.Models.Booking
import core.Models.ServiceTimeSlot
import core.Utils.CoreConstants

class BookingsAdapter(
    private var items: ArrayList<Booking>,
    var resources: Int,
    private val listener: OnItemClickListener?
) :
    RecyclerView.Adapter<BookingsAdapter.ViewHolder>() {
    var backup: ArrayList<Booking> = ArrayList<Booking>(items)

    interface OnItemClickListener {
        fun onEmptyList()
        fun onTimeSlotDelete(timeslot: ServiceTimeSlot)
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

        fun bind(
            position: Int,
            listener: OnItemClickListener?
        ) {
            val bookingItem = items[position]
            val service = bookingItem.booking_service
            title.text = service!!.title
            price.text = if (service.isDiscounted() && service.discountedPrice() != service.price) {
                "$${service.discountedPrice()}"
            } else {
                "$${service.price}"
            }

            bookingItem.booking_dates!!.forEach { dateItem ->
                dateItem.timeslots!!.forEach { timeslotItem ->

                    subp_title.text = String.format("%s / %s - %s", dateItem.name,
                        CoreConstants.getTimeString(
                            timeslotItem.start_hour!!,
                            timeslotItem.start_min!!
                        ),
                        CoreConstants.getTimeString(
                            timeslotItem.end_hour!!,
                            timeslotItem.end_min!!
                        )
                    )

                }
            }

        }

    }

    fun addAll(collection: Array<Booking>) {
        items.addAll(collection)
        backup.addAll(collection)
        notifyDataSetChanged()
    }

    fun addAll(collection: Collection<Booking>) {
        items.addAll(collection)
        backup.addAll(collection)
        notifyDataSetChanged()
    }

    fun setData(collection: Collection<Booking>) {
        clear()
        addAll(collection)
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