package core.Models

import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.ServerTimestamp
import com.google.gson.annotations.Expose
import java.io.Serializable
import java.util.*

class CartItem : Serializable {
    @Expose
    var quantity: Int = 0
    @Expose var catName: String? = null
    @Expose var product:Product? = null
    // 2021-01-10
    @Expose var note : String? = null
    // 2021-01-04
    @Expose
    var subProduct: Product ? = null

}