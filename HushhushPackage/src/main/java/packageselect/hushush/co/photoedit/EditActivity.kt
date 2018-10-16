package packageselect.hushush.co.photoedit

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.*
import android.os.Bundle
import android.os.Environment.getExternalStorageDirectory
import android.support.v7.app.AppCompatActivity
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.view.*
import com.almeros.android.multitouch.MoveGestureDetector
import com.robertlevonyan.components.kex.toast
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import kotlinx.android.synthetic.main.edit_content.*
import kotlinx.android.synthetic.main.editor_view.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.longToast
import org.jetbrains.anko.uiThread
import packageselect.hushush.co.R
import packageselect.hushush.co.packages.HushushPackages
import java.io.File
import java.io.FileOutputStream


class EditActivity : AppCompatActivity() {

    private var textX = 0f
    private var textY = 0f

    private var textXNotFixed = 0f
    private var textYNotFixed = 0f

    private var padding = 40f

    private var textLeft = 0f
    private var textRight = 0f
    private var textTop = 0f
    private var textBottom = 0f

    private var translateX = 0f
    private var translateY = 0f
    private var scaleFactor = 1f

    private var xCoord = 0f
    private var yCoord = 0f
    private var xCoordContinous = 0f
    private var yCoordContinous = 0f

    private var xCoordContinousNotFixed = 0f
    private var yCoordContinousNotFixed = 0f

    private var scaleFocusX = 0f
    private var scaleFocusY = 0f

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
    private var currentTextSize = 20
    private val editor: EditorView by lazy { EditorView(this) }

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION


        setContentView(R.layout.edit_activity)

        val screenSize = intent.getStringExtra(HushushPackages.screenSize)

        editorView.visibility = View.GONE



        extractScreenSize(screenSize)

        canvas.addView(editor, ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT))

        mMoveDetector = MoveGestureDetector(applicationContext, MoveListener())
        mScaleDetector = ScaleGestureDetector(applicationContext, ScaleListener())


        editor.setOnTouchListener { _, event ->
            xCoordContinous = fixX(event.x)
            yCoordContinous = fixY(event.y)

            xCoordContinousNotFixed = event.x
            yCoordContinousNotFixed = event.y

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

                            //canvasClicked
                        }
                    }

                }
            }

            mMoveDetector.onTouchEvent(event)
            mScaleDetector.onTouchEvent(event)
            true
        }

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

        changeText.setOnClickListener {
            val addTextDialog = AddTextDialog()

            addTextDialog.arguments = Bundle().apply {
                putString(AddTextDialog.TEXT, currentText)
                putInt(AddTextDialog.COLOR, currentColor)
                putString(AddTextDialog.TYPEFACE, currentTypefaceName)
                putInt(AddTextDialog.TEXTSIZE, currentTextSize)
            }

            addTextDialog.show(supportFragmentManager, "ADD_TEXT")
            addTextDialog.setOnEditCompletedListener(object : AddTextDialog.OnEditCompletedListener {

                override fun editCompleted(text: String, color: Int, typeface: Typeface, typefaceName: String, textSize: Int) {

                    currentColor = color
                    currentTypeface = typeface
                    currentText = text
                    currentTypefaceName = typefaceName
                    currentTextSize = textSize

                }

            })
        }

        saveAndProceed.setOnClickListener {
            editor.saveImage()
        }

    }


    private fun fixX(x: Float): Float {
        val xTranslationFix = x - translateX
        val xScaleFix = xTranslationFix / scaleFactor
        return xScaleFix
    }

    private fun fixY(y: Float): Float {
        val yTranslationFix = y - translateY
        val yScaleFix = yTranslationFix / scaleFactor
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
                            val height = (screenSizeY / (screenSizeX / width.toFloat())).toInt()

                            val thumbnailBitmap = Bitmap.createScaledBitmap(scaledBitmap, width, height, false)

                            uiThread {

                                selectImageLayout.visibility = View.GONE

                                //  todo set image here
                                editor.setSrc(scaledBitmap)
                                editor.setThumbnail(thumbnailBitmap)
                                editorView.visibility = View.VISIBLE

                                textX = thumbnailBitmap.width / 2f
                                textY = thumbnailBitmap.height / 2f + editor.height / 2f - thumbnailBitmap!!.height / 2
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


            if (!(xCoordContinous > textLeft &&
                            xCoordContinous < textRight
                            && yCoordContinous > textTop &&
                            yCoordContinous < textBottom)) {
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
            scaleFactor = Math.max(0.5f, Math.min(scaleFactor, 2.5f))

            scaleFocusX = detector.focusX
            scaleFocusY = detector.focusY

            return true
        }
    }

    private inner class EditorView @JvmOverloads constructor(
            context: Context, attrs: AttributeSet? = null
    ) : View(context, attrs) {

        private var bitmap: Bitmap? = null
        private var thumbnailBitmap: Bitmap? = null


        override fun onDraw(nullableCanvas: Canvas?) {
            super.onDraw(nullableCanvas)
            nullableCanvas?.let { canvas ->

                canvas.save()



                if (xCoordContinous > textLeft &&
                        xCoordContinous < textRight
                        && yCoordContinous > textTop &&
                        yCoordContinous < textBottom) {

                    textX = xCoordContinous
                    textY = yCoordContinous

                    textXNotFixed = xCoordContinousNotFixed
                    textYNotFixed = yCoordContinousNotFixed

                }


                canvas.translate(translateX, translateY)


                canvas.scale(scaleFactor, scaleFactor)


                if (bitmap != null)
                    canvas.drawBitmap(bitmap!!, 0f, 0f, null)

                val textPaint =
                        Paint().apply {
                            textAlign = Paint.Align.CENTER
                            color = currentColor
                            typeface = currentTypeface
                            textSize = currentTextSize.toFloat()
                        }


                canvas.drawText(currentText, textX,
                        textY,
                        textPaint)

                textLeft = (textX - textPaint.measureText(currentText) / 2) - padding
                textRight = (textX + textPaint.measureText(currentText) / 2) + padding
                textTop = (textY + textPaint.fontMetrics.top) - padding
                textBottom = (textY + textPaint.fontMetrics.bottom) + padding

                // canvas.drawRect(textLeft, textTop, textRight, textBottom, Paint().apply { color = Color.parseColor("#AA000000") })

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

                    val textPaint =
                            Paint().apply {
                                textAlign = Paint.Align.CENTER
                                color = currentColor
                                typeface = currentTypeface
                                textSize = currentTextSize.toFloat()
                            }


                    canvas.drawText(currentText,textX,textY, textPaint)


                    val file = File(getExternalStorageDirectory().absolutePath + "/sign.png")

                    try {
                        image.compress(Bitmap.CompressFormat.JPEG, 100, FileOutputStream(file))
                        uiThread {
                            toast("Completed!!")
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }


        }
    }

}

