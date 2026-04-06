package com.familymealplanner.ui.export

import android.net.Uri
import android.util.Base64
import com.familymealplanner.ui.screens.MealGroup
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Serializable
data class GroceriesExportPacketV1(
    @SerialName("schema")
    val schema: String = "littlechef.groceries.export",
    @SerialName("version")
    val version: Int = 1,
    @SerialName("exportedAt")
    val exportedAt: Long,
    @SerialName("languageCode")
    val languageCode: String,
    @SerialName("groups")
    val groups: List<GroceriesExportGroupV1>
)

@Serializable
data class GroceriesExportGroupV1(
    @SerialName("mealNameCanonical")
    val mealNameCanonical: String,
    @SerialName("mealNameDisplay")
    val mealNameDisplay: String,
    @SerialName("plannedDate")
    val plannedDate: Long? = null,
    @SerialName("mealTypes")
    val mealTypes: List<String> = emptyList(),
    @SerialName("items")
    val items: List<GroceriesExportItemV1>
)

@Serializable
data class GroceriesExportItemV1(
    @SerialName("ingredientNameCanonical")
    val ingredientNameCanonical: String,
    @SerialName("ingredientNameDisplay")
    val ingredientNameDisplay: String,
    @SerialName("quantity")
    val quantity: Double,
    @SerialName("unit")
    val unit: String
)

object GroceriesExportEncoderV1 {
    private val json = Json {
        encodeDefaults = true
        explicitNulls = false
        ignoreUnknownKeys = true
    }

    fun buildPacketFromUiState(
        mealGroups: List<MealGroup>,
        languageCode: String,
        translateIngredient: (String) -> String,
        translateCategory: (String) -> String,
        exportedAt: Long = System.currentTimeMillis()
    ): GroceriesExportPacketV1 {
        val groups = mealGroups.mapNotNull { group ->
            val uncheckedItems = group.items.filter { !it.isChecked }
            if (uncheckedItems.isEmpty()) return@mapNotNull null

            GroceriesExportGroupV1(
                mealNameCanonical = group.mealName,
                mealNameDisplay = translateCategory(group.mealName),
                plannedDate = group.plannedDate,
                mealTypes = group.mealTypes.map { it.name },
                items = uncheckedItems.map { item ->
                    GroceriesExportItemV1(
                        ingredientNameCanonical = item.ingredientName,
                        ingredientNameDisplay = translateIngredient(item.ingredientName),
                        quantity = item.quantity,
                        unit = item.unit
                    )
                }
            )
        }

        return GroceriesExportPacketV1(
            exportedAt = exportedAt,
            languageCode = languageCode,
            groups = groups
        )
    }

    fun encodeToDeepLink(packet: GroceriesExportPacketV1): String {
        val jsonString = json.encodeToString(packet)
        val encoded = Base64.encodeToString(
            jsonString.toByteArray(Charsets.UTF_8),
            Base64.URL_SAFE or Base64.NO_WRAP or Base64.NO_PADDING
        )

        return Uri.Builder()
            .scheme("littlehelper")
            .authority("groceries")
            .appendPath("import")
            .appendQueryParameter("payload", encoded)
            .build()
            .toString()
    }
}

