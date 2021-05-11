package tech.alexjones.ffcinema.model

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.math.BigDecimal
import java.time.LocalDateTime

@Table
data class MovieShowing(
    @Id
    val showingId: Long,
    val movieId: String,
    val showTime: LocalDateTime,
    val ticketPrice: BigDecimal,
)