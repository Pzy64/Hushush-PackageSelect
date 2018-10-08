package packageselect.hushush.co.packages.dao
import com.google.gson.annotations.SerializedName
import java.io.Serializable


data class Packages(
    val packages: List<Package>
) {
    data class Package(
        val name: String,
        val items: List<Item>,
        val id: Int,
        @SerializedName("pic_url")
        val picUrl: String,
        @SerializedName("default_ticket_count")
        val defaultTicketCount: Int,
        @SerializedName("default_package_amount")
        val defaultPackageAmount: Int,
        @SerializedName("extra_one_user_amount")
        val extraOneUserAmount: Int
    ) {
        data class Item(
            val itemname: String,
            val category: String
        )
    }
}