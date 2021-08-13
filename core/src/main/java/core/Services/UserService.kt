package core.Services

import androidx.annotation.Nullable
import com.app.fooddeliverysystem.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import core.Core
import core.Listeners.ServiceListener
import core.Models.Order
import core.Models.User
import core.Utils.AppLog


object UserService {
    var TAG = UserService.javaClass.name;

    private val db = FirebaseFirestore.getInstance()
    lateinit var userCollection: CollectionReference

    fun setCollection(collectionName: String) {
        userCollection = db.collection(collectionName)
    }

    fun getRandDocId() : String{
        if(userCollection == null) {
            return "random_user_id"
        }
        else {
            return userCollection.document().id
        }
    }

    fun isPhoneRegistered(
        number: String,
        listener: ServiceListener<Boolean?, String?>
    ) {
//        var number = number
//        number = "+92" + number.substring(1, number.length)
        userCollection.whereEqualTo("phone", number).get().addOnSuccessListener {
            val queryDocumentSnapshots = it
            if (queryDocumentSnapshots != null && queryDocumentSnapshots.size() > 0) {
                listener.success(true)
            } else listener.success(false)
        }.addOnFailureListener { listener.error(it.message) }
    }

    fun registerUser(user: User, listener: ServiceListener<String, String>) {
        userCollection.document(user.id!!).set(user).addOnCompleteListener {
            if (it.isSuccessful) {
                User.saveUser(user);
                listener.success("User Registered");
            } else {
                listener.error(it.getException()!!.message ?: "Something went wrong!");
            }
        }
    }

    fun updateDeviceToken() {
        val user = User.getUser()!!
        userCollection.document(user.id!!).update("token", User.getToken())
    }

    fun updateCustomer(
        userId: String,
        userName: String, email: String, address: String,
        listener: ServiceListener<String?, String?>
    ) {
        userCollection.document(userId).update("name", userName, "email", email, "address", address)
            .addOnCompleteListener {
                if (it.isSuccessful)
                    listener.success("Information Updated!")
                else
                    listener.error(it.exception?.message)
            }
    }

    fun updateDeliveryAddress(
        userId: String,
        address: String, area : String, region: String, city: String,
        listener: ServiceListener<String?, String?>
    ) {
        userCollection.document(userId)
            .update("address", address, "region", region, "city", city, "area" , area)
            .addOnCompleteListener {
                if (it.isSuccessful)
                    listener.success("Information Updated!")
                else
                    listener.error(it.exception?.message)
            }
    }

    fun updateUserPoints(
        userId: String,
        user_points: Int,
        listener: ServiceListener<String?, String?>
    ) {
        userCollection.document(userId)
            .update("user_points", user_points)
            .addOnCompleteListener {
                if (it.isSuccessful)
                    listener.success("Information Updated!")
                else
                    listener.error(it.exception?.message)
            }
    }

    fun updateCustomerInfo(
        userId: String,
        userName: String, phone: String, email: String, address: String, area : String, region: String, city: String,
        listener: ServiceListener<String?, String?>
    ) {
        userCollection.document(userId).update("name", userName, "phone", phone, "email", email ,"address", address, "region", region, "city", city, "area" , area)
            .addOnCompleteListener {
                if (it.isSuccessful)
                    listener.success("Information Updated!")
                else
                    listener.error(it.exception?.message)
            }
    }

    fun changeNotificationStatus(On: Boolean, listener: ServiceListener<String, String>) {
        userCollection.document(User.getUser()?.id!!).update("notifications", On)
            .addOnSuccessListener {
                listener.success("Notifications status Updated")
            }.addOnFailureListener {
            listener.error(it.message!!)
        }
    }

    private var listenerRegistration: ListenerRegistration? = null
    fun startUserUpdates(listener: ServiceListener<User?, String?>) {
//        stopUserUpdates()
        listenerRegistration = userCollection.document(FirebaseAuth.getInstance().uid!!)
            .addSnapshotListener { documentSnapshot, e ->
                if (documentSnapshot != null) {
                    val user = documentSnapshot?.toObject(User::class.java)
                    if (user!=null) {
                        AppLog.d(TAG, userCollection.path+"\n${user}")
                        User.saveUser(user!!)
                        listener.success(user)
                    }else{
                        listener.error("User no longer exist!")
                    }
                } else
                    listener.error(e?.message)
            }
    }

    fun getUserData(listener: ServiceListener<User?, String?>) {
        userCollection.document(FirebaseAuth.getInstance().uid!!).get().addOnSuccessListener { documentSnapshot ->
            if (documentSnapshot != null) {
                val user = documentSnapshot?.toObject(User::class.java)
                if (user!=null) {
                    User.saveUser(user!!)
                    listener.success(user)
                }
                else{
                    listener.error("User no longer exist!")
                }
            }
            else {
                listener.error("User no longer exist!")
            }
        }.addOnFailureListener { exception ->
            listener.error("User no longer exist!, " + exception.message)
        }
    }

    fun getCustomerUserData(user_id : String, listener: ServiceListener<User, String?>) {
        userCollection.document(user_id).get().addOnSuccessListener { documentSnapshot ->
            if (documentSnapshot != null) {
                val user = documentSnapshot?.toObject(User::class.java)
                if (user!=null) {
                    listener.success(user!!)
                }
                else{
                    listener.error("User no longer exist!")
                }
            }
            else {
                listener.error("User no longer exist!")
            }
        }.addOnFailureListener { exception ->
            listener.error("User no longer exist!, " + exception.message)
        }
    }

    fun stopUserUpdates() {
        if (listenerRegistration != null) listenerRegistration!!.remove()
    }

    fun logOut(user: User, listener: ServiceListener<String, String>) {
        userCollection.document(user.id!!).update(
            "token", null, "online", false, "lastSeen",
            FieldValue.serverTimestamp()
        ).addOnCompleteListener {
            if (it.isSuccessful) {
                stopUserUpdates()
                User.removeUser()
                Order.clearCart()
                FirebaseAuth.getInstance().signOut()
                listener.success(Core.getInstance().getString(R.string.user_logout));
            } else {
                listener.error(it.getException()!!.message ?: "Something went wrong!");
            }
        }

    }
}