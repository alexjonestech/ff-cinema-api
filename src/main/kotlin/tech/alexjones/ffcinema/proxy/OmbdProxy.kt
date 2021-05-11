package tech.alexjones.ffcinema.proxy

import com.fasterxml.jackson.databind.JsonNode
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitBody
import tech.alexjones.ffcinema.key.OMDB_API_KEY

@RestController
@RequestMapping("/info")
class OmbdProxy {

    val webClient = WebClient.builder()
        .baseUrl("http://www.omdbapi.com")
        .build()

    @GetMapping("/{id}")
    suspend fun getMovieInfo(@PathVariable id: String) =
        webClient.get()
            .uri("/?apikey=$OMDB_API_KEY&i=$id")
            .retrieve()
            .awaitBody<JsonNode>()
}
