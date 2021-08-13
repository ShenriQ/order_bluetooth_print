package com.app.urantia.Models

import java.io.Serializable

open class BaseModel<T> : Serializable {
    init {
        System.out.println("OkHttp: ******************************")
    }
    var code: Int = 0
    var message: String = ""
    open var data:T? = null
    var meta:MetaModel? = null
    var links:Links? = null
}

data class MetaModel(val current_page: Int, val from: Int, val last_page: Int, val to: Int, val total: Int) /*: BaseModel<Any>()*/

data class Links(val first:String?, val last:String?, val prev:String?, val next:String?)