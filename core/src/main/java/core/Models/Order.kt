package core.Models

import android.content.Context.MODE_PRIVATE
import com.google.firebase.firestore.ServerTimestamp
import com.google.gson.Gson
import com.google.gson.annotations.Expose
import com.google.gson.reflect.TypeToken
import core.Core
import core.Services.ObservableOrder
import core.Services.ProductService
import core.Utils.CoreConstants
import kotlinx.android.synthetic.main.item_order_details.view.*
import java.io.Serializable
import java.util.*
import kotlin.collections.ArrayList

class Order : Serializable {
    @Expose
    var id: String? = null

    @Expose
    var no: String? = null

    @Expose
    var customer: User? = null

    @Expose
    var driver: User? = null

    @Expose
    var coupon: Coupon? = null

    @Expose
    var points: Int? = null

    @Expose
    var points_discount: Double? = null

    @Expose
    var subTotal: String? = null

    @Expose
    var total: String? = null

    @Expose
    var shipping_cost: Int? = null

    @Expose
    var amount: Int? = null

    @Expose
    var token: String? = null

    @Expose
    var cod: Boolean = false

    @Expose
    var ifbooking: Boolean = false

    @Expose
    var products: ArrayList<CartItem> = ArrayList()

    @Expose
    var bookings: ArrayList<Booking> = ArrayList()

    @Expose
    var date: Date? = null

    @Expose
    var order_date: Long? = null

    @Expose
    var status: Int = CoreConstants.NEW_ORDER

    @Expose
    var order_note: String? = null

    @Expose
    var order_number: Int? = null

    @ServerTimestamp
    @Expose
    var createdAt: Date? = null

    @ServerTimestamp
    @Expose
    var updatedAt: Date? = null

    // for api
    @Expose
    var app_id: String? = null

    companion object {
        private val SP_KEY = "cart"

        //        private val TOKEN = "token"
        private val observableOrder = ObservableOrder.getInstance()

        fun addCartItem(cartItem: CartItem) {
            val pref = Core.getContext().getSharedPreferences(CoreConstants.PREFS_KEY, MODE_PRIVATE)
            val items = getCart()
            var index = items.indexOfFirst { item: CartItem -> ( item.product!!.id == cartItem.product!!.id && item.subProduct!!.id == cartItem.subProduct!!.id )}
            if (index != -1 ) {
                items[index].quantity = items[index].quantity + cartItem.quantity
            } else {
                items.add(cartItem)
            }

            val jsonItems = Gson().toJson(items)
            pref.edit().putString(SP_KEY, jsonItems).apply()
            calculatePriceCountAndUpdate(items)
        }

        fun removeCartItem(cartItem: CartItem) : Boolean {
            val pref = Core.getContext().getSharedPreferences(CoreConstants.PREFS_KEY, MODE_PRIVATE)
            val items = getCart()

            var removeIndex = items.indexOfFirst { item: CartItem -> ( item.product!!.id == cartItem.product!!.id && item.subProduct!!.id == cartItem.subProduct!!.id )}

            if (removeIndex != -1) {
                items.removeAt(removeIndex)
                val jsonItems = Gson().toJson(items)
                pref.edit().putString(SP_KEY, jsonItems).apply()
                calculatePriceCountAndUpdate(items)

                return true
            }
            else {
                return false
            }
        }

        fun getCart(): ArrayList<CartItem> {
            val pref = Core.getContext().getSharedPreferences(CoreConstants.PREFS_KEY, MODE_PRIVATE)
            val items = pref.getString(SP_KEY, "")
            val myType = object : TypeToken<ArrayList<CartItem>>() {}.type
            return if (items == "") ArrayList<CartItem>() else Gson().fromJson(
                items,
                myType
            )
        }

        fun clearCart() {
            val pref = Core.getContext().getSharedPreferences(CoreConstants.PREFS_KEY, MODE_PRIVATE)
            pref.edit().remove(SP_KEY).apply()
            observableOrder.change(Pair<Double, Int>(0.0, 0))
        }

        fun calculatePriceCountAndUpdate(cartItems: ArrayList<CartItem>) {
            var sub_total = 0.0
            for (item in cartItems) {
                val item_price = (if (item.product!!.isDiscounted()) item.product!!.discountedPrice() else item.product!!.price!!).toDouble()
                sub_total += item_price * item.quantity
            }
            observableOrder.change(Pair<Double, Int>(sub_total, cartItems.size))
        }

    }
}