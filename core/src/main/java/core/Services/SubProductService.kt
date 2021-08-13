package core.Services

import com.google.firebase.firestore.CollectionReference
import core.Models.Product
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import core.Listeners.ServiceListener
import core.Utils.CoreConstants
import java.io.File


object SubProductService {
    //        final String key = AppUtils.formatDate("dd MMM yyyy hh:mm:ss", new Date());
//        val path = "Files/Users/" + FirebaseAuth.getInstance().uid + "/Profile/" + file.getName()



    private val db = FirebaseFirestore.getInstance()
    fun getCollection(p_id : String) : CollectionReference {
        return db.collection(CoreConstants.Products_Coll ).document(p_id).collection("sub_products")
    }

    fun addUpdateProduct(
        p_id: String,
        product: Product,
        file: File?,
        listener: ServiceListener<String?, String?>
    ) {
        var msg: String = "Product Updated"

        val id = if (product.id != null) {//Updating
            product.updatedAt = null
            product.id
        } else {//Adding
            msg = "Product Added"
            product.id = getCollection(p_id).document().getId()
            product.id
        }
        if (file == null) {//Updating product without file
            product.updatedAt = null
            getCollection(p_id).document(id!!).set(product).addOnCompleteListener {
                if (it.isSuccessful)
                    listener.success(msg)
                else
                    listener.error(it.exception?.message)
            }
        }
        else {
            val path = "Products/${id}/sub_${file.name}"
            FileServices.UploadFile(file, path, object : ServiceListener<String?, String?> {
                override fun success(url: String?) {
                    product.image = url
                    getCollection(p_id).document(id!!).set(product).addOnCompleteListener {
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

    fun deleteProduct(p_id : String, id: String, listener: ServiceListener<String?, String?>) {
        getCollection(p_id).document(id).delete().addOnCompleteListener {
            if (it.isSuccessful)
                listener.success("Product Deleted!")
            else
                listener.error(it.exception?.message)
        }
    }

    fun updateCategory(p_id : String, old_cat : String, new_cat : String, listener: ServiceListener<String?, String?>) {
        getCollection(p_id).whereEqualTo("catId", old_cat).get().addOnSuccessListener {
            if (it != null) {
                var tmp_items = ArrayList(it.toObjects(Product::class.java))

                tmp_items.forEach { item ->
                    var new_item = item
                    new_item.catId = new_cat
                    getCollection(p_id).document(new_item.id!!).set(new_item)
                }

                listener.success("Success")
            } else {
                listener.success("Success")
            }
        }.addOnFailureListener {
            listener.error(it.message!!)
        }
    }

    fun deleteCategory(p_id : String, cat_id : String, listener: ServiceListener<String?, String?>) {
        getCollection(p_id).whereEqualTo("catId", cat_id).get().addOnSuccessListener {
            if (it != null) {
                var tmp_items = ArrayList(it.toObjects(Product::class.java))

                tmp_items.forEach { item ->
                    getCollection(p_id).document(item.id!!).delete()
                }

                listener.success("Success")
            } else {
                listener.success("Success")
            }
        }.addOnFailureListener {
            listener.error(it.message!!)
        }
    }


    private var liveListener: ListenerRegistration? = null
    var list: ArrayList<Product>? = null
    fun getAllProductsListener(p_id : String, listener: ServiceListener<ArrayList<Product>, String>) {
        if (liveListener == null) {
            liveListener = getCollection(p_id).addSnapshotListener { querySnapshot, exception
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

    fun getAllProducts(p_id : String,  listener: ServiceListener<ArrayList<Product>, String>) {
        getCollection(p_id).whereEqualTo("enable", true).get().addOnSuccessListener {
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