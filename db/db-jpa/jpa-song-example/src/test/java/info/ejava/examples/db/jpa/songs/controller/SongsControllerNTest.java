package info.ejava.examples.db.jpa.songs.controller;

import org.assertj.core.api.BDDAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

import info.ejava.examples.common.dto.DtoUtil;
import info.ejava.examples.common.dto.JsonUtil;
import info.ejava.examples.common.dto.XmlUtil;
import info.ejava.examples.common.web.ServerConfig;
import info.ejava.examples.db.jpa.songs.NTestConfiguration;
import info.ejava.examples.db.jpa.songs.TestProfileResolver;
import info.ejava.examples.db.jpa.songs.dto.SongDTO;
import info.ejava.examples.db.jpa.songs.dto.SongDTOFactory;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@SpringBootTest(classes = {NTestConfiguration.class},
                webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles(resolver = TestProfileResolver.class)
@Tag("springboot")
@Slf4j
@DisplayName("Songs Controller NTest")
public class SongsControllerNTest {

    @Autowired
    private ServerConfig serverConfig;
    @Autowired
    private SongDTOFactory songDTOFactory;
    @Autowired
    private WebTestClient wtc;
    @Autowired
    private WebClient webClient;
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private TestRestTemplate rtt;

    private DtoUtil jsonUtil = JsonUtil.instance();
    private DtoUtil xmlUtil = XmlUtil.instance();


    @BeforeEach
    void init() {
        log.info("{}", serverConfig.getBaseUrl());
    }

    void thenSongsEqual(SongDTO result, SongDTO expected) {
        if (expected.getId()==0) { //we don't know what the value will be
            BDDAssertions.then(result.getId()).isNotZero(); //but it will be assigned
        } else {
            BDDAssertions.then(result.getId()).isEqualTo(expected.getId());
        }
        BDDAssertions.then(result.getArtist()).isEqualTo(expected.getArtist());
        BDDAssertions.then(result.getTitle()).isEqualTo(expected.getTitle());
        BDDAssertions.then(result.getReleased()).isEqualTo(expected.getReleased());
    }


    @Test
    void create_song() {
        // given
        SongDTO song = songDTOFactory.make();
        WebTestClient.RequestHeadersSpec<?> request = wtc.post()
                                                        .uri(SongsController.SONGS_PATH)
                                                        .contentType(MediaType.APPLICATION_JSON)
                                                        .accept(MediaType.APPLICATION_JSON)
                                                        .body(Mono.just(song),SongDTO.class);
        
        // when
        WebTestClient.ResponseSpec response = request.exchange();

        // then
        response.expectStatus().isCreated().expectHeader().contentType(MediaType.APPLICATION_JSON);
        SongDTO createdSong = response.returnResult(SongDTO.class).getResponseBody().blockFirst();
        BDDAssertions.then(createdSong.getId()).isNotZero();

        String expectedlocation = UriComponentsBuilder.fromUri(serverConfig.getBaseUrl())
                                                        .path(SongsController.SONG_PATH)  
                                                        .build(createdSong.getId()).toString();
                                                        
        response.expectHeader().location(expectedlocation);

        thenSongsEqual(createdSong,song);                                                                      

    }

    
}
