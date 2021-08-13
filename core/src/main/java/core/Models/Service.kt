package core.Models

import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.ServerTimestamp
import com.google.gson.annotations.Expose
import java.io.Serializable
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.roundToInt

class Service : Serializable {
    @Expose
    var id: String? = null
    @Expose
    var title: String? = null
    @Expose
    var desc: String? = null
    @Expose
    var price: String? = null
    @Expose
    var discount: String? = null
    @Expose
    var stock: String? = null
    @Expose
    var catId: String? = null
    @Expose
    var image: String? = null
    @Expose
    var enable: Boolean = true
    @Expose
    var everyday: Boolean? = false
    @Expose
    var weight : Int? = null
    @Expose
    var userPoints: String? = null

    @ServerTimestamp
    @Expose
    var createdAt: Date? = null

    @ServerTimestamp
    @Expose
    var updatedAt: Date? = null

    @get:Exclude
    var category: ServiceCategory? = null


    fun getW() : Int {
        if (weight == null)
        {
            return 0
        }
        else
        {
            return weight!!
        }
    }
    fun discountedPrice(): String {
        if (discount == null) discount = "0"
        if (discount == "") discount = "0"
        if (price == null) price = "0"
        if (price == "") price = "0"
        val dicousnt = discount!!.toDouble()
        val dicousntPerc = dicousnt / 100
        val total = price!!.toDouble() - (price!!.toDouble() * dicousntPerc)
        return total.roundToInt().toString()
    }

    fun isDiscounted(): Boolean {
        val ret_val = discount != null && discount != "0" && discount != ""
        return ret_val
    }

}