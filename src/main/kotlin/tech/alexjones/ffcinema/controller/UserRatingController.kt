package tech.alexjones.ffcinema.controller

import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactive.awaitFirstOrNull
import org.springframework.web.bind.annotation.*
import tech.alexjones.ffcinema.extension.withScoreInRange
import tech.alexjones.ffcinema.model.UserRating
import tech.alexjones.ffcinema.repository.UserRatingRepository

@RestController
@RequestMapping("/ratings")
class UserRatingController(val userRatingRepository: UserRatingRepository) {

    @GetMapping
    suspend fun getAll() =
        userRatingRepository
            .findAll()
            .asFlow()

    @GetMapping("/{userId}")
    suspend fun getById(@PathVariable userId: Long) =
        userRatingRepository
            .findById(userId)
            .awaitFirstOrNull()

    @DeleteMapping
    suspend fun delete(@PathVariable userId: Long, @RequestParam movieId: String?) =
        movieId?.let {
            deleteByMovieId(userId, movieId) } ?:
            deleteAll(userId)

    suspend fun deleteAll(userId: Long) =
        userRatingRepository
            .deleteById(userId)
            .awaitFirstOrNull()

    suspend fun deleteByMovieId(userId: Long, movieId: String) =
        userRatingRepository
            .findById(userId)
            .map { deleteUserRating(it, movieId) }
            .flatMap { userRatingRepository.save(it) }
            .ignoreElement()
            .awaitFirstOrNull()

    private fun deleteUserRating(userRating: UserRating, movieId: String) =
        UserRating(
            userRating.userId,
            userRating.ratings
                .filter { it.movieId != movieId })

    @PostMapping
    suspend fun save(@RequestBody userRating: UserRating) =
        userRatingRepository
            .findById(userRating.userId)
            .defaultIfEmpty(userRating)
            .map { updateUserRating(it, userRating) }
            .flatMap { userRatingRepository.save(it) }
            .awaitFirstOrNull()

    private fun updateUserRating(
        oldUserRating: UserRating,
        newUserRating: UserRating) =
        UserRating(
            newUserRating.userId,
            updateRatings(
                oldUserRating.ratings.withScoreInRange(),
                newUserRating.ratings.withScoreInRange()))

    private fun updateRatings(
        oldRatings: List<UserRating.Rating>,
        newRatings: List<UserRating.Rating>) =
        newRatings +
                oldRatings
                    .filterNot { oldRating ->
                        newRatings
                            .map { it.movieId }
                            .toSet()
                            .contains(oldRating.movieId) }
}
