package com.upisoundbox.domain.model

data class RawNotification(
    val packageName: String,
    val notificationKey: String?,
    val postedAt: Long,
    val title: String?,
    val text: String?,
    val bigTitle: String? = null,
    val bigText: String? = null,
    val textLines: List<String> = emptyList(),
    val category: String? = null
) {
    fun fullText(): String {
        return buildString {
            title?.let { append(it).append(" ") }
            text?.let { append(it).append(" ") }
            bigTitle?.let { append(it).append(" ") }
            bigText?.let { append(it).append(" ") }
            if (textLines.isNotEmpty()) {
                append(textLines.joinToString(" ")).append(" ")
            }
        }.trim()
    }
}
