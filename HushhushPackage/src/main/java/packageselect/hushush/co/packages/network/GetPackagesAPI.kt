package packageselect.hushush.co.packages.network

import com.amstertec.ebin.network.APIClient
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import packageselect.hushush.co.packages.dao.Packages
import retrofit2.Response

object GetPackagesAPI {

    fun onPackageRecieved(
            clientToken: String,
            func: (Response<Packages?>?, status: Int) -> Unit) {
        doAsync {
            try {
                val service = APIClient.getClient().create(GetPackagesRequest::class.java)
                val call = service.postComplaint(clientToken)
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