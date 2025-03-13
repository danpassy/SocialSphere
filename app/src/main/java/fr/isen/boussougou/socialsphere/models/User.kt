package fr.isen.boussougou.socialsphere.models

data class User(
    val id: String,
    val name: String,
    val surname: String,
    val job: String,
    val profileImageUrl: String
)
