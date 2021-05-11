package tech.alexjones.ffcinema.controller

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import reactor.core.publisher.Mono
import tech.alexjones.ffcinema.model.UserRating
import tech.alexjones.ffcinema.repository.UserRatingRepository

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class UserRatingControllerTest {
    private val userRatingRepository: UserRatingRepository = mockk()
    private val userRatingController = UserRatingController(userRatingRepository)

    private val validRating1 = UserRating(1,
        listOf(
            UserRating.Rating("tt1", 2)))

    private val validRating2 = UserRating(1,
        listOf(
            UserRating.Rating("tt2", 4)))

    private val validRatingCombined = UserRating(1,
        listOf(
            UserRating.Rating("tt2", 4),
            UserRating.Rating("tt1", 2)))

    private val invalidRating = UserRating(1,
        listOf(
            UserRating.Rating("tt3", 6)))

    @BeforeAll
    fun init() {
        every {
            userRatingRepository.deleteById(any<Long>())
        } returns
                Mono.empty()

        every {
            userRatingRepository.save(any())
        } returns
                Mono.empty()
    }

    @Nested
    inner class Delete {
        @Test
        fun `Should delete user rating with movie id when present `() {
            every {
                userRatingRepository.findById(any<Long>())
            } returns
                    Mono.just(validRatingCombined)

            runBlocking {
                userRatingController
                    .delete(
                        validRatingCombined.userId,
                        validRatingCombined.ratings[0].movieId) }

            verify {
                userRatingRepository
                    .save(validRating1) }
        }

        @Test
        fun `Should delete all user ratings for user when present `() {
            every {
                userRatingRepository.findById(any<Long>())
            } returns
                    Mono.just(validRatingCombined)

            runBlocking {
                userRatingController
                    .delete(
                        validRatingCombined.userId,
                        null) }

            verify {
                userRatingRepository
                    .deleteById(validRatingCombined.userId) }
        }
    }

    @Nested
    inner class Post {
        @Test
        fun `Should add user rating when user is new `() {
            every {
                userRatingRepository.findById(any<Long>())
            } returns
                    Mono.empty()

            runBlocking {
                userRatingController
                    .save(validRating1) }

            verify {
                userRatingRepository
                    .save(validRating1) }
        }

        @Test
        fun `Should update user rating when score is in range `() {
            every {
                userRatingRepository.findById(any<Long>())
            } returns
                    Mono.just(validRating1)

            runBlocking {
                userRatingController
                    .save(validRating2) }

            verify {
                userRatingRepository
                    .save(validRatingCombined) }
        }

        @Test
        fun `Should not update user rating when score is out of range `() {
            every {
                userRatingRepository.findById(any<Long>())
            } returns
                    Mono.just(validRating1)

            runBlocking {
                userRatingController
                    .save(invalidRating) }

            verify {
                userRatingRepository
                    .save(validRating1) }
        }
    }
}
