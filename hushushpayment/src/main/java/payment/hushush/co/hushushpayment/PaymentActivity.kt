package payment.hushush.co.hushushpayment

import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.webkit.*
import android.widget.Toast
import kotlinx.android.synthetic.main.content_payment.*

class PaymentActivity : AppCompatActivity() {

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

        const val PERMISSIONS_REQUEST_STORAGE = 1000
        const val FILECHOOSER_RESULTCODE = 1002
        const val FILECHOOSER_REQUESTCODE = 1004
    }

    private var doubleBackToExitPressedOnce = false

    private var filePathCallback: ValueCallback<Array<Uri>>? = null

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
        setContentView(R.layout.activity_payment)

        webview.settings.javaScriptEnabled = true
        webview.settings.allowFileAccess = true
        webview.webViewClient = SecureWebViewClient()
        webview.webChromeClient = SecureWebChromeClient()

        webview.loadUrl(makePostUrl())
    }

    private inner class SecureWebViewClient : WebViewClient() {

        override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {

            return super.shouldOverrideUrlLoading(view, request)
        }

    }

    private inner class SecureWebChromeClient : WebChromeClient() {

        override fun onShowFileChooser(webView: WebView?, filePathCallback: ValueCallback<Array<Uri>>?, fileChooserParams: FileChooserParams?): Boolean {

            this@PaymentActivity.filePathCallback = filePathCallback


            var intent: Intent? = null
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                intent = fileChooserParams!!.createIntent()
                intent.type = "image/*"
            }
            try {
                startActivityForResult(intent, FILECHOOSER_REQUESTCODE)
            } catch (e: ActivityNotFoundException) {
                this@PaymentActivity.filePathCallback = null
                Toast.makeText(applicationContext, "Cannot Open Image Chooser", Toast.LENGTH_LONG).show()
                return false
            }

            return true
        }

        override fun onJsAlert(view: WebView?, url: String?, message: String?, result: JsResult?): Boolean {
            result!!.confirm()
            return true
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == FILECHOOSER_REQUESTCODE) {
            if (filePathCallback == null)
                return
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                filePathCallback!!.onReceiveValue(WebChromeClient.FileChooserParams.parseResult(resultCode, data))
            }

        }

    }


    override fun onDestroy() {
        webview.clearCache(true)
        webview.clearFormData()
        webview.clearHistory()
        webview.clearMatches()
        super.onDestroy()
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

    private fun getStoragePermission() {
        /**
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(this.applicationContext,
                        android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this,
                    arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                    PERMISSIONS_REQUEST_STORAGE)
        }
    }

    /**
     * Handles the result of the request for location permissions.
     */
    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>,
                                            grantResults: IntArray) {
        when (requestCode) {
            PERMISSIONS_REQUEST_STORAGE -> {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.isNotEmpty() && grantResults[0] != PackageManager.PERMISSION_GRANTED) {

                }
                else    {
                   //permission granted
                }
            }
        }
    }

}
