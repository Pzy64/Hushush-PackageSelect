package payment.hushush.co.hushushpayment

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.fields.*
import packageselect.hushush.co.PackageSelectActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        submit.setOnClickListener {
            val i = Intent(this, PackageSelectActivity::class.java)
            i.putExtra(PackageSelectActivity.clientToken, client_token.text.toString())
            i.putExtra(PackageSelectActivity.bookingId, booking_id.text.toString())
            i.putExtra(PackageSelectActivity.selectedDate, selected_date.text.toString())
            i.putExtra(PackageSelectActivity.movieName, movie_name.text.toString())
            i.putExtra(PackageSelectActivity.mLocation, location.text.toString())
            i.putExtra(PackageSelectActivity.theatreName, theatre_name.text.toString())
            i.putExtra(PackageSelectActivity.showTime, show_time.text.toString())
            i.putExtra(PackageSelectActivity.screenNumber, screen_number.text.toString())
            i.putExtra(PackageSelectActivity.seatCount, seat_count.text.toString())
            i.putExtra(PackageSelectActivity.customerName, customer_name.text.toString())
            i.putExtra(PackageSelectActivity.mobileNumber, mobile_number.text.toString())
            i.putExtra(PackageSelectActivity.userEmail, user_email.text.toString())
            i.putExtra(PackageSelectActivity.seatId, seat_id.text.toString())
            i.putExtra(PackageSelectActivity.screenSize, screen_size.text.toString())
            i.putExtra(PackageSelectActivity.callbackUrl, callback_url.text.toString())
            i.putExtra(PackageSelectActivity.checksumHash, "hash")
            startActivity(i)
        }

    }
}
