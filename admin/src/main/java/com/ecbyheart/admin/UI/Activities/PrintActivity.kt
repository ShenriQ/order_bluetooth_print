package com.ecbyheart.admin.UI.Activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import com.ecbyheart.admin.R
import com.mazenrashed.printooth.Printooth
import com.mazenrashed.printooth.data.DefaultPrintingImagesHelper
import com.mazenrashed.printooth.data.PrintingImagesHelper
import com.mazenrashed.printooth.data.converter.Converter
import com.mazenrashed.printooth.data.converter.DefaultConverter
import com.mazenrashed.printooth.data.printable.Printable
import com.mazenrashed.printooth.data.printable.TextPrintable
import com.mazenrashed.printooth.data.printer.DefaultPrinter
import com.mazenrashed.printooth.data.printer.Printer
import com.mazenrashed.printooth.ui.ScanningActivity
import com.mazenrashed.printooth.utilities.Printing
import com.mazenrashed.printooth.utilities.PrintingCallback
import core.Models.Order
import core.UI.BaseActivity
import core.Utils.AppUtils
import core.Utils.CoreConstants
import java.nio.charset.Charset
import java.util.*
import kotlin.collections.ArrayList


class PrintActivity : BaseActivity() {

    lateinit var order: Order
    var printing : Printing? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_print)
        if (intent.hasExtra("order"))
            order = intent.getSerializableExtra("order") as Order

        if(Printooth.hasPairedPrinter()) {
            printing = Printooth.printer()
        }

        setupComponents(this)
    }

    lateinit var paired_deivce: LinearLayout
    lateinit var device_name: TextView
    lateinit var device_address: TextView

    lateinit var pair_unpairBtn: Button
    lateinit var printBtn: Button

    override fun initializeComponents() {
        paired_deivce = findViewById(R.id.paired_deivce)
        device_name = findViewById(R.id.device_name)
        device_address = findViewById(R.id.device_address)

        pair_unpairBtn = findViewById(R.id.pair_unpair_btn)
        printBtn = findViewById(R.id.confirm)

        initViews()
    }

    private fun initViews() {
        if(Printooth.hasPairedPrinter()) {
            pair_unpairBtn.setText("不成對")
            device_name.text = Printooth.getPairedPrinter()?.name
            device_address.text = Printooth.getPairedPrinter()?.address
            paired_deivce.visibility = View.VISIBLE
        }
        else {
            pair_unpairBtn.setText("配對")
            paired_deivce.visibility = View.GONE
        }
    }

    override fun setupListeners() {
        setToolbar { onGoBack() }
        setTitle("訂單打印")

        pair_unpairBtn.setOnClickListener {
            if (Printooth.hasPairedPrinter()) Printooth.removeCurrentPrinter()
            else startActivityForResult(
                Intent(this, ScanningActivity::class.java),
                ScanningActivity.SCANNING_FOR_PRINTER)
            initViews()
        }

        printBtn.setOnClickListener {
            if (!Printooth.hasPairedPrinter()) startActivityForResult(Intent(this,
                ScanningActivity::class.java),
                ScanningActivity.SCANNING_FOR_PRINTER)
            else printOrder()
        }


        printing?.printingCallback = object : PrintingCallback {
            override fun connectingWithPrinter() {
                toast("Connecting with printer")
            }

            override fun printingOrderSentSuccessfully() {
                toast("Order sent to printer")
            }

            override fun connectionFailed(error: String) {
                toast("Failed to connect printer")
            }

            override fun onError(error: String) {
                toast(error)
            }

            override fun onMessage(message: String) {
                toast("Message: $message")
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == ScanningActivity.SCANNING_FOR_PRINTER && resultCode == Activity.RESULT_OK)
            printOrder()
        initViews()
    }

    private fun printOrder() {
        val printables = getPrintablesOrder()
        printing?.print(printables)
    }

    private fun getPrintablesOrder() = ArrayList<Printable>().apply {

        add(TextPrintable.Builder()
            .setText("订单详细") //订单详细
            .setCharacterCode(DefaultPrinter.CHARCODE_ARABIC_CP864)
            .setLineSpacing(DefaultPrinter.LINE_SPACING_60)
            .setAlignment(DefaultPrinter.ALIGNMENT_CENTER)
            .setEmphasizedMode(DefaultPrinter.EMPHASIZED_MODE_BOLD)
            .setUnderlined(DefaultPrinter.UNDERLINED_MODE_ON)
            .setNewLinesAfter(2)
            .build())
        if(order.ifbooking == true) {
            // print booking list
            order.bookings.forEach { bookingItem ->
                val booking_service = bookingItem.booking_service!!
                val price = if (booking_service.isDiscounted()) {
                    "$${booking_service.discountedPrice()}"
                } else {
                    "$${booking_service.price}"
                }

                addAll(getPrintableInfoRow("${booking_service.title}", price))

                bookingItem.booking_dates!!.forEach { dateItem ->
                    dateItem.timeslots!!.forEach { timeslotItem ->

                        val sub_title = String.format("%s / %s - %s", dateItem.name,
                            CoreConstants.getTimeString(
                                timeslotItem.start_hour!!,
                                timeslotItem.start_min!!
                            ),
                            CoreConstants.getTimeString(
                                timeslotItem.end_hour!!,
                                timeslotItem.end_min!!
                            )
                        )

                        addAll(getPrintableInfoRow("", sub_title))
                    }
                }
            }
            // end booking list
        }
        else {
            // print product list
            order.products.forEach { cartItem ->
                val product = cartItem.product!!
                val price = if (product.isDiscounted()) {
                    "$${AppUtils.formatNumber(product.discountedPrice())}"
                } else {
                    "$${AppUtils.formatNumber(product.price) }"
                }

                addAll(getPrintableInfoRow("${cartItem.quantity} x ${product.title}", price))
                // sub product list
                val sub_title  = cartItem.subProduct!!.catId!!.split("=@=")[0] + " / " + cartItem.subProduct!!.title

                addAll(getPrintableInfoRow("    $sub_title",  ""))
            }
            // end product list
        }

        add(getDivider())
        addAll(getPrintableInfoRow("客戶名稱", order.customer?.name))
        addAll(getPrintableInfoRow("電話號碼", order.customer?.phone))
        addAll(getPrintableInfoRow("送貨地址", "${order.customer?.address} "))
        addAll(getPrintableInfoRow("訂單編號", order.no))
        if (order.order_date != null)
        {
            addAll(getPrintableInfoRow("訂單日期", AppUtils.formatDate(CoreConstants.DATE, Date(order.order_date!!))))
            addAll(getPrintableInfoRow("訂單時間", AppUtils.formatDate(CoreConstants.TIME, Date(order.order_date!!))))
        }

        var quantity = 0
        for (product in order.products) {
            quantity += product.quantity
        }
        addAll(getPrintableInfoRow("訂單數量", quantity.toString()))
        addAll(getPrintableInfoRow("付款方法", if (order.cod) "現金" else "信用卡"))
        addAll(getPrintableInfoRow("特別指引", order.order_note))
        add(getDivider())
        addAll(getPrintableInfoRow("訂單總額", "$${AppUtils.formatNumber(order.total)}"))
//

//        add(
//            TextPrintable.Builder()
//            .setText(" " +  + " Hello World : été è à '€' içi Bò Xào Coi Xanh")
////            .setCharacterCode(DefaultPrinter.CHARCODE_PC1252)
//            .setNewLinesAfter(1)
//            .build())

//        add(TextPrintable.Builder()
//            .setText("Hello World : été è à €")
//            .setCharacterCode(DefaultPrinter.CHARCODE_PC1252)
//            .setNewLinesAfter(1)
//            .build())
//
//        add(TextPrintable.Builder()
//            .setText("Hello World")
//            .setLineSpacing(DefaultPrinter.LINE_SPACING_60)
//            .setAlignment(DefaultPrinter.ALIGNMENT_CENTER)
//            .setEmphasizedMode(DefaultPrinter.EMPHASIZED_MODE_BOLD)
//            .setUnderlined(DefaultPrinter.UNDERLINED_MODE_ON)
//            .setNewLinesAfter(1)
//            .build())
//
//        add(TextPrintable.Builder()
//            .setText("Hello World")
//            .setAlignment(DefaultPrinter.ALIGNMENT_RIGHT)
//            .setEmphasizedMode(DefaultPrinter.EMPHASIZED_MODE_BOLD)
//            .setUnderlined(DefaultPrinter.UNDERLINED_MODE_ON)
//            .setNewLinesAfter(1)
//            .build())
//
//        add(TextPrintable.Builder()
//            .setText("اختبار العربية")
//            .setAlignment(DefaultPrinter.ALIGNMENT_CENTER)
//            .setEmphasizedMode(DefaultPrinter.EMPHASIZED_MODE_BOLD)
//            .setFontSize(DefaultPrinter.FONT_SIZE_NORMAL)
//            .setUnderlined(DefaultPrinter.UNDERLINED_MODE_ON)
//            .setCharacterCode(DefaultPrinter.)
//            .setNewLinesAfter(1)
//            .setCustomConverter(ArabicConverter()) // change only the converter for this one
//            .build())
    }

    private fun getPrintableInfoRow(name : String, value : String?) : ArrayList<Printable> {
         var ret = ArrayList<Printable>()
         ret.add(TextPrintable.Builder()
                .setText(" $name   : ")
                .setCharacterCode(DefaultPrinter.CHARCODE_HEBREW)
                .setAlignment(DefaultPrinter.ALIGNMENT_LEFT)
                .setEmphasizedMode(DefaultPrinter.EMPHASIZED_MODE_BOLD)
                .build())
         ret.add(TextPrintable.Builder()
                .setText(" $value ")
                .setCharacterCode(DefaultPrinter.CHARCODE_HEBREW)
                .setAlignment(DefaultPrinter.ALIGNMENT_RIGHT)
                .setNewLinesAfter(1)
                .build())
         return  ret
    }

    private fun getDivider() : Printable {
        return TextPrintable.Builder()
            .setText("------------------")
            .setLineSpacing(DefaultPrinter.LINE_SPACING_60)
            .setAlignment(DefaultPrinter.ALIGNMENT_CENTER)
            .setNewLinesAfter(1)
            .build()
    }
}
