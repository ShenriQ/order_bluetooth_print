package core.Utils

import core.Models.ImpInfo
import core.Models.Order
import java.util.*

object CoreConstants {
    const val PRINT_LOGS = true
    const val ENGLISH = "en"
    const val Chinese = "zh"
    val PREFS_KEY = "CORE PREFS"

    val DATE_TIME = "dd/MM/yyyy HH:mm"
    val DEFAULT_DATE_TIME = "dd-MM-yyyy HH:mm"
    val DATE = "dd-MM-yyyy"
    val TIME = "HH:mm"

    const val MEMBER_ADMIN = "ADMIN"
    const val MEMBER_CUSTOMER = "CUSTOMER"
    const val MEMBER_DRIVER = "DRIVER"

    const val NEW_ORDER = 0;
    const val INPROGRESS_ORDER = 1;
    const val COMPLETED_ORDER = 2;

    const val stripe_publishable_key = "pk_live_51JLPj5GmFIO2XONufnvAkBjjVkdz6WPHEJ67tJdH8NugoqA6rcZpX1RJEsB8yRBaNcwuPHpb4wC5aX3sjuwORzg1006dfqRAQi"
    const val APP_ID = "01_"

    const val Admins_Coll = APP_ID + "Admins"
    const val Bookings_Coll = APP_ID + "Bookings"
    const val Categories_Coll = APP_ID + "Categories"
    const val Channels_Coll = APP_ID + "Channels"
    const val Contents_Coll = APP_ID + "Contents"
    const val Coupons_Coll = APP_ID + "Coupons"
    const val Customers_Coll = APP_ID + "Customers"
    const val Drivers_Coll = APP_ID + "Drivers"
    const val Messages_Coll = APP_ID + "Messages"
    const val Orders_Coll = APP_ID + "Orders"
    const val Products_Coll = APP_ID + "Products"
    const val PushMessages_Coll = APP_ID + "PushMessages"
    const val ServiceCats_Coll = APP_ID + "ServiceCats"
    const val Services_Coll = APP_ID + "Services"
    const val meta_Coll = APP_ID + "meta"
    const val productStats_Coll = APP_ID + "productStats"
    const val userOrderStats_Coll = APP_ID + "userOrderStats"

    //    [CityCode][day][hour][minutes][user id]
    fun makeTitle(order: Order): String {

        var city_code = order.customer?.city
        var region_code = order.customer?.region
        var area_code = order.customer?.area

        var impInfo = ImpInfo.getImpInfo()
        if (impInfo != null)
        {
            var cities_level1 = impInfo.cities_level1
            if (cities_level1 != null && !cities_level1.filter { item -> item.name == order.customer?.city}.isEmpty())
            {
                city_code = cities_level1.first { item -> item.name == order.customer?.city}.city_code
            }

            var cities_level2 = impInfo.cities_level2
            if (cities_level2 != null && !cities_level2.filter { item -> item.name == order.customer?.region}.isEmpty())
            {
                region_code = cities_level2.first { item -> item.name == order.customer?.region}.city_code
            }

            var cities_level3 = impInfo.cities_level3
            if (cities_level3 != null && !cities_level3.filter { item -> item.name == order.customer?.area}.isEmpty())
            {
                area_code = cities_level3.first { item -> item.name == order.customer?.area}.city_code
            }
        }

        city_code = city_code?.toUpperCase()
        region_code = region_code?.toUpperCase()
        area_code = area_code?.toUpperCase()

        var datecode = AppUtils.formatDate("ddHHmmss", order.date )
        if (order.order_date != null)
        {
            datecode = AppUtils.formatDate("ddHHmmss", Date(order.order_date!!) )
        }
//        return "#${city_code}-${region_code}-${area_code}-${datecode}-${order.customer?.id?.takeLast(4)}"
        return "#${datecode}-${order.customer?.id?.takeLast(4)}"
    }

    fun getSubCatName(sub_catId : String?) : String {
        if (sub_catId == null) {
            return ""
        }
        val tmpArr = sub_catId.split("=@=").toTypedArray()
        if (tmpArr.size > 0 ) {
            return tmpArr[0]
        }
        return ""
    }
    fun getSubCatWeight(sub_catId : String?) : String {
        if (sub_catId == null) {
            return ""
        }
        val tmpArr = sub_catId.split("=@=").toTypedArray()
        if (tmpArr.size > 1 ) {
            return tmpArr[1]
        }
        return ""
    }
    fun getSubCatLimit(sub_catId : String?) : String {
        if (sub_catId == null) {
            return ""
        }
        val tmpArr = sub_catId.split("=@=").toTypedArray()
        if (tmpArr.size > 2 ) {
            return tmpArr[2]
        }
        return ""
    }
    fun getSubCatRequired(sub_catId : String?) : String {
        if (sub_catId == null) {
            return ""
        }
        val tmpArr = sub_catId.split("=@=").toTypedArray()
        if (tmpArr.size > 3 ) {
            return tmpArr[3]
        }
        return ""
    }

    fun getTimeString(hour : Int, min : Int) : String{
        var str = ""
        if (hour < 10) {
            str = "0${hour}"
        }
        else {
            str = "${hour}"
        }
        str = str + ":"
        if (min < 10) {
            str = str + "0${min}"
        }
        else {
            str = str + "${min}"
        }
        return str
    }
}