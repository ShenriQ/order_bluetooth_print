package com.ecbyheart.admin.UI.Activities

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.Switch
import androidx.recyclerview.widget.RecyclerView
import com.ecbyheart.admin.Adapters.AdminHomeAdapter
import com.ecbyheart.admin.R
import com.ecbyheart.admin.Services.AdminsService
import core.Services.CategoryService
import core.Services.UserService
import core.Listeners.ServiceListener
import core.Models.Category
import core.Models.ImpInfo
import core.Models.User
import core.Services.ContantService
import core.Services.ObservableUser
import core.UI.BaseActivity
import core.Utils.AppLog
import java.util.*
import kotlin.collections.ArrayList

class AdminHomeActivity : BaseActivity(), java.util.Observer {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_home)
        ObservableUser.getInstance().addObserver(this)
        setupComponents(this)
        showLoader()
        UserService.startUserUpdates(object :
            ServiceListener<User?, String?> {
            override fun success(success: User?) {
                hideLoader()
                AppLog.i("User Update Service: ", "Started!");
                setUserData()
                getEnabledMenus()
                UserService.updateDeviceToken()
            }

            override fun error(error: String?) {
                hideLoader()
                AppLog.i("User Update Service: ", error);
            }
        });
        ContantService.getImpInfo(object :ServiceListener<ImpInfo,String>{
            override fun success(success: ImpInfo) {
                AppLog.i("impInfo: ", "Date Fetched!")
            }

            override fun error(error: String) {
                AppLog.i("impInfo: ", "Failed!")
            }
        })
    }

    var menu = ArrayList<String>()
    lateinit var adminHomeAdaper : AdminHomeAdapter
    lateinit var logout: LinearLayout
    lateinit var chatBtn: LinearLayout
    lateinit var onlineSwitch: Switch
    lateinit var listView: RecyclerView
    override fun initializeComponents() {
        logout = findViewById(R.id.logout)
        onlineSwitch = findViewById(R.id.online)
        listView = findViewById(R.id.listView)
        chatBtn = findViewById(R.id.chatBtn)

        adminHomeAdaper = AdminHomeAdapter(
            menu,
            R.layout.item_admin_homebtn,
            object : AdminHomeAdapter.OnItemClickListener {
                override fun onItemClick(position: Int) {
                    onStartActivity(
                        Intent(
                            this@AdminHomeActivity, when (menu.get(position)) {
                                getString(R.string.menu_orders) -> {
                                    OrdersListActivity::class.java
                                }
                                getString(R.string.menu_booking) -> {
                                    BookingsActivity::class.java
                                }
                                else -> {
                                    null
                                }
                            }
                        )
                    )
                }
            })
            listView.adapter = adminHomeAdaper
    }

    override fun setupListeners() {
        setTitle("管理後台")

        chatBtn.setOnClickListener {

        }

        logout.setOnClickListener {

            showDialog(
                "您確定要登出嗎?",
                "",
                getString(R.string.yes),
                DialogInterface.OnClickListener { dialogInterface, i ->
                    showLoader()
                    UserService.logOut(User.getUser()!!, object : ServiceListener<String, String> {
                        override fun success(success: String) {
                            hideLoader()
                            toast(success)
                            onStartActivityWithClearStack(
                                Intent(this@AdminHomeActivity, LoginActivity::class.java)
                            )
                        }

                        override fun error(error: String) {
                            hideLoader()
                            toast(error)
                        }
                    })
                },
                getString(R.string.no),
                null
            )

        }

        showLoader()
        CategoryService.getAllCategoriesListener(object :
            ServiceListener<ArrayList<Category>, String> {
            override fun success(success: ArrayList<Category>) {
                hideLoader()
            }

            override fun error(error: String) {
                hideLoader()
                toast(error)
            }
        })
    }

    fun setUserData() {
        onlineSwitch.isChecked = User.getUser()?.online!!
        onlineSwitch.setOnClickListener {
            onlineSwitch.isChecked = !onlineSwitch.isChecked
            showDialog(
                getString(R.string.change_status),
                if (onlineSwitch.isChecked) getString(R.string.want_to_offline) else getString(R.string.want_to_online),
                getString(R.string.yes),
                DialogInterface.OnClickListener { it, int ->
                    AdminsService.changeOnlineStatus(!onlineSwitch.isChecked,
                        object : ServiceListener<String, String> {
                            override fun success(success: String) {
                                toast(success)
//                                onlineSwitch.isChecked = !onlineSwitch.isChecked
                            }

                            override fun error(error: String) {
                                toast(error)
                            }
                        })
                },
                getString(R.string.no),
                null
            )
        }
    }

    fun getEnabledMenus ()  {
        if (User.getUser()!!.superAdmin ) {
            menu = arrayListOf<String>(
                getString(R.string.menu_orders),
                getString(R.string.menu_booking)
            )
        }
        else {
            var items = ArrayList<String>()
            items.add(getString(R.string.menu_orders))
            items.add(getString(R.string.menu_booking))

            menu = items
        }
        adminHomeAdaper.setData(menu)
    }

    override fun onDestroy() {
        CategoryService.removeListener()
        ObservableUser.getInstance().deleteObserver(this)
        super.onDestroy()
    }

    override fun update(p0: Observable?, user: Any?) {
        onlineSwitch.isChecked = (user as User).online
    }
}
