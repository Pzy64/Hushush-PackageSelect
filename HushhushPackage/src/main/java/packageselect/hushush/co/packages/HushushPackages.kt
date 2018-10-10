package packageselect.hushush.co.packages

import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.widget.Toast
import kotlinx.android.synthetic.main.content_package_select.*
import packageselect.hushush.co.R
import packageselect.hushush.co.packages.adapters.PackagesAdapter
import packageselect.hushush.co.packages.network.GetPackagesAPI


class HushushPackages : AppCompatActivity() {

    companion object {
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

    private fun makePostUrl(): String = "http://192.168.100.70:3000/api/initapi?" +
            "$clientToken=${intent.getStringExtra(clientToken)}&" +
            "$bookingId=${intent.getStringExtra(bookingId)}&" +
            "$selectedDate=${intent.getStringExtra(selectedDate)}&" +
            "$movieName=${intent.getStringExtra(movieName)}&" +
            "$mLocation=${intent.getStringExtra(mLocation)}&" +
            "$theatreName=${intent.getStringExtra(theatreName)}&" +
            "$showTime=${intent.getStringExtra(showTime)}&" +
            "$screenNumber=${intent.getStringExtra(screenNumber)}&" +
            "$seatCount=${intent.getStringExtra(seatCount)}&" +
            "$customerName=${intent.getStringExtra(customerName)}&" +
            "$mobileNumber=${intent.getStringExtra(mobileNumber)}&" +
            "$userEmail=${intent.getStringExtra(userEmail)}&" +
            "$seatId=${intent.getStringExtra(seatId)}&" +
            "$screenSize=${intent.getStringExtra(screenSize)}&" +
            "$callbackUrl=${intent.getStringExtra(callbackUrl)}&" +
            "$checksumHash=${intent.getStringExtra(checksumHash)}"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_package_select)

        callGetPackagesAPI()
    }

    private fun callGetPackagesAPI() {

        GetPackagesAPI.onPackageRecieved(intent.getStringExtra(clientToken)) { res, status ->
            when (status) {
                200 -> {
                    if (res != null) {
                        val packages = res.body()
                        if (packages != null) {
                            recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
                            recyclerView.adapter = PackagesAdapter(
                                    packages,
                                    intent.getStringExtra(seatCount),
                                    intent.getStringExtra(screenSize)
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
