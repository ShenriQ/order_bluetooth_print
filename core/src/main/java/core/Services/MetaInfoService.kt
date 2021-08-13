package core.Services

import com.google.firebase.firestore.FirebaseFirestore
import core.Listeners.ServiceListener
import core.Models.VersionInfo
import core.Utils.CoreConstants
import kotlin.collections.ArrayList

object MetaInfoService {

    private val db = FirebaseFirestore.getInstance()
    private val contentCollection = db.collection(CoreConstants.meta_Coll )

    var admin_phones : ArrayList<String> = ArrayList<String>()
    fun getAdminPhones(listener: ServiceListener<ArrayList<String>, String>) {
        contentCollection.document("admin_info").get()
            .addOnSuccessListener {
                if (it != null) {
                    admin_phones = if (it.get("phones") != null) it.get("phones") as ArrayList<String> else ArrayList<String>()
                    listener.success(admin_phones)
                }
                else {
                    listener.error("invalid data")
                }
            }
            .addOnFailureListener {
                listener.error(it?.message!!)
            }
    }

    fun getAppVersion(listener: ServiceListener<VersionInfo, String>) {
        contentCollection.document("version_info").get()
            .addOnSuccessListener {
                if (it != null) {
                    val version_info = it.toObject(VersionInfo::class.java)
                    if (version_info == null)
                        listener.error("Invalid version_info!")
                    else
                        listener.success(version_info)
                }
                else {
                    listener.error("invalid version_info!")
                }
            }
            .addOnFailureListener {
                listener.error(it?.message!!)
            }

    }
}