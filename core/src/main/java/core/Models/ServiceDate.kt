package core.Models

import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.ServerTimestamp
import com.google.gson.annotations.Expose
import java.io.Serializable
import java.util.*

class ServiceDate : Serializable {

    @Expose
    var id: String? = null

    @Expose
    var name: String? = null

    @Expose
    var date: Long? = null

    @Expose
    var capacity: Int = 0

    @Expose
    var timeslots: ArrayList<ServiceTimeSlot> ? = null

}