package payment.hushush.co.hushushpayment

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import kotlinx.android.synthetic.main.fields.*
import packageselect.hushush.co.SelectPackage

class MainActivity : AppCompatActivity() {


    private val REQ = 10001
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        submit.setOnClickListener {
            val i = Intent(this, SelectPackage::class.java)
            i.putExtra(SelectPackage.clientToken, client_token.text.toString())
            i.putExtra(SelectPackage.bookingId, booking_id.text.toString())
            i.putExtra(SelectPackage.selectedDate, selected_date.text.toString())
            i.putExtra(SelectPackage.movieName, movie_name.text.toString())
            i.putExtra(SelectPackage.mLocation, location.text.toString())
            i.putExtra(SelectPackage.theatreName, theatre_name.text.toString())
            i.putExtra(SelectPackage.showTime, show_time.text.toString())
            i.putExtra(SelectPackage.screenNumber, screen_number.text.toString())
            i.putExtra(SelectPackage.seatCount, seat_count.text.toString())
            i.putExtra(SelectPackage.customerName, customer_name.text.toString())
            i.putExtra(SelectPackage.mobileNumber, mobile_number.text.toString())
            i.putExtra(SelectPackage.userEmail, user_email.text.toString())
            i.putExtra(SelectPackage.seatId, seat_id.text.toString())
            i.putExtra(SelectPackage.screenSize, screen_size.text.toString())
            i.putExtra(SelectPackage.callbackUrl, callback_url.text.toString())
            i.putExtra(SelectPackage.checksumHash, "hash")
            startActivityForResult(i, REQ)
        }

        submit.performClick()

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQ) {
            if (resultCode == AppCompatActivity.RESULT_OK) {
                Toast.makeText(applicationContext, "OK", Toast.LENGTH_SHORT).show()
            } else if (resultCode == AppCompatActivity.RESULT_CANCELED) {
                Toast.makeText(applicationContext, "CANCELLED", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
