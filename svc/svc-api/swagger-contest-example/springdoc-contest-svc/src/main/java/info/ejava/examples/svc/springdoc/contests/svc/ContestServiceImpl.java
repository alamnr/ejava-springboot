package info.ejava.examples.svc.springdoc.contests.svc;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.coyote.BadRequestException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import info.ejava.examples.common.exceptions.ClientErrorException;
import info.ejava.examples.svc.springdoc.contest.dto.ContestDTO;
import info.ejava.examples.svc.springdoc.contest.dto.ContestListDTO;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ContestServiceImpl implements ContestService {
    
    private final AtomicInteger nextId = new AtomicInteger(1);
    private final Map<Integer,ContestDTO> contests;

    public ContestServiceImpl(Map<Integer,ContestDTO> contestMap){
        this.contests = contestMap;
    }
    
    @Override
    public ContestDTO createContest(ContestDTO newContest) {
        //log.info(" new contest in service - {}", newContest);
        validate(newContest);
        newContest.setId(nextId.getAndAdd(1));
        contests.put(newContest.getId(), newContest);
        return newContest;
    }           
        
    @Override
    public ContestDTO getContest(int id) {
        ContestDTO contest = contests.get(id);
        if(contest != null){
            return contest;
        } else {
            throw new ClientErrorException.NotFoundException( "contest[%d] not found",id);
        }
    }

    @Override
    public ContestListDTO getContests(int offset, int limit) {
        validatePaging(offset,limit);
        List<ContestDTO> responses = new ArrayList<>(limit);
        Iterator<ContestDTO> itr = contests.values().iterator();
        for(int i=0; itr.hasNext() && (limit == 0 || responses.size()<limit); i++){
            ContestDTO q = itr.next();
            if(i>=offset){
                responses.add(q);
            }
        }

        return ContestListDTO.builder()
                    .offset(offset).limit(limit).total(contests.size()).contests(responses).build();
    }
        
    private void validatePaging(int offset, int limit) {
        if(offset <0 || limit <0) {
            throw new ClientErrorException.BadRequestException( "offset[%d] or limit[%d] must be >= 0 ", offset, limit);
        }
                
    }
        
    @Override
    public boolean doesContestExist(int id) {
        return contests.containsKey(id);
    }

    @Override
    public void updateContest(int id, ContestDTO contestUpdate) {
        validate(contestUpdate);
        if(contests.containsKey(id)){
            contestUpdate.setId(id);
            contests.put(contestUpdate.getId(), contestUpdate);
        } else {
            throw new ClientErrorException.NotFoundException("contest[%d] not found",id );
        }
        
    }

    @Override
    public void deleteContest(int id) {
        contests.remove(id);
    }

    @Override
    public void deleteAllContests() {
        contests.clear();
    }
    
    @SuppressWarnings("deprecation")
    private void validate(ContestDTO newContest) {
        if(StringUtils.isEmpty(newContest.getHomeTeam())) {
            throw new ClientErrorException.InvalidInputException("home team is required");
        }
        if(StringUtils.isEmpty(newContest.getAwayTeam())){
            throw new ClientErrorException.InvalidInputException("away team is required");
        }
    }
}
