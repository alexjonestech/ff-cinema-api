package tech.alexjones.ffcinema.controller

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import reactor.core.publisher.Flux
import tech.alexjones.ffcinema.repository.MovieShowingRepository

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class MovieShowingControllerTest {
    private val movieShowingRepository: MovieShowingRepository = mockk()
    private val movieShowingController = MovieShowingController(movieShowingRepository)

    @BeforeAll
    fun init() {
        every {
            movieShowingRepository.findAll()
        } returns
                Flux.empty()
        every {
            movieShowingRepository.findAllByMovieId(any())
        } returns
                Flux.empty()
    }

    @Nested
    inner class Get {
        @Test
        fun `Should get all showings of given movie `() {
            runBlocking {
                movieShowingController
                    .get("") }

            verify {
                movieShowingRepository
                    .findAllByMovieId(any()) }
        }

        @Test
        fun `Should get all showings of all movies `() {
            runBlocking {
                movieShowingController
                    .get(null) }

            verify {
                movieShowingRepository
                    .findAll() }
        }
    }
}
