package com.example.nogorok.features.rest.longrest

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import android.view.LayoutInflater
import com.example.nogorok.databinding.DialogLongRestHelpBinding

class DialogFragment : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val binding = DialogLongRestHelpBinding.inflate(LayoutInflater.from(context))

        binding.btnClose.setOnClickListener {
            dismiss()
        }

        val dialog = AlertDialog.Builder(requireContext())
            .setView(binding.root)
            .create()

        // 배경을 둥글게 보여주기 위해 Dialog의 배경을 투명하게 설정
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        return dialog
    }

}