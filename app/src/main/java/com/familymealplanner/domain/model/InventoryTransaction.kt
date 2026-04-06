package com.familymealplanner.domain.model

data class InventoryTransaction(
    val id: String,
    val ingredientId: String,
    val quantityChange: Double,
    val status: TransactionStatus,
    val reason: String,
    val mealPlanId: String?,
    val createdAt: Long,
    val updatedAt: Long
)

enum class TransactionStatus {
    COMMITTED,
    RESERVED,
    RELEASED
}

data class PantryItem(
    val ingredient: Ingredient,
    val availableQuantity: Double,
    val reservedQuantity: Double,
    val reservations: List<Reservation> = emptyList()
)

data class Reservation(
    val mealPlanId: String,
    val mealName: String,
    val quantity: Double
)
