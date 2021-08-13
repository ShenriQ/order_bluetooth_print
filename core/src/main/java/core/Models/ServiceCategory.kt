package core.Models

import com.google.firebase.firestore.ServerTimestamp
import java.io.Serializable
import java.util.*

class ServiceCategory: Serializable{
    var id: String? = null
    var title: String? = null
    var desc: String? = null
    var weight : Int? = null
    var image: String? = null
    var enable:Boolean = true
    var cities: ArrayList<CityItem> = ArrayList()
    @ServerTimestamp
    var createdAt: Date? = null

    @ServerTimestamp
    var updatedAt: Date? = null

    override fun toString(): String {
        return title!!
    }

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
}