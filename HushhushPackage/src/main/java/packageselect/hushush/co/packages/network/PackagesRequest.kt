package packageselect.hushush.co.packages.network

import okhttp3.ResponseBody
import packageselect.hushush.co.packages.dao.Packages
import retrofit2.Call
import retrofit2.http.*
import okhttp3.RequestBody
import okhttp3.MultipartBody




interface PackagesRequest {

    @POST("/api/getallpackages")
    @FormUrlEncoded
    fun getPackages(
            @Field("client_token") clientToken: String
    ): Call<Packages>

    @Headers("Accept: application/json")
    @Multipart
    @POST("/api/uploadimage")
    fun uploadImage(
            @Part file: MultipartBody.Part,
            @Part("booking_id") bookingId: RequestBody,
            @Part("hushush_id") hushushId: RequestBody
    ): Call<ResponseBody>

}