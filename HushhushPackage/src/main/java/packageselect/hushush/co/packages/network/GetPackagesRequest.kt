package packageselect.hushush.co.packages.network

import packageselect.hushush.co.packages.dao.Packages
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface GetPackagesRequest {
    @POST("/api/getallpackages")
    @FormUrlEncoded
    fun postComplaint(
            @Field("client_token") clientToken: String
    ): Call<Packages>
}