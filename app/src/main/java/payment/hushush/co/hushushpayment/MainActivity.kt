package payment.hushush.co.hushushpayment

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.fields.*
import packageselect.hushush.co.photoedit.SelectHushushPackage

class MainActivity : AppCompatActivity() {


    private val REQ = 10001
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        submit.setOnClickListener {
            val i = Intent(this, SelectHushushPackage::class.java)
            i.putExtra(SelectHushushPackage.clientToken, client_token.text.toString())
            i.putExtra(SelectHushushPackage.bookingId, booking_id.text.toString())
            i.putExtra(SelectHushushPackage.selectedDate, selected_date.text.toString())
            i.putExtra(SelectHushushPackage.movieName, movie_name.text.toString())
            i.putExtra(SelectHushushPackage.mLocation, location.text.toString())
            i.putExtra(SelectHushushPackage.theatreName, theatre_name.text.toString())
            i.putExtra(SelectHushushPackage.showTime, show_time.text.toString())
            i.putExtra(SelectHushushPackage.screenNumber, screen_number.text.toString())
            i.putExtra(SelectHushushPackage.seatCount, seat_count.text.toString())
            i.putExtra(SelectHushushPackage.customerName, customer_name.text.toString())
            i.putExtra(SelectHushushPackage.mobileNumber, mobile_number.text.toString())
            i.putExtra(SelectHushushPackage.userEmail, user_email.text.toString())
            i.putExtra(SelectHushushPackage.seatId, seat_id.text.toString())
            i.putExtra(SelectHushushPackage.screenSize, screen_size.text.toString())
            i.putExtra(SelectHushushPackage.callbackUrl, callback_url.text.toString())
            i.putExtra(SelectHushushPackage.checksumHash, "hash")
            i.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
            startActivityForResult(i, REQ)

        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQ) {
            if (resultCode == AppCompatActivity.RESULT_OK) {
               startActivity(Intent(this, SuccessActivity::class.java))
            } else if (resultCode == AppCompatActivity.RESULT_CANCELED) {
                startActivity(Intent(this, ErrorActivity::class.java))
            }
        }
    }
}
