package packageselect.hushush.co.packages.adapters

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.package_card.view.*
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.toast
import packageselect.hushush.co.R
import packageselect.hushush.co.SelectPackage
import packageselect.hushush.co.packages.HushushPackages
import packageselect.hushush.co.packages.dao.HushushData
import packageselect.hushush.co.packages.dao.Package
import packageselect.hushush.co.packages.dao.Pkgs
import packageselect.hushush.co.photoedit.EditActivity
import java.util.logging.Handler


class PackagesAdapter(val pkg: Pkgs, val hushushData: HushushData) : RecyclerView.Adapter<PackagesAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
            ViewHolder(LayoutInflater
                    .from(parent.context).inflate(R.layout.package_card, parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(pkg.packages[position])
    }

    override fun getItemCount(): Int = pkg.packages.size

    inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {

        fun bind(data: Package) {
            with(view) {
                if (data.defaultTicketCount.toFloatOrNull() == null || data.defaultTicketPrice.toFloatOrNull() == null || data.extraOneTicketAmount.toFloatOrNull() == null) {
                    context?.toast("Error getting packages!")
                } else {
                    Glide.with(this).load(data.picUrl).into(image)
                    packageName.text = data.name

                    price.text = "₹ " + (((hushushData.seatCount.toFloat() - data.defaultTicketCount.toFloat()) * data.extraOneTicketAmount.toFloat()) + data.extraOneTicketAmount.toFloat()).toString()
                    var item = ""
                    for (i in data.itemIncludes) {
                        item += i.itemname
                        if (i.category == "person")
                            item += ": ${hushushData.seatCount}"
                        else
                            item += ": 1"
                        item += ", "
                    }
                    items.text = item

                    card.setOnClickListener {
                        val intent = Intent()
                        intent.putExtra(SelectPackage.DATA, hushushData)
                        intent.putExtra(Pkgs.TAG, data)
                        (context as AppCompatActivity).setResult(SelectPackage.RES_HUSHPACKAGE_OK,intent)
                        (context as AppCompatActivity).finish()
                    }
                }
            }
        }

    }
}