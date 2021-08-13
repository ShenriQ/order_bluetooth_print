package com.ecbyheart.admin.UI.Activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.ecbyheart.admin.R
import core.Core
import core.Services.UserService
import core.Listeners.ServiceListener
import core.Models.User
import core.UI.BaseActivity
import core.Utils.CoreConstants

class LoginActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        setupComponents(this)
    }

    lateinit var login: Button
    lateinit var phone: EditText
    lateinit var register: TextView
    override fun initializeComponents() {
        login = findViewById(R.id.login)
        register = findViewById(R.id.register)
        phone = findViewById(R.id.phone)
    }

    override fun setupListeners() {
        login.setOnClickListener {
            val validated = when {
                phone.text.isEmpty() -> {
                    phone.error = getString(R.string.invalid_phone_number)
                    false
                }
                else -> true
            }
            if (validated) {
                showLoader()
                UserService.isPhoneRegistered("+852${phone.text.toString()}",
                    object : ServiceListener<Boolean?, String?> {
                        override fun success(isRegistered: Boolean?) {
                            hideLoader()
                            if (!isRegistered!!)
                                toast(getString(R.string.no_user_registered_with_this_number))
                            else {
                                val user = User().apply {
                                    phone = "+852${this@LoginActivity.phone.text.toString()}"
                                }
                                val intent = Intent(
                                    this@LoginActivity,
                                    AdminVerifyActivity::class.java
                                )
                                onStartActivity(intent.apply {
                                    putExtra("user", user)
                                })
                            }
                        }

                        override fun error(error: String?) {
                            hideLoader()
                            toast(error)
                        }
                    })
            }
        }
        register.setOnClickListener {
            onStartActivityWithClearStack(
                Intent(this, RegisterActivity::class.java)
            )
        }
    }

}
