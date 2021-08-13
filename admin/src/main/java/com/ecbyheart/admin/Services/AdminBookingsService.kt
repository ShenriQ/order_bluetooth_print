package com.ecbyheart.admin.Services

import core.Models.Order
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import core.Listeners.ServiceListener
import core.Models.User
import core.Utils.CoreConstants


object AdminBookingsService {
    //        final String key = AppUtils.formatDate("dd MMM yyyy hh:mm:ss", new Date());
//        val path = "Files/Users/" + FirebaseAuth.getInstance().uid + "/Profile/" + file.getName()
    private val db = FirebaseFirestore.getInstance()
    private val bookingCollection = db.collection(CoreConstants.Bookings_Coll )

    fun UpdateBookingStatus(
        order: Order,
        status: Int,
        listener: ServiceListener<String?, String?>
    ) {
        bookingCollection.document(order.id!!).update("status", status).addOnCompleteListener {
            if (it.isSuccessful)
                listener.success("success")
            else
                listener.error(it.exception?.message)
        }
    }

    fun deleteBooking(id: String, listener: ServiceListener<String?, String?>) {
        bookingCollection.document(id).delete().addOnCompleteListener {
            if (it.isSuccessful)
                listener.success("Booking Deleted!")
            else
                listener.error(it.exception?.message)
        }
    }

    private var newBookingsliveListener: ListenerRegistration? = null
    var newBookingsList: ArrayList<Order>? = null
    fun getNewBookings(listener: ServiceListener<ArrayList<Order>, String?>) {
        if (newBookingsliveListener == null) {
            newBookingsliveListener =
                bookingCollection
//                    .whereEqualTo("customer.city", User.getUser()?.city)
                    .whereEqualTo("status", CoreConstants.NEW_ORDER)
                    .addSnapshotListener { querySnapshot, exception
                        ->
                        if (querySnapshot != null) {
                            newBookingsList =
                                ArrayList(querySnapshot.toObjects(Order::class.java))
                            listener.success(newBookingsList!!)
                        } else {
                            listener.error(exception?.message!!)
                        }
                    }
        } else
            listener.success(newBookingsList!!)
    }

    fun removeNewBookingsListener() {
        if (newBookingsliveListener != null) {
            newBookingsliveListener!!.remove()
            newBookingsliveListener = null
        }
    }

    private var inprogressBookingsliveListener: ListenerRegistration? = null
    var inprogressBookingsList: ArrayList<Order>? = null
    fun getInprogressBookings(listener: ServiceListener<ArrayList<Order>, String?>) {
        if (inprogressBookingsliveListener == null) {
            inprogressBookingsliveListener =
                bookingCollection
//                    .whereEqualTo("customer.city", User.getUser()?.city)
                    .whereEqualTo("status", CoreConstants.INPROGRESS_ORDER)
                    .addSnapshotListener { querySnapshot, exception
                        ->
                        if (querySnapshot != null) {
                            inprogressBookingsList =
                                ArrayList(querySnapshot.toObjects(Order::class.java))
                            listener.success(inprogressBookingsList!!)
                        } else {
                            listener.error(exception?.message!!)
                        }
                    }
        } else
            listener.success(inprogressBookingsList!!)
    }

    fun removeInprogressBookingsListener() {
        if (inprogressBookingsliveListener != null) {
            inprogressBookingsliveListener!!.remove()
            inprogressBookingsliveListener = null
        }
    }

    fun getCompletedBookings(lastCount: Int, listener: ServiceListener<ArrayList<Order>, String?>) {
        val user = User.getUser()!!
        bookingCollection
//                    .whereEqualTo("customer.city", user.city)
//            .whereEqualTo("driver.id", user.id)
            .whereEqualTo("status", CoreConstants.COMPLETED_ORDER)
            .get().addOnSuccessListener {
                val orders = it.toObjects(Order::class.java)
                if (orders.isNullOrEmpty())
                    listener.success(ArrayList())
                else
                    listener.success(ArrayList(orders))
            }.addOnFailureListener {
                listener.error(it.message)
            }
    }

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

    fun getCustomerBookings(customer_id: String, listener: ServiceListener<ArrayList<Order>, String?>) {
        bookingCollection.whereEqualTo("customer.id", customer_id)
            .get().addOnSuccessListener {
                val orders = it.toObjects(Order::class.java)
                if (orders.isNullOrEmpty())
                    listener.success(ArrayList())
                else
                    listener.success(ArrayList(orders))
            }.addOnFailureListener {
                listener.error(it.message)
            }
    }

    private var customerAllBookingsliveListener: ListenerRegistration? = null
    var customerAllBookingsList: ArrayList<Order>? = null
    fun getCutomerAllBookings(listener: ServiceListener<ArrayList<Order>, String>) {
        if (customerAllBookingsliveListener == null) {
            customerAllBookingsliveListener =
                bookingCollection.whereEqualTo("customer.id", User.getUser()?.id)
                    .addSnapshotListener { querySnapshot, exception
                        ->
                        if (querySnapshot != null) {
                            customerAllBookingsList =
                                ArrayList(querySnapshot.toObjects(Order::class.java))
                            listener.success(customerAllBookingsList!!)
                        } else {
                            listener.error(exception?.message!!)
                        }
                    }
        } else
            listener.success(customerAllBookingsList!!)
    }

    fun removeCutomerAllBookingsListener() {
        if (customerAllBookingsliveListener != null) {
            customerAllBookingsliveListener!!.remove()
            customerAllBookingsliveListener = null
        }
    }
}