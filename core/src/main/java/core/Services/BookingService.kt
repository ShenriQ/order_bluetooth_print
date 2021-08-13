package core.Services

import androidx.annotation.NonNull
import core.Models.Order
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.navbus.driver.android.utils.HelperMethods
import core.Listeners.ServiceListener
import core.Models.User
import core.Utils.CoreConstants
import core.Utils.GsonUtils
import core.Utils.NetworkUtils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


object BookingService {
    //        final String key = AppUtils.formatDate("dd MMM yyyy hh:mm:ss", new Date());
//        val path = "Files/Users/" + FirebaseAuth.getInstance().uid + "/Profile/" + file.getName()
    private val db = FirebaseFirestore.getInstance()
    private val bookingCollection = db.collection(CoreConstants.Bookings_Coll )

    fun getOrder(orderID: String, listener: ServiceListener<Order, String?>) {
        bookingCollection.document(orderID).get().addOnSuccessListener {
            val order = it.toObject(Order::class.java)
            if (order == null)
                listener.error("Invalid Order!")
            else
                listener.success(order)
        }.addOnFailureListener {
            listener.error(it.message)
        }
    }

    private var allbookingListener: ListenerRegistration? = null
    var allBookingList: ArrayList<Order> = ArrayList()
    fun getAllBookingsListener(listener: ServiceListener<ArrayList<Order>, String>) {
        if (allbookingListener == null) {
            allbookingListener =
                bookingCollection.whereEqualTo("customer.id", User.getUser()?.id)
                    .addSnapshotListener { querySnapshot, exception
                        ->
                        if (querySnapshot != null) {
                            allBookingList =
                                ArrayList(querySnapshot.toObjects(Order::class.java))
                            listener.success(allBookingList)
                        } else {
                            listener.error(exception?.message!!)
                        }
                    }
        } else {
            listener.success(allBookingList)
        }
    }

    fun removeAllBookingListener() {
        if (allbookingListener != null) {
            allbookingListener!!.remove()
            allbookingListener = null
        }
    }
}