package com.ecbyheart.admin.UI.Activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ecbyheart.admin.R
import com.ecbyheart.admin.Services.AdminBookingsService
import com.ecbyheart.admin.Services.AdminOrderService
import core.Adapters.BookingsAdapter
import core.Adapters.OrderProductsAdapter
import core.Listeners.ServiceListener
import core.Models.Order
import core.Services.OrderService
import core.UI.BaseActivity
import core.Utils.AppUtils
import core.Utils.CoreConstants
import java.util.*

class AdminOrderDetailsActivity : BaseActivity() {

    lateinit var order: Order
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detailed_order_details)
        if (intent.hasExtra("order"))
            order = intent.getSerializableExtra("order") as Order
        setupComponents(this)
    }

    lateinit var listView: RecyclerView
    lateinit var date: TextView
    lateinit var time: TextView
    lateinit var dateLabel: TextView
    lateinit var timeLabel: TextView
    lateinit var orderQuantitiy:TextView
    lateinit var paymentMethod: TextView
    lateinit var orderNo:TextView
    lateinit var customerName: TextView
    lateinit var phone: TextView
    lateinit var deliveryAddress: TextView
    lateinit var total: TextView
    lateinit var confirm: Button
    lateinit var complete: Button
    lateinit var order_note: TextView
    override fun initializeComponents() {
        listView = findViewById(R.id.listView)
        date = findViewById(R.id.date)
        time = findViewById(R.id.time)
        customerName = findViewById(R.id.customerName)
        phone = findViewById(R.id.phone)
        deliveryAddress = findViewById(R.id.deliveryAddress)
        dateLabel = findViewById(R.id.dateLabel)
        timeLabel = findViewById(R.id.timeLabel)
        orderQuantitiy = findViewById(R.id.orderQuantitiy)
        paymentMethod = findViewById(R.id.paymentMethod)
        orderNo = findViewById(R.id.orderNo)
        total = findViewById(R.id.total)
        confirm = findViewById(R.id.confirm)
        complete = findViewById(R.id.complete)
        order_note = findViewById(R.id.order_note)

        if(order.ifbooking == true) {
            listView.adapter = BookingsAdapter(order.bookings, R.layout.item_booking_detail, null)
        }
        else {
            listView.adapter = OrderProductsAdapter(order.products, R.layout.item_order_details, null)
        }

        if (order.order_date != null)
        {
            date.setText(AppUtils.formatDate(CoreConstants.DATE, Date(order.order_date!!)))
            time.setText(AppUtils.formatDate(CoreConstants.TIME, Date(order.order_date!!)))
        }
        else {
            date.setText("")
            time.setText("")
        }

        customerName.setText(order.customer?.name)
        phone.setText(order.customer?.phone)
        deliveryAddress.setText("${order.customer?.address}, ${order.customer?.city}")
        total.setText("$${AppUtils.formatNumber(order.total)}")
        orderNo.setText(order.no)
        order_note.setText(order.order_note)
        var quantity = 0
        for (product in order.products) {
            quantity += product.quantity
        }
        orderQuantitiy.setText(quantity.toString())
        paymentMethod.setText(
            if (order.cod)
                getString(R.string.cash_on_delivery)
            else
                getString(R.string.card)
        )
    }

    override fun setupListeners() {
        setToolbar { onGoBack() }

        when (order.status) {
            CoreConstants.NEW_ORDER -> {
                setTitle(getString(R.string.order_details))
                confirm.text = "打印"
                confirm.setOnClickListener {
                    goPrintPage()
                }
                complete.setOnClickListener {
                    completeOrder()
                }
            }
            CoreConstants.INPROGRESS_ORDER -> {
                setTitle(getString(R.string.order_details))
                confirm.text = "打印"
                confirm.setOnClickListener {
                    goPrintPage()
                }
                complete.setOnClickListener {
                    completeOrder()
                }
            }
            CoreConstants.COMPLETED_ORDER -> {
                setTitle(getString(R.string.order_details))
                confirm.text = "打印"
                confirm.setOnClickListener {
                    goPrintPage()
                }
                complete.text = "確認完成"
                complete.isEnabled = false
                //overriding time from order delivery time to order complete time
//                dateLabel.setText(getString(R.string.completed_date))
//                timeLabel.setText(getString(R.string.completed_time))
//                time.setText(AppUtils.formatDate(CoreConstants.TIME, order.updatedAt))
            }
        }
    }

    fun goPrintPage(){
        startActivity(
            Intent(
                this@AdminOrderDetailsActivity,
                PrintActivity::class.java
            ).apply {
                putExtra("order", order)
            }
        )
    }

    fun completeOrder() {
        if(order.ifbooking == true) {
            showLoader()
            AdminBookingsService.UpdateBookingStatus(
                order,
                CoreConstants.COMPLETED_ORDER,
                object : ServiceListener<String?, String?> {
                    override fun success(success: String?) {
                        hideLoader()
                        complete.text = "已完成"
                        complete.isEnabled = false
                    }

                    override fun error(error: String?) {
                        hideLoader()
                        toast(error)
                    }
                })
        }
        else {
            showLoader()
            AdminOrderService.UpdateOrderStatus(
                order,
                CoreConstants.COMPLETED_ORDER,
                object : ServiceListener<String?, String?> {
                    override fun success(success: String?) {
                        hideLoader()
                        complete.text = "已完成"
                        complete.isEnabled = false
                    }

                    override fun error(error: String?) {
                        hideLoader()
                        toast(error)
                    }
                })
        }
    }
}
