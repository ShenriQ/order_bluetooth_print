package core.Models

import com.google.gson.annotations.Expose
import java.io.Serializable
import kotlin.collections.ArrayList

class Booking : Serializable {
    @Expose
    var id: String? = null

    @Expose
    var booking_service: Service ? = null

    @Expose
    var booking_dates:  ArrayList<ServiceDate> ? = null
}