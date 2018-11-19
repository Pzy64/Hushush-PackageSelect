package packageselect.hushush.co.packages

import android.app.Activity
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.widget.Toast
import kotlinx.android.synthetic.main.content_package_select.*
import packageselect.hushush.co.R
import packageselect.hushush.co.SelectPackage
import packageselect.hushush.co.packages.adapters.PackagesAdapter
import packageselect.hushush.co.packages.dao.HushushData
import packageselect.hushush.co.packages.network.PackagesAPI


class HushushPackages : AppCompatActivity() {

    private var doubleBackToExitPressedOnce = false
    private lateinit var data: HushushData

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_package_select)

        setResult(SelectPackage.RES_HUSHPACKAGE_CANCEL)

        data = intent.getSerializableExtra(SelectPackage.DATA) as HushushData

        callGetPackagesAPI()

    }

    private fun callGetPackagesAPI() {

        PackagesAPI.onPackageRecieved(data.clientToken) { res, status ->
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
