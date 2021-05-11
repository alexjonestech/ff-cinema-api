package tech.alexjones.ffcinema.repository

import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import tech.alexjones.ffcinema.model.MovieShowing

@Repository
interface MovieShowingRepository : ReactiveCrudRepository<MovieShowing, Long> {

    @Query("select * from movie_showing where movie_id=?")
    fun findAllByMovieId(movieId: String): Flux<MovieShowing>
}
