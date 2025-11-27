package info.ejava.examples.db.jpa.songs.jpa;

import org.assertj.core.api.Assertions;
import org.assertj.core.api.BDDAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import info.ejava.examples.db.jpa.songs.NTestConfiguration;
import info.ejava.examples.db.jpa.songs.bo.Song;
import info.ejava.examples.db.jpa.songs.dao.JpaSongDAO;
import info.ejava.examples.db.jpa.songs.dto.SongDTOFactory;
import info.ejava.examples.db.jpa.songs.svc.SongsMapper;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TransactionRequiredException;
import lombok.Locked.Read;
import lombok.extern.slf4j.Slf4j;

@SpringBootTest(classes = NTestConfiguration.class,properties = "db.populate=false")
//@ActiveProfiles(resolver = TestProfileResolver.class)
@Tag("springboot")
@Slf4j
public class JpaSongsNTest {
    @Autowired 
    private JpaSongDAO jpaDao;
    @Autowired
    private SongDTOFactory dtoFactory;
    @Autowired
    private SongsMapper mapper;
    @Autowired
    private EntityManager em;

    @BeforeEach    
    void cleanUp() {
        jpaDao.deleteAll();
    }

    @Test
    void create(){
        // given an instance
        Song song = mapper.map(dtoFactory.make());

        // when save to db
        jpaDao.create(song);

        // then
        BDDAssertions.then(jpaDao.existsById(song.getId())).isTrue();
        BDDAssertions.then(song.getId()).isNotZero();
    }

    @Test
    void findById_exists() {
        // given a persisted instance
        Song song = mapper.map(dtoFactory.make());
        jpaDao.create(song);

        // when finding
        Song dbSong = jpaDao.findById(song.getId());

        // then entity is persisted
        BDDAssertions.then(dbSong.getId()).isEqualTo(song.getId());
        BDDAssertions.then(dbSong.getTitle()).isEqualTo(song.getTitle());
        BDDAssertions.then(dbSong.getArtist()).isEqualTo(song.getArtist());
        BDDAssertions.then(dbSong.getReleased()).isEqualTo(song.getReleased());
    }

    @Test
    void findById_does_not_exist() {
        // given 
        int missingId = 12345;

        // when
        Song dbSong = jpaDao.findById(missingId);

        // then
        BDDAssertions.then(dbSong).isNull();
    }

    @Test
    @Transactional
    void update_entity()  {

        // given a persisted instance
        Song originalSong = mapper.map(dtoFactory.make());
        jpaDao.create(originalSong);
        String newTitle = dtoFactory.title();

        // when updating
        originalSong.setTitle(newTitle);
        jpaDao.flush();

        // then - db has new state
        Song dbSong = jpaDao.findById(originalSong.getId());
        BDDAssertions.then(dbSong.getTitle()).isEqualTo(newTitle);
    }

    @Test
    void update_exist() {
        // given a persisted instance
        Song originalSong = mapper.map(dtoFactory.make());
        jpaDao.create(originalSong);
        Song updatedSong = mapper.map(dtoFactory.make(s->{s.setId(originalSong.getId()); return s;} ));
        Assertions.assertThat(updatedSong.getTitle()).isNotEqualTo(originalSong.getTitle());

        // when - updating
        jpaDao.update(updatedSong);

        // then - db has new state
        Song dbSong = jpaDao.findById(originalSong.getId());
        BDDAssertions.then(dbSong.getTitle()).isEqualTo(updatedSong.getTitle());
        BDDAssertions.then(dbSong.getArtist()).isEqualTo(updatedSong.getArtist());
        BDDAssertions.then(dbSong.getReleased()).isEqualTo(updatedSong.getReleased());
    }

    @Test
    @Transactional
    void delete_exist(){
        // given a persisted instance
        Song song = mapper.map(dtoFactory.make());
        jpaDao.create(song);
        jpaDao.flush(); // flush the persistent context and permanently save to db 

        // when deleting
        jpaDao.delete(song);
        jpaDao.flush(); // permanently save to DB

        // then 
        BDDAssertions.then(jpaDao.findById(song.getId())).isNull();
    }

    @Test
    void delete_does_not_exist() {
        // given a bad id
        int missingId = 12345;

        // when deleting
        jpaDao.deleteById(missingId);
    }

    @Test
    void transaction_missing() {
        //given - an instance
        Song song = mapper.map(dtoFactory.make());

        //when - persist is called without a tx, an exception is thrown
        //em.persist(song);
        Assertions.assertThatThrownBy(()->em.persist(song))
                .isInstanceOf(TransactionRequiredException.class);
    }

    @Test
    @Transactional
    void transaction_present_in_caller() {
        //given - an instance
        Song song = mapper.map(dtoFactory.make());

        //when  - persist called within caller transaction, no exception thrown
        em.persist(song);
        em.flush(); //force DB interaction

        //then
        BDDAssertions.then(em.find(Song.class, song.getId())).isNotNull();
    }

     @Test
    void transaction_present_in_component() {
        //given - an instance
        Song song = mapper.map(dtoFactory.make());

        //when  - persist called within component transaction, no exception thrown
        jpaDao.create(song);

        //then
        BDDAssertions.then(jpaDao.findById(song.getId())).isNotNull();
    }

    @Test
    void transaction_common_needed() {
        //given a persisted instance
        Song song = mapper.map(dtoFactory.make());
        jpaDao.create(song); //song is detached at this point

        //when - removing detached entity we get an exception
        //jpaDao.delete(song);
        Assertions.assertThatThrownBy(()->jpaDao.delete(song))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @Transactional
    void transaction_common_present() {
        //given a persisted instance
        Song song = mapper.map(dtoFactory.make());
        jpaDao.create(song); //song is detached at this point

        //when - removing managed entity, it works
        jpaDao.delete(song);

        //then
        BDDAssertions.then(jpaDao.findById(song.getId())).isNull();
    }



}

