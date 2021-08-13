package core.Models

import com.google.firebase.firestore.ServerTimestamp
import java.io.Serializable
import java.util.*

class VersionInfo :Serializable {
    var android_version: Int? = null
    var ios_version: Double? = null
    var android_link: String? = null
    var ios_link: String? = null
    var alert_title: String? = null
    var alert_body: String? = null
    var alert_body_ios : String? = null
    var alert_update_btn : String? = null
    var alert_skip_btn : String? = null
    var force: Boolean? = null

    constructor()

    constructor(android_version: Int?, ios_version: Double?, android_link: String?, ios_link: String?, alert_title: String?, alert_body: String?, alert_body_ios : String?, alert_update_btn: String?, alert_skip_btn: String?, force : Boolean?) {
        this.android_version = android_version
        this.ios_version = ios_version
        this.android_link = android_link
        this.ios_link = ios_link
        this.alert_title = alert_title
        this.alert_body = alert_body
        this.alert_body_ios = alert_body_ios
        this.alert_update_btn = alert_update_btn
        this.alert_skip_btn = alert_skip_btn
        this.force = force
    }
}