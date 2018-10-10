package packageselect.hushush.co.photoedit.adapters

import android.graphics.Color
import android.graphics.Typeface
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.typeface_card.view.*
import packageselect.hushush.co.R


class TypefaceAdapter(val typefaces: LinkedHashMap<String, Typeface>, val itemClick: (Typeface?) -> Unit) : RecyclerView.Adapter<TypefaceAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
            ViewHolder(LayoutInflater
                    .from(parent.context).inflate(R.layout.typeface_card, parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val keyAtPosition = typefaces.keys.elementAt(position)
        holder.bind(keyAtPosition)
    }

    override fun getItemCount(): Int = typefaces.size

    inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {

        fun bind(typeface: String) {
            with(view) {
                typefacePreview.typeface = typefaces[typeface]
                typefacePreview.text = typeface.substring(0, typeface.length-4)
                card.setOnClickListener {
                    itemClick(typefaces[typeface])
                }
            }
        }

    }
}