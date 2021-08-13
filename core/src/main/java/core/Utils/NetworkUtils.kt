package core.Utils

import com.google.gson.GsonBuilder
import core.Core
import okhttp3.Cache
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.util.concurrent.TimeUnit

object NetworkUtils {
    private var retrofit: Retrofit? = null

    public fun getAPIService(): Api {
        return getClient()!!.create(Api::class.java)
    }

    private fun getClient(): Retrofit? {
        if (retrofit == null) {
            val logging = HttpLoggingInterceptor()
            logging.level = HttpLoggingInterceptor.Level.BODY

            val httpClient: OkHttpClient.Builder = OkHttpClient.Builder()
            httpClient.addInterceptor(logging)
            httpClient.addInterceptor((Interceptor {
                it.proceed(
                    it.request().newBuilder()
                        //.header("token", "api.Pd*!(5675")
//                        .header(
//                            "Authorization",
//                            if (User.getUser()?.token == null)
//                                ""
//                            else
//                                "Bearer "+ User.getUser()!!.token
//                        )
                        .build()
                )
            }))
            val httpCacheDirectory = File(Core.getContext().getCacheDir(), "responses")
            val cacheSize = 10 * 1024 * 1024
            httpClient.cache(Cache(httpCacheDirectory, cacheSize.toLong()))
            httpClient.readTimeout(60, TimeUnit.SECONDS)
            httpClient.connectTimeout(60, TimeUnit.SECONDS)
            httpClient.writeTimeout(60, TimeUnit.SECONDS)

            retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
//                .addConverterFactory(GsonConverterFactory.create())
                .addConverterFactory(
                    GsonConverterFactory.create(
                        GsonBuilder().excludeFieldsWithoutExposeAnnotation().create()
                    )
                )
                .client(httpClient.build())
                .build()
        }
        return retrofit
    }

    /////////////////////////////////////////////////////////////////////////////
//    all as build is sent to client. Data from database is removed. For Further development use following URLs in your app configuration as base url
//    REST API: http://69.162.81.82:1001/
//    Socket: http://69.162.81.82:1002/
//    Admin Panel: http://69.162.81.82:1003/
//    ------------
    private const val BASE_URL = "http://69.162.81.82:3333/api/v1/" //Live

    //    private const val BASE_URL = "http://69.162.81.82:1001/api/v1/" //Debug
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val UPDATE = "user/update"
    const val VERIFY_CODE = "verify"
    const val RESEND_CODE = "resendCode"
    const val FORGOT_PASSWORD = "password/forgot"
    const val RESET_PASSWORD = "password/reset"
    const val CHANGE_PASSWORD = "password/change"
    const val LOGOUT = "logout"
    const val COMMON_SERVICES = "getCommonServices"
    const val ME = "user/me"
    const val GET_ROUTE = "getRouteByBusNumber"
    const val GET_HISTORY = "getHistory"
    const val GET_VEHICLE_DETAILS = "getVehicleDetails"
    const val GET_NOTIFICATIONS = "getNotifications"
}