package core.Services

import androidx.annotation.NonNull
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.navbus.driver.android.utils.HelperMethods
import core.Listeners.ServiceListener
import core.Models.Category
import core.Models.DeleteCategory
import core.Utils.CoreConstants
import core.Utils.GsonUtils
import core.Utils.NetworkUtils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File


object CategoryService {
    //        final String key = AppUtils.formatDate("dd MMM yyyy hh:mm:ss", new Date());
//        val path = "Files/Users/" + FirebaseAuth.getInstance().uid + "/Profile/" + file.getName()
    private val db = FirebaseFirestore.getInstance()
    private val categoryCollection = db.collection(CoreConstants.Categories_Coll )

    fun addUpdateCategory(
        category: Category,
        file: File?,
        listener: ServiceListener<String?, String?>
    ) {
        var msg: String = "Category Updated"
        if (file == null) {//Updating category without file
            category.updatedAt = null
            categoryCollection.document(category.id!!).set(category).addOnCompleteListener {
                if (it.isSuccessful)
                    listener.success(msg)
                else
                    listener.error(it.exception?.message)
            }
        } else {
            val id = if (category.id != null)
            {//Updating
                category.updatedAt = null
                category.id
            } else {//Adding
                msg = "Category Added"
                category.id = categoryCollection.document().getId()
                category.id
            }
            val path = "Categories/${id}/${file.name}"
            FileServices.UploadFile(file, path, object : ServiceListener<String?, String?> {
                override fun success(url: String?) {
                    category.image = url
                    categoryCollection.document(id!!).set(category).addOnCompleteListener {
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

    fun deleteCategory(categoryID:String, listener: ServiceListener<String?, String?>){
        val del_cat = DeleteCategory(categoryID, CoreConstants.APP_ID);
        NetworkUtils.getAPIService().deleteCategory(deleteCategory = del_cat)
            .enqueue(object :
                Callback<Any> {
                override fun onFailure(@NonNull call: Call<Any>, @NonNull t: Throwable) {
                    listener.error(t.message)
                }

                override fun onResponse(
                    @NonNull call: Call<Any>,
                    @NonNull response: Response<Any>
                ) {
                    if (HelperMethods.isValidHttpResponse(response)) {
                        val jsonObject = GsonUtils.toJSON(response.body())
                        if (jsonObject.getBoolean("success")) {
                            listener.success(jsonObject.getString("message"))
                        }else
                        listener.error(jsonObject.getString("message"))
//                        listener.error(response.body()!!.toString())
                    } else listener.error(
                        response.errorBody()?.string()
//                    HelperMethods.getErrorMessage(response)
                    )
                }
            })
    }
    private var liveListener: ListenerRegistration? = null
    var list: ArrayList<Category>? = null
    fun getAllCategoriesListener(listener: ServiceListener<ArrayList<Category>, String>) {
        if (liveListener == null) {
            liveListener = categoryCollection.addSnapshotListener { querySnapshot, exception
                ->
                if (querySnapshot != null) {
                    list = ArrayList(querySnapshot.toObjects(Category::class.java))
                    list?.sortWith(Comparator { o1: Category, o2: Category -> o1.getW().compareTo(o2.getW()) })
                    listener.success(list!!)
                } else {
                    listener.error(exception?.message!!)
                }
            }
        }else
            listener.success(list!!)
    }

    fun removeListener() {
        if (liveListener != null) {
            liveListener!!.remove()
            liveListener = null
        }
    }

    fun getAllCategories(listener: ServiceListener<ArrayList<Category>, String>) {
        categoryCollection.whereEqualTo("enable", true).get().addOnSuccessListener {
            if (it != null) {
                list = ArrayList(it.toObjects(Category::class.java))
//                list?.sortWith(Comparator { o1: Category, o2: Category -> o1.title!!.compareTo(o2.title!!) })
                list?.sortWith(Comparator { o1: Category, o2: Category -> o1.getW().compareTo(o2.getW()) })
                listener.success(list!!)
            } else {
                list = ArrayList()
                listener.success(list!!)
            }

        }.addOnFailureListener {
            listener.error(it.message!!)
        }
    }

    fun getCategory(categoryID:String, listener: ServiceListener<Category?, String>) {
        categoryCollection.document(categoryID).get().addOnSuccessListener {
            if (it != null) {
                listener.success(it.toObject(Category::class.java))
            } else {
                listener.success(null)
            }

        }.addOnFailureListener {
            listener.error(it.message!!)
        }
    }
}