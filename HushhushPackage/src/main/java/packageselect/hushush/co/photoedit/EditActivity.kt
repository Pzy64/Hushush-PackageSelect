package packageselect.hushush.co.photoedit

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity;
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import packageselect.hushush.co.R

import kotlinx.android.synthetic.main.edit_activity.*
import kotlinx.android.synthetic.main.edit_content.*
import android.R.attr.data
import android.app.Activity
import android.view.View
import com.robertlevonyan.components.kex.set


class EditActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.edit_activity)
        setSupportActionBar(toolbar)

        selectImage.setOnClickListener {
            CropImage.activity()
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .start(this)
        }


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            val result = CropImage.getActivityResult(data)
            if (resultCode == Activity.RESULT_OK) {
                val resultUri = result.uri
                selectImageLayout.visibility = View.GONE
                image.visibility = View.VISIBLE
                image.set(resultUri)
            }
        }
    }
}
