package com.example.drawermenugoogleauthorization.task

import android.net.Uri
import java.io.File
import java.util.*

data class Task(
    val taskId: String? = null,
    val title:String? = null,
    val description: String? = null,
    var year: Int? = null,
    var month: Int? = null,
    var dayOfMonth: Int? = null,
    val hour: Int? = null,
    val minute: Int? = null,
    val notification: Boolean = false,
    val notificationID: Int = 0,
    val file: String? = null,
    var isDone: Boolean = false,
    var isDeleted: Boolean = false,
)
