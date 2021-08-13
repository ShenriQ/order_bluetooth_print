package com.ecbyheart.admin.Services

import androidx.annotation.NonNull
import com.google.firebase.firestore.*
import com.google.gson.internal.LinkedTreeMap
import com.navbus.driver.android.utils.HelperMethods
import core.Listeners.ServiceListener
import core.Models.EmailItem
import core.Services.FileServices
import core.Utils.CoreConstants
import core.Utils.NetworkUtils
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File


object MailService {

    private val db = FirebaseFirestore.getInstance()
    private val mailsCollection = db.collection("Mail")

    var list = ArrayList<EmailItem>()
    fun getAllEmails(listener: ServiceListener<ArrayList<EmailItem>, String>) {
        mailsCollection.get().addOnSuccessListener {
            if (it != null) {
                list = ArrayList(it.toObjects(EmailItem::class.java))
                list.sortWith(Comparator { o1: EmailItem, o2: EmailItem -> o2.time!!.compareTo(o1.time!!) })
                listener.success(list!!)
            } else {
                list = ArrayList()
                listener.success(list!!)
            }

        }.addOnFailureListener {
            listener.error(it.message!!)
        }
    }


    fun deleteEmail(notice: EmailItem, listener: ServiceListener<String?, String?>) {
        mailsCollection.document(notice.id!!).delete().addOnSuccessListener {
            listener.success("success")
        }.addOnFailureListener {
            listener.error(it.message!!)
        }
    }


    fun sendEmail(
        email_body: EmailItem,
        img_file: File?,
        listener: ServiceListener<String?, String?>
    ) {
        if (img_file == null) {
            _sendEmail(email_body, listener)
        } else {
            val path = "Mail_Imgs/${email_body.time}_img_${img_file.name}"
            FileServices.UploadFile(img_file, path, object : ServiceListener<String?, String?> {
                override fun success(url: String?) {
                    email_body.image = url
                    _sendEmail(email_body, listener)
                }

                override fun error(error: String?) {
                    listener.error(error)
                }
            })
        }
    }


    fun _sendEmail(email_body: EmailItem, listener: ServiceListener<String?, String?>) {
        email_body.app_id = CoreConstants.APP_ID;
        NetworkUtils.getAPIService().sendEmail(email_body = email_body)
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