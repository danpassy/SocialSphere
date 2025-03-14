package fr.isen.boussougou.socialsphere.models

data class Comment(
    val text: String = "",
    val userName: String = "",
    val timestamp: Long = 0L
)
