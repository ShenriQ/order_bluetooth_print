package com.app.urantia.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.api.load
import com.app.fooddeliverysystem.R
import core.Models.Channel
import core.Models.Message
import core.Models.User
import core.UI.BaseActivity
import core.Utils.AppUtils
import core.Utils.CoreConstants

class ChatDetailsAdapter(
    private val mContext: BaseActivity,
    private val items: ArrayList<Message>,
    private var resources: Int,
    private var channel: Channel,
    private val listener: OnItemClickListener?
) : RecyclerView.Adapter<ChatDetailsAdapter.ViewHolder>() {
    lateinit var user: User

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }

    init {
        user = User.getUser()!!
    }//        items = Constants.linksArray;

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var v: View? = null
        v = when (viewType) {
            1 -> LayoutInflater.from(parent.context)
                .inflate(R.layout.chat_text_item_1, parent, false)
            2 -> LayoutInflater.from(parent.context)
                .inflate(R.layout.chat_text_item_2, parent, false)
            10 -> LayoutInflater.from(parent.context)
                .inflate(R.layout.chat_admin_item, parent, false)
            else ->
                LayoutInflater.from(parent.context).inflate(resources, parent, false)
        }
        return ViewHolder(itemView = v)
    }

    override fun getItemViewType(position: Int): Int {
        val item: Message = items[position]
        when {//senderID = cuX9mZeTxxOqmlPtQXZZpN8WvSd2 , adminID = b8Wlw3YL4GPvr6VgAPVgcim2PRY2 , customerID = cuX9mZeTxxOqmlPtQXZZpN8WvSd2
            user.id.equals(item.senderId) -> {
                return when (item.msgType) {
                    "1" -> 1
                    "10" -> 10
                    else -> 1
                }
            }
            else -> {
                when (item.msgType) {
                    "1" -> return 2;
                    "10" -> return 10;
                    else -> return 2
                }
            }
//            "Image" -> return 1
//            "Video" -> return 2
//            else ->
//                return 0;
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position, listener)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        //        private val richLinkView: RichLinkViewTelegram
        private val msg: TextView?
        private val name: TextView?
        private val time: TextView?
        private val imageView: ImageView?
        private val icon: ImageView?

        init {
//            richLinkView = itemView.findViewById(R.id.richLinkView)
            msg = itemView.findViewById(R.id.msg)
            name = itemView.findViewById(R.id.name)
            time = itemView.findViewById(R.id.time)
            imageView = itemView.findViewById(R.id.imageView)
            icon = itemView.findViewById(R.id.image)
        }

        fun bind(position: Int, listener: OnItemClickListener?) {
            var message: Message = items[position];
            if (name != null) {
                name.setText(message.senderName)
                icon?.load(when {
                    channel.member?.type == CoreConstants.MEMBER_CUSTOMER && message.senderId == channel.member?.id-> {
//                        name.setText(channel.member?.name)
                        R.drawable.member
                    }
                    channel.member?.type == CoreConstants.MEMBER_DRIVER && message.senderId == channel.member?.id-> {
//                        name.setText(channel.member?.name)
                        R.drawable.driver
                    }
                    else -> {
//                        name.setText(channel.admin?.name)
                        R.drawable.admin
                    }
                })
                /*when {//senderID = cuX9mZeTxxOqmlPtQXZZpN8WvSd2 , adminID = b8Wlw3YL4GPvr6VgAPVgcim2PRY2 , customerID = cuX9mZeTxxOqmlPtQXZZpN8WvSd2
                    user.id.equals(message.senderId) -> {
                        icon?.load(
                            when (channel.member?.type) {
                                CoreConstants.MEMBER_CUSTOMER -> {
                                    name.setText(channel.member?.name)
                                    R.drawable.member
                                }
                                CoreConstants.MEMBER_DRIVER -> {
                                    name.setText(channel.member?.name)
                                    R.drawable.driver
                                }
                                else -> {
                                    name.setText(channel.admin?.name)
                                    R.drawable.admin
                                }
                            }
                        )
//                        if (channel.member?.type == CoreConstants.MEMBER_CUSTOMER)
//                            icon?.load(R.drawable.member)
//                        else
//                            icon?.load(R.drawable.driver)
                    }
                    else -> {
                        icon?.load(
                            when (channel.member?.type) {
                                CoreConstants.MEMBER_CUSTOMER -> {
                                    name.setText(channel.member?.name)
                                    R.drawable.member
                                }
                                CoreConstants.MEMBER_DRIVER -> {
                                    name.setText(channel.member?.name)
                                    R.drawable.driver
                                }
                                else -> {
                                    name.setText(channel.admin?.name)
                                    R.drawable.admin
                                }
                            }
                        )
                    }
                }*/

//                Glide.with(icon.context).load(receiver.image_url).into(icon)
            }
            if (time != null) {
                time.setText(AppUtils.formatDate("yyyy年MM月dd日 HH:mm", message.date))
            }

            var image: String? = null
            when (message.msgType) {
                "1" -> {
                    msg!!.text = message.message
                }
                "10" -> {
                    msg!!.text = message.message
                }
            }
            itemView.setOnClickListener {
                listener?.onItemClick(position)
            }
        }
//        fun getReceiver():ChatUser?{
//            var receiver: ChatUser? = null
//            var receiverID: String? = null
//            for (member in (mContext as ChatDetailsActivity).members) {
//                if (member.id != user.id) {
//                    receiver = member
//                    receiverID = member.id
//                    return receiver
//                }
//            }
//            return null
//        }
    }


    fun addAll(collection: Array<Message>) {
        items.addAll(collection)
//        backup.addAll(collection)
        notifyDataSetChanged()
    }

    fun addAll(collection: Collection<Message>) {
        items.addAll(collection)
//        backup.addAll(collection)
        notifyDataSetChanged()
    }

    fun setData(collection: Collection<Message>) {
        clear()
        addAll(collection)
    }

    fun clear() {
        items.clear()
//        backup.clear()
        notifyDataSetChanged()
    }
}