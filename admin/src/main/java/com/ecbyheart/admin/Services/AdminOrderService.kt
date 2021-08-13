package com.ecbyheart.admin.Services

import core.Models.Order
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import core.Listeners.ServiceListener
import core.Models.User
import core.Utils.CoreConstants


object AdminOrderService {
    //        final String key = AppUtils.formatDate("dd MMM yyyy hh:mm:ss", new Date());
//        val path = "Files/Users/" + FirebaseAuth.getInstance().uid + "/Profile/" + file.getName()
    private val db = FirebaseFirestore.getInstance()
    private val orderCollection = db.collection(CoreConstants.Orders_Coll )

    fun UpdateOrderStatus(
        order: Order,
        status: Int,
        listener: ServiceListener<String?, String?>
    ) {
        orderCollection.document(order.id!!).update("status", status).addOnCompleteListener {
            if (it.isSuccessful)
                listener.success("success")
            else
                listener.error(it.exception?.message)
        }
    }

    fun deleteOrder(id: String, listener: ServiceListener<String?, String?>) {
        orderCollection.document(id).delete().addOnCompleteListener {
            if (it.isSuccessful)
                listener.success("Order Deleted!")
            else
                listener.error(it.exception?.message)
        }
    }

    private var newOrdersliveListener: ListenerRegistration? = null
    var newOrdersList: ArrayList<Order>? = null
    fun getNewOrders(listener: ServiceListener<ArrayList<Order>, String?>) {
        if (newOrdersliveListener == null) {
            newOrdersliveListener =
                orderCollection
//                    .whereEqualTo("customer.city", User.getUser()?.city)
                    .whereEqualTo("status", CoreConstants.NEW_ORDER)
                    .addSnapshotListener { querySnapshot, exception
                        ->
                        if (querySnapshot != null) {
                            newOrdersList =
                                ArrayList(querySnapshot.toObjects(Order::class.java))
                            listener.success(newOrdersList!!)
                        } else {
                            listener.error(exception?.message!!)
                        }
                    }
        } else
            listener.success(newOrdersList!!)
    }

    fun removeNewOrdersListener() {
        if (newOrdersliveListener != null) {
            newOrdersliveListener!!.remove()
            newOrdersliveListener = null
        }
    }

    private var inprogressOrdersliveListener: ListenerRegistration? = null
    var inprogressOrdersList: ArrayList<Order>? = null
    fun getInprogressOrders(listener: ServiceListener<ArrayList<Order>, String?>) {
        if (inprogressOrdersliveListener == null) {
            inprogressOrdersliveListener =
                orderCollection
//                    .whereEqualTo("customer.city", User.getUser()?.city)
                    .whereEqualTo("status", CoreConstants.INPROGRESS_ORDER)
                    .addSnapshotListener { querySnapshot, exception
                        ->
                        if (querySnapshot != null) {
                            inprogressOrdersList =
                                ArrayList(querySnapshot.toObjects(Order::class.java))
                            listener.success(inprogressOrdersList!!)
                        } else {
                            listener.error(exception?.message!!)
                        }
                    }
        } else
            listener.success(inprogressOrdersList!!)
    }

    fun removeInprogressOrdersListener() {
        if (inprogressOrdersliveListener != null) {
            inprogressOrdersliveListener!!.remove()
            inprogressOrdersliveListener = null
        }
    }

    fun getCompletedOrders(lastCount: Int, listener: ServiceListener<ArrayList<Order>, String?>) {
        val user = User.getUser()!!
        orderCollection
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
        orderCollection.document(orderID).get().addOnSuccessListener {
            val order = it.toObject(Order::class.java)
            if (order == null)
                listener.error("Invalid Order!")
            else
                listener.success(order)
        }.addOnFailureListener {
            listener.error(it.message)
        }
    }

    fun getCustomerOrders(customer_id: String, listener: ServiceListener<ArrayList<Order>, String?>) {
        orderCollection.whereEqualTo("customer.id", customer_id)
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


    private var customerAllOrdersliveListener: ListenerRegistration? = null
    var customerAllOrdersList: ArrayList<Order>? = null
    fun getCutomerAllOrders(listener: ServiceListener<ArrayList<Order>, String>) {
        if (customerAllOrdersliveListener == null) {
            customerAllOrdersliveListener =
                orderCollection.whereEqualTo("customer.id", User.getUser()?.id)
                    .addSnapshotListener { querySnapshot, exception
                        ->
                        if (querySnapshot != null) {
                            customerAllOrdersList =
                                ArrayList(querySnapshot.toObjects(Order::class.java))
                            listener.success(customerAllOrdersList!!)
                        } else {
                            listener.error(exception?.message!!)
                        }
                    }
        } else
            listener.success(customerAllOrdersList!!)
    }

    fun removeCutomerAllOrdersListener() {
        if (customerAllOrdersliveListener != null) {
            customerAllOrdersliveListener!!.remove()
            customerAllOrdersliveListener = null
        }
    }
}