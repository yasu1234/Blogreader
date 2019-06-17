package com.kumaydevelop.rssreader

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.app.AlertDialog

class AlertDialog : DialogFragment() {
    var title = "title"
    var okText = "OK"
    var onOkClickListener : DialogInterface.OnClickListener? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(context!!)
        builder.setTitle(title)
                .setPositiveButton(okText, onOkClickListener)
        return builder.create()
    }

    override fun onPause() {
        super.onPause()
        // ダイアログを閉じる場合
        dismiss()
    }
}