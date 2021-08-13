package core.Services

import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import core.Listeners.ServiceListener
import core.Models.CityItem
import core.Models.ImpInfo
import core.Utils.CoreConstants
import java.util.*
import kotlin.collections.ArrayList

object ContantService {
    //        final String key = AppUtils.formatDate("dd MMM yyyy hh:mm:ss", new Date());
//        val path = "Files/Users/" + FirebaseAuth.getInstance().uid + "/Profile/" + file.getName()
    private val db = FirebaseFirestore.getInstance()
    private val contentCollection = db.collection(CoreConstants.Contents_Coll  )

    fun addUpdateContent(
        content: String, contentType: String,
        listener: ServiceListener<String?, String?>
    ) {

        if(contentType == "Important Notes") {
            contentCollection.document(contentType).update("text", content).addOnCompleteListener {
                if (it.isSuccessful)
                    listener.success("$contentType Updated")
                else
                    listener.error(it.exception?.message)
            }
        }
        else {
            val tmp = hashMapOf(
                "text" to content
            )
            contentCollection.document(contentType).set(tmp).addOnCompleteListener {
                if (it.isSuccessful)
                    listener.success("$contentType Updated")
                else
                    listener.error(it.exception?.message)
            }
        }

    }

    fun deleteContent(id: String, listener: ServiceListener<String?, String?>) {
        contentCollection.document(id).delete().addOnCompleteListener {
            if (it.isSuccessful)
                listener.success("Content Deleted!")
            else
                listener.error(it.exception?.message)
        }
    }

    fun getContent(contentType: String, listener: ServiceListener<String, String>) {
        contentCollection.document(contentType).get().addOnSuccessListener {
            val snapshot = it
            val data = if (snapshot.get("text") != null) snapshot.get("text") as String else ""
            listener.success(data)
        }.addOnFailureListener { listener.error(it.message!!) }
    }

    var cities_level1 : ArrayList<CityItem> = ArrayList<CityItem>()
    var cities_level2 : ArrayList<CityItem> = ArrayList<CityItem>()
    var cities_level3 : ArrayList<CityItem> = ArrayList<CityItem>()
    fun getImpInfo(listener: ServiceListener<ImpInfo, String>) {
        contentCollection.document("Important Notes").get()
            .addOnSuccessListener {
                if (it != null) {
                    val imp_Info = it.toObject(ImpInfo::class.java)
                    if(imp_Info != null) {
                        ImpInfo.saveImpInfo(imp_Info!!)
                        cities_level1 = imp_Info.cities_level1
                        cities_level2 = imp_Info.cities_level2
                        cities_level3 = imp_Info.cities_level3
                        listener.success(imp_Info)
                    }
                    else{
                        listener.error("No data")
                    }
                }
            }
            .addOnFailureListener {
                listener.error(it?.message!!)
            }
    }

    fun updateImpInfo(impInfo: ImpInfo, listener: ServiceListener<String?, String?>) {
        contentCollection.document("Important Notes")
            .set(impInfo)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    ImpInfo.saveImpInfo(impInfo)
                    listener.success("Success!")
                }
                else
                    listener.error(it.exception?.message)
            }
    }

    fun addCity(level : String, city: CityItem, listener: ServiceListener<String?, String?>) {
        contentCollection.document("Important Notes")
            .update(level, FieldValue.arrayUnion(city))
            .addOnCompleteListener {
                if (it.isSuccessful)
                    listener.success("City Added!")
                else
                    listener.error(it.exception?.message)
            }
    }


    fun deleteCity(level : String, city: CityItem, listener: ServiceListener<String?, String?>) {
        contentCollection.document("Important Notes")
            .update(level, FieldValue.arrayRemove(city))
            .addOnCompleteListener {
                if (it.isSuccessful)
                    listener.success("City Deleted!")
                else
                    listener.error(it.exception?.message)
            }
    }


    private var liveListener: ListenerRegistration? = null
    var impInfo: ImpInfo? = null
    fun getImpInfoListener(listener: ServiceListener<ImpInfo, String>) {
        if (liveListener == null) {
            liveListener = contentCollection.document("Important Notes")
                .addSnapshotListener { querySnapshot, exception
                    ->
                    if (querySnapshot != null) {
                        impInfo = querySnapshot.toObject(ImpInfo::class.java)
                        if(impInfo != null) {
                            cities_level1 = impInfo!!.cities_level1
                            cities_level2 = impInfo!!.cities_level2
                            cities_level3 = impInfo!!.cities_level3
                            listener.success(impInfo!!)
                        }
                        else {
                            listener.error("No data")
                        }
                    } else {
                        listener.error(exception?.message!!)
                    }
                }
        } else {
            if(impInfo != null) {
                listener.success(impInfo!!)
            }
            else{
                listener.error("No data")
            }
        }

    }

    fun removeListener() {
        if (liveListener != null) {
            liveListener!!.remove()
            liveListener = null
        }
    }
}