package core.Models

import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.ServerTimestamp
import com.google.gson.annotations.Expose
import java.io.Serializable
import java.util.*

class ServiceTimeSlot : Serializable {

    @Expose
    var id: String? = null

    @Expose
    var capacity: Int = 0

    @Expose
    var used_cnt: Int = 0

    @Expose
    var name: String? = null

    @Expose
    var start_hour: Int? = null

    @Expose
    var start_min: Int? = null

    @Expose
    var end_hour: Int? = null

    @Expose
    var end_min: Int? = null

    fun initData(_id : String?, _capacity : Int, _used_cnt : Int, _name : String?, _start_hour : Int?, _start_min : Int?, _end_hour : Int? , _end_min : Int?) {
        this.id = _id
        this.capacity = _capacity
        this.used_cnt = _used_cnt
        this.name = _name
        this.start_hour = _start_hour
        this.start_min = _start_min
        this.end_hour = _end_hour
        this.end_min = _end_min
    }
}