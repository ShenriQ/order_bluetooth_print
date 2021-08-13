package core.Services

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import core.Listeners.ServiceListener
import core.Models.*
import core.Utils.CoreConstants
import java.io.File
import java.util.*

object ServicesService {
    //        final String key = AppUtils.formatDate("dd MMM yyyy hh:mm:ss", new Date());
//        val path = "Files/Users/" + FirebaseAuth.getInstance().uid + "/Profile/" + file.getName()
    private val db = FirebaseFirestore.getInstance()
    private val serviceCollection = db.collection(CoreConstants.Services_Coll )

    fun addUpdateItem(
        item: Service,
        file: File?,
        listener: ServiceListener<String?, String?>
    ) {
        var msg: String = "Service Updated"
        if (file == null) {//Updating service without file
            item.updatedAt = null
            serviceCollection.document(item.id!!).set(item).addOnCompleteListener {
                if (it.isSuccessful)
                    listener.success(msg)
                else
                    listener.error(it.exception?.message)
            }
        } else {
            val id = if (item.id != null) {//Updating
                item.updatedAt = null
                item.id
            } else {//Adding
                msg = "Service Added"
                item.id = serviceCollection.document().getId()
                item.id
            }
            val path = "Services/${id}/${file.name}"
            FileServices.UploadFile(file, path, object : ServiceListener<String?, String?> {
                override fun success(url: String?) {
                    item.image = url
                    serviceCollection.document(id!!).set(item).addOnCompleteListener {
                        if (it.isSuccessful)
                            listener.success(msg)
                        else
                            listener.error(it.exception?.message)
                    }
                }

                override fun error(error: String?) {
                    listener.error(error)
                }
            })
        }
    }

    fun updateEveryday(service_id: String, isEveryday : Boolean, listener: ServiceListener<String, String>) {
        serviceCollection.document(service_id).update(
            "everyday", isEveryday
        ).addOnSuccessListener {
            listener.success("success")
        }.addOnFailureListener {
            listener.error(it.message!!)
        }
    }

    fun deleteItem(id: String, listener: ServiceListener<String?, String?>) {
        serviceCollection.document(id).delete().addOnCompleteListener {
            if (it.isSuccessful)
                listener.success("Service Deleted!")
            else
                listener.error(it.exception?.message)
        }
    }

    private var liveListener: ListenerRegistration? = null
    var list: ArrayList<Service>? = null
    fun getAllItemsListener(listener: ServiceListener<ArrayList<Service>, String>) {
        if (liveListener == null) {
            liveListener = serviceCollection.addSnapshotListener { querySnapshot, exception
                ->
                if (querySnapshot != null) {
                    list = ArrayList(querySnapshot.toObjects(Service::class.java))
                    listener.success(list!!)
                } else {
                    list = ArrayList()
                    listener.error(exception?.message!!)
                }
            }
        } else {
            if(list == null) {
                list = ArrayList()
            }
            listener.success(list!!)
        }

    }

    fun getAllItems(listener: ServiceListener<ArrayList<Service>, String>) {
        serviceCollection.get().addOnSuccessListener {
            if (it != null) {
                list = ArrayList(it.toObjects(Service::class.java))
                listener.success(list!!)
            } else {
                list = ArrayList()
                listener.success(list!!)
            }

        }.addOnFailureListener {
            if(list == null) {
                list = ArrayList()
            }
            listener.error(it.message!!)
        }
    }

    fun removeListener() {
        if (liveListener != null) {
            liveListener!!.remove()
            liveListener = null
        }
    }

    // SERVICE DATES

    fun addUpdateServiceDate(
        service_id : String,
        item: ServiceDate,
        listener: ServiceListener<String?, String?>
    ) {
        var msg: String = "Service Updated"
        val id = if (item.id != null) {//Updating
            item.id
        } else {//Adding
            msg = "Service Added"
            item.id = serviceCollection.document(service_id).collection("Dates").document().getId()
            item.id
        }
        serviceCollection.document(service_id).collection("Dates").document(id!!).set(item).addOnCompleteListener {
            if (it.isSuccessful)
                listener.success(msg)
            else
                listener.error(it.exception?.message)
        }
    }

    fun addUpdateEveryDayServiceDate(
        service_id : String,
        item: ServiceDate,
        listener: ServiceListener<String?, String?>
    ) {
        serviceCollection.document(service_id).collection("everyday").document("everyday").get().addOnSuccessListener {
            var serviceDate = it.toObject(ServiceDate::class.java)

            if (serviceDate != null && serviceDate!!.timeslots != null && item.timeslots != null) {
                serviceDate.timeslots!!.addAll(item.timeslots!!)
            }
            else {
                serviceDate = item
            }

            serviceCollection.document(service_id).collection("everyday").document("everyday").set(serviceDate).addOnCompleteListener {
                if (it.isSuccessful)
                    listener.success("Service Updated!")
                else
                    listener.error(it.exception?.message)
            }

        }.addOnFailureListener {
            listener.error(it.message)
        }


    }

    fun deleteDate(service_id : String, id: String, listener: ServiceListener<String?, String?>) {
        serviceCollection.document(service_id).collection("Dates").document(id).delete().addOnCompleteListener {
            if (it.isSuccessful)
                listener.success("Service Updated!")
            else
                listener.error(it.exception?.message)
        }
    }

    fun deleteEveryDayDate(service_id : String, listener: ServiceListener<String?, String?>) {
        serviceCollection.document(service_id).collection("everyday").document("everyday").delete().addOnCompleteListener {
            if (it.isSuccessful)
                listener.success("Service Updated!")
            else
                listener.error(it.exception?.message)
        }
    }

    private var serviceDateLiveListener: ListenerRegistration? = null
    var dates_list: ArrayList<ServiceDate>? = null
    fun getAllServiceDatesListener(service_id: String, listener: ServiceListener<ArrayList<ServiceDate>, String>) {
        if (serviceDateLiveListener == null) {
            serviceDateLiveListener = serviceCollection.document(service_id).collection("Dates").addSnapshotListener { querySnapshot, exception
                ->
                if (querySnapshot != null) {
                    dates_list = ArrayList(querySnapshot.toObjects(ServiceDate::class.java))
                    listener.success(dates_list!!)
                } else {
                    listener.error(exception?.message!!)
                }
            }
        } else {
            if(dates_list == null) {
                dates_list = ArrayList()
            }
            listener.success(dates_list!!)
        }

    }


    fun getAllEveryDayTimeslots(service_id: String, listener: ServiceListener<ArrayList<ServiceTimeSlot>, String?>) {
        serviceCollection.document(service_id).collection("everyday").document("everyday").get().addOnSuccessListener {
            val serviceDate = it.toObject(ServiceDate::class.java)
            if (serviceDate != null && serviceDate!!.timeslots != null) {
                listener.success(serviceDate!!.timeslots!!)
            }
            else {
                listener.error("Invalid Timeslots!")
            }
        }.addOnFailureListener {
            listener.error(it.message)
        }
    }

    fun getAllServiceDates(service_id: String, listener: ServiceListener<ArrayList<ServiceDate>, String>) {
        serviceCollection.document(service_id).collection("Dates").get().addOnSuccessListener {
            if (it != null) {
                dates_list = ArrayList(it.toObjects(ServiceDate::class.java))
                listener.success(dates_list!!)
            } else {
                dates_list = ArrayList()
                listener.success(dates_list!!)
            }

        }.addOnFailureListener {
            if(dates_list == null) {
                dates_list = ArrayList()
            }
            listener.error(it.message!!)
        }
    }

    fun removeDateListener(){
        if(serviceDateLiveListener != null ) {
            serviceDateLiveListener!!.remove()
            serviceDateLiveListener = null
        }
    }


    private var serviceItemLiveListener: ListenerRegistration? = null
    var service_list: ArrayList<Service>? = null
    fun getAllServiceItemsListener(service_id: String, listener: ServiceListener<ArrayList<Service>, String>) {
        if (serviceItemLiveListener == null) {
            serviceItemLiveListener = serviceCollection.whereEqualTo("id", service_id).addSnapshotListener { querySnapshot, exception
                ->
                if (querySnapshot != null) {
                    service_list = ArrayList(querySnapshot.toObjects(Service::class.java))
                    listener.success(service_list!!)
                } else {
                    listener.error(exception?.message!!)
                }
            }
        } else
            listener.success(service_list!!)
    }

    fun removeServiceItemsListener(){
        if(serviceItemLiveListener != null ) {
            serviceItemLiveListener!!.remove()
            serviceItemLiveListener = null
        }
    }

    private var serviceEveryDatesLiveListener: ListenerRegistration? = null
    var everyday_list: ArrayList<ServiceDate>? = null
    fun getServiceEveryDatesLiveListener(service_id: String, listener: ServiceListener<ArrayList<ServiceDate>, String>) {
        if (serviceEveryDatesLiveListener == null) {
            serviceEveryDatesLiveListener = serviceCollection.document(service_id).collection("everyday").addSnapshotListener { querySnapshot, exception
                ->
                if (querySnapshot != null) {
                    everyday_list = ArrayList(querySnapshot.toObjects(ServiceDate::class.java))
                    listener.success(everyday_list!!)
                } else {
                    listener.error(exception?.message!!)
                }
            }
        } else
            listener.success(everyday_list!!)
    }

    fun removeServiceEveryDatesLiveListener(){
        if(serviceEveryDatesLiveListener != null ) {
            serviceEveryDatesLiveListener!!.remove()
            serviceEveryDatesLiveListener = null
        }
    }
}