package info.ejava.examples.svc.failsafe;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

import static org.assertj.core.api.BDDAssertions.then;

@SpringBootTest(classes=ClientTestConfiguration.class,
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Slf4j
public class FailsafeRestTemplateIT {
    @Autowired
    private RestTemplate authnUser;
    @Autowired
    private URI authnUrl;

    @BeforeEach
    public void setUp() {
        log.info("baseUrl={}", authnUrl);
    }

    @Test
    public void user_can_call_authenticated() {
        //given a URL to an endpoint that accepts only authenticated calls
        URI url = UriComponentsBuilder.fromUri(authnUrl).queryParam("name", "jim").build().toUri();

        //when called with an authenticated identity
        ResponseEntity<String> response = authnUser.getForEntity(url, String.class);

        //then expected results returned
        then(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        then(response.getBody()).isEqualTo("hello, jim");
    }
}
