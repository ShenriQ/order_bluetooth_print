package core.Models

import android.content.Context
import com.google.gson.Gson
import core.Core
import core.Services.ObservableUser
import core.Utils.CoreConstants

class ImpInfo {
    var text:String? = null
    var banners: ArrayList<String>? = null
    var ad_banner:String? = null
    var deliveryTime:String? = ""
    var userPoint: Double? = null
    var showProducts : Boolean? = true
    var showServices : Boolean? = true
    var enableUserPoints : Boolean? = true
    var freeshipping : Int? = null

    var shippings : ArrayList<ShippingPriceItem>? = ArrayList<ShippingPriceItem>()

    var Regions:HashMap<String,ArrayList<String>> = HashMap<String,ArrayList<String>>()
    var cities_level1:ArrayList<CityItem> = ArrayList<CityItem>()
    var cities_level2:ArrayList<CityItem> = ArrayList<CityItem>()
    var cities_level3:ArrayList<CityItem> = ArrayList<CityItem>()
    var cities_map:HashMap<String,String> = HashMap<String,String>()

    companion object{
        private val SP_KEY = "cites"
        private val observableUser = ObservableUser.getInstance()
        fun saveImpInfo(impInfo: ImpInfo) {
            val pref = Core.getContext().getSharedPreferences(
                CoreConstants.PREFS_KEY,
                Context.MODE_PRIVATE
            )
            val sImpInfo = Gson().toJson(impInfo)
            pref.edit().putString(SP_KEY, sImpInfo).apply()
        }

        fun getImpInfo(): ImpInfo? {
            val pref = Core.getContext().getSharedPreferences(
                CoreConstants.PREFS_KEY,
                Context.MODE_PRIVATE
            )
            val sImpInfo = pref.getString(SP_KEY, "")
            return if (sImpInfo == "") null else Gson().fromJson<ImpInfo>(sImpInfo, ImpInfo::class.java)
        }
    }
}