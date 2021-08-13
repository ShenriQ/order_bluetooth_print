package com.navbus.driver.android.utils

import android.app.Activity
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import com.app.urantia.Models.BaseModel
import com.app.urantia.Models.ErrorModelPojo
import com.google.gson.Gson
import core.UI.BaseActivity
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import java.io.File
import java.math.RoundingMode
import java.text.DecimalFormat

object HelperMethods {

    /*
    *           Filter Http Request
    *
    * */
    @JvmStatic
    fun isValidHttpResponse(response: Response<*>?): Boolean {

        var isValid = response?.isSuccessful ?: false

        response?.body().let {
            if (it is BaseModel<*>)
                isValid = (it.code == 200 || it.code == 204 || it.code == 201)
        }

        return isValid
    }

    /*
   *           If Error Code like 404 api server will return data in ErrorBody
   *
   * */
    @JvmStatic
    fun getErrorMessage(response: Response<*>?): String {
        response?.errorBody().let {

            // Log.e("Message: ", "" + response)

            val responedData = response?.errorBody()!!.string()
            if (responedData.startsWith("{") && responedData.endsWith("}")) {
                val parse: ErrorModelPojo =
                    Gson().fromJson(responedData, ErrorModelPojo::class.java)
                if (parse.data != null && parse.data?.keySet() != null) {
//                    return parse.data!!.errors!!
                    val key: String = parse.data!!.keySet().elementAt(0)
                    val message: String = parse.data!!.get(key).asString
                    return message
                }
                return parse.message

////                if (parse.data.size > 0) {
////                    try {
////                        // Get any element at Zero index
////                        val key: String = parse.data[0].keySet().elementAt(0)
////                        val message: String = parse.data[0].get(key).toString()
////                        return message
////                    } catch (ex: Exception) {
////                        ex.printStackTrace()
////                    }
////                }
            }
        }
        return "Please try gain"

    }

    fun networkError(activity: BaseActivity? = null, t: Throwable?) {

        var message: String = "Error please try again"
        if (activity is BaseActivity && !activity.isInternetConnected())
            message = "No internet"

        activity?.toast(message)
    }

    fun isInternetConnected(activity: Activity): Boolean {
        val cm = activity.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork: NetworkInfo? = cm.activeNetworkInfo
        val isConnected: Boolean = activeNetwork?.isConnectedOrConnecting == true
        return isConnected
    }


    /*
    *               Upload Image MultiPart
    * */
    fun prepareFilePart(
        context: Context,
        partName: String,
        byteArray: ByteArray
    ): MultipartBody.Part {
        val requestFile: RequestBody =
            RequestBody.create(
                MediaType.parse("image/jpeg"), byteArray
            )
        // MultipartBody.Part is used to send also the actual file name
        return MultipartBody.Part.createFormData(partName, "test.jpg", requestFile)
    }

    @JvmStatic
    fun prepareFilePart(context: Context, partName: String, file: File?): MultipartBody.Part? {
        if (file == null)
            return null
        val requestFile: RequestBody =
            RequestBody.create(
                MediaType.parse("image/jpeg"), file
            )
        // MultipartBody.Part is used to send also the actual file name
        return MultipartBody.Part.createFormData(partName, file?.name, requestFile)
    }

    @JvmStatic
    fun preparePdfFilePart(context: Context, partName: String, file: File): MultipartBody.Part {
        val requestFile: RequestBody =
            RequestBody.create(
                MediaType.parse("application/pdf"), file
            )
        // MultipartBody.Part is used to send also the actual file name
        return MultipartBody.Part.createFormData(partName, file.name, requestFile)
    }

    fun roundOffDecimal(number: Double): Double? {
        val df = DecimalFormat("#.##")
        df.roundingMode = RoundingMode.CEILING
        return df.format(number).toDouble()
    }

}