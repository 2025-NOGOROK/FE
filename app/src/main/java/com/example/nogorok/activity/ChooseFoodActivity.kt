package com.example.nogorok.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.nogorok.ChooseFoodActivityBinding
import com.example.nogorok.R
import com.example.nogorok.adapters.ChooseFoodAdapter
import com.example.nogorok.utils.FoodInfoTable
import com.example.nogorok.utils.getMealType
import com.example.nogorok.utils.getTime
import com.example.nogorok.utils.showToast
import com.example.nogorok.viewmodel.ChooseFoodViewModel
import com.example.nogorok.viewmodel.HealthViewModelFactory
import com.samsung.android.sdk.health.data.request.DataType.NutritionType.MealType

class ChooseFoodActivity : AppCompatActivity() {

    private lateinit var binding: ChooseFoodActivityBinding
    private lateinit var chooseFoodViewModel: ChooseFoodViewModel
    private lateinit var chooseFoodAdapter: ChooseFoodAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        chooseFoodViewModel = ViewModelProvider(
            this, HealthViewModelFactory(this)
        )[ChooseFoodViewModel::class.java]

        chooseFoodAdapter = ChooseFoodAdapter(chooseFoodViewModel)

        binding = DataBindingUtil
            .setContentView<ChooseFoodActivityBinding>(this, R.layout.choose_food)
            .apply {
                viewModel = chooseFoodViewModel
                chooseFoodList.layoutManager = LinearLayoutManager(this@ChooseFoodActivity)
                chooseFoodList.adapter = chooseFoodAdapter
            }

        setup(getMealType(intent), getTime(intent))
        setChooseFoodObserver()
    }

    private fun setup(mealType: MealType, time: String) {
        chooseFoodAdapter.updateList(mealType, FoodInfoTable.keys(), time)
    }

    private fun setChooseFoodObserver() {
        /** Show insert success response */
        chooseFoodViewModel.nutritionInsertResponse.observe(this) {
            showToast(this, "Data inserted")
            finish()
        }

        /** Show toast on exception occurrence **/
        chooseFoodViewModel.exceptionResponse.observe(this) { message ->
            showToast(this, message)
        }
    }
}
