package packageselect.hushush.co.packages.dao

import com.google.gson.annotations.SerializedName
import java.io.Serializable


data class Pkgs(
        val packages: List<Package>
) : Serializable {
    companion object {
        const val TAG = "Package"
    }
}

data class Package(
        @SerializedName("default_ticket_count")
        val defaultTicketCount: String,
        @SerializedName("default_ticket_price")
        val defaultTicketPrice: String,
        @SerializedName("extra_one_ticket_amount")
        val extraOneTicketAmount: String,
        val id: String,
        @SerializedName("item_includes")
        val itemIncludes: List<ItemInclude>,
        val name: String,
        @SerializedName("pic_url")
        val picUrl: String
) : Serializable

data class ItemInclude(
        val category: String,
        val itemname: String
) : Serializable