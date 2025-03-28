package info.ejava.examples.svc.springdoc.contests.svc;

import info.ejava.examples.svc.springdoc.contest.dto.ContestDTO;
import info.ejava.examples.svc.springdoc.contest.dto.ContestListDTO;

public interface ContestService {

    ContestDTO createContest(ContestDTO newContest);
    ContestDTO getContest(int id);
    ContestListDTO getContests(int offset, int limit);
    boolean doesContestExist(int id);

    void updateContest(int id, ContestDTO contestUpdate);
    void deleteContest(int id);
    void deleteAllContests();

}


// Intellisense means Content Assist / Code hinting / Code Completion
