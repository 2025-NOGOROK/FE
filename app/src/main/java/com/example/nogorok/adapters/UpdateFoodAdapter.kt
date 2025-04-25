package com.example.nogorok.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.example.nogorok.R
import com.example.nogorok.databinding.UpdateFoodListBinding
import com.example.nogorok.utils.FoodInfoTable
import com.example.nogorok.utils.NutritionUpdateData
import com.example.nogorok.utils.createNutritionDataPoint
import com.example.nogorok.utils.showToast
import com.example.nogorok.viewmodel.UpdateFoodViewModel
import com.samsung.android.sdk.health.data.helper.DataUtilHelper
import com.samsung.android.sdk.health.data.request.DataType.NutritionType.MealType
import java.time.format.DateTimeFormatter
import java.util.ArrayList

class UpdateFoodAdapter(private var viewModel: UpdateFoodViewModel) :
    RecyclerView.Adapter<UpdateFoodAdapter.ViewHolder>() {

    private var updateFoodData: MutableList<NutritionUpdateData> = mutableListOf()
    private lateinit var mMealType: MealType
    private lateinit var mTime: String
    private lateinit var context: Context

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): UpdateFoodAdapter.ViewHolder {
        val binding =
            UpdateFoodListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        context = parent.context
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: UpdateFoodAdapter.ViewHolder, position: Int) {
        initializeOnClickListeners(holder, position)

        val updateFood = updateFoodData[position]
        val localTime = DataUtilHelper.instantToLocal(updateFood.updateTime!!)

        holder.binding.foodTitle.text = updateFood.title
        holder.binding.updateTime.text = localTime.format(DateTimeFormatter.ofPattern("HH:mm"))
        if (updateFood.isClient) {
            val updateTitle = updateFood.title + context.getString(R.string.times) + updateFood.amount
            holder.binding.foodTitle.text = updateTitle
            holder.binding.dataSourceName.text = context.getString(R.string.data_source_dairy)
        } else {
            holder.binding.foodDelete.visibility = View.GONE
            holder.binding.dataSourceName.text = context.getString(R.string.data_source_other)
        }
    }

    override fun getItemCount(): Int {
        return updateFoodData.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateList(mealType: MealType, foodList: ArrayList<NutritionUpdateData>, time: String) {
        mMealType = mealType
        mTime = time
        updateFoodData = foodList.toMutableList()
        notifyDataSetChanged()
    }

    private fun initializeOnClickListeners(holder: ViewHolder, position: Int) {
        holder.binding.updateCardView.setOnClickListener {
            if (holder.binding.dataSourceName.text == context.getString(R.string.data_source_other)) {
                showToast(context, "No permission to update other client's data")
            } else {
                onItemClickNameListView(
                    holder.binding.foodTitle.text.split(" x ")[0],
                    updateFoodData[position].amount,
                    updateFoodData[position].uid
                )
            }
        }

        holder.binding.foodDelete.setOnClickListener {
            viewModel.deleteNutritionData(updateFoodData[position].uid)
        }
    }

    private fun onItemClickNameListView(foodName: String, amount: Float, uId: String) {

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
        inputView!!.setText(amount.toString())
        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
            val inputValue = inputView.text.toString()
            if (inputValue == "" || inputValue.toFloat() == amount) {
                showToast(context, "Invalid input")
            } else if (inputValue.toFloat() > 0) {
                viewModel.updateNutritionData(
                    uId,
                    createNutritionDataPoint(foodName, inputValue.toFloat(), mMealType, mTime)
                )
            } else {
                viewModel.deleteNutritionData(uId)
            }
        }
    }

    inner class ViewHolder(val binding: UpdateFoodListBinding) :
        RecyclerView.ViewHolder(binding.root)
}
