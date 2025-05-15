package com.example.nogorok

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class SamsungHealthPermissionBottomSheet(
    private val onComplete: () -> Unit = {},
    private val onCancel: () -> Unit = {}
) : BottomSheetDialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_samsung_health_permission, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<Button>(R.id.btnOk).setOnClickListener {
            onComplete()
            dismiss()
        }

        view.findViewById<Button>(R.id.btnCancel).setOnClickListener {
            onCancel()
            dismiss()
        }
    }
}