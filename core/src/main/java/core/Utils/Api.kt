package core.Utils

import core.Models.*
import org.json.JSONObject
import retrofit2.Call
import retrofit2.http.*

interface Api {
//    @Headers("Content-Type: application/json")

    @POST()
    fun createOrder(@Url url: String = "https://us-central1-user-f06f7.cloudfunctions.net/createOrder", @Body order: Order): Call<Any>

//    @POST()
//    fun createOrder(@Url url: String = "http://10.10.11.102:5000/user-f06f7/us-central1/createOrder", @Body order: Order): Call<Any>

    @POST()
    fun createBooking(@Url url: String = "https://us-central1-user-f06f7.cloudfunctions.net/createBooking", @Body order: Order): Call<Any>

//    @POST()
//    fun createBooking(@Url url: String = "http://10.10.11.102:5000/user-f06f7/us-central1/createBooking", @Body order: Order): Call<Any>

    @POST()
    fun sendPush(@Url url: String = "https://us-central1-user-f06f7.cloudfunctions.net/sendPush", @Body notice: Notice): Call<Any>

//    @POST()
//    fun sendPush(@Url url: String = "http://10.10.11.102:5000/user-f06f7/us-central1/sendPush", @Body notice: Notice): Call<Any>

    @POST()
    fun sendEmail(@Url url: String = "https://us-central1-user-f06f7.cloudfunctions.net/sendEmail", @Body email_body: EmailItem): Call<Any>

//    @POST()
//    fun sendEmail(@Url url: String = "http://10.10.11.102:5000/user-f06f7/us-central1/sendEmail", @Body email_body: EmailItem): Call<Any>

    @POST()
    fun deleteCategory(@Url url: String = "https://us-central1-user-f06f7.cloudfunctions.net/deleteCategory", @Body deleteCategory: DeleteCategory): Call<Any>

    @POST()
    fun deleteServiceCategory(@Url url: String = "https://us-central1-user-f06f7.cloudfunctions.net/deleteServiceCategory", @Body deleteCategory: DeleteServiceCategory): Call<Any>
}