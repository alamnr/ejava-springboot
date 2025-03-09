package info.ejava.examples.svc.springdoc.contests.dto;

import java.util.Map;
import java.util.stream.Collectors;

import org.assertj.core.api.BDDAssertions;
import org.junit.jupiter.api.Test;

import info.ejava.examples.svc.springdoc.contest.dto.ContestDTO;
import info.ejava.examples.svc.springdoc.contest.dto.ContestDTOFactory;
import info.ejava.examples.svc.springdoc.contest.dto.ContestListDTO;
import info.ejava.examples.svc.springdoc.contest.dto.ContestDTOFactory.ContestListDTOFactory;
import lombok.extern.slf4j.Slf4j;
import net.datafaker.Faker;

@Slf4j
public abstract class MarshallingTestBase {

    protected static final Faker faker = new Faker();
    protected ContestDTOFactory contestDTOFactory = new ContestDTOFactory();

    public void init() { }

    protected abstract <T> String marshall(T object) throws Exception;
    protected abstract <T> T unmarshal(Class<T> type, String buffer) throws Exception;

    private <T> T marshal_and_demarshal(T obj, Class<T> type) throws Exception {
        String buffer = marshall(obj);
        T result = unmarshal(type, buffer);
        return result;
    }

    @Test
    public void contest_dto_marshals() throws Exception {
        // given -a contest
        ContestDTO contest = contestDTOFactory.make();

        // when - marshalled to a string and unmarshalled back to an object
        ContestDTO result = marshal_and_demarshal(contest, ContestDTO.class);

        // then
        compare(result, contest);
    }

    @Test
    public void contentsList_dto_marshals() throws Exception {
        // given some contests
        ContestListDTO contestLists = contestDTOFactory.listBuilder().make(3, 3, ContestDTOFactory.oneUpId);

        // when 
        ContestListDTO result  = marshal_and_demarshal(contestLists, ContestListDTO.class);

        // then 
        BDDAssertions.then(result.getCount()).isEqualTo(contestLists.getCount());
        Map<Integer, ContestDTO> contestMap = result.getContests().stream().collect(Collectors.toMap(ContestDTO::getId, q->q));
        for (ContestDTO expected: contestLists.getContests()) {
            ContestDTO actual = contestMap.remove(expected.getId());
            compare(actual, expected);
        }


    }

    private void compare(ContestDTO lhs, ContestDTO rhs) {
        BDDAssertions.then(lhs.getAwayScore()).isEqualTo(rhs.getAwayScore());
        BDDAssertions.then(lhs.getHomeScore()).isEqualTo(rhs.getHomeScore());
        BDDAssertions.then(lhs.getAwayTeam()).isEqualTo(rhs.getAwayTeam());
        BDDAssertions.then(lhs.getHomeTeam()).isEqualTo(rhs.getHomeTeam());
        BDDAssertions.then(lhs.getScheduledStart()).isEqualTo(rhs.getScheduledStart());
        BDDAssertions.then(lhs.getDuration()).isEqualTo(rhs.getDuration());
        BDDAssertions.then(lhs.getId()).isEqualTo(rhs.getId());
    }

}
