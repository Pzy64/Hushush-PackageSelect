package packageselect.hushush.co.packages.network

import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import packageselect.hushush.co.network.APIClient
import packageselect.hushush.co.packages.dao.Pkgs
import retrofit2.Response
import java.io.File


object PackagesAPI {

    fun onPackageRecieved(
            clientToken: String,
            func: (Response<Pkgs?>?, status: Int) -> Unit) {
        doAsync {
            try {
                val service = APIClient.getClient().create(PackagesRequest::class.java)
                val call = service.getPackages(clientToken)
                val result = call.execute()

                uiThread {
                    func(result, result.code())
                }
            } catch (exc: Exception) {
                uiThread { func(null, 404) }
            }
        }
    }

    fun onUploadComplete(image: File,
                         bookingId: String,
                         hushushId: String
                         , func: (Response<ResponseBody?>?, status: Int) -> Unit) {
        doAsync {
            try {

                val requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), image)
                val fileToUpload = MultipartBody.Part.createFormData("presentation_image", image.name, requestBody)

                val bid = RequestBody.create(MediaType.parse("text/plain"), bookingId)
                val hid = RequestBody.create(MediaType.get("text/plain"), hushushId)

                val service = APIClient.getClient().create(PackagesRequest::class.java)
                val call = service.uploadImage(fileToUpload, bid, hid)
                val result = call.execute()

                uiThread {
                    func(result, result.code())
                }
            } catch (exc: Exception) {
                uiThread { func(null, 404) }
            }
        }
    }
}