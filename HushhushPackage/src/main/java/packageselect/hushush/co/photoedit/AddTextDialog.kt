package packageselect.hushush.co.photoedit

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import com.robertlevonyan.components.kex.toast
import kotlinx.android.synthetic.main.add_text_dialog.*
import packageselect.hushush.co.R
import packageselect.hushush.co.photoedit.adapters.ColorAdapter
import packageselect.hushush.co.photoedit.adapters.TypefaceAdapter

class AddTextDialog : DialogFragment() {

    companion object {
        const val COLOR = "COLOR"
        const val TYPEFACE = "TYPEFACE"
        const val TEXT = "TEXT"
        const val TEXTSIZE = "TEXTSIZE"
    }

    private var selectedTypefaceName = ""
    private var selectedTextSize = 20

    private var listener: OnEditCompletedListener? = null

    private var typefaces = LinkedHashMap<String, Typeface>()

    override fun onStart() {
        super.onStart()
        val dialog = dialog
        if (dialog != null) {
            val width = ViewGroup.LayoutParams.MATCH_PARENT
            val height = ViewGroup.LayoutParams.WRAP_CONTENT
            dialog.window.setLayout(width, height)
        }
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.add_text_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        colorRv.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        colorRv.adapter = ColorAdapter { color ->
            text.setTextColor(color)

        }


        apply.setOnClickListener {
            if (listener != null) {
                listener!!.editCompleted(text.text.toString(), text.currentTextColor, text.typeface, selectedTypefaceName,selectedTextSize)
                dialog.dismiss()
            }
        }

        cancel.setOnClickListener {
            dialog.dismiss()
        }

        loadTypefaces()
        typefaceRv.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        typefaceRv.adapter = TypefaceAdapter(typefaces) { typeface, typefaceName ->
            if (typeface != null) {
                text.typeface = typeface
                selectedTypefaceName = typefaceName
            }
        }

        textSize.setOnSeekBarChangeListener(object :SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (progress  < 35)
                text.textSize = progress.toFloat()
                else
                    text.textSize = 35f
                selectedTextSize = progress
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }
        })

        if (arguments != null) {
           selectedTextSize =  arguments!!.getInt(TEXTSIZE, 20)
            textSize.progress =selectedTextSize
            if (selectedTextSize < 35)
                text.textSize = selectedTextSize.toFloat()
            else
                text.textSize = 35f
            text.setText(arguments!!.getString(TEXT, ""))
            text.setTextColor(arguments!!.getInt(COLOR, Color.WHITE))
            context!!.toast(arguments!!.getString(TYPEFACE)?: "hello")
            if (typefaces.contains(arguments!!.getString(TYPEFACE))) {
                text.typeface = Typeface.createFromAsset(context!!.assets, "fonts/${arguments!!.getString(TYPEFACE)}")
                selectedTypefaceName = arguments!!.getString(TYPEFACE) ?: ""
            }
        }

    }

    fun setOnEditCompletedListener(listener: OnEditCompletedListener) {
        this.listener = listener
    }

    private fun loadTypefaces() {
        val fonts = context!!.assets.list("fonts")
        if (fonts != null && context != null) {
            for (i in fonts) {
                typefaces[i] = Typeface.createFromAsset(context!!.assets, "fonts/$i")
            }
        }
    }

    interface OnEditCompletedListener {
        fun editCompleted(text: String, color: Int, typeface: Typeface, typefaceName: String, textSize:Int)
    }


}