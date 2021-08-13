package core.Services

import core.Models.Product
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import core.Listeners.ServiceListener
import core.Utils.CoreConstants
import java.io.File


object ProductService {
    //        final String key = AppUtils.formatDate("dd MMM yyyy hh:mm:ss", new Date());
//        val path = "Files/Users/" + FirebaseAuth.getInstance().uid + "/Profile/" + file.getName()
    private val db = FirebaseFirestore.getInstance()
    val productCollection = db.collection(CoreConstants.Products_Coll )

    fun addUpdateProduct(
        product: Product,
        file: File?,
        listener: ServiceListener<String?, String?>
    ) {
        var msg: String = "Product Updated"
        if (file == null) {//Updating product without file
            product.updatedAt = null
            productCollection.document(product.id!!).set(product).addOnCompleteListener {
                if (it.isSuccessful)
                    listener.success(msg)
                else
                    listener.error(it.exception?.message)
            }
        } else {
            val id = if (product.id != null) {//Updating
                product.updatedAt = null
                product.id
            } else {//Adding
                msg = "Product Added"
                product.id = productCollection.document().getId()
                product.id
            }
            val path = "Products/${id}/${file.name}"
            FileServices.UploadFile(file, path, object : ServiceListener<String?, String?> {
                override fun success(url: String?) {
                    product.image = url
                    productCollection.document(id!!).set(product).addOnCompleteListener {
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

    fun deleteProduct(id: String, listener: ServiceListener<String?, String?>) {
        productCollection.document(id).delete().addOnCompleteListener {
            if (it.isSuccessful)
                listener.success("Product Deleted!")
            else
                listener.error(it.exception?.message)
        }
    }

    private var liveListener: ListenerRegistration? = null
    var list: ArrayList<Product>? = null
    fun getAllProductsListener(listener: ServiceListener<ArrayList<Product>, String>) {
        if (liveListener == null) {
            liveListener = productCollection.addSnapshotListener { querySnapshot, exception
                ->
                if (querySnapshot != null) {
                    list = ArrayList(querySnapshot.toObjects(Product::class.java))
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

    var today_list: ArrayList<Product>? = null
    fun getAllProductsOnToday(date_str : String, listener: ServiceListener<ArrayList<Product>, String>) {
        productCollection.whereEqualTo("enable", true).get().addOnSuccessListener {
            if (it != null) {
                today_list = ArrayList(it.toObjects(Product::class.java))
                today_list = today_list!!.filter { item -> item.avail_dates?.contains(date_str) == true || item.everyday == true} as ArrayList<Product>
                listener.success(today_list!!)
            } else {
                today_list = ArrayList()
                listener.success(today_list!!)
            }

        }.addOnFailureListener {
            listener.error(it.message!!)
        }
    }

    fun getAllProducts(listener: ServiceListener<ArrayList<Product>, String>) {
        productCollection.whereEqualTo("enable", true).get().addOnSuccessListener {
            if (it != null) {
                list = ArrayList(it.toObjects(Product::class.java))
                listener.success(list!!)
            } else {
                list = ArrayList()
                listener.success(list!!)
            }

        }.addOnFailureListener {
            listener.error(it.message!!)
        }
    }
}