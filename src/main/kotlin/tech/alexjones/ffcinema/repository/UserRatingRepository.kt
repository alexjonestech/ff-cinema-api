package tech.alexjones.ffcinema.repository

import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import org.springframework.stereotype.Repository
import tech.alexjones.ffcinema.model.UserRating

@Repository
interface UserRatingRepository : ReactiveMongoRepository<UserRating, Long>