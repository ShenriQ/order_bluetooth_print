package com.app.urantia.UserInterface.Activities

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.app.fooddeliverysystem.R
import com.app.urantia.Adapters.ChatDetailsAdapter
import core.Listeners.ServiceListener
import core.Models.Channel
import core.Models.Message
import core.Models.User
import core.Services.ChatService
import core.UI.BaseActivity
import core.Utils.CoreConstants

class ChatDetailsActivity : BaseActivity() {

    lateinit var title: TextView
    lateinit var back: ImageView
//    lateinit var videoCall: ImageView
//    lateinit var room_id: String
//    var msg_type: String = ChatConstants.ONE_TO_ONE
//    lateinit var members: ArrayList<ChatUser>

    lateinit var channel: Channel
    lateinit var channel_id : String
    lateinit var senderType: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        channel = Channel()
        if (intent.hasExtra("channel")) {
            channel = intent.getSerializableExtra("channel") as Channel
        }
        channel_id = ""
        if (intent.hasExtra("channel_id")) {
            channel_id = intent.getStringExtra("channel_id")
        }
        if (intent.hasExtra("senderType")) {
            senderType = intent.getStringExtra("senderType")
        }
        setContentView(R.layout.activity_chat_details)

//        room_id = intent.getStringExtra("room")
//        msg_type = intent.getStringExtra("msg_type")
//        members = intent.getSerializableExtra("members") as ArrayList<ChatUser>
//        fileChooser = FileChooser(this)
        setupComponents(this)
    }

    lateinit var msgBox: EditText
    lateinit var send: ImageView
    lateinit var listView : RecyclerView

    val list = ArrayList<Message>();//Constants.demoArray.subList(0, 2)

    override fun initializeComponents() {
        /*title = findViewById(R.id.title)
        title.text = "Chat Details"
        title.visibility = View.VISIBLE

        back = findViewById(R.id.back)
        back.setImageResource(R.drawable.back)
        back.visibility = View.VISIBLE
        back.setOnClickListener {
//            val intent = Intent(this, ChatListActivity::class.java)
//            intent.putExtra("msg_type", msg_type)
//            onStartActivityWithRemoveRange(intent)
        }*/

        msgBox = findViewById(R.id.msgBox)
        send = findViewById(R.id.send)
        listView = findViewById<RecyclerView>(R.id.listView)
    }

    lateinit var adapter: ChatDetailsAdapter
    override fun setupListeners() {
        setTitle("客戶服務")
        setToolbar {
            if(channel_id == "") {
                onGoBack()
            }
            else {
                val intent = packageManager.getLaunchIntentForPackage(packageName)
                onStartActivityWithClearStack(
                    intent
                )
            }
        }
        if (senderType == CoreConstants.MEMBER_ADMIN) {
            chatButton {
                showDialog(
                    "End Session",
                    "Do you want to end session?",
                    "End",
                    DialogInterface.OnClickListener { dialogInterface, i ->
                        endChat()
                    },
                    "Cancel",
                    null
                )
            }
        }

        if(channel.id == null) {
            showLoader()
            ChatService.getChannel(channel_id, object : ServiceListener<Channel, String?> {
                override fun success(success: Channel) {
                    hideLoader()
                    channel = success
                    configChannel(channel)
                }

                override fun error(error: String?) {
                    hideLoader()
                    toast(error)
                }
            })
        }
        else {
            configChannel(channel)
        }

    }

    fun configChannel(ch: Channel) {
        adapter = ChatDetailsAdapter(
            this@ChatDetailsActivity,
            list,
            R.layout.chat_text_item_2,
            ch,
            null
        )
        listView.adapter = adapter

        setData(ch)

        send.setOnClickListener {
            if (msgBox.text.isNotEmpty()) {
                val user = User.getUser()!!
                ChatService.addUpdateMessage(Message(
                    null, ch.id, msgBox.text.toString(), "1",
                    user.id, user.name, senderType
                ), object : ServiceListener<String?, String?> {
                    override fun success(success: String?) {
                        closeKeyboard()
                        msgBox.setText(null)
                    }

                    override fun error(error: String?) {
                        toast(error)
                    }
                })
            }
        }
    }

    fun setData(ch: Channel) {
        showLoader()
        ChatService.getAllMessages(ch, object : ServiceListener<ArrayList<Message>, String> {
            override fun success(success: ArrayList<Message>) {
                hideLoader()
                adapter.setData(success)
            }

            override fun error(error: String) {
                hideLoader()
                toast(error)
            }
        })
        if(!ch.active){
            send.isEnabled = false
            msgBox.isEnabled = false
        }
    }

    fun endChat() {
        if (channel == null) {
            return
        }
        showLoader()
        ChatService.endChat(channel, senderType, object : ServiceListener<String?, String?> {
            override fun success(success: String?) {
                hideLoader()
                toast(success)
                onGoBack()
            }

            override fun error(error: String?) {
                hideLoader()
                toast(error)
            }
        })
    }

//    override fun onBackPressed() {
//        val intent = Intent(this, ChatListActivity::class.java)
//        intent.putExtra("msg_type", msg_type)
//        onStartActivityWithRemoveRange(intent)
//    }


    override fun onDestroy() {
        ChatService.removeListener()
        if (channel != null) {
            if (channel.admin != null && channel.admin?.id == User.getUser()!!.id)
                ChatService.updateReadStatus(channel)
        }
        super.onDestroy()
    }
}
