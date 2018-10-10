package packageselect.hushush.co.photoedit

import android.graphics.Color
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.widget.LinearLayoutManager
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.add_text_dialog.*
import packageselect.hushush.co.R
import packageselect.hushush.co.photoedit.adapters.ColorAdapter

class AddTextDialog() : DialogFragment() {

    companion object {
        const val COLOR = "COLOR"
        const val TEXT = "TEXT"
    }

    private var listener: OnEditCompletedListener? = null

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

        if (arguments != null) {
            text.setText(arguments!!.getString(TEXT, ""))

            previewText.text = arguments!!.getString(TEXT, "")
            previewText.setTextColor(arguments!!.getInt(COLOR, Color.WHITE))
            if (isColorDark(arguments!!.getInt(COLOR, Color.WHITE)))
                previewBackground.setCardBackgroundColor(Color.parseColor("#dadada"))
            else
                previewBackground.setCardBackgroundColor(Color.parseColor("#202020"))
        }

        colorRv.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        colorRv.adapter = ColorAdapter() { color ->
            previewText.setTextColor(color)
            if (isColorDark(color))
                previewBackground.setCardBackgroundColor(Color.parseColor("#dadada"))
            else
                previewBackground.setCardBackgroundColor(Color.parseColor("#202020"))

        }

        text.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                previewText.text = s.toString()
            }
        })

        apply.setOnClickListener {
            if (listener != null) {
                listener!!.editCompleted(previewText.text.toString(), previewText.currentTextColor)
                dialog.dismiss()
            }
        }

        cancel.setOnClickListener {
            dialog.dismiss()
        }
    }

    private fun isColorDark(color: Int): Boolean {
        val darkness = 1 - (0.299 * Color.red(color) + 0.587 * Color.green(color) + 0.114 * Color.blue(color)) / 255
        return (darkness > 0.4)
    }

    fun setOnEditCompletedListener(listener: OnEditCompletedListener) {
        this.listener = listener
    }

    interface OnEditCompletedListener {
        fun editCompleted(text: String, color: Int)
    }
}