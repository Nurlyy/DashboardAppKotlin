package com.example.drawermenugoogleauthorization.task

import android.net.Uri
import java.io.File
import java.util.*

data class Task(
//    val taskId: String?,
    val title:String,
    val description: String? = null,
    val year: Int,
    val month: Int,
    val dayOfMonth: Int,
    val hour: Int,
    val minute: Int,
    val notification: Boolean = false,
    val file: String? = null,
    val isDone: Boolean = false,
    val isDeleted: Boolean = false,
)
