package tech.alexjones.ffcinema.repository

import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository
import tech.alexjones.ffcinema.model.MovieShowing

@Repository
interface MovieShowingRepository : ReactiveCrudRepository<MovieShowing, Long>