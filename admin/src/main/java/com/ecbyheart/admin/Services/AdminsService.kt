package com.ecbyheart.admin.Services

import androidx.annotation.Nullable
import com.ecbyheart.admin.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import core.Core
import core.Listeners.ServiceListener
import core.Models.User
import core.Utils.AppLog
import core.Utils.CoreConstants


object AdminsService {
    var TAG = AdminsService.javaClass.name;

    private val db = FirebaseFirestore.getInstance()
    private val adminsCollection = db.collection( CoreConstants.Admins_Coll )

    fun changeOnlineStatus(online: Boolean, listener: ServiceListener<String, String>) {
        adminsCollection.document(User.getUser()?.id!!).update(
            "online", online, "lastSeen",
            FieldValue.serverTimestamp()
        ).addOnSuccessListener {
            listener.success(Core.getInstance().getString(R.string.status_updated))
        }.addOnFailureListener {
            listener.error(it.message!!)
        }
    }

    private var liveListener: ListenerRegistration? = null
    var list: ArrayList<User>? = null
    fun getAllAdmins(listener: ServiceListener<ArrayList<User>, String>) {
        if (liveListener == null) {
            liveListener = adminsCollection.addSnapshotListener { querySnapshot, exception
                ->
                if (querySnapshot != null) {
                    list = ArrayList(querySnapshot.toObjects(User::class.java))
                    listener.success(list!!)
                } else {
                    listener.error(exception?.message!!)
                }
            }
        } else
            listener.success(list!!)
    }

    fun removeListener() {
        if (liveListener != null) {
            liveListener!!.remove()
            liveListener = null
        }
    }

    fun deleteAdmin(user: User, listener: ServiceListener<String?, String?>) {
        adminsCollection.document(user.id!!).delete().addOnSuccessListener {
            listener.success(Core.getInstance().getString(R.string.status_updated))
        }.addOnFailureListener {
            listener.error(it.message!!)
        }
    }

    fun changePermission(user: User, permType: String, perm : Boolean, listener: ServiceListener<String, String>) {
        adminsCollection.document(user.id!!).update(
            permType, perm
        ).addOnSuccessListener {
            listener.success("success")
        }.addOnFailureListener {
            listener.error(it.message!!)
        }
    }
}