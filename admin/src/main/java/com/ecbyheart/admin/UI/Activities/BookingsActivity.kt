package com.ecbyheart.admin.UI.Activities

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.ecbyheart.admin.Adapters.OrdersListAdapter
import com.ecbyheart.admin.R
import com.ecbyheart.admin.Services.AdminBookingsService
import core.Adapters.TextWatcherAdapter
import core.Listeners.ServiceListener
import core.Models.Order
import core.UI.BaseActivity
import core.Utils.AppLog
import core.Utils.CoreConstants
import java.text.SimpleDateFormat
import java.util.*
import kotlin.Comparator
import kotlin.collections.ArrayList

class BookingsActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bookings)
        setupComponents(this)
    }

    lateinit var bookingType: Spinner
    lateinit var search: EditText
    lateinit var adapter: OrdersListAdapter
    lateinit var listView: RecyclerView
    lateinit var cb_date_filter: CheckBox
    lateinit var filter_date: TextView

    override fun initializeComponents() {
        bookingType = findViewById(R.id.bookingType)
        listView = findViewById(R.id.listView)
        cb_date_filter = findViewById(R.id.cb_date_filter)
        filter_date = findViewById(R.id.filter_date)

        val list = ArrayList<Order>()
        adapter = OrdersListAdapter(
            this,
            list,
            R.layout.item_admin_order,
            object : OrdersListAdapter.OnItemClickListener {
                override fun onItemClick(position: Int) {
                    startActivity(
                        Intent(
                            this@BookingsActivity,
                            AdminOrderDetailsActivity::class.java
                        ).apply {
                            putExtra("order", list.get(position))
                        }
                    )
                }
            })
        listView.adapter = adapter

        search = findViewById(R.id.search)
        search.addTextChangedListener(
            TextWatcherAdapter(
                search,
                TextWatcherAdapter.TextWatcherListener { view, text ->
                    searchOrder(search.text.toString(), filter_date.text.toString())
                })
        )

        bookingType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                setData(0, position)
            }
        }

        cb_date_filter.isChecked = false
        cb_date_filter.setOnClickListener(View.OnClickListener {
            if (cb_date_filter.isChecked) // every day checked
            {

            }
            else
            {
                filter_date.text = ""
                searchOrder(search.text.toString(), "")
            }
        })

        filter_date.setOnClickListener {
            if (cb_date_filter.isChecked) {
                openDatePickerDialog(filter_date)
            }
        }
    }

    fun searchOrder(text : String, datestr : String) {
        adapter.filter(text, datestr)
    }

    fun setData(lastCount: Int = 0, status: Int) {
        AdminBookingsService.removeNewBookingsListener()
        AdminBookingsService.removeInprogressBookingsListener()
        when (status) {
            CoreConstants.NEW_ORDER -> {
                showLoader()
                AdminBookingsService.getNewBookings(object : ServiceListener<ArrayList<Order>, String?> {
                    override fun success(success: ArrayList<Order>) {
                        hideLoader()
                        success.sortWith(Comparator { o1: Order, o2: Order ->
                            o2.createdAt!!.time.compareTo(
                                o1.createdAt!!.time
                            )
                        })

                        adapter.setData(success)
                        filter_date.text = ""
                        search.setText("")
                    }

                    override fun error(error: String?) {
                        hideLoader()
                        toast(error)
                    }
                })
            }
//            CoreConstants.INPROGRESS_ORDER -> {
//                showLoader()
//                AdminBookingsService.getInprogressBookings(object :
//                    ServiceListener<ArrayList<Order>, String?> {
//                    override fun success(success: ArrayList<Order>) {
//                        hideLoader()
//                        success.sortWith(Comparator { o1: Order, o2: Order ->
//                            o2.createdAt!!.time.compareTo(
//                                o1.createdAt!!.time
//                            )
//                        })
//                        adapter.setData(success)
//                        filter_date.text = ""
//                        search.setText("")
//                    }
//
//                    override fun error(error: String?) {
//                        hideLoader()
//                        toast(error)
//                    }
//                })
//            }
            1 -> {
                showLoader()
                AdminBookingsService.getCompletedBookings(lastCount,
                    object : ServiceListener<ArrayList<Order>, String?> {
                        override fun success(success: ArrayList<Order>) {
                            hideLoader()
                            success.sortWith(Comparator { o1: Order, o2: Order ->
                                o2.createdAt!!.time.compareTo(
                                    o1.createdAt!!.time
                                )
                            })
                            adapter.setData(success)
                            filter_date.text = ""
                            search.setText("")
                        }

                        override fun error(error: String?) {
                            hideLoader()
                            toast(error)
                        }
                    })
            }
        }
    }

    override fun setupListeners() {
        setToolbar { onGoBack() }
        setTitle(getString(R.string.menu_booking))
    }

    override fun onDestroy() {
        AdminBookingsService.removeNewBookingsListener()
        AdminBookingsService.removeInprogressBookingsListener()
        super.onDestroy()
    }

    private fun openDatePickerDialog(textView:TextView) {
        val cal = Calendar.getInstance()
        val dialog = DatePickerDialog(
            this, R.style.myDatepickerDialog,
            DatePickerDialog.OnDateSetListener { _, year, i2, i3 ->

                var day = i3.toString()
                var _month = i2
                var month = (++_month).toString()
                day = if (day.length == 1) "0$day" else day
                month = if (month.length == 1) "0$month" else month

                Date()
                val stringDate = String.format("%s-%s-%s", day, month, year)
                val time = "00:00"
                val date = SimpleDateFormat(CoreConstants.DEFAULT_DATE_TIME, Locale.US).parse("$stringDate $time")
                AppLog.d(tag, "date: $stringDate")
//                searchDate = date
                textView.setText(stringDate)
                textView.setTag(date)

                // search booking
                searchOrder(search.text.toString(), stringDate)
            },
            cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)
        )

        dialog.show()
    }
}
