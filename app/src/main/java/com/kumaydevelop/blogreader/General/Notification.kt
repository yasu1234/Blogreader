package com.kumaydevelop.blogreader.General

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.kumaydevelop.blogreader.Activity.MainActivity
import com.kumaydevelop.blogreader.Model.BlogModel
import com.kumaydevelop.blogreader.R

private const val CHANNEL_ID = "update_channel"
private const val REQUEST_CODE = 1

// チャンネルを作成する
fun createChannel(context: Context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val channel = NotificationChannel(
                CHANNEL_ID,
            "新着",
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
        enableVibration(true)
        setShowBadge(true)
    }

    val manager = context.getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(channel)
    }
}

fun notifyUpdate(context: Context, blog: BlogModel) {
    // 通知をタップしたときに一覧画面を開くようにする
    val intent = Intent(context, MainActivity::class.java)

    val pendingIntent = PendingIntent.getActivity(context, REQUEST_CODE, intent, PendingIntent.FLAG_ONE_SHOT)

    val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.icon)
            .setContentTitle("記事更新")
            .setContentText(blog.title + "の記事が更新しました")
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

    NotificationManagerCompat.from(context).notify(blog.id.toInt(), notification)
}