package packageselect.hushush.co.photoedit

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.os.Environment
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.robertlevonyan.components.kex.set
import com.robertlevonyan.components.kex.toast
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import ja.burhanrashid52.photoeditor.OnPhotoEditorListener
import ja.burhanrashid52.photoeditor.PhotoEditor
import ja.burhanrashid52.photoeditor.ViewType
import kotlinx.android.synthetic.main.edit_activity.*
import kotlinx.android.synthetic.main.edit_content.*
import kotlinx.android.synthetic.main.editor_view.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.longToast
import org.jetbrains.anko.uiThread
import packageselect.hushush.co.R
import packageselect.hushush.co.packages.HushushPackages
import java.lang.Exception


class EditActivity : AppCompatActivity() {

    private var screenSizeX = 0
    private var screenSizeY = 0

    private val photoEditor by lazy {
        PhotoEditor.Builder(this, image)
                .setPinchTextScalable(true)
                .build()
    }

    private var currentText = ""
    private var currentColor = Color.WHITE
    private var currentTypeface: Typeface? = null
    private var currentTypefaceName: String = ""
    private var currentView: View? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.edit_activity)
        setSupportActionBar(toolbar)

        val screenSize = intent.getStringExtra(HushushPackages.screenSize)

        editorView.visibility = View.GONE

        extractScreenSize(screenSize)

        selectImage.setOnClickListener {
            if (screenSizeX != 0 && screenSizeY != 0)
                CropImage.activity()
                        .setFixAspectRatio(true)
                        .setAspectRatio(screenSizeX, screenSizeY)
                        .setMinCropResultSize(screenSizeX, screenSizeY)
                        .setActivityTitle("CROP IMAGE")
                        .setAutoZoomEnabled(true)
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .start(this)
            else
                toast("Screen size format error")
        }

        addText.setOnClickListener {
            val addTextDialog = AddTextDialog()

            addTextDialog.arguments = Bundle().apply {
                putString(AddTextDialog.TEXT, currentText)
                putInt(AddTextDialog.COLOR, currentColor)
                putString(AddTextDialog.TYPEFACE, currentTypefaceName)
            }

            addTextDialog.show(supportFragmentManager, "ADD_TEXT")
            addTextDialog.setOnEditCompletedListener(object : AddTextDialog.OnEditCompletedListener {

                override fun editCompleted(text: String, color: Int, typeface: Typeface, typefaceName: String) {

                    currentColor = color
                    currentTypeface = typeface
                    currentText = text
                    currentTypefaceName = typefaceName

                    if (currentView == null)
                        currentView = photoEditor.addText(currentTypeface, currentText, currentColor)
                    else
                        photoEditor.editText(currentView, currentTypeface, currentText, currentColor)
                }

            })
        }

        undo.setOnClickListener {
            photoEditor.undo()
        }
        redo.setOnClickListener {
            photoEditor.redo()
        }

        photoEditor.setOnPhotoEditorListener(object : OnPhotoEditorListener {
            override fun onEditTextChangeListener(rootView: View?, text: String?, colorCode: Int) {}
            override fun onStartViewChangeListener(viewType: ViewType?) {}
            override fun onRemoveViewListener(numberOfAddedViews: Int) {}
            override fun onRemoveViewListener(viewType: ViewType?, numberOfAddedViews: Int) {
                currentView = null
            }

            override fun onAddViewListener(viewType: ViewType?, numberOfAddedViews: Int) {}
            override fun onStopViewChangeListener(viewType: ViewType?) {}
        })

        saveAndProceed.setOnClickListener {

            photoEditor.saveAsFile(Environment.getExternalStorageDirectory().absolutePath + "/b.jpg",object: PhotoEditor.OnSaveListener{
                override fun onSuccess(imagePath: String) {
                     }

                override fun onFailure(exception: Exception) {
                }
            })
        }

    }

    private fun extractScreenSize(screenSize: String) {
        val regex = "(\\d+)x(\\d*)".toRegex()
        val match = regex.find(screenSize)
        val result = match?.destructured
        if (result != null) {
            val (x, y) = result
            screenSizeX = x.toInt()
            screenSizeY = y.toInt()
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            val result = CropImage.getActivityResult(data)
            if (resultCode == Activity.RESULT_OK) {
                val resultUri = result.uri

                doAsync {
                    val imageStream = contentResolver.openInputStream(resultUri)
                    val bitmap = BitmapFactory.decodeStream(imageStream)
                    if (bitmap != null) {
                        if (bitmap.width >= screenSizeX && bitmap.height >= screenSizeY) {
                            val scaledBitmap = Bitmap.createScaledBitmap(bitmap, screenSizeX, screenSizeY, false)
                            uiThread {
                                selectImageLayout.visibility = View.GONE
                                image.source.set(scaledBitmap)
                                editorView.visibility = View.VISIBLE
                            }

                        } else {
                            uiThread {
                                longToast("Cropped image must have minimum resolution of $screenSizeX X $screenSizeY")
                            }
                        }
                    }
                }

            }
        }
    }



    inner class Editor : View() {


    }
}