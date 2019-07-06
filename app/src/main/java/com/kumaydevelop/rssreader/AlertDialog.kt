package com.kumaydevelop.rssreader

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.app.AlertDialog

class AlertDialog : DialogFragment() {
    var title = "title"
    var okText = "OK"
    var cancelText = "キャンセル"
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