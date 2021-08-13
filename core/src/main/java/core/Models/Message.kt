package core.Models

import com.google.firebase.firestore.ServerTimestamp
import java.io.Serializable
import java.util.*

class Message :Serializable {
    var id: String? = null
    var channelId: String? = null
    var message: String? = null
    var msgType: String? = null
    var senderId: String? = null
    var senderName: String? = null
    var senderType: String? = null
    @ServerTimestamp
    var date: Date? = null

    constructor()

    constructor(id: String?, channelId: String?, message: String?, msgType: String?, senderId: String?, senderName: String?, senderType: String?) {
        this.id = id
        this.channelId = channelId
        this.message = message
        this.msgType = msgType
        this.senderId = senderId
        this.senderName = senderName
        this.senderType = senderType
    }
}