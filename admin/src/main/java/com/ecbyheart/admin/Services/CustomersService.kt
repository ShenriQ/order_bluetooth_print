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
import java.util.*

object CustomersService {
    var TAG = CustomersService.javaClass.name;

    private val db = FirebaseFirestore.getInstance()
    private val customersCollection = db.collection(CoreConstants.Customers_Coll )

    fun changeOnlineStatus(online: Boolean, listener: ServiceListener<String, String>) {
        customersCollection.document(User.getUser()?.id!!).update(
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
    fun getAllCustomers(listener: ServiceListener<ArrayList<User>, String>) {
        if (liveListener == null) {
            liveListener = customersCollection.addSnapshotListener { querySnapshot, exception
                ->
                if (querySnapshot != null) {
                    list = ArrayList(querySnapshot.toObjects(User::class.java))
                    list!!.sortWith(Comparator { o1, o2 ->  o1.createdAt!!.compareTo(o2.createdAt!!) })
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

    fun deleteCustomer(user: User, listener: ServiceListener<String?, String?>) {
        customersCollection.document(user.id!!).delete().addOnSuccessListener {
            listener.success(Core.getInstance().getString(R.string.status_updated))
        }.addOnFailureListener {
            listener.error(it.message!!)
        }
    }

    fun updateCustomer(user: User, listener: ServiceListener<String?, String?>) {
        customersCollection.document(user.id!!).update(
            "city", user.city, "region", user.region, "area", user.area, "level", user.level
        ).addOnSuccessListener {
            listener.success("success")
        }.addOnFailureListener {
            listener.error(it.message!!)
        }
    }

    fun updateUserPoints(user: User, listener: ServiceListener<String?, String?>) {
        customersCollection.document(user.id!!).update(
            "user_points", user.user_points
        ).addOnSuccessListener {
            listener.success("success")
        }.addOnFailureListener {
            listener.error(it.message!!)
        }
    }
}