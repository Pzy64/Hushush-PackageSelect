package packageselect.hushush.co.packages

import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.widget.Toast
import kotlinx.android.synthetic.main.content_package_select.*
import packageselect.hushush.co.R
import packageselect.hushush.co.packages.adapters.PackagesAdapter
import packageselect.hushush.co.packages.dao.HushushData
import packageselect.hushush.co.packages.network.PackagesAPI


class HushushPackages : AppCompatActivity() {

    companion object {

        const val DATA = "HUSHUSHDATA"

        const val clientToken = "client_token"
        const val bookingId = "booking_id"
        const val selectedDate = "selected_date"
        const val movieName = "movie_name"
        const val mLocation = "location"
        const val theatreName = "theatre_name"
        const val showTime = "show_time"
        const val screenNumber = "screen_number"
        const val seatCount = "seat_count"
        const val customerName = "customer_name"
        const val mobileNumber = "mobile_number"
        const val userEmail = "user_email"
        const val seatId = "seat_id"
        const val screenSize = "screen_size"
        const val callbackUrl = "callback_url"
        const val checksumHash = "checksumhash"
    }

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
        setContentView(R.layout.activity_package_select)

        callGetPackagesAPI()

    }

    private fun callGetPackagesAPI() {

        PackagesAPI.onPackageRecieved(intent.getStringExtra(clientToken)) { res, status ->
            when (status) {
                200 -> {
                    if (res != null) {
                        val packages = res.body()

                        if (packages != null) {
                            recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
                            recyclerView.adapter = PackagesAdapter(
                                    packages,
                                    data
                            )
                        }
                    }
                }
            }
        }
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
