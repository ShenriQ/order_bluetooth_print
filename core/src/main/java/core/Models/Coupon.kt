package core.Models

import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.ServerTimestamp
import core.Utils.AppUtils
import java.io.Serializable
import java.util.*
import kotlin.collections.ArrayList

class Coupon: Serializable{
    var id: String? = null
    var title: String? = null
    var desc: String? = null
    var code: String? = null
    var discount: String? = null
    var fixed : Boolean? = false
    var usedBy: ArrayList<String> = ArrayList()
    var from: Date? = null
    var to: Date? = null
    var enable:Boolean = true

    @ServerTimestamp
    var createdAt: Date? = null

    @ServerTimestamp
    var updatedAt: Date? = null

    @Exclude
    fun getFrom():String{
        return AppUtils.formatDate("dd/MM/yyyy",from)
    }

    @Exclude
    fun getTo():String{
        return AppUtils.formatDate("dd/MM/yyyy",to)
    }
}