package com.ecbyheart.admin.UI.Activities.Fragment

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.RelativeLayout
import android.widget.TextView
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.BarLineChartBase
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IAxisValueFormatter
import com.google.firebase.firestore.FirebaseFirestore
import com.ecbyheart.admin.R
import com.ecbyheart.admin.Services.AdminOrderService
import com.ecbyheart.admin.UI.Activities.ReportActivity
import core.Listeners.ServiceListener
import core.Models.EarningStats
import core.Models.Order
import core.Models.StatsEntry
import core.UI.BaseFragment
import core.Utils.AppUtils
import core.Utils.CoreConstants
import java.util.*
import kotlin.collections.ArrayList


class ReportFragment : BaseFragment() {
    private class LabelFormatter internal constructor(
        var chart: BarLineChartBase<*>,
        var labels: ArrayList<String>
    ) :
        IAxisValueFormatter {
        override fun getFormattedValue(value: Float, axis: AxisBase): String {
            print("======================value : $value")
            if (value.toInt() < 0) return ""
            if (value.toInt() >= labels.size) return ""
            return labels[value.toInt()]
        }

    }

    private val db = FirebaseFirestore.getInstance()
    private val metaCollection = db.collection(CoreConstants.meta_Coll )
    private val userStatsCollection = db.collection(CoreConstants.userOrderStats_Coll )
    private val productStatsCollection = db.collection(CoreConstants.productStats_Coll )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_report, container, false)
        return root
    }

    var report_type = "order"
    var item_id = ""

    var view_type = "day"
    var cur_date_str = ""
    var cur_date = Calendar.getInstance()

    lateinit var prev_btn : ImageButton
    lateinit var next_btn : ImageButton
    lateinit var cur_date_txt : TextView
    lateinit var chart: BarChart
    lateinit var order_total: TextView
    lateinit var booking_total : TextView
    lateinit var buy_total : TextView
    lateinit var sales_total : TextView

    lateinit var order_view : RelativeLayout
    lateinit var booking_view : RelativeLayout
    lateinit var sales_view : RelativeLayout
    lateinit var buy_view : RelativeLayout

    val groupSpace = 0.06f
    val barSpace = 0.02f // x2 dataset
    val barWidth = 0.45f // x2 dataset
    // (0.02 + 0.45) * 2 + 0.06 = 1.00 -> interval per "group"

    var xLabels = ArrayList<String>()

    override fun initializeComponents(rootView: View) {
        prev_btn = rootView.findViewById(R.id.prev_btn)
        next_btn = rootView.findViewById(R.id.next_btn)
        cur_date_txt = rootView.findViewById(R.id.cur_date)
        chart = rootView.findViewById(R.id.chart)
        order_total = rootView.findViewById(R.id.order_total)
        booking_total = rootView.findViewById(R.id.booking_total)
        buy_total = rootView.findViewById(R.id.buy_total)
        sales_total = rootView.findViewById(R.id.sales_total)

        order_view = rootView.findViewById(R.id.order_view)
        booking_view = rootView.findViewById(R.id.booking_view)
        sales_view = rootView.findViewById(R.id.sales_view)
        buy_view = rootView.findViewById(R.id.buy_view)

        order_view.visibility = View.GONE
        booking_view.visibility = View.GONE
        sales_view.visibility = View.GONE
        buy_view.visibility = View.GONE

        if (report_type == "order") {
            order_view.visibility = View.VISIBLE
            booking_view.visibility = View.VISIBLE
        }
        else if (report_type == "user") {
            buy_view.visibility = View.VISIBLE
        }
        else if (report_type == "product") {
            sales_view.visibility = View.VISIBLE
        }

        chart.setData(BarData(BarDataSet(ArrayList<BarEntry>(), ""), BarDataSet(ArrayList<BarEntry>(), "")))
        chart.groupBars(0f, groupSpace, barSpace) // perform the "explicit" grouping
        chart.description.isEnabled = false
        chart.setFitBars(true)
        chart.isDoubleTapToZoomEnabled = false

        chart.xAxis.setCenterAxisLabels(true)
        chart.xAxis.position = XAxis.XAxisPosition.BOTTOM
        chart.xAxis.setDrawGridLines(false)
        chart.xAxis.setAxisMinimum(0f)
        chart.xAxis.setAxisMaximum(5f)
        chart.xAxis.granularity = 1f

        chart.axisLeft.valueFormatter = IAxisValueFormatter { value, axis -> "$${value.toInt()}" }
        chart.axisLeft.setDrawZeroLine(true)
        //chart.axisLeft.setDrawGridLines(false)
        chart.axisRight.isEnabled = false
        chart.animateXY(2000, 2000)

        chart.invalidate()

        setupView()
    }

    override fun setupListeners(rootView: View) {
        prev_btn.setOnClickListener{
            updateCurDate(false)
        }
        next_btn.setOnClickListener{
            updateCurDate(true)
        }
    }

    fun setCurDateStr(date : Calendar){
        if (view_type == "day") {
            val today_str = AppUtils.formatDate("yyyy-MM-dd", Date())
            cur_date_str = AppUtils.formatDate("yyyy-MM-dd", date.time)
            if (cur_date_str == today_str) {
                cur_date_txt.text = "Today"
            }
            cur_date_txt.text =  cur_date_str
        }
        else if (view_type == "month") {
            val today_str = AppUtils.formatDate("yyyy-MM", Date())
            cur_date_str = AppUtils.formatDate("yyyy-MM", date.time)
            if (cur_date_str == today_str) {
                cur_date_txt.text =  "This month"
            }
            cur_date_txt.text =  cur_date_str
        }
        else {
            val today_str = AppUtils.formatDate("yyyy", Date())
            cur_date_str = AppUtils.formatDate("yyyy", date.time)
            if (cur_date_str == today_str) {
                cur_date_txt.text =  "This year"
            }
            cur_date_txt.text =  cur_date_str
        }
    }

    fun updateCurDate(flag : Boolean){
        if (flag) { // increase
            if (view_type == "day") {
                cur_date.add(Calendar.DAY_OF_MONTH, 1)
            }
            else if (view_type == "month") {
                cur_date.add(Calendar.MONTH, 1)
            }
            else {
                cur_date.add(Calendar.YEAR, 1)
            }
        }
        else { // descrease
            if (view_type == "day") {
                cur_date.add(Calendar.DAY_OF_MONTH, -1)
            }
            else if (view_type == "month") {
                cur_date.add(Calendar.MONTH, -1)
            }
            else {
                cur_date.add(Calendar.YEAR, -1)
            }
        }
        setupView()
    }

    fun setupView() {
        setCurDateStr(cur_date)

        if (report_type == "order") {
            showLoader()
            metaCollection.document("order_stats").get().addOnSuccessListener {
                var order_earningStats = it.toObject(EarningStats::class.java)
                metaCollection.document("booking_stats").get().addOnSuccessListener {
                    hideLoader()
                    var booking_earningStats = it.toObject(EarningStats::class.java)

                    xLabels = ArrayList()
                    for( i in -2..2) {
                        var tmpDate = Calendar.getInstance()
                        tmpDate.time = cur_date.time

                        if (view_type == "day") {
                            tmpDate.add(Calendar.DAY_OF_MONTH, i)
                            xLabels.add(AppUtils.formatDate("yyyy-MM-dd", tmpDate.time))
                        }
                        else if (view_type == "month") {
                            tmpDate.add(Calendar.MONTH, i)
                            xLabels.add(AppUtils.formatDate("yyyy-MM", tmpDate.time))
                        }
                        else {
                            tmpDate.add(Calendar.YEAR, i)
                            xLabels.add(AppUtils.formatDate("yyyy", tmpDate.time))
                        }
                    }

                    var dataSet = getDataSet(order_earningStats, booking_earningStats, xLabels)
                    dataSet.barWidth = barWidth
                    chart.data = dataSet
                    chart.groupBars(0f, groupSpace, barSpace) // perform the "explicit" grouping

                    val xAxisFormatter: IAxisValueFormatter = LabelFormatter(chart, xLabels)
                    chart.xAxis.valueFormatter = xAxisFormatter
                    chart.invalidate()

                }.addOnFailureListener {
                    hideLoader()
                    toast(it.message)
                }
            }.addOnFailureListener {
                hideLoader()
                toast(it.message)
            }
        }
        else if (report_type == "user") {
            showLoader()
            userStatsCollection.document(item_id).get().addOnSuccessListener {
                    hideLoader()
                    var user_earningStats = it.toObject(EarningStats::class.java)

                    xLabels = ArrayList()
                    for( i in -2..2) {
                        var tmpDate = Calendar.getInstance()
                        tmpDate.time = cur_date.time

                        if (view_type == "day") {
                            tmpDate.add(Calendar.DAY_OF_MONTH, i)
                            xLabels.add(AppUtils.formatDate("yyyy-MM-dd", tmpDate.time))
                        }
                        else if (view_type == "month") {
                            tmpDate.add(Calendar.MONTH, i)
                            xLabels.add(AppUtils.formatDate("yyyy-MM", tmpDate.time))
                        }
                        else {
                            tmpDate.add(Calendar.YEAR, i)
                            xLabels.add(AppUtils.formatDate("yyyy", tmpDate.time))
                        }
                    }

                    var dataSet = getDataSet(user_earningStats, null, xLabels)
                    dataSet.barWidth = barWidth
                    chart.data = dataSet
//                    chart.groupBars(0f, groupSpace, barSpace) // perform the "explicit" grouping

                    val xAxisFormatter: IAxisValueFormatter = LabelFormatter(chart, xLabels)
                    chart.xAxis.valueFormatter = xAxisFormatter
                    chart.invalidate()

            }.addOnFailureListener {
                hideLoader()
                toast(it.message)
            }
        }
        else if (report_type == "product") {
            showLoader()
            productStatsCollection.document(item_id).get().addOnSuccessListener {
                hideLoader()
                var user_earningStats = it.toObject(EarningStats::class.java)

                xLabels = ArrayList()
                for( i in -2..2) {
                    var tmpDate = Calendar.getInstance()
                    tmpDate.time = cur_date.time

                    if (view_type == "day") {
                        tmpDate.add(Calendar.DAY_OF_MONTH, i)
                        xLabels.add(AppUtils.formatDate("yyyy-MM-dd", tmpDate.time))
                    }
                    else if (view_type == "month") {
                        tmpDate.add(Calendar.MONTH, i)
                        xLabels.add(AppUtils.formatDate("yyyy-MM", tmpDate.time))
                    }
                    else {
                        tmpDate.add(Calendar.YEAR, i)
                        xLabels.add(AppUtils.formatDate("yyyy", tmpDate.time))
                    }
                }

                var dataSet = getDataSet(user_earningStats, null, xLabels)
                dataSet.barWidth = barWidth
                chart.data = dataSet
//                    chart.groupBars(0f, groupSpace, barSpace) // perform the "explicit" grouping

                val xAxisFormatter: IAxisValueFormatter = LabelFormatter(chart, xLabels)
                chart.xAxis.valueFormatter = xAxisFormatter
                chart.invalidate()

            }.addOnFailureListener {
                hideLoader()
                toast(it.message)
            }
        }

//        var filter_start_miliseconds :Long = 0
//        var filter_end_miliseconds : Long = 0
//
//        if (view_type == "day") {
//
//            var date_str = AppUtils.formatDate("yyyy-MM-dd", cur_date.time)
//            print("start date : ${date_str + " 00:00:00"}")
//            print("end date : ${date_str + " 23:59:59"}")
//            filter_start_miliseconds = AppUtils.parseDate(date_str + " 00:00:00").time  //  "yyyy-MM-dd HH:mm:ss";
//            filter_end_miliseconds = AppUtils.parseDate(date_str + " 23:59:59").time
//
//        }
//        else if (view_type == "month") {
//            var date_str = AppUtils.formatDate("yyyy-MM", cur_date.time)
//
//            val calendar = Calendar.getInstance()
//            calendar[Calendar.YEAR] = cur_date.get(Calendar.YEAR)
//            calendar[Calendar.MONTH] = cur_date.get(Calendar.MONTH)
//            val numDays = calendar.getActualMaximum(Calendar.DATE)
//
//            print("start date : ${date_str + "01 00:00:00"}")
//            print("end date : ${date_str + "-$numDays 23:59:59"}")
//            filter_start_miliseconds = AppUtils.parseDate(date_str + "01 00:00:00").time  //  "yyyy-MM-dd HH:mm:ss";
//            filter_end_miliseconds = AppUtils.parseDate(date_str + "-$numDays 23:59:59").time
//        }
//        else {
//            var date_str = AppUtils.formatDate("yyyy", cur_date.time)
//
//            print("start date : ${date_str + "-01-01 00:00:00"}")
//            print("end date : ${date_str + "-12-31 23:59:59"}")
//            filter_start_miliseconds = AppUtils.parseDate(date_str + "-01-01 00:00:00").time  //  "yyyy-MM-dd HH:mm:ss";
//            filter_end_miliseconds = AppUtils.parseDate(date_str + "-12-31 23:59:59").time
//        }
//
//        showLoader()
//        orderCollection.whereLessThan("order_date", filter_start_miliseconds).whereGreaterThan("order_date", filter_end_miliseconds)
//            .get().addOnSuccessListener {
//                hideLoader()
//                val orders = it.toObjects(Order::class.java)
//                if (orders.isNullOrEmpty()) {
//                    orders.sortWith(Comparator { o1: Order, o2: Order ->
//                        o2.createdAt!!.time.compareTo(
//                            o1.createdAt!!.time
//                        )
//                    })
//
//                }
//            }.addOnFailureListener {
//                hideLoader()
//                toast(it.message)
//            }
    }

    private fun getDataSet(order_stats : EarningStats ?, booking_stats : EarningStats ? , labels: ArrayList<String>) :  BarData {

        var order_data_list = ArrayList<StatsEntry>()
        var booking_data_list = ArrayList<StatsEntry>()
        if (view_type == "day") {
            if (order_stats != null && order_stats!!.day_hist != null) {
                order_data_list = order_stats!!.day_hist!!
            }
            if (booking_stats != null && booking_stats!!.day_hist != null) {
                booking_data_list = booking_stats!!.day_hist!!
            }
        }
        else if (view_type == "month") {
            if (order_stats != null && order_stats!!.month_hist != null) {
                order_data_list = order_stats!!.month_hist!!
            }
            if (booking_stats != null && booking_stats!!.month_hist != null) {
                booking_data_list = booking_stats!!.month_hist!!
            }
        }
        else {
            if (order_stats != null && order_stats!!.year_hist != null) {
                order_data_list = order_stats!!.year_hist!!
            }
            if (booking_stats != null && booking_stats!!.year_hist != null) {
                booking_data_list = booking_stats!!.year_hist!!
            }
        }

        // show info
        val order_curitem = order_data_list.firstOrNull{ item -> item.key == cur_date_str }
        var order_curval = 0
        if (order_curitem != null) {
            order_curval = order_curitem!!.value!!
        }
        order_total.text = "$$order_curval"

        val booking_curitem = booking_data_list.firstOrNull{ item -> item.key == cur_date_str }
        var booking_curval = 0
        if (booking_curitem != null) {
            booking_curval = booking_curitem!!.value!!
        }
        booking_total.text = "$$booking_curval"

        if (report_type == "user") {
            buy_total.text = "$order_curval 产品"
        }
        else  if (report_type == "product") {
            sales_total.text = "$order_curval 产品"
        }

        //
        val orderValueSet = java.util.ArrayList<BarEntry>()
        val bookingValueSet = java.util.ArrayList<BarEntry>()
        var cnt = 0
        labels.forEach {
            val order_hist_entry_item = order_data_list.firstOrNull{ item -> item.key == it }
            var order_y_val = 0.0f
            if (order_hist_entry_item != null) {
                order_y_val = order_hist_entry_item!!.value!!.toFloat()
            }

            orderValueSet.add(BarEntry(cnt.toFloat(), order_y_val))

            val booking_hist_entry_item = booking_data_list.firstOrNull{ item -> item.key == it }
            var booking_y_val = 0.0f
            if (booking_hist_entry_item != null) {
                booking_y_val = booking_hist_entry_item!!.value!!.toFloat()
            }

            bookingValueSet.add(BarEntry(cnt.toFloat(), booking_y_val))

            cnt += 1
        }

        var barDataSet1 : BarDataSet? = null
        var barDataSet2 : BarDataSet? = null

        if (report_type == "user") {
            barDataSet1 = BarDataSet(orderValueSet, "产品采购量")
            barDataSet1.color = Color.rgb(0, 0, 155)
            return  BarData(barDataSet1 )
        }
        else  if (report_type == "product") {
            barDataSet1 = BarDataSet(orderValueSet, "销售数量")
            barDataSet1.color = Color.rgb(0, 0, 155)
            return  BarData(barDataSet1 )
        }
        else {
            barDataSet1 = BarDataSet(orderValueSet, getString(R.string.menu_orders))
            barDataSet1.color = Color.rgb(0, 0, 155)

            barDataSet2 = BarDataSet(bookingValueSet, getString(R.string.menu_booking))
            barDataSet2.color = Color.rgb(155, 0, 0)

            return  BarData(barDataSet1, barDataSet2)
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}
