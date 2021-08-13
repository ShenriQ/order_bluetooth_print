package core.Services

import android.net.Uri
import com.google.firebase.storage.FirebaseStorage
import core.Listeners.ServiceListener
import java.io.File


object FileServices {
    val TAG = FileServices.javaClass.simpleName
//    var firestore = FirebaseFirestore.getInstance()
    fun UploadFile(
        file: File?,
        path: String?,
        listener: ServiceListener<String?, String?>
    ) {
        val storageRef = FirebaseStorage.getInstance().reference
        val fileUri: Uri = Uri.fromFile(file)
        //        Files/workspaceid/Users/userid/filename
        val pathRef = storageRef.child(path!!)
        val uploadTask = pathRef.putFile(fileUri)
        uploadTask.addOnFailureListener { exception -> // Handle unsuccessful uploads
            listener.error(exception.message)
        }.addOnSuccessListener {
            pathRef.downloadUrl.addOnSuccessListener { uri ->
                listener.success(uri.toString())
            }
        } /*.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                task.getResult().getStorage().getDownloadUrl();
            }
        });*/
    }

    fun DeleteFile(
        path: String?,
        listener: ServiceListener<String?, String?>
    ) {
        val storageRef = FirebaseStorage.getInstance().reference
        val desertRef = storageRef.child(path!!)
        desertRef.delete().addOnSuccessListener { // File deleted successfully
            listener.success("File Deleted")
        }.addOnFailureListener { exception -> // Uh-oh, an error occurred!
            listener.error(exception.message)
        }
    }
}