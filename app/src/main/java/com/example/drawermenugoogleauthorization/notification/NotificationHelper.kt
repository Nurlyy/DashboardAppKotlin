package com.example.drawermenugoogleauthorization.notification

import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.text.format.DateFormat
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import com.example.drawermenugoogleauthorization.EmptyActivity
import com.example.drawermenugoogleauthorization.MainActivity
import com.example.drawermenugoogleauthorization.R
import java.util.*

const val CHANNEL_ID = "testID"
const val NOTIFICATION_ID = "1456"
const val titleExtra = "titleExtra"
const val messageExtra = "messageExtra"

class NotificationHelper: BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        // Create an Intent for the activity you want to start
        val resultIntent = Intent(context, EmptyActivity::class.java)
        // Create the TaskStackBuilder
        val resultPendingIntent: PendingIntent? = TaskStackBuilder.create(context).run {
            // Add the intent, which inflates the back stack
            addNextIntentWithParentStack(resultIntent)
            // Get the PendingIntent containing the entire back stack
            getPendingIntent(0,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        }

        Log.d("MyTag", "onReceive: RECEIVED!")
        val notification = NotificationCompat.Builder(context!!, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(intent?.getStringExtra(titleExtra))
            .setContentText(intent?.getStringExtra(messageExtra))
            .setContentIntent(resultPendingIntent)
            .setAutoCancel(true)
            .build()

        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(NOTIFICATION_ID.toInt(), notification)
    }
}