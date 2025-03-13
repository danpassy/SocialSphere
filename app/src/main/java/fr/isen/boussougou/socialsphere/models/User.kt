package fr.isen.boussougou.socialsphere.models

data class User(
    val id: String,
    val name: String,
    val surname: String,
    val job: String,
    val profileImageUrl: String,
    val followersCount: Int = 0, // Nouveau champ pour le nombre de followers
    val followingCount: Int = 0  // Nouveau champ pour le nombre de personnes suivies
)
