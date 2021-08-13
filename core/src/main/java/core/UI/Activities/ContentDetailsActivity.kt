package core.UI.Activities

import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import com.app.fooddeliverysystem.R
import core.Listeners.ServiceListener
import core.Models.User
import core.Services.ChatService
import core.Services.ContantService
import core.UI.BaseActivity

class ContentDetailsActivity : BaseActivity() {

    lateinit var content: String
    lateinit var trans: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_content)
        if (intent.hasExtra("content")) {
            content = intent.getStringExtra("content")!!
            trans = intent.getStringExtra("trans")!!
        }
        setupComponents(this)
    }


    lateinit var save: Button
    lateinit var contentText: TextView
    lateinit var contentTitle: TextView
    override fun initializeComponents() {
        contentTitle = findViewById(R.id.contentTitle)
        contentText = findViewById(R.id.contentText)
        save = findViewById(R.id.save)
        setData()
    }

    fun setData() {
        showLoader()
        ContantService.getContent(content, object : ServiceListener<String, String> {
            override fun success(success: String) {
                hideLoader()
                contentText.setText(success)
            }

            override fun error(error: String) {
                hideLoader()
                toast(error)
            }
        })
    }

    override fun setupListeners() {
        setToolbar { onGoBack() }
        setTitle(trans)
        chatButton {
            ChatService.startChat(this)
        }

        contentTitle.setText(trans)
        contentTitle.setTypeface(contentTitle.getTypeface(), Typeface.BOLD);
        contentTitle.visibility = View.GONE

        contentText.isFocusable = false
        contentText.setHint("Loading..")
        contentText.background = null
        contentText.setPadding(0,0,0,0)
        save.visibility = View.GONE
    }

}
