package core.Services

import android.content.Intent
import com.app.fooddeliverysystem.R
import com.app.urantia.UserInterface.Activities.ChatDetailsActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.WriteBatch
import core.Core
import core.Listeners.ServiceListener
import core.Models.*
import core.UI.BaseActivity
import core.Utils.CoreConstants
import java.util.*
import kotlin.collections.ArrayList


object ChatService {
    //        final String key = AppUtils.formatDate("dd MMM yyyy hh:mm:ss", new Date());
//        val path = "Files/Users/" + FirebaseAuth.getInstance().uid + "/Profile/" + file.getName()
    private val db = FirebaseFirestore.getInstance()
    private val channelCollection = db.collection(CoreConstants.Channels_Coll )
    private val messageCollection = db.collection(CoreConstants.Messages_Coll )

    fun getCreateChat(
        memberType: String,
        listener: ServiceListener<Channel, String>
    ) {
        val user = User.getUser()!!
        channelCollection.whereEqualTo("member.id", user.id).whereEqualTo("active", true).get()
            .addOnSuccessListener {
                if (it.documents.isNullOrEmpty()) {
                    //Add New Channel
                    val channelID = channelCollection.document().id
                    val member = Member(user.id, user.name, memberType)
                    val messageID = messageCollection.document().id
                    val message = Message(
                        messageID,
                        channelID,
                        "您好，歡迎光臨，請問有什麼可以幫到您？",
                        "10",
                        user.id,
                        user.name,
                        memberType
                    )
                    val channel: Channel = Channel(channelID, member, null, message, true, false)
                    val batch = db.batch()
                    batch.set(channelCollection.document(channelID), channel)
                    batch.set(messageCollection.document(messageID), message)
                    batch.commit().addOnSuccessListener {
                        listener.success(channel)
                    }.addOnFailureListener {
                        listener.error(it.message!!)
                    }
                } else {
                    //Get Old Channel
                    val channel = it.documents.first().toObject(Channel::class.java)
                    listener.success(channel!!)
                }
            }.addOnFailureListener {
                listener.error(it.message!!)
            }
    }

    fun addUpdateMessage(
        message: Message,
        listener: ServiceListener<String?, String?>
    ) {
        var msg: String = "Message Updated"
        val id = if (message.id != null) {//Updating
            message.date = null
            message.id
        } else {//Adding
            msg = "Message Added"
            message.id = messageCollection.document().getId()
            message.id
        }
        val batch = db.batch()
        batch.set(messageCollection.document(id!!), message)
        batch.update(channelCollection.document(message.channelId!!), "message", message)
        batch.update(channelCollection.document(message.channelId!!), "read", false)
        batch.commit().addOnCompleteListener {
            if (it.isSuccessful)
                listener.success(msg)
            else
                listener.error(it.exception?.message)
        }
    }

    fun updateReadStatus(channel: Channel) {
        channelCollection.document(channel.id!!).update("read", true)
    }

    fun endChat(
        channel: Channel, senderType: String,
        listener: ServiceListener<String?, String?>
    ) {
        val user = User.getUser()!!
        val messageID = messageCollection.document().id
        val message = Message(
            messageID,
            channel.id,
            Core.getInstance().getString(R.string.session_ended),
            "10",
            user.id,
            user.name,
            senderType
        )
        val batch = db.batch()
        batch.update(channelCollection.document(channel.id!!), "active", false)
        batch.set(messageCollection.document(messageID), message)
        batch.commit().addOnCompleteListener {
            if (it.isSuccessful)
                listener.success(Core.getInstance().getString(R.string.session_ended))
            else
                listener.error(it.exception?.message)
        }
    }

    fun deleteMessage(id: String, listener: ServiceListener<String?, String?>) {
        messageCollection.document(id).delete().addOnCompleteListener {
            if (it.isSuccessful)
                listener.success("Message Deleted")
            else
                listener.error(it.exception?.message)
        }
    }

    private var channelLiveListener: ListenerRegistration? = null
    var channelsList: ArrayList<Channel>? = null
    fun getAllChannel(listener: ServiceListener<ArrayList<Channel>, String?>) {
        val user = User.getUser()!!
        if (channelLiveListener == null) {
            channelCollection
                .orderBy("message.date", Query.Direction.DESCENDING)
                .addSnapshotListener { querySnapshot, exception
                    ->
                    if (querySnapshot != null) {
                        channelsList = ArrayList(querySnapshot.toObjects(Channel::class.java))
                        listener.success(channelsList!!)
                    } else {
                        listener.error(exception?.message!!)
                    }
                }
        } else
            listener.success(channelsList!!)
    }

    fun removeChannelListener() {
        if (channelLiveListener != null) {
            channelLiveListener!!.remove()
            channelLiveListener = null
        }
    }

    private var liveListener: ListenerRegistration? = null
    var list: ArrayList<Message>? = null
    fun getAllMessages(channel: Channel, listener: ServiceListener<ArrayList<Message>, String>) {
        if (liveListener == null) {
            liveListener = messageCollection.whereEqualTo("channelId", channel.id)
                .orderBy("date", Query.Direction.ASCENDING)
                .addSnapshotListener { querySnapshot, exception
                    ->
                    if (querySnapshot != null) {
                        list = ArrayList(querySnapshot.toObjects(Message::class.java))
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

    fun getAllOldChannels(listener: ServiceListener<ArrayList<Channel>, String?>) {
        val user = User.getUser()!!
        channelCollection.whereEqualTo("admin.id", user.id).whereEqualTo("active", false)
            .orderBy("message.date", Query.Direction.ASCENDING)
            .addSnapshotListener { querySnapshot, exception
                ->
                if (querySnapshot != null) {
                    val channelsList = ArrayList(querySnapshot.toObjects(Channel::class.java))
                    listener.success(channelsList)
                } else {
                    listener.error(exception?.message!!)
                }
            }
    }

    fun getChannel(channelID: String, listener: ServiceListener<Channel, String?>) {
        channelCollection.document(channelID).get().addOnSuccessListener {
            val channel = it.toObject(Channel::class.java)
            if (channel == null)
                listener.error("Invalid channel!")
            else
                listener.success(channel)
        }.addOnFailureListener {
            listener.error(it.message)
        }
    }

    fun startChat(baseActivity: BaseActivity) {
        baseActivity.showLoader()
        ChatService.getCreateChat(
            CoreConstants.MEMBER_CUSTOMER,
            object : ServiceListener<Channel, String> {
                override fun success(success: Channel) {
                    baseActivity.hideLoader()
                    baseActivity.onStartActivity(
                        Intent(baseActivity, ChatDetailsActivity::class.java)
                            .apply {
                                putExtra("channel", success)
                                putExtra("senderType", CoreConstants.MEMBER_CUSTOMER)
                            }
                    )
                }

                override fun error(error: String) {
                    baseActivity.hideLoader()
                    baseActivity.toast(error)
                }
            })
    }

    fun startDriverChat(baseActivity: BaseActivity) {
        baseActivity.showLoader()
        ChatService.getCreateChat(
            CoreConstants.MEMBER_DRIVER,
            object : ServiceListener<Channel, String> {
                override fun success(success: Channel) {
                    baseActivity.hideLoader()
                    baseActivity.onStartActivity(
                        Intent(baseActivity, ChatDetailsActivity::class.java)
                            .apply {
                                putExtra("channel", success)
                                putExtra("senderType", CoreConstants.MEMBER_DRIVER)
                            }
                    )
                }

                override fun error(error: String) {
                    baseActivity.hideLoader()
                    baseActivity.toast(error)
                }
            })
    }
}