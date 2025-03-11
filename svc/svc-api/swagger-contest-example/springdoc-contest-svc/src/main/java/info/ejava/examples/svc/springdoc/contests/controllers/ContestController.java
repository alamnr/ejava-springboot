package info.ejava.examples.svc.springdoc.contests.controllers;

import java.net.URI;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import info.ejava.examples.svc.springdoc.contest.api.ContestApi;
import info.ejava.examples.svc.springdoc.contest.dto.ContestDTO;
import info.ejava.examples.svc.springdoc.contest.dto.ContestListDTO;
import info.ejava.examples.svc.springdoc.contest.dto.MessageDTO;
import info.ejava.examples.svc.springdoc.contests.svc.ContestService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

@Tag(name = "contest-controller", description = "manages-contests")
@RestController
@Slf4j
public class ContestController {
    private final ContestService contestService;

    public ContestController(ContestService contestService) {
        this.contestService = contestService;
    }

   

    @RequestMapping(path = ContestApi.CONTESTS_PATH,
                    method = RequestMethod.POST,
                    consumes = {MediaType.APPLICATION_JSON_VALUE,MediaType.APPLICATION_XML_VALUE},
                    produces = {MediaType.APPLICATION_JSON_VALUE,MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<ContestDTO> createContest(@RequestBody ContestDTO newContest){
        log.info("ne Contest in controller v- {} ", newContest);
        ContestDTO createdContest = contestService.createContest(newContest) ;

        URI url = ServletUriComponentsBuilder.fromCurrentRequest()
                                .replacePath(ContestApi.CONTEST_PATH)
                                .build(createdContest.getId());
        
        ResponseEntity<ContestDTO> response = ResponseEntity.created(url).body(createdContest);
        return response;

    }

    @RequestMapping(path = ContestApi.CONTESTS_PATH, method = RequestMethod.GET,
                                produces = {MediaType.APPLICATION_JSON_VALUE,MediaType.APPLICATION_XML_VALUE} )
    public ResponseEntity<ContestListDTO> getContests(@RequestParam(name = "offset",defaultValue = "0") int offset,
                                                      @RequestParam(name = "limit", defaultValue = "0") int limit ) {
            ContestListDTO contests  =  contestService.getContests(offset, limit);
            String url = ServletUriComponentsBuilder.fromCurrentRequest().build().toUriString();
            ResponseEntity<ContestListDTO> response = ResponseEntity.ok().header(HttpHeaders.CONTENT_LOCATION, url).body(contests);
                                                      
            return response;

    }

    @RequestMapping(path = ContestApi.CONTEST_PATH, method = RequestMethod.GET,
                    produces = {MediaType.APPLICATION_JSON_VALUE,MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<ContestDTO> getContest(@PathVariable("contestId") int id ){
        ContestDTO contest = contestService.getContest(id);
        String url = ServletUriComponentsBuilder.fromCurrentRequestUri().build(id).toString();
        ResponseEntity<ContestDTO> response = ResponseEntity.ok()
                                                .header(HttpHeaders.CONTENT_LOCATION, url)
                                                .body(contest);
        return response;
    }

    @RequestMapping(path = ContestApi.CONTEST_PATH,
                    method = RequestMethod.HEAD,
                    produces = {MediaType.APPLICATION_JSON_VALUE,MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<Void> doesContestExist(@PathVariable("contestId") int id) {
        boolean exist = contestService.doesContestExist(id);
        ResponseEntity<Void>  response = exist ? 
                                         ResponseEntity.ok().build() : 
                                         ResponseEntity.notFound().build();
        return response;        
    }

    @RequestMapping(path = ContestApi.CONTEST_PATH, method = RequestMethod.PUT,
                    consumes = {MediaType.APPLICATION_JSON_VALUE,MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<Void> updateResponse(@PathVariable("contestId") int id, @RequestBody ContestDTO contest) {
        contestService.updateContest(id, contest);
        ResponseEntity<Void> response = ResponseEntity.ok().build();
        return response;
    }


    @RequestMapping(path = ContestApi.CONTEST_PATH, method = RequestMethod.DELETE)
    public ResponseEntity<Void> deleteContest(@PathVariable("contestId") int id) {
        contestService.deleteContest(id);
        ResponseEntity<Void> response = ResponseEntity.noContent().build();
        return response;
    }

    @RequestMapping(method=RequestMethod.DELETE,
                    path=ContestApi.CONTESTS_PATH)
    public ResponseEntity<Void> deleteAllContest(){
        contestService.deleteAllContests();
        ResponseEntity<Void> response = ResponseEntity.noContent().build();
        return response;
    }

}
