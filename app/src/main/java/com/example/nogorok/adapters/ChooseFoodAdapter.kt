package com.example.nogorok.adapters

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.nogorok.R
import com.example.nogorok.databinding.ChooseFoodListBinding
import com.example.nogorok.utils.FoodInfoTable
import com.example.nogorok.utils.createNutritionDataPoint
import com.example.nogorok.utils.showToast
import com.example.nogorok.viewmodel.ChooseFoodViewModel
import com.samsung.android.sdk.health.data.request.DataType.NutritionType.MealType

class ChooseFoodAdapter(private var viewModel: ChooseFoodViewModel) :
    RecyclerView.Adapter<ChooseFoodAdapter.ViewHolder>() {

    private var foodItemList: List<String> = listOf()
    private lateinit var mMealType: MealType
    private lateinit var mTime: String
    private lateinit var context: Context

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ChooseFoodAdapter.ViewHolder {
        val binding =
            ChooseFoodListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        context = parent.context
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ChooseFoodAdapter.ViewHolder, position: Int) {
        initializeOnClickListeners(holder, position)

        val foodItem = foodItemList[position]

        holder.binding.chooseFood.text = foodItem
    }

    override fun getItemCount(): Int {
        return foodItemList.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateList(mealType: MealType, foodList: List<String>, time: String) {
        mMealType = mealType
        mTime = time
        foodItemList = foodList
        notifyDataSetChanged()
    }

    private fun initializeOnClickListeners(holder: ChooseFoodAdapter.ViewHolder, position: Int) {
        holder.binding.chooseFoodCard.setOnClickListener {
            onItemClickNameListView(position)
        }
    }

    private fun onItemClickNameListView(position: Int) {
        val foodName = foodItemList[position]

        val alertDialogBuilder = AlertDialog.Builder(context)
            .setTitle(R.string.food_info)
            .setMessage(
                context.getString(
                    R.string.msg_dialog_add_meal,
                    foodName,
                    FoodInfoTable[foodName]?.calories
                )
            )
            .setPositiveButton(R.string.date_picker_confirm_button, null)
            .setView(R.layout.alert_dialog_view)

        val alertDialog = alertDialogBuilder.show()
        val inputView = alertDialog.findViewById<EditText>(R.id.take_input)
        inputView.hint = context.getString(R.string.enter_amount_hint)
        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
            val inputValue = inputView.text.toString()
            if (inputValue == "") {
                showToast(context, "Invalid input")
            } else if (inputValue.toFloat() > 0) {
                viewModel.insertNutritionData(
                    createNutritionDataPoint(foodName, inputValue.toFloat(), mMealType, mTime)
                )
            } else {
                Toast.makeText(context, "Enter valid input", Toast.LENGTH_SHORT).show()
            }
        }
    }

    inner class ViewHolder(val binding: ChooseFoodListBinding) :
        RecyclerView.ViewHolder(binding.root)
}
