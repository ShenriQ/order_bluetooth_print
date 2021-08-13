package core.Services

import androidx.annotation.NonNull
import com.google.firebase.firestore.*
import com.google.gson.internal.LinkedTreeMap
import com.navbus.driver.android.utils.HelperMethods
import core.Listeners.ServiceListener
import core.Models.Notice
import core.Utils.CoreConstants
import core.Utils.NetworkUtils
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


object NoticesService {

    private val db = FirebaseFirestore.getInstance()
    private val noticesCollection = db.collection(CoreConstants.PushMessages_Coll )

    var list = ArrayList<Notice>()
    fun getAllNotices(listener: ServiceListener<ArrayList<Notice>, String>) {
        noticesCollection.get().addOnSuccessListener {
            if (it != null) {
                list = ArrayList(it.toObjects(Notice::class.java))
                list.sortWith(Comparator { o1: Notice, o2: Notice -> o2.time!!.compareTo(o1.time!!) })
                listener.success(list!!)
            } else {
                list = ArrayList()
                listener.success(list!!)
            }

        }.addOnFailureListener {
            listener.error(it.message!!)
        }
    }


    fun deleteNotice(notice: Notice, listener: ServiceListener<String?, String?>) {
        noticesCollection.document(notice.id!!).delete().addOnSuccessListener {
            listener.success("success")
        }.addOnFailureListener {
            listener.error(it.message!!)
        }
    }

    fun sendNotice(notice: Notice, listener: ServiceListener<String?, String?>) {
        notice.app_id = CoreConstants.APP_ID;
        NetworkUtils.getAPIService().sendPush(notice = notice)
            .enqueue(object :
                Callback<Any> {
                override fun onFailure(@NonNull call: Call<Any>, @NonNull t: Throwable) {
                    val aa = t.message
                    listener.error(t.message)
                }

                override fun onResponse(
                    @NonNull call: Call<Any>,
                    @NonNull response: Response<Any>
                ) {
                    if (HelperMethods.isValidHttpResponse(response)) {
                        val res_data : LinkedTreeMap<String, String> = response.body() as LinkedTreeMap<String, String>
                        if (res_data == null)
                        {
                            listener.success(response.body().toString())
                        }
                        else
                        {
                            listener.success(res_data.get("message"))
                        }

                    } else {
                        val err_data = response.errorBody()?.string()
                        var obj = JSONObject(err_data)
                        if(obj == null)
                        {
                            listener.error(err_data)
                        }
                        else
                        {
                            listener.error(obj.getString("message"))
                        }

                    }
                }
            })
    }
}