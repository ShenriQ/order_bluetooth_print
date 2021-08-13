package com.ecbyheart.admin.UI.Activities

import android.content.Intent
import android.os.Bundle
import android.widget.*
import com.ecbyheart.admin.R
import core.Services.UserService
import core.Listeners.ServiceListener
import core.Models.User
import core.UI.BaseActivity

class RegisterActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        setupComponents(this)
    }

    lateinit var phone: EditText
    lateinit var name: EditText
    lateinit var email: EditText
    lateinit var backbtn: ImageButton
    lateinit var register: Button
    lateinit var agreement: CheckBox
    lateinit var promotions: CheckBox
    override fun initializeComponents() {
        backbtn = findViewById(R.id.back)
        register = findViewById(R.id.register)
        phone = findViewById(R.id.phone)
        name = findViewById(R.id.name)
        email = findViewById(R.id.email)
        agreement = findViewById(R.id.agreement)
        promotions = findViewById(R.id.promotions)
    }

    override fun setupListeners() {
        register.setOnClickListener {
            val validated = when {
                name.text.isEmpty() -> {
                    name.error = getString(R.string.enter_name)
                    false
                }
                phone.text.isEmpty() -> {
                    phone.error = getString(R.string.enter_number)
                    false
                }
                email.text.isEmpty() -> {
                    email.error = getString(R.string.enter_email)
                    false
                }
                !agreement.isChecked -> {
                    toast(getString(R.string.please_accept_agreement))
                    false
                }
                !promotions.isChecked -> {
                    toast(getString(R.string.please_allow_promotions))
                    false
                }
                else -> true
            }
            if (validated) {
                val phoneNum = "+852${phone.text.toString()}"
                showLoader()
                UserService.isPhoneRegistered(phoneNum,
                    object : ServiceListener<Boolean?, String?> {
                        override fun success(isRegistered: Boolean?) {
                            hideLoader()
                            if (isRegistered!!)
                                toast(getString(R.string.number_already_registered))
                            else {
                                val user = User().apply {
                                    name = this@RegisterActivity.name.text.toString()
                                    phone = phoneNum
                                    email = this@RegisterActivity.email.text.toString()
                                }
                                val intent = Intent(
                                    this@RegisterActivity,
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
        backbtn.setOnClickListener {
            onStartActivityWithClearStack(
                Intent(this, LoginActivity::class.java)
            )
        }
    }
}
