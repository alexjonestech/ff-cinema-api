package tech.alexjones.ffcinema.controller

import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactive.awaitFirstOrNull
import org.springframework.web.bind.annotation.*
import tech.alexjones.ffcinema.model.MovieShowing
import tech.alexjones.ffcinema.repository.MovieShowingRepository

@RestController
@RequestMapping("/showings")
class MovieShowingController(val movieShowingRepository: MovieShowingRepository) {

    @GetMapping
    suspend fun get(@RequestParam movieId: String?) =
        movieId?.let {
            getAllByMovieId(it) } ?:
            getAll()

    suspend fun getAll() =
        movieShowingRepository
            .findAll()
            .asFlow()

    suspend fun getAllByMovieId(movieId: String) =
        movieShowingRepository
            .findAllByMovieId(movieId)
            .asFlow()

    @GetMapping("/{showingId}")
    suspend fun getById(@PathVariable showingId: Long) =
        movieShowingRepository
            .findById(showingId)
            .awaitFirstOrNull()

    @DeleteMapping("/{showingId}")
    suspend fun delete(@PathVariable showingId: Long) =
        movieShowingRepository
            .deleteById(showingId)
            .awaitFirstOrNull()

    @PostMapping
    suspend fun save(@RequestBody movieShowing: MovieShowing) =
        movieShowingRepository
            .save(movieShowing)
            .awaitFirstOrNull()
}
