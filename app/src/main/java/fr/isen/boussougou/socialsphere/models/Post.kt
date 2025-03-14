package fr.isen.boussougou.socialsphere.models

data class Post(
    val id: String? = null,
    val userName: String? = null,
    val userProfileImageUrl: String? = null,
    val mediaUrl: String? = null,
    val description: String? = null,
    val likesCount: Int? = 0,
    val commentsCount: Int? = 0
)
