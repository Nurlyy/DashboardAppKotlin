package com.example.drawermenugoogleauthorization.task

import android.net.Uri
import java.io.File
import java.util.*

data class Task(
//    val taskId: String?,
    val title:String? = null,
    val description: String? = null,
    val year: Int? = null,
    val month: Int? = null,
    val dayOfMonth: Int? = null,
    val hour: Int? = null,
    val minute: Int? = null,
    val notification: Boolean = false,
    val file: String? = null,
    val isDone: Boolean = false,
    val isDeleted: Boolean = false,
    val isFavourite: Boolean = false,
)
