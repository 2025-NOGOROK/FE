package com.example.nogorok.activity

import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.nogorok.R
import com.example.nogorok.UpdateFoodActivityBinding
import com.example.nogorok.adapters.UpdateFoodAdapter
import com.example.nogorok.utils.NutritionUpdateData
import com.example.nogorok.utils.getMealType
import com.example.nogorok.utils.getTime
import com.example.nogorok.utils.showToast
import com.example.nogorok.utils.AppConstants
import com.example.nogorok.viewmodel.HealthViewModelFactory
import com.example.nogorok.viewmodel.UpdateFoodViewModel
import com.samsung.android.sdk.health.data.request.DataType.NutritionType.MealType
import java.util.ArrayList

class UpdateFoodActivity : AppCompatActivity() {

    private lateinit var binding: UpdateFoodActivityBinding
    private lateinit var updateFoodViewModel: UpdateFoodViewModel
    private lateinit var updateFoodAdapter: UpdateFoodAdapter
    private lateinit var mealType: MealType

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        updateFoodViewModel = ViewModelProvider(
            this, HealthViewModelFactory(this)
        )[UpdateFoodViewModel::class.java]

        updateFoodAdapter = UpdateFoodAdapter(updateFoodViewModel)

        binding = DataBindingUtil
            .setContentView<UpdateFoodActivityBinding>(this, R.layout.update_food)
            .apply {
                viewModel = updateFoodViewModel
                updateFoodList.layoutManager = LinearLayoutManager(this@UpdateFoodActivity)
                updateFoodList.adapter = updateFoodAdapter
            }

        mealType = getMealType(intent)
        binding.textDesc.text = mealType.toString().format()
        val foodList = intent.extras?.getParcelableArrayList(
            AppConstants.BUNDLE_KEY_NUTRITION_DATA,
            NutritionUpdateData::class.java
        )

        setup(foodList, getTime(intent))
        setUpdateFoodObserver()
    }

    private fun setup(foodList: ArrayList<NutritionUpdateData>?, time: String) {
        if (foodList != null) {
            updateFoodAdapter.updateList(mealType, foodList, time)
        }
    }

    private fun setUpdateFoodObserver() {
        /**  Show update success response */
        updateFoodViewModel.nutritionUpdateResponse.observe(this) {
            showToast(this, "Data updated")
            finish()
        }

        /**  Show delete success response */
        updateFoodViewModel.nutritionDeleteResponse.observe(this) {
            showToast(this, "Data deleted")
            finish()
        }

        /** Show toast on exception occurrence **/
        updateFoodViewModel.exceptionResponse.observe(this) { message ->
            showToast(this, message)
        }
    }

    private fun String.format(): String =
        this.lowercase().replaceFirstChar { it.titlecase() }.replace("_", " ")
}
