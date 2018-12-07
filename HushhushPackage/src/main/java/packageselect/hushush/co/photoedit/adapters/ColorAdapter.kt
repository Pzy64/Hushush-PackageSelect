package packageselect.hushush.co.photoedit.adapters

import android.graphics.Color
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.graphics.drawable.LayerDrawable
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.OvalShape
import android.support.v7.graphics.Palette
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.color_card.view.*
import packageselect.hushush.co.R


class ColorAdapter(val colors: ArrayList<String>, val itemClick: (Int) -> Unit) : RecyclerView.Adapter<ColorAdapter.ViewHolder>() {



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
            ViewHolder(LayoutInflater
                    .from(parent.context).inflate(R.layout.color_card, parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(colors[position])
    }

    override fun getItemCount(): Int = colors.size

    inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {

        fun bind(colorString: String) {
            with(view) {
                buildColorPickerView(color, Color.parseColor(colorString))

                card.setOnClickListener {
                    itemClick(Color.parseColor(colorString))
                }
            }
        }

    }

    private fun buildColorPickerView(view: View, colorCode: Int) {

        val biggerCircle = ShapeDrawable(OvalShape())
        biggerCircle.intrinsicHeight = 20
        biggerCircle.intrinsicWidth = 20
        biggerCircle.bounds = Rect(0, 0, 20, 20)
        biggerCircle.paint.color = colorCode

        val smallerCircle = ShapeDrawable(OvalShape())
        smallerCircle.intrinsicHeight = 5
        smallerCircle.intrinsicWidth = 5
        smallerCircle.bounds = Rect(0, 0, 5, 5)
        smallerCircle.paint.color = Color.WHITE
        smallerCircle.setPadding(10, 10, 10, 10)
        val drawables = arrayOf<Drawable>(smallerCircle, biggerCircle)

        val layerDrawable = LayerDrawable(drawables)

        view.background = layerDrawable
    }
}