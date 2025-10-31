package com.example.projetosaveit.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.projetosaveit.R

object NotificaoUtils {
    private const val CANAL_ID = "canal_vitrine"
    private const val NOTIFICATION_ID = 1001

    fun notificarProdutoAdicionado(context: Context, nomeProduto: String) {
        val notificationManager = NotificationManagerCompat.from(context)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val canal = NotificationChannel(
                CANAL_ID,
                "Novos produtos na vitrine",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notificações sobre novos produtos adicionados à vitrine"
                enableVibration(true)
            }
            val manager = context.getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(canal)
        }

        val builder = NotificationCompat.Builder(context, CANAL_ID)
            .setSmallIcon(R.drawable.ic_notifications_black_24dp)
            .setContentTitle("Novo produto na vitrine!")
            .setContentText("O produto \"$nomeProduto\" foi adicionado.")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)

        if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS)
            != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        notificationManager.notify(NOTIFICATION_ID, builder.build())
    }
}
