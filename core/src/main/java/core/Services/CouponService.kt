package core.Services

import com.app.fooddeliverysystem.R
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import core.Core
import core.Listeners.ServiceListener
import core.Models.Coupon
import core.Models.User
import core.Utils.CoreConstants
import java.util.*
import kotlin.collections.ArrayList


object CouponService {
    //        final String key = AppUtils.formatDate("dd MMM yyyy hh:mm:ss", new Date());
//        val path = "Files/Users/" + FirebaseAuth.getInstance().uid + "/Profile/" + file.getName()
    private val db = FirebaseFirestore.getInstance()
    private val couponCollection = db.collection(CoreConstants.Coupons_Coll )

    fun addUpdateCoupon(
        coupon: Coupon,
        listener: ServiceListener<String?, String?>
    ) {
        var msg: String = Core.getInstance().getString(R.string.coupon_updated)
        val id = if (coupon.id != null) {//Updating
            coupon.updatedAt = null
            coupon.id
        } else {//Adding
            msg = Core.getInstance().getString(R.string.coupon_added)
            coupon.id = couponCollection.document().getId()
            coupon.id
        }
        couponCollection.document(id!!).set(coupon).addOnCompleteListener {
            if (it.isSuccessful)
                listener.success(msg)
            else
                listener.error(it.exception?.message)
        }
    }

    fun deleteCoupon(id: String, listener: ServiceListener<String?, String?>) {
        couponCollection.document(id).delete().addOnCompleteListener {
            if (it.isSuccessful)
                listener.success(Core.getInstance().getString(R.string.coupon_deleted))
            else
                listener.error(it.exception?.message)
        }
    }

    fun getCoupon(code: String, deliveryDate: Date, listener: ServiceListener<Coupon, String?>) {
        couponCollection.whereEqualTo("code", code).get().addOnSuccessListener {
            val coupons = it.toObjects(Coupon::class.java)
            if (coupons.isNullOrEmpty())
                listener.error(Core.getInstance().getString(R.string.invalid_coupon))
            else {
                val coupon = coupons.first()
                if (deliveryDate.after(coupon.from) && deliveryDate.before(coupon.to))
                    if (coupon.usedBy.contains(User.getUser()?.id))
                        listener.error(Core.getInstance().getString(R.string.already_used_coupon))
                    else
                        listener.success(coupon)
                else
                    listener.error(Core.getInstance().getString(R.string.invalid_expired_coupon))
            }
        }.addOnFailureListener {
            listener.error(it.message)
        }
    }

    fun updateCouponUsedBy(
        couponId: String,
        listener: ServiceListener<String?, String?>
    ) {
        couponCollection.document(couponId)
            .update("usedBy", FieldValue.arrayUnion(User.getUser()!!.id)).addOnCompleteListener {
            if (it.isSuccessful)
                listener.success(Core.getInstance().getString(R.string.coupon_updated))
            else
                listener.error(it.exception?.message)
        }
    }

    private var liveListener: ListenerRegistration? = null
    var list: ArrayList<Coupon>? = null
    fun getAllCoupons(listener: ServiceListener<ArrayList<Coupon>, String>) {
        if (liveListener == null) {
            liveListener = couponCollection.addSnapshotListener { querySnapshot, exception
                ->
                if (querySnapshot != null) {
                    list = ArrayList(querySnapshot.toObjects(Coupon::class.java))
                    listener.success(list!!)
                } else {
                    listener.error(exception?.message!!)
                }
            }
        } else
            listener.success(list!!)
    }

    fun removeListener() {
        if (liveListener != null) {
            liveListener!!.remove()
            liveListener = null
        }
    }
}