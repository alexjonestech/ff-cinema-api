package tech.alexjones.ffcinema.extension

import tech.alexjones.ffcinema.model.UserRating

fun Collection<UserRating.Rating>.withScoreInRange() = this.filter { (1..5).contains(it.score) }
