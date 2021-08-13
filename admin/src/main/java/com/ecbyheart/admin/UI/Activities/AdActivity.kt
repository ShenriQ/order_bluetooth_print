package com.ecbyheart.admin.UI.Activities

import android.content.DialogInterface
import android.net.Uri
import android.os.Bundle
import android.widget.*
import coil.api.load
import coil.transform.RoundedCornersTransformation
import com.ecbyheart.admin.R
import com.armdroid.filechooser.Content
import com.armdroid.filechooser.FileType
import com.armdroid.filechooser.OnContentSelectedListener
import core.Listeners.ServiceListener
import core.Models.ImpInfo
import core.Services.ContantService
import core.Services.FileServices
import core.UI.BaseActivity
import core.Utils.AppLog
import core.Utils.AppUtils
import core.Utils.FileChooser
import java.io.File

class AdActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ad)
        fileChooser = FileChooser(this)
        setupComponents(this)
    }

    var isEditing: Boolean = false
    lateinit var selectImage: ImageView
    lateinit var remove: ImageButton
    //    lateinit var name: EditText
    lateinit var save: Button

    override fun initializeComponents() {
        selectImage = findViewById(R.id.selectImage)
        remove = findViewById(R.id.remove)
//        name = findViewById(R.id.name)
        save = findViewById(R.id.save)
        selectImage.setOnClickListener {
            openChooser(selectImage)
        }
        remove.setOnClickListener {
            imageFile = null
            selectImage.setImageResource(R.drawable.placeholder_horizontal)
        }
        setDataInViews()
    }

    fun setDataInViews() {
//        name.setText(product.title)
        showLoader()
        ContantService.getImpInfo(object : ServiceListener<ImpInfo, String> {
            override fun success(success: ImpInfo) {
                hideLoader()
                if (!success.ad_banner.isNullOrBlank()) {
                    isEditing = true
                    selectImage.load(success.ad_banner) {
                        placeholder(R.drawable.placeholder_horizontal)
                        error(R.drawable.placeholder_horizontal)
//                    transformations(RoundedCornersTransformation(50f))
                    }
                }

            }

            override fun error(error: String) {
                hideLoader()
                toast(error)
            }
        })
    }

    override fun setupListeners() {
        setToolbar { onGoBack() }
        setTitle("廣告")

        save.setOnClickListener {
            val validate = when {
//                name.text.isEmpty() -> {
//                    name.error = "Enter title!"
//                    false
//                }
                (!isEditing && imageFile == null) -> {
//                    toast("Select image!")
                    /*onGoBack()
                    false*/
                    true
                }
                else -> {
                    true
                }
            }
            if (validate) {
                showDialog(
                    getString(R.string.update),
                    "確定更新此廣告？",
                    getString(R.string.yes),
                    DialogInterface.OnClickListener { c, d ->
                        save()
                    },
                    getString(R.string.no),
                    null
                )
            }
        }
    }

    fun save() {
        var impInfo = ImpInfo.getImpInfo()
        if (impInfo == null) {
            impInfo = ImpInfo()
        }
        showLoader()
        if (imageFile != null) {
            val path = "Contents/ad_banner/${imageFile!!.name}"
            FileServices.UploadFile(imageFile, path, object : ServiceListener<String?, String?> {
                override fun success(url: String?) {
                    impInfo.ad_banner = url
                    updateInfo(impInfo)
                }

                override fun error(error: String?) {
                }
            })
        }
        else {
            impInfo.ad_banner = null
            updateInfo(impInfo)
        }
    }

    fun updateInfo(impInfo: ImpInfo) {
        ContantService.updateImpInfo(impInfo, object : ServiceListener<String?, String?> {
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

    var imageFile: File? = null
    fun openChooser(imageView: ImageView) {
        fileChooser.getImage(object : OnContentSelectedListener {
            override fun onContentSelected(fileType: Int, content: Content) {
                if (fileType == FileType.TYPE_IMAGE) {
//                                imageView.setImageBitmap(content.getBitmap());
                    imageView.setImageURI(null)
                    val file = File(content.getPath())
                    if (file.length() == 0L) {
                        AppUtils.Toast(getString(R.string.invalid_file))
                    } else {
                        AppLog.e("Path: ", file.name)
                        imageFile = file
//                        imageView.scaleType = ImageView.ScaleType.FIT_XY
                        imageView.load(file) {
                            placeholder(R.drawable.placeholder_horizontal)
                            transformations(RoundedCornersTransformation(50f))
                        }
                        imageView.setImageURI(Uri.fromFile(file))
                    }
                }
            }

            override fun onError(error: com.armdroid.filechooser.Error?) {
                toast(error?.message)
            }
        }, true)
    }
}
