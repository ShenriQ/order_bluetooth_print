package core.Models

import com.google.firebase.firestore.ServerTimestamp
import com.google.gson.annotations.Expose
import java.io.Serializable
import java.util.*

class Notice :Serializable {
    @Expose
    var id: String? = null
    @Expose
    var title: String? = null
    @Expose
    var message: String? = null
    @Expose
    var time: Long? = null

    // for api
    @Expose
    var app_id: String? = null

    constructor()

    constructor(id: String?, title: String?, message: String?, time: Long?, app_id : String?) {
        this.id = id
        this.title = title
        this.message = message
        this.time = time
        this.app_id = app_id
    }
}