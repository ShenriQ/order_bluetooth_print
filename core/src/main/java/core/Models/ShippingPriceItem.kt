package core.Models

import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.ServerTimestamp
import com.google.gson.annotations.Expose
import java.io.Serializable
import java.util.*

class ShippingPriceItem : Serializable {
    @Expose var weight_from : Int? = null
    @Expose var weight_to: Int? = null
    @Expose var price: Int? = null
}