package info.ejava.examples.svc.springdoc.contest.client;

import java.net.URI;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

import info.ejava.examples.common.web.ServerConfig;
import info.ejava.examples.svc.springdoc.contest.api.ContestApi;
import info.ejava.examples.svc.springdoc.contest.dto.ContestDTO;
import info.ejava.examples.svc.springdoc.contest.dto.ContestListDTO;
import reactor.core.publisher.Mono;

public class ContestsWebClientImpl implements ContestApi {

    private final URI baseUrl;
    private final RestTemplate restTemplate = null;
    private final WebClient webclient;
    private final MediaType mediaType;
    
    public ContestsWebClientImpl(WebClient webClient, ServerConfig serverConfig, String mediaType) {
        this.webclient = webClient;
        baseUrl = UriComponentsBuilder.fromUri(serverConfig.getBaseUrl()).build().toUri();
        this.mediaType = MediaType.valueOf(mediaType);
    }

    public ContestsWebClientImpl(WebClient webClient, ServerConfig serverConfig) {
        this(webClient, serverConfig, MediaType.APPLICATION_JSON_VALUE);
    }

    @Override
    public Mono<ResponseEntity<ContestListDTO>> getContests(Integer offset, Integer limit) {
        UriComponentsBuilder urlBuilder = UriComponentsBuilder.fromUri(baseUrl).path(CONTESTS_PATH);
        if(offset != null){
            urlBuilder = urlBuilder.queryParam("offset", offset);
        }
        if(limit != null){
            urlBuilder = urlBuilder.queryParam("limit", limit);
        }

        URI url = urlBuilder.build().toUri();

        WebClient.RequestHeadersSpec<?> requestSpec = webclient.get().uri(url).accept(mediaType);
        return requestSpec.retrieve().toEntity(ContestListDTO.class);
    }

    @Override
    public Mono<ResponseEntity<ContestDTO>> createContest(ContestDTO contest) {
        URI url = UriComponentsBuilder.fromUri(baseUrl).path(CONTESTS_PATH).build().toUri();
        WebClient.RequestHeadersSpec<?> requestSpec = webclient.post().uri(url).contentType(mediaType).accept(mediaType).bodyValue(contest);
        return requestSpec.retrieve().toEntity(ContestDTO.class);
    }

    @Override
    public Mono<ResponseEntity<Void>> updateContest(int id, ContestDTO contest) {
        URI url = UriComponentsBuilder.fromUri(baseUrl).path(CONTEST_PATH).build(id);
        WebClient.RequestHeadersSpec<?> requestSpec = webclient.put().contentType(mediaType).bodyValue(contest);
        return requestSpec.retrieve().toEntity(Void.class);
    }

    @Override
    public Mono<ResponseEntity<Void>> containsContest(int id) {
        URI url = UriComponentsBuilder.fromUri(baseUrl).path(CONTEST_PATH).build(id);
        WebClient.RequestHeadersSpec<?> requestSpec =  webclient.head().uri(url);
        return requestSpec.retrieve().toEntity(Void.class);
    }

    @Override
    public Mono<ResponseEntity<ContestDTO>> getContest(int id) {
        URI url = UriComponentsBuilder.fromUri(baseUrl).path(CONTEST_PATH).build(id);
        WebClient.RequestHeadersSpec<?> requestSpec = webclient.get().uri(url).accept(mediaType);
        return requestSpec.retrieve().toEntity(ContestDTO.class);
    }

    @Override
    public Mono<ResponseEntity<Void>> deleteContest(int id) {
        URI url = UriComponentsBuilder.fromUri(baseUrl).path(CONTEST_PATH).build(id);
        WebClient.RequestHeadersSpec<?> requestSpec = webclient.delete().uri(url);
        return requestSpec.retrieve().toEntity(Void.class);
    }

    @Override
    public Mono<ResponseEntity<Void>> deleteAllContests() {
        URI url = UriComponentsBuilder.fromUri(baseUrl).path(CONTESTS_PATH).build().toUri();
        WebClient.RequestHeadersSpec<?> requestSpec = webclient.delete().uri(url);
        return requestSpec.retrieve().toEntity(Void.class);
    }


}
