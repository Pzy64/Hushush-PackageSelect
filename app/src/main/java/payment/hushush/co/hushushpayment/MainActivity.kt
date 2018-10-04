package payment.hushush.co.hushushpayment

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.fields.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        submit.setOnClickListener {
            val i = Intent(this, PaymentActivity::class.java)
            i.putExtra(PaymentActivity.clientToken, client_token.text.toString())
            i.putExtra(PaymentActivity.bookingId, booking_id.text.toString())
            i.putExtra(PaymentActivity.selectedDate, selected_date.text.toString())
            i.putExtra(PaymentActivity.movieName, movie_name.text.toString())
            i.putExtra(PaymentActivity.mLocation, location.text.toString())
            i.putExtra(PaymentActivity.theatreName, theatre_name.text.toString())
            i.putExtra(PaymentActivity.showTime, show_time.text.toString())
            i.putExtra(PaymentActivity.screenNumber, screen_number.text.toString())
            i.putExtra(PaymentActivity.seatCount, seat_count.text.toString())
            i.putExtra(PaymentActivity.customerName, customer_name.text.toString())
            i.putExtra(PaymentActivity.mobileNumber, mobile_number.text.toString())
            i.putExtra(PaymentActivity.userEmail, user_email.text.toString())
            i.putExtra(PaymentActivity.seatId, seat_id.text.toString())
            i.putExtra(PaymentActivity.screenSize, screen_size.text.toString())
            i.putExtra(PaymentActivity.callbackUrl, callback_url.text.toString())
            i.putExtra(PaymentActivity.checksumHash, "hash")
            startActivity(i)
        }

    }
}
