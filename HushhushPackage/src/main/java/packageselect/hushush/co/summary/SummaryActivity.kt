package packageselect.hushush.co.summary

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import kotlinx.android.synthetic.main.summary_activity.*
import kotlinx.android.synthetic.main.summary_content.*
import packageselect.hushush.co.R
import packageselect.hushush.co.packages.dao.Pkgs
import java.io.File

class SummaryActivity : AppCompatActivity() {

    private lateinit var data: Package

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.summary_activity)
        setSupportActionBar(toolbar)
        toolbar.title = "Preview"

        data = intent.getSerializableExtra(Pkgs.TAG) as Package

        Glide.with(this)
                .load(File(externalCacheDir.absolutePath + "/image.jpg"))
                .apply(RequestOptions()
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .skipMemoryCache(true)
                )
                .into(preview)

        selectedPackageName.text = data.name

    }

}
