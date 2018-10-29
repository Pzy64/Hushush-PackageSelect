package packageselect.hushush.co.summary

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity;
import packageselect.hushush.co.R

import kotlinx.android.synthetic.main.summary_activity.*

class SummaryActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.summary_activity)
        setSupportActionBar(toolbar)

    }

}
