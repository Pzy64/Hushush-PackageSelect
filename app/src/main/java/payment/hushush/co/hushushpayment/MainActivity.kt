package payment.hushush.co.hushushpayment

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.fields.*
import packageselect.hushush.co.packages.HushushPackages

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        submit.setOnClickListener {
            val i = Intent(this, HushushPackages::class.java)
            i.putExtra(HushushPackages.clientToken, client_token.text.toString())
            i.putExtra(HushushPackages.bookingId, booking_id.text.toString())
            i.putExtra(HushushPackages.selectedDate, selected_date.text.toString())
            i.putExtra(HushushPackages.movieName, movie_name.text.toString())
            i.putExtra(HushushPackages.mLocation, location.text.toString())
            i.putExtra(HushushPackages.theatreName, theatre_name.text.toString())
            i.putExtra(HushushPackages.showTime, show_time.text.toString())
            i.putExtra(HushushPackages.screenNumber, screen_number.text.toString())
            i.putExtra(HushushPackages.seatCount, seat_count.text.toString())
            i.putExtra(HushushPackages.customerName, customer_name.text.toString())
            i.putExtra(HushushPackages.mobileNumber, mobile_number.text.toString())
            i.putExtra(HushushPackages.userEmail, user_email.text.toString())
            i.putExtra(HushushPackages.seatId, seat_id.text.toString())
            i.putExtra(HushushPackages.screenSize, screen_size.text.toString())
            i.putExtra(HushushPackages.callbackUrl, callback_url.text.toString())
            i.putExtra(HushushPackages.checksumHash, "hash")
            startActivity(i)
        }

    }
}
