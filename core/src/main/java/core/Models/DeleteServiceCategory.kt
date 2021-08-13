package core.Models
import com.google.gson.annotations.Expose
import java.io.Serializable

class DeleteServiceCategory :Serializable {
    @Expose
    var id: String? = null

    // for api
    @Expose
    var app_id: String? = null

    constructor()

    constructor(id: String?, app_id: String?) {
        this.id = id
        this.app_id = app_id
    }
}