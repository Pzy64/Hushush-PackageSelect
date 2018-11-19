package packageselect.hushush.co.summary

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import kotlinx.android.synthetic.main.summary_activity.*
import kotlinx.android.synthetic.main.summary_content.*
import packageselect.hushush.co.R
import packageselect.hushush.co.SelectPackage
import packageselect.hushush.co.packages.dao.HushushData
import packageselect.hushush.co.packages.dao.Package
import packageselect.hushush.co.packages.dao.Pkgs
import java.io.File

class SummaryActivity : AppCompatActivity() {

    private var doubleBackToExitPressedOnce = false

    private lateinit var data: Package
    private lateinit var hushushData: HushushData
    private var totalPrice = 0f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.summary_activity)
        setSupportActionBar(toolbar)


        setResult(SelectPackage.RES_SUMMARY_CANCEL)

        data = intent.getSerializableExtra(Pkgs.TAG) as Package
        hushushData = intent.getSerializableExtra(SelectPackage.DATA) as HushushData

        populateView()

        calculateTotal()

        selectPackage.setOnClickListener {

            val intent = Intent()
            intent.putExtra(SelectPackage.packageName, data.name)
            intent.putExtra(SelectPackage.packageId, data.id)
            intent.putExtra(SelectPackage.packagePrice, totalPrice)
            setResult(SelectPackage.RES_SUMMARY_OK, intent)
            finish()
        }


    }

    private fun calculateTotal() {

        val price = (((hushushData.seatCount.toFloat() - data.defaultTicketCount.toFloat()) * data.extraOneTicketAmount.toFloat()) + data.extraOneTicketAmount.toFloat())

        cgst.text = "200"
        sgst.text = "150"
        discount.text = "21.55"

        totalPrice = price + cgst.text.toString().toFloat() + +sgst.text.toString().toFloat() - discount.text.toString().toFloat()

        total.text = totalPrice.toString()
    }

    private fun populateView() {
        Glide.with(this)
                .load(File(externalCacheDir.absolutePath + "/image.jpg"))
                .apply(RequestOptions()
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .skipMemoryCache(true)
                )
                .into(preview)

        selectedPackageName.text = data.name

        packagePrice.text = (((hushushData.seatCount.toFloat() - data.defaultTicketCount.toFloat()) * data.extraOneTicketAmount.toFloat()) + data.extraOneTicketAmount.toFloat()).toString()
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

