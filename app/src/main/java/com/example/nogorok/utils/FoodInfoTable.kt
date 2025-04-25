package com.example.nogorok.utils

import com.example.nogorok.utils.DailyIntakeCalories.NutritionInfo

object FoodInfoTable {
    private val sFoodInfoTable = mapOf(
        "Croissant" to NutritionInfo(
            "Croissant", 4.67f, 11.97f, 6.646f, 0.624f, 3.149f, 0f, 26.11f, 1.5f, 6.42f,
            38f, 424f, 67f, 0f, 0f, 2f, 6f,
            231f
        ),
        "Milk" to NutritionInfo(
            "Milk", 3.22f, 3.25f, 1.865f, 0.195f, 0.812f, 0f, 4.52f, 0f, 5.26f,
            10f, 40f, 143f, 0f, 0f, 11f, 0f,
            60f
        ),
        "Apple" to NutritionInfo(
            "Apple", 0.36f, 0.23f, 0.039f, 0.07f, 0.01f, 0f, 19.06f, 3.3f, 14.34f,
            0f, 1f, 148f, 2f, 10f, 1f, 1f,
            72F
        ),
        "Cheese Pizza" to NutritionInfo(
            "Cheese Pizza", 10.6f, 10.1f, 4.304f, 1.776f, 2.823f, 0f, 26.08f, 1.6f, 3.06f,
            21f, 462f, 138f, 0f, 0f, 18f, 9f,
            237f
        ),
        "Orange Juice" to NutritionInfo(
            "Orange Juice", 0.73f, 0.21f, 0.025f, 0.042f, 0.038f, 0f, 10.9f, 0.2f, 8.81f,
            0f, 1f, 210f, 4f, 87f, 1f, 1f,
            47f
        ),
        "Spaghetti" to NutritionInfo(
            "Spaghetti", 8.06f, 1.29f, 0.245f, 0.444f, 0.182f, 0f, 42.95f, 2.5f, 0.78f,
            0f, 325f, 63f, 0f, 0f, 1f, 10f,
            220f
        ),
        "Soda" to NutritionInfo(
            "Soda", 0.26f, 0.04f, 0f, 0f, 0f, 0f, 36.05f, 0f, 33.76f,
            0f, 22f, 4f, 0f, 0f, 0f, 0f,
            140f
        ),
        "Potato Chips" to NutritionInfo(
            "Potato Chips", 1.84f, 10.49f, 3.069f, 3.408f, 2.755f, 0f, 13.93f, 1.2f, 1.15f,
            0f, 147f, 460f, 0f, 9f, 1f, 2f,
            153f
        ),
        "Hamburger" to NutritionInfo(
            "Hamburger", 11.62f, 9.22f, 3.361f, 0.948f, 3.212f, 0f, 32.31f, 2.2f, 6.32f,
            28f, 504f, 237f, 1f, 4f, 12f, 14f,
            257f
        )
    )

    operator fun get(foodName: String): NutritionInfo? {
        return sFoodInfoTable[foodName]
    }

    fun keys(): List<String> {
        return sFoodInfoTable.keys.toList()
    }
}
