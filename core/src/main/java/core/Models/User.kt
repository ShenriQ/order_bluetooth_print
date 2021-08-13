package core.Models

import android.content.Context.MODE_PRIVATE
import com.google.firebase.firestore.ServerTimestamp
import com.google.gson.Gson
import com.google.gson.annotations.Expose
import core.Core
import core.Services.ObservableUser
import core.Utils.CoreConstants
import java.io.Serializable
import java.util.*

open class User : Serializable {
    @Expose
    var id: String? = null
    @Expose
    var name: String? = null
    @Expose
    var phone: String? = null
    @Expose
    var email: String? = null
    @Expose
    var user_points: Int? = null
    @Expose
    var address: String? = null
    @Expose
    var city: String? = null
    @Expose
    var region: String? = null
    @Expose
    var area: String? = null
    @Expose
    var level: String? = null
    @Expose
    var notifications: Boolean = false
    @Expose
    var online: Boolean = false
    @Expose
    var superAdmin: Boolean = false

    @Expose
    var can_admins : Boolean? = null
    @Expose
    var can_service : Boolean? = null
    @Expose
    var can_product : Boolean? = null
    @Expose
    var can_chat : Boolean? = null
    @Expose
    var can_order : Boolean? = null
    @Expose
    var can_coupon : Boolean? = null
    @Expose
    var can_content : Boolean? = null
    @Expose
    var can_customers : Boolean? = null
    @Expose
    var can_booking : Boolean? = null
    @Expose
    var can_fcm : Boolean? = null
    @Expose
    var can_salesreport : Boolean? = null
    @Expose
    var can_userpoint : Boolean? = null
    @Expose
    var can_email : Boolean? = null
    @Expose
    var platform: String? = "Android"
    @Expose
    var token: String? = null

    @ServerTimestamp
    @Expose
    var createdAt: Date? = null
    @Expose
    var lastSeen: Date? = null

    companion object {
        private val SP_KEY = "user"
        private val TOKEN = "token"
        private val observableUser = ObservableUser.getInstance()
        fun saveUser(user: User) {
            val pref = Core.getContext().getSharedPreferences(CoreConstants.PREFS_KEY, MODE_PRIVATE)
            val sUser = Gson().toJson(user)
            pref.edit().putString(SP_KEY, sUser).apply()
            observableUser.change(user)
        }

        fun getUser(): User? {
            val pref = Core.getContext().getSharedPreferences(CoreConstants.PREFS_KEY, MODE_PRIVATE)
            val sUser = pref.getString(SP_KEY, "")
            return if (sUser == "") null else Gson().fromJson<User>(sUser, User::class.java)
        }

        fun removeUser() {
            val pref = Core.getContext().getSharedPreferences(CoreConstants.PREFS_KEY, MODE_PRIVATE)
            pref.edit().remove(SP_KEY).apply()
        }

        fun saveToken(token: String) {
            val pref = Core.getContext().getSharedPreferences(CoreConstants.PREFS_KEY, MODE_PRIVATE)
            pref.edit().putString(TOKEN, token).apply()
        }

        fun getToken(): String? {
            val pref = Core.getContext().getSharedPreferences(CoreConstants.PREFS_KEY, MODE_PRIVATE)
            val token = pref.getString(TOKEN, "")
            return if (token == "") null else token
        }
    }

    override fun toString(): String {
        return "User(id=$id, name=$name, phone=$phone, email=$email, address=$address, region=$region, area = $area, city=$city, notifications=$notifications, online=$online, superAdmin=$superAdmin, platform=$platform, token=$token, createdAt=$createdAt, lastSeen=$lastSeen)"
    }


}