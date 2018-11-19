package packageselect.hushush.co

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import org.jetbrains.anko.toast
import packageselect.hushush.co.packages.HushushPackages
import packageselect.hushush.co.packages.dao.HushushData
import packageselect.hushush.co.packages.dao.Pkgs
import packageselect.hushush.co.photoedit.EditActivity
import packageselect.hushush.co.summary.SummaryActivity

class SelectPackage : AppCompatActivity() {

    companion object {
        const val DATA = "HUSHUSHDATA"

        const val clientToken = "client_token"
        const val screenSize = "screen_size"
        const val seatCount = "seat_count"

        const val bookingId = "booking_id"
        const val selectedDate = "selected_date"
        const val movieName = "movie_name"
        const val mLocation = "location"
        const val theatreName = "theatre_name"
        const val showTime = "show_time"
        const val screenNumber = "screen_number"
        const val customerName = "customer_name"
        const val mobileNumber = "mobile_number"
        const val userEmail = "user_email"
        const val seatId = "seat_id"
        const val callbackUrl = "callback_url"
        const val checksumHash = "checksumhash"

        const val RES_HUSHPACKAGE_CANCEL = 100
        const val RES_HUSHPACKAGE_OK = 104
        const val RES_EDITACTIVITY_CANCEL = 101
        const val RES_EDITACTIVITY_OK = 105
        const val RES_SUMMARY_CANCEL = 102
        const val RES_SUMMARY_OK = 103
    }

    private val REQ_CODE = 1023


    private var doubleBackToExitPressedOnce = false

    private val data: HushushData by lazy { makeDataObject() }

    private fun makeDataObject(): HushushData {
        val data = HushushData()

        data.clientToken = intent.getStringExtra(clientToken)
        data.bookingId = intent.getStringExtra(bookingId)
        data.selectedDate = intent.getStringExtra(selectedDate)
        data.movieName = intent.getStringExtra(movieName)
        data.mLocation = intent.getStringExtra(mLocation)
        data.theatreName = intent.getStringExtra(theatreName)
        data.showTime = intent.getStringExtra(showTime)
        data.screenNumber = intent.getStringExtra(screenNumber)
        data.seatCount = intent.getStringExtra(seatCount)
        data.customerName = intent.getStringExtra(customerName)
        data.mobileNumber = intent.getStringExtra(mobileNumber)
        data.userEmail = intent.getStringExtra(userEmail)
        data.seatId = intent.getStringExtra(seatId)
        data.screenSize = intent.getStringExtra(screenSize)

        return data
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_package)

        val i = Intent(this, HushushPackages::class.java)
        i.putExtra(SelectPackage.DATA, data)
        startActivityForResult(i, REQ_CODE)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQ_CODE) {
            when (resultCode) {

                RES_HUSHPACKAGE_CANCEL -> {
                    toast(" packageselect cancelled!")
                }

                RES_HUSHPACKAGE_OK -> {
                    toast(" packageselect ok!")
                    if (data != null) {
                        val intent = Intent(this, EditActivity::class.java)
                        intent.putExtra(SelectPackage.DATA, data.getSerializableExtra(SelectPackage.DATA))
                        intent.putExtra(Pkgs.TAG, data.getSerializableExtra(Pkgs.TAG))
                        startActivityForResult(intent, REQ_CODE)
                    }
                }

                RES_EDITACTIVITY_CANCEL -> {
                    toast(" edit cancelled!")
                }

                RES_EDITACTIVITY_OK -> {
                    toast(" edit ok!")
                    if (data!=null) {
                        val intent = Intent(this, SummaryActivity::class.java)
                        intent.putExtra(SelectPackage.DATA, data.getSerializableExtra(SelectPackage.DATA))
                        intent.putExtra(Pkgs.TAG, data.getSerializableExtra(Pkgs.TAG))
                        startActivityForResult(intent, REQ_CODE)
                    }
                }

                RES_SUMMARY_CANCEL -> {
                    toast(" summary cancelled!")
                }

                RES_SUMMARY_OK -> {
                    toast(" summary ok!")
                }
            }

        }
    }
}
