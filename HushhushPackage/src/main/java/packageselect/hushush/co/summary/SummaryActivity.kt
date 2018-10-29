package packageselect.hushush.co.summary

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity;
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import packageselect.hushush.co.R

import kotlinx.android.synthetic.main.summary_activity.*
import kotlinx.android.synthetic.main.summary_content.*
import java.io.File

class SummaryActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.summary_activity)
        setSupportActionBar(toolbar)
        toolbar.title = "Preview"


        Glide.with(this)
                .load(File(externalCacheDir.absolutePath + "/image.jpg"))
                .apply(RequestOptions()
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .skipMemoryCache(true)
                )
                .into(preview)


    }

}
