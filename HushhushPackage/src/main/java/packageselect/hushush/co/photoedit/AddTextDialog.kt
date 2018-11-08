package packageselect.hushush.co.photoedit

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.add_text_dialog.*
import packageselect.hushush.co.R
import packageselect.hushush.co.photoedit.adapters.ColorAdapter
import packageselect.hushush.co.photoedit.adapters.TypefaceAdapter

class AddTextDialog : DialogFragment() {

    companion object {
        const val COLOR = "COLOR"
        const val TYPEFACE = "TYPEFACE"
        const val TEXT = "TEXT"
    }

    private var selectedTypefaceName = ""
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
                listener!!.editCompleted(text.text.toString(), text.currentTextColor, text.typeface, selectedTypefaceName)
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

        if (arguments != null) {
            text.setText(arguments!!.getString(TEXT, "Add text here..."))
            text.setTextColor(arguments!!.getInt(COLOR, Color.WHITE))
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
        fun editCompleted(text: String, color: Int, typeface: Typeface, typefaceName: String)
    }


}