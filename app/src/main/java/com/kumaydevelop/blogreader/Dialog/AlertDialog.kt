package com.kumaydevelop.blogreader.Dialog

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.appcompat.app.AlertDialog

class AlertDialog : DialogFragment() {
    var title = "title"
    var okText = "OK"
    var cancelText = ""
    var onOkClickListener : DialogInterface.OnClickListener? = null
    var onCancelClickListener : DialogInterface.OnClickListener? = null

    // OKボタンとキャンセルボタンを作成
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(context!!)
        builder.setTitle(title)
                .setPositiveButton(okText, onOkClickListener)
        builder.setTitle(title)
                .setNegativeButton(cancelText, onCancelClickListener)
        return builder.create()
    }

    override fun onPause() {
        super.onPause()
        // ダイアログを閉じる
        dismiss()
    }
}