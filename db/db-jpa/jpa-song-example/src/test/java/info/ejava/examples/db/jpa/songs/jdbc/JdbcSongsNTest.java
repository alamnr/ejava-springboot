package info.ejava.examples.db.jpa.songs.jdbc;

import java.sql.SQLException;
import java.util.NoSuchElementException;

import javax.sql.DataSource;

import org.assertj.core.api.Assertions;
import org.assertj.core.api.BDDAssertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import info.ejava.examples.db.jpa.songs.NTestConfiguration;
import info.ejava.examples.db.jpa.songs.bo.Song;
import info.ejava.examples.db.jpa.songs.dao.JdbcSongDAO;
import info.ejava.examples.db.jpa.songs.dto.SongDTOFactory;
import info.ejava.examples.db.jpa.songs.svc.SongsMapper;
import lombok.extern.slf4j.Slf4j;

@SpringBootTest(classes = NTestConfiguration.class)
//@ActiveProfiles(resolver = TestProfileResolver.class)
@Tag("springboot")
@Slf4j
public class JdbcSongsNTest {
    
    @Autowired
    private JdbcSongDAO jdbcDao;
    @Autowired
    private SongDTOFactory dtoFactory;
    @Autowired
    private SongsMapper mapper;
    @Autowired
    private DataSource dataSource;

    @BeforeEach
    void cleanUp() throws SQLException {
        log.info("dbUrl = {}", dataSource.getConnection().getMetaData().getURL());
        jdbcDao.deleteAll();
    }
    
    @Test
    void create() throws SQLException {
        // given an entity instance
        Song song = mapper.map(dtoFactory.make());

        // when persisting 
        jdbcDao.create(song);

        // then entity is persisted
        BDDAssertions.then(song.getId()).isNotZero();
        BDDAssertions.then(jdbcDao.existsById(song.getId())).isTrue();
    }

    @Test
    void findById_exists() throws SQLException {
        // given a persistant instance
        Song song = mapper.map(dtoFactory.make());
        jdbcDao.create(song);

        // when finding
        Song dbSong = jdbcDao.findById(song.getId());

        // then entity is persisted
        BDDAssertions.then(dbSong.getId()).isEqualTo(song.getId());
        BDDAssertions.then(dbSong.getTitle()).isEqualTo(song.getTitle());
        BDDAssertions.then(dbSong.getArtist()).isEqualTo(song.getArtist());
        BDDAssertions.then(dbSong.getReleased()).isEqualTo(song.getReleased());
    }

    @Test
    void findById_does_not_exist() throws SQLException {
        // given a persisted instance
        int missingId = 12345;

        // when / then finding
        Assertions.assertThatThrownBy(()-> jdbcDao.findById(missingId)).isInstanceOf(NoSuchElementException.class);
    }

    @Test
    void update_exist() throws SQLException {
        // given a persisted instance
        Song originalSong = mapper.map(dtoFactory.make());
        jdbcDao.create(originalSong);

        Song updateSong = mapper.map(dtoFactory.make(s->{s.setId(originalSong.getId());return s;}));
        Assertions.assertThat(updateSong.getTitle()).isNotEqualTo(originalSong.getTitle());

        log.info("original song - {}", originalSong);
        log.info("updated song - {}", updateSong);

        // when updating
        jdbcDao.update(updateSong);

        // then - db has new state
        Song dbSong = jdbcDao.findById(originalSong.getId());
        BDDAssertions.then(dbSong.getTitle()).isEqualTo(updateSong.getTitle());
        BDDAssertions.then(dbSong.getArtist()).isEqualTo(updateSong.getArtist());
        BDDAssertions.then(dbSong.getReleased()).isEqualTo(updateSong.getReleased());

    }

    @Test
    void update_does_not_exist() throws SQLException {
        //given a persisted instance
        Song originalSong = mapper.map(dtoFactory.make());
        jdbcDao.create(originalSong);
        Song updatedSong = mapper.map(dtoFactory.make(s->{ s.setId(originalSong.getId()); return s; }));
        Assertions.assertThat(updatedSong.getTitle()).isNotEqualTo(originalSong.getTitle());

        //when - updating
        jdbcDao.update(updatedSong);

        //then - db has new state
        Song dbSong = jdbcDao.findById(originalSong.getId());
        BDDAssertions.then(dbSong.getTitle()).isEqualTo(updatedSong.getTitle());
        BDDAssertions.then(dbSong.getArtist()).isEqualTo(updatedSong.getArtist());
        BDDAssertions.then(dbSong.getReleased()).isEqualTo(updatedSong.getReleased());
    }

    @Test
    void delete_exist() throws SQLException {
        // given a persistrd instance
        Song song = mapper.map(dtoFactory.make());
        jdbcDao.create(song);

        // when deleting
        jdbcDao.deleteById(song.getId());

        // then no long in db
        BDDAssertions.then(jdbcDao.existsById(song.getId())).isFalse();
    }

    @Test
    void delete_does_not_exist() throws SQLException {
        // given a bad ID
        int missingId = 12345;
        // when - deleting missing id , then - exception
        Assertions.assertThatThrownBy(()-> jdbcDao.deleteById(missingId)).isInstanceOf(NoSuchElementException.class);
    }



}
