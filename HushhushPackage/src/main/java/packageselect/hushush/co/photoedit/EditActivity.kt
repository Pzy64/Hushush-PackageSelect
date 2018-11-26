package packageselect.hushush.co.photoedit

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.graphics.*
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.util.Log
import android.view.*
import android.widget.Toast
import com.robertlevonyan.components.kex.toast
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import kotlinx.android.synthetic.main.edit_content.*
import kotlinx.android.synthetic.main.editor_view.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.longToast
import org.jetbrains.anko.uiThread
import packageselect.hushush.co.R
import packageselect.hushush.co.SelectPackage
import packageselect.hushush.co.packages.dao.HushushData
import packageselect.hushush.co.packages.dao.Package
import packageselect.hushush.co.packages.dao.Pkgs
import packageselect.hushush.co.photoedit.cropper.Resizer
import packageselect.hushush.co.photoedit.gesture.MoveGestureDetector
import java.io.File
import java.io.FileOutputStream


class EditActivity : AppCompatActivity() {

    private val STORAGE_REQ = 1001
    private val REQ_LOADIMG = 1556

    private var translateX = 0f
    private var translateY = 0f
    private var scaleFactor = 1f

    private var scaledHeight = 0
    private var scaledWidth = 0

    private var screenTranslateX = 0f
    private var screenTranslateY = 0f
    private var screenScaleFactor = 1f

    private var xCoord = 0f
    private var yCoord = 0f
    private var xCoordContinous = 0f
    private var yCoordContinous = 0f


    private var scaleFocusX = 0f
    private var scaleFocusY = 0f

    private var isScaling = false

    private var touchX = -1f
    private var touchY = -1f

    private lateinit var mScaleDetector: ScaleGestureDetector
    private lateinit var mMoveDetector: MoveGestureDetector

    private var screenSizeX = 0
    private var screenSizeY = 0

    private var currentText = ""
    private var currentColor = Color.WHITE
    private var currentTypeface: Typeface? = null
    private var currentTypefaceName: String = ""
    private var currentTextSize = 30
    private val editor: EditorView by lazy { EditorView(this) }

    private var doubleBackToExitPressedOnce = false

    private lateinit var data: HushushData
    private lateinit var pkg: Package

    private var cropUri: Uri? = null
    private var resultUri: Uri? = null
    private var cropScaledUri: Uri? = null

    @SuppressLint("ClickableViewAccessibility")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION


        setContentView(R.layout.edit_activity)

        setResult(SelectPackage.RES_EDITACTIVITY_CANCEL)

        if (externalCacheDir != null) {
            cropUri = Uri.fromFile(File(externalCacheDir.absolutePath + "/image.jpg"))
            cropScaledUri = Uri.fromFile(File(externalCacheDir.absolutePath + "/scaled"))
            resultUri = Uri.fromFile(File(externalCacheDir.absolutePath + "/result.jpg"))
        }

        data = intent.getSerializableExtra(SelectPackage.DATA) as HushushData
        pkg = intent.getSerializableExtra(Pkgs.TAG) as Package

        val screenSize = data.screenSize

        editorView.visibility = View.GONE

        extractScreenSize(screenSize)

        canvas.addView(editor, ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT))

        mMoveDetector = MoveGestureDetector(applicationContext, MoveListener())
        mScaleDetector = ScaleGestureDetector(applicationContext, ScaleListener())


        editor.setOnTouchListener { _, event ->
            xCoordContinous = fixX(event.x)
            yCoordContinous = fixY(event.y)

            when (event.action) {
                MotionEvent.ACTION_DOWN -> {

                    xCoord = fixX(event.x)
                    yCoord = fixY(event.y)

                }
                MotionEvent.ACTION_UP -> {
                    if (event.x in ((xCoord - 10)..(xCoord + 10))) {
                        touchX = fixX(event.x)
                        if (event.y in ((yCoord - 10)..(yCoord + 10))) {
                            touchY = fixY(event.y)
                        }
                    }

                }
            }

            mMoveDetector.onTouchEvent(event)
            mScaleDetector.onTouchEvent(event)
            true
        }

        resolution.text = "Image resolution must be \ngreater than or equal to ${data.screenSize}"

        selectImageLayout.setOnClickListener {
            if (screenSizeX != 0 && screenSizeY != 0) {
                val i = Intent(
                        Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI)

                startActivityForResult(i, REQ_LOADIMG)
            } else
                toast("Screen size format error")
        }

        changeText.setOnClickListener {
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

                }

            })
        }

        saveAndProceed.setOnClickListener {
            saveImage()
        }
    }

    private fun saveImage() {
        android.support.v7.app.AlertDialog.Builder(this)
                .setTitle("Confirm Action")
                .setMessage("Save changes and Upload?")
                .setPositiveButton("YES") { _, _ ->

                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M)
                        getStoragePermission()
                    else
                        editor.saveImage()

                }
                .setNegativeButton("NO") { dialog, _ ->
                    dialog.dismiss()
                }
                .show()
    }

    private fun getStoragePermission() {
        if (ContextCompat.checkSelfPermission(this.applicationContext,
                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this,
                    arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    STORAGE_REQ)
        } else
            editor.saveImage()
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>,
                                            grantResults: IntArray) {
        when (requestCode) {
            STORAGE_REQ -> {
                if (grantResults.isNotEmpty() && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    toast("Can't Save image without storage permission")
                } else {
                    editor.saveImage()
                }
            }
        }
    }


    private fun fixX(x: Float): Float {
        val xTranslationFix = x - screenTranslateX
        val xScaleFix = xTranslationFix / screenScaleFactor
        return xScaleFix
    }

    private fun fixY(y: Float): Float {
        val yTranslationFix = y - screenTranslateY
        val yScaleFix = yTranslationFix / screenScaleFactor
        return yScaleFix
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
            if (resultCode == AppCompatActivity.RESULT_OK) {
                result.doAsync {

                    val displayMetrics = DisplayMetrics()
                    windowManager.defaultDisplay.getMetrics(displayMetrics)

                    val height: Int
                    val width: Int
                    if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
                        height = displayMetrics.widthPixels
                        width = displayMetrics.heightPixels
                    } else {
                        height = displayMetrics.heightPixels
                        width = displayMetrics.widthPixels
                    }

                    uiThread {

                        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE

                        selectImageLayout.visibility = View.GONE

                        if (screenSizeX / screenSizeY > width / height) {
                            /**  to fit width */
                            screenScaleFactor = width / screenSizeX.toFloat()
                            scaleFactor = screenScaleFactor

                            screenTranslateY = (height - (screenSizeY * scaleFactor)) / 2
                            translateY = screenTranslateY

                            scaledWidth = width
                            scaledHeight = (width / (screenSizeX / screenSizeY.toFloat())).toInt()

                            Log.d("YYY", "FIT W $scaledWidth $scaledHeight")
                        } else {
                            /**  to fit height */
                            screenScaleFactor = height / screenSizeY.toFloat()
                            scaleFactor = screenScaleFactor

                            screenTranslateX = (width - (screenSizeX * screenScaleFactor)) / 2f
                            translateX = screenTranslateX

                            scaledHeight = height
                            scaledWidth = (height * (screenSizeX / screenSizeY.toFloat())).toInt()

                            Log.d("YYY", "FIT H $scaledWidth $scaledHeight")
                        }


                        if (cropUri != null && cropScaledUri != null) {

                            Resizer(this@EditActivity)
                                    .setTargetLength(scaledWidth)
                                    .setQuality(75)
                                    .setOutputFilename("image")
                                    .setOutputDirPath(cropScaledUri!!.path)
                                    .setSourceImage(File(cropUri!!.path))
                                    .setOutputFormat(Bitmap.CompressFormat.JPEG)
                                    .resizedFile

                        }


                        editor.start()
                        editorView.visibility = View.VISIBLE

                        translateX = screenSizeX / 2f
                        translateY = screenSizeY / 2f
                    }

                }

            }
        } else if (requestCode == REQ_LOADIMG && resultCode == AppCompatActivity.RESULT_OK && data != null) {

            val selectedImage = data.data
            if (selectedImage != null) {
                val options = BitmapFactory.Options()
                options.inJustDecodeBounds = true
                BitmapFactory.decodeStream(contentResolver.openInputStream(selectedImage), null, options)

                if (options.outHeight >= screenSizeY || options.outWidth >= screenSizeX) {

                    CropImage.activity(selectedImage)
                            .setFixAspectRatio(true)
                            .setAspectRatio(screenSizeX, screenSizeY)
                            .setMinCropResultSize(screenSizeX, screenSizeY)
                            .setActivityTitle("CROP IMAGE")
                            .setAutoZoomEnabled(true)
                            .setRequestedSize(screenSizeX, screenSizeY)
                            .setGuidelines(CropImageView.Guidelines.ON)
                            .setOutputCompressFormat(Bitmap.CompressFormat.JPEG)
                            .setOutputUri(cropUri)
                            .start(this)
                } else {
                    longToast("Cropped image must have minimum resolution of $screenSizeX X $screenSizeY")
                }
            }
        }
    }

    private inner class MoveListener : MoveGestureDetector.SimpleOnMoveGestureListener() {
        override fun onMove(detector: MoveGestureDetector?): Boolean {
            val d = detector!!.focusDelta

            translateX += d.x
            translateY += d.y

            return true
        }
    }

    private inner class ScaleListener : ScaleGestureDetector.SimpleOnScaleGestureListener() {
        override fun onScale(detector: ScaleGestureDetector): Boolean {
            scaleFactor *= detector.scaleFactor // scale change since previous event
            // Don't let the object get too small or too large.
            scaleFactor = Math.max(0.1f, Math.min(scaleFactor, 3f))

            currentTextSize = (scaleFactor * 100).toInt()

            scaleFocusX = detector.focusX
            scaleFocusY = detector.focusY

            return true
        }

        override fun onScaleBegin(detector: ScaleGestureDetector?): Boolean {

            isScaling = true

            return super.onScaleBegin(detector)


        }

        override fun onScaleEnd(detector: ScaleGestureDetector?) {
            super.onScaleEnd(detector)

            isScaling = false

        }
    }

    private inner class EditorView @JvmOverloads constructor(
            context: Context, attrs: AttributeSet? = null
    ) : View(context, attrs) {

        val textPaint =
                Paint().apply {
                    textAlign = Paint.Align.CENTER
                    color = currentColor
                    typeface = currentTypeface
                    textSize = currentTextSize.toFloat()
                }

        private val mat: Matrix by lazy {
            Matrix().apply { postScale(screenSizeX.toFloat() / scaledWidth, screenSizeX.toFloat() / scaledWidth) }
        }

        private var scaledBitmap: Bitmap? = null

        fun start() {
            scaledBitmap = BitmapFactory.decodeFile(cropScaledUri!!.path + "/image.jpg")
        }

        override fun onDraw(nullableCanvas: Canvas?) {
            super.onDraw(nullableCanvas)
            nullableCanvas?.let { canvas ->

                canvas.save()

                canvas.translate(screenTranslateX, screenTranslateY)
                canvas.scale(screenScaleFactor, screenScaleFactor)

                if (scaledBitmap != null) {
                    canvas.drawBitmap(scaledBitmap!!, mat, null)
                }
                textPaint.apply {
                    color = currentColor
                    typeface = currentTypeface
                    textSize = currentTextSize.toFloat()
                }

                canvas.drawText(currentText, translateX, translateY, textPaint)

                canvas.restore()

            }
            invalidate()
        }


        fun saveImage() {

            doAsync {
                if (cropUri != null && resultUri != null) {
                    val bitmap = BitmapFactory.decodeStream(contentResolver.openInputStream(cropUri!!))

                    val image = Bitmap.createBitmap(screenSizeX, screenSizeY, Bitmap.Config.ARGB_8888)
                    val canvas = Canvas(image)

                    canvas.drawBitmap(bitmap, 0f, 0f, null)

                    canvas.drawText(currentText, translateX, translateY, textPaint)


                    val file = File(resultUri!!.path)

                    try {
                        image.compress(Bitmap.CompressFormat.JPEG, 100, FileOutputStream(file))

                        val intent = Intent()
                        intent.putExtra(SelectPackage.DATA, data)
                        intent.putExtra(Pkgs.TAG, pkg)
                        setResult(SelectPackage.RES_EDITACTIVITY_OK, intent)
                        finish()

                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        }
    }

    override fun onBackPressed() {

        if (doubleBackToExitPressedOnce) {
            super.onBackPressed()
            return
        }
        doubleBackToExitPressedOnce = true
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show()
        Handler().postDelayed({ doubleBackToExitPressedOnce = false }, 2000)

    }
}

