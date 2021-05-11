package tech.alexjones.ffcinema.model

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document
data class UserRating(
    @Id
    val userId: Long,
    val ratings: List<Rating>,
) {
    data class Rating(
        val movieId: String,
        val score: Short,
    )
}