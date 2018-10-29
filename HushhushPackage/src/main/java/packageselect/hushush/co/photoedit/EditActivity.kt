package packageselect.hushush.co.photoedit

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.graphics.*
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.util.Log
import android.view.*
import com.almeros.android.multitouch.MoveGestureDetector
import com.robertlevonyan.components.kex.toast
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import kotlinx.android.synthetic.main.edit_content.*
import kotlinx.android.synthetic.main.editor_view.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.longToast
import org.jetbrains.anko.uiThread
import packageselect.hushush.co.R
import packageselect.hushush.co.packages.HushushPackages
import packageselect.hushush.co.packages.dao.HushushData
import packageselect.hushush.co.packages.network.PackagesAPI
import packageselect.hushush.co.summary.SummaryActivity
import java.io.File
import java.io.FileOutputStream


class EditActivity : AppCompatActivity() {

    private val STORAGE_REQ = 1001

    private var textX = 0f
    private var textY = 0f

    private var deltaX = 0f
    private var deltaY = 0f

    private var padding = 40f

    private var textLeft = 0f
    private var textRight = 0f
    private var textTop = 0f
    private var textBottom = 0f

    private var translateX = 0f
    private var translateY = 0f
    private var scaleFactor = 1f

    private var screenTranslateX = 0f
    private var screenTranslateY = 0f
    private var screenscaleFactor = 1f


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

    private lateinit var data: HushushData

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION


        setContentView(R.layout.edit_activity)


        data = intent.getSerializableExtra(HushushPackages.DATA) as HushushData

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
        val xScaleFix = xTranslationFix / screenscaleFactor
        return xScaleFix
    }

    private fun fixY(y: Float): Float {
        val yTranslationFix = y - screenTranslateY
        val yScaleFix = yTranslationFix / screenscaleFactor
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
            if (resultCode == Activity.RESULT_OK) {
                val resultUri = result.uri

                doAsync {
                    val imageStream = contentResolver.openInputStream(resultUri)
                    val bitmap = BitmapFactory.decodeStream(imageStream)
                    if (bitmap != null) {
                        if (bitmap.width >= screenSizeX && bitmap.height >= screenSizeY) {
                            val scaledBitmap = Bitmap.createScaledBitmap(bitmap, screenSizeX, screenSizeY, false)

                            val displayMetrics = DisplayMetrics()
                            windowManager.defaultDisplay.getMetrics(displayMetrics)
                            val width = displayMetrics.widthPixels
                            val height = /*(screenSizeY / (screenSizeX / width.toFloat())).toInt()*/ displayMetrics.heightPixels


                            uiThread {


                                requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE

                                selectImageLayout.visibility = View.GONE

                                /**  to fit width
                                 *   scaleFactor  = height / screenSizeY.toFloat()
                                 */

                                /**  to fit height
                                 */
                                screenscaleFactor = width / screenSizeY.toFloat()
                                scaleFactor = screenscaleFactor

                                screenTranslateX = (height - (screenSizeX * screenscaleFactor)) / 2
                                translateX = screenTranslateX

                                translateX = width / 2f
                                translateY = height / 2f


                                editor.setSrc(scaledBitmap)

                                editorView.visibility = View.VISIBLE

                                textX = screenSizeX / 2f
                                textY = screenSizeY / 2f
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

    private inner class MoveListener : MoveGestureDetector.SimpleOnMoveGestureListener() {
        override fun onMove(detector: MoveGestureDetector?): Boolean {
            val d = detector!!.focusDelta

            if (!isScaling) {
                translateX += d.x
                translateY += d.y

            }

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

        private var bitmap: Bitmap? = null
        private var thumbnailBitmap: Bitmap? = null


        override fun onDraw(nullableCanvas: Canvas?) {
            super.onDraw(nullableCanvas)
            nullableCanvas?.let { canvas ->

                canvas.save()

                Log.d("YYY", "l: $textLeft r: $textRight x: $xCoord del:$deltaX")

                if (
                        !isScaling) {


                    textX = translateX
                    textY = translateY

                }

                canvas.translate(screenTranslateX, screenTranslateY)
                canvas.scale(screenscaleFactor, screenscaleFactor)

                if (bitmap != null)
                    canvas.drawBitmap(bitmap!!, 0f, 0f, null)

                textPaint.apply {
                    color = currentColor
                    typeface = currentTypeface
                    textSize = currentTextSize.toFloat()
                }

                canvas.drawText(currentText, textX, textY, textPaint)

                textLeft = (textX - textPaint.measureText(currentText) / 2) - padding
                textRight = (textX + textPaint.measureText(currentText) / 2) + padding
                textTop = (textY + textPaint.fontMetrics.top) - padding
                textBottom = (textY + textPaint.fontMetrics.bottom) + padding

                deltaX = ((textRight - textLeft) / 2) - xCoord


                canvas.restore()

            }
            invalidate()
        }


        fun setSrc(image: Bitmap) {
            bitmap = image
        }

        fun setThumbnail(thumb: Bitmap) {
            thumbnailBitmap = thumb
        }


        fun saveImage() {

            doAsync {
                if (bitmap != null) {

                    val image = Bitmap.createBitmap(screenSizeX, screenSizeY, Bitmap.Config.ARGB_8888)
                    val canvas = Canvas(image)


                    canvas.drawBitmap(bitmap!!, 0f, 0f, null)

                    canvas.drawText(currentText, textX, textY, textPaint)

                    val file = File(externalCacheDir.absolutePath + "/image.jpg")

                    try {
                        image.compress(Bitmap.CompressFormat.JPEG, 100, FileOutputStream(file))

                        startActivity(intentFor<SummaryActivity>())

                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }


        }
    }
}

