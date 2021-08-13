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


object OrderService {
    //        final String key = AppUtils.formatDate("dd MMM yyyy hh:mm:ss", new Date());
//        val path = "Files/Users/" + FirebaseAuth.getInstance().uid + "/Profile/" + file.getName()
    private val db = FirebaseFirestore.getInstance()
    private val orderCollection = db.collection(CoreConstants.Orders_Coll )

    fun addUpdateOrder(
        order: Order,
        listener: ServiceListener<String?, String?>
    ) {
        var msg: String = "Order Updated"
        val id = if (order.id != null) {//Updating
            order.updatedAt = null
            order.id
        } else {//Adding
            msg = "Order Placed"
            order.id = orderCollection.document().getId()
            order.id
        }
        order.app_id = CoreConstants.APP_ID;
        if (order.ifbooking == true) {
            NetworkUtils.getAPIService().createBooking(order = /*GsonUtils.toJSON(*/order/*)*/)
                .enqueue(object :
                    Callback<Any> {
                    override fun onFailure(@NonNull call: Call<Any>, @NonNull t: Throwable) {
                        listener.error(t.message)
                    }

                    override fun onResponse(
                        @NonNull call: Call<Any>,
                        @NonNull response: Response<Any>
                    ) {
                        if (HelperMethods.isValidHttpResponse(response)) {
                            listener.success(response.body()!!.toString())
                        } else listener.error(
                            response.errorBody()?.string()
                        )
                    }
                })
        }
        else {
            NetworkUtils.getAPIService().createOrder(order = /*GsonUtils.toJSON(*/order/*)*/)
                .enqueue(object :
                    Callback<Any> {
                    override fun onFailure(@NonNull call: Call<Any>, @NonNull t: Throwable) {
                        listener.error(t.message)
                    }

                    override fun onResponse(
                        @NonNull call: Call<Any>,
                        @NonNull response: Response<Any>
                    ) {
                        if (HelperMethods.isValidHttpResponse(response)) {
                            listener.success(response.body()!!.toString())
                        } else listener.error(
                            response.errorBody()?.string()
                        )
                    }
                })
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

    fun getNewAndInprogressOrders(listener: ServiceListener<ArrayList<Order>, String?>) {
//        .whereField("customer.id", isEqualTo: User.shared!.id)
//        .whereField("status", isGreaterThanOrEqualTo: 1)
//        .whereField("status", isLessThanOrEqualTo: 0)
//        new = 0,
//        inprogress = 1,
//        completed = 2
//        where status is >= 0 && where status is <=1
        orderCollection.whereEqualTo("customer.id", User.getUser()?.id)
            .whereGreaterThanOrEqualTo("status", 0)
            .whereLessThanOrEqualTo("status", 1)
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

    fun getCompletedOrders(listener: ServiceListener<ArrayList<Order>, String?>) {
        orderCollection.whereEqualTo("customer.id", User.getUser()?.id)
            .whereEqualTo("status", 2)
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

    private var customerAllOrdersliveListener: ListenerRegistration? = null
    var customerAllOrdersList: ArrayList<Order> = ArrayList()
    fun getCutomerAllOrders(listener: ServiceListener<ArrayList<Order>, String>) {
        if (customerAllOrdersliveListener == null) {
            customerAllOrdersliveListener =
                orderCollection.whereEqualTo("customer.id", User.getUser()?.id)
                    .addSnapshotListener { querySnapshot, exception
                        ->
                        if (querySnapshot != null) {
                            customerAllOrdersList =
                                ArrayList(querySnapshot.toObjects(Order::class.java))
                            customerAllOrdersList.sortWith(Comparator { o1: Order, o2: Order ->
                                        o2.createdAt!!.time.compareTo(
                                            o1.createdAt!!.time
                                        )
                                    })
                            listener.success(customerAllOrdersList)
                        } else {
                            listener.error(exception?.message!!)
                        }
                    }
        } else
            listener.success(customerAllOrdersList)
    }

    fun removeCutomerAllOrdersListener() {
        if (customerAllOrdersliveListener != null) {
            customerAllOrdersliveListener!!.remove()
            customerAllOrdersliveListener = null
        }
    }
}