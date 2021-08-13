package com.ecbyheart.admin.UI.Activities

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.Window
import android.view.WindowManager
import com.ecbyheart.admin.R
import com.app.urantia.UserInterface.Activities.ChatDetailsActivity
import com.google.android.gms.tasks.OnCompleteListener
import core.Services.UserService
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.iid.FirebaseInstanceId
import core.Listeners.ServiceListener
import core.Models.User
import core.UI.BaseActivity
import core.Utils.AppLog
import core.Utils.CoreConstants

class SplashActivity : BaseActivity() {

    lateinit var channel_id : String
    lateinit var senderType: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        this.getWindow().setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN)

        channel_id = ""
        senderType = ""
        if (intent.hasExtra("channel_id")) {
            channel_id = intent.getStringExtra("channel_id")
        }
        if (intent.hasExtra("senderType")) {
            senderType = intent.getStringExtra("senderType")
        }

        setContentView(R.layout.activity_splash)
        UserService.setCollection(CoreConstants.Admins_Coll)
        setupComponents(this)
    }

    override fun initializeComponents() {
        FirebaseInstanceId.getInstance().instanceId
            .addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    AppLog.i(tag, "getInstanceId failed", task.exception)
                    return@OnCompleteListener
                }
                // Get new Instance ID token
                val token = task.result!!.token
                AppLog.i(tag, token)
//                Toast.makeText(this@SplashActivity, token, Toast.LENGTH_SHORT).show()
                User.saveToken(token)
            })
    }

    override fun setupListeners() {
        var attached: Boolean = false
        Handler().postDelayed(Runnable {
            val currentUser = FirebaseAuth.getInstance().currentUser
            if (currentUser != null) {
                if(channel_id != "" && senderType != "") {
                    val chatActivity =
                        Intent(this@SplashActivity, ChatDetailsActivity::class.java)
                    chatActivity.putExtra("channel_id", channel_id)
                    chatActivity.putExtra("senderType", senderType)
                    onStartActivityWithClearStack(chatActivity)
                }
                else{
                    onStartActivityWithClearStack(
                        Intent(
                            this@SplashActivity,
                            AdminHomeActivity::class.java
                        )
                    )
                }

            } else {
                val homeActivity = Intent(this@SplashActivity, LoginActivity::class.java)
                onStartActivityWithClearStack(homeActivity)
            }
        }, 2000)

        /*val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            UserService.startUserUpdates(object :ServiceListener<User?,String?>{
                override fun success(success: User?) {
                    if (!attached){
                        attached = true
                        onStartActivityWithClearStack(Intent(this@SplashActivity, AdminHomeActivity::class.java))
                    }
                }

                override fun error(error: String?) {
                    toast(error)
                }

            })
        }else{
            val homeActivity = Intent(this@SplashActivity, LoginActivity::class.java)
            onStartActivityWithClearStack(homeActivity)
        }*/
    }
}
