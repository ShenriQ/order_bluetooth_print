package core.Models

import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.ServerTimestamp
import com.google.gson.annotations.Expose
import java.io.Serializable
import java.util.*

class CityItem : Serializable {
    @Expose var parent_city : String? = null
    @Expose var name: String? = null
    @Expose var city_code: String? = null
}