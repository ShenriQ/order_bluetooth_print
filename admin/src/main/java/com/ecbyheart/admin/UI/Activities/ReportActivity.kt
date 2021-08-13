package com.ecbyheart.admin.UI.Activities

import android.app.DatePickerDialog
import android.graphics.Color
import android.os.Bundle
import android.widget.TextView
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.BarLineChartBase
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IAxisValueFormatter
import com.ecbyheart.admin.R
import com.ecbyheart.admin.Services.AdminOrderService
import core.Listeners.ServiceListener
import core.Models.Order
import core.UI.BaseActivity
import core.Utils.AppLog
import core.Utils.CoreConstants
import java.text.SimpleDateFormat
import java.util.*
import kotlin.Comparator


class ReportActivity : BaseActivity() {

    private class LabelFormatter internal constructor(
        var chart: BarLineChartBase<*>,
        var labels: Array<String>
    ) :
        IAxisValueFormatter {
        override fun getFormattedValue(value: Float, axis: AxisBase): String {
            print("======================value : $value")
            if (value.toInt() < 0) return ""
            if (value.toInt() >= labels.size) return ""
            return labels[value.toInt()]
        }

    }

    val groupSpace = 0.06f
    val barSpace = 0.02f // x2 dataset
    val barWidth = 0.45f // x2 dataset
    // (0.02 + 0.45) * 2 + 0.06 = 1.00 -> interval per "group"

    lateinit var chart: BarChart
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_report)
        setupComponents(this)
    }

    override fun initializeComponents() {
        chart = findViewById(R.id.chart)

        val labels = arrayOf("Name1", "Name2", "Name3", "Name4", "Name5")
        val xAxisFormatter: IAxisValueFormatter = LabelFormatter(chart, labels)
        var data = getDataSet()
        data.barWidth = barWidth // set the width of each bar

        chart.setData(data)
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
        chart.xAxis.valueFormatter = xAxisFormatter // IAxisValueFormatter { value, axis ->  getXAxisValues()[value.toInt()] }

        chart.axisLeft.valueFormatter = IAxisValueFormatter { value, axis -> "$$value" }
        chart.axisLeft.setDrawZeroLine(true)
        //chart.axisLeft.setDrawGridLines(false)
        chart.axisRight.isEnabled = false
        chart.animateXY(2000, 2000)
        chart.invalidate()

    }

    private fun getDataSet() :  BarData {
        val valueSet1 = ArrayList<BarEntry>()
        val v1e1 = BarEntry(0f, 110.000f) // Jan
        valueSet1.add(v1e1)
        val v1e2 = BarEntry(1f, 40.000f) // Feb
        valueSet1.add(v1e2)
        val v1e3 = BarEntry(2f, 60.000f) // Mar
        valueSet1.add(v1e3)
        val v1e4 = BarEntry(3f, 30.000f) // Apr
        valueSet1.add(v1e4)
        val v1e5 = BarEntry(4f, 90.000f) // May
        valueSet1.add(v1e5)
        val valueSet2 = ArrayList<BarEntry>()
        val v2e1 = BarEntry(0f, 150.000f) // Jan
        valueSet2.add(v2e1)
        val v2e2 = BarEntry(1f, 90.000f) // Feb
        valueSet2.add(v2e2)
        val v2e3 = BarEntry(2f, 120.000f) // Mar
        valueSet2.add(v2e3)
        val v2e4 = BarEntry(3f, 60.000f) // Apr
        valueSet2.add(v2e4)
        val v2e5 = BarEntry(4f, 20.000f) // May
        valueSet2.add(v2e5)
        val barDataSet1 = BarDataSet(valueSet1, "Brand 1")
        barDataSet1.color = Color.rgb(0, 0, 155)
        val barDataSet2 = BarDataSet(valueSet2, "Brand 2")
        barDataSet2.color = Color.rgb(155, 0, 0)

        return  BarData(barDataSet1, barDataSet2)

    }

    private fun getXAxisValues(): ArrayList<String> {
        val xAxis = ArrayList<String>()
        xAxis.add("")
        xAxis.add("X 1")
        xAxis.add("X 2")
        xAxis.add("X 3")
        xAxis.add("X 4")
        xAxis.add("X 5")
        xAxis.add("")

        return xAxis
    }

    fun setData() {
        showLoader()
        AdminOrderService.getNewOrders(object : ServiceListener<ArrayList<Order>, String?> {
            override fun success(success: ArrayList<Order>) {
                hideLoader()
                success.sortWith(Comparator { o1: Order, o2: Order ->
                    o2.createdAt!!.time.compareTo(
                        o1.createdAt!!.time
                    )
                })
            }

            override fun error(error: String?) {
                hideLoader()
                toast(error)
            }
        })
    }

    override fun setupListeners() {
//        setToolbar { onGoBack() }
//        setTitle("")
    }

    override fun onDestroy() {
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

            },
            cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)
        )

        dialog.show()
    }
}
