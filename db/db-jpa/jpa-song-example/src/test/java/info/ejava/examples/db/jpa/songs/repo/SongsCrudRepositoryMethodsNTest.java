package info.ejava.examples.db.jpa.songs.repo;

import java.sql.Timestamp;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.assertj.core.api.Assertions;
import org.assertj.core.api.BDDAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Example;

import info.ejava.examples.db.jpa.songs.NTestConfiguration;
import info.ejava.examples.db.jpa.songs.bo.Song;
import info.ejava.examples.db.jpa.songs.dto.SongDTOFactory;
import info.ejava.examples.db.jpa.songs.svc.SongsMapper;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

@SpringBootTest(classes = {NTestConfiguration.class},
        properties = "db.populate=false")
//@ActiveProfiles(resolver = TestProfileResolver.class)
@Tag("springboot")
@Slf4j
@DisplayName("Repository Crud Methods")
public class SongsCrudRepositoryMethodsNTest {
    
    @Autowired
    private SongsRepository songsRepo;
    @Autowired
    private SongsMapper mapper;
    @Autowired
    private SongDTOFactory dtoFactory;
    @Value("${spring.datasource.url}")
    private String dbUrl;

    @BeforeEach
    void setUp() {
        log.info("dbUrl - {}" , dbUrl);
        songsRepo.deleteAll();
    }

    @Test
    void save_new(){
        // given an entity instance
        Song song = mapper.map(dtoFactory.make());
        Assertions.assertThat(song.getId()).isZero();

        // when persisting
        songsRepo.save(song);
        // Hibernate: call next value for REPOSONGS_SONG_SEQUENCE
        // Hibernate: insert into reposongs_song (artist, released, title, id) values (?,?,?,?)

        // then entity is persisted
        BDDAssertions.then(song.getId()).isNotZero();
        
    }

    @Test
    void save_update( ) {
        // given an entity instance
        Song song  = mapper.map(dtoFactory.make());
        BDDAssertions.then(song.getId()).isZero();
        songsRepo.save(song);
        songsRepo.flush();
        BDDAssertions.then(song.getId()).isNotZero();
        

        Song updatedSong = Song.builder().id(song.getId()).title("new title")
                                .artist(song.getArtist()).released(song.getReleased()).build();
        
        // when persisting update
        songsRepo.save(updatedSong);

        // then entity is persisted
        BDDAssertions.then(songsRepo.findOne(Example.of(updatedSong))).isPresent();
    }

    @Test
    @Transactional
    void exists() {
        // given a persisted entity instance
        Song pojoSong = mapper.map(dtoFactory.make());
        songsRepo.save(pojoSong);

        // when - determining of entity  exists
        boolean exists = songsRepo.existsById(pojoSong.getId());

        // then
        BDDAssertions.then(exists).isTrue();
    }

    @Test
    void findById_found() {
        // given a persisted entity instance
        Song pojosong = mapper.map(dtoFactory.make());
        BDDAssertions.then(pojosong.getId()).isZero();
        songsRepo.save(pojosong);
        BDDAssertions.then(pojosong.getId()).isNotZero();

        // when -  finding the existing entity
        Optional<Song> result = songsRepo.findById(pojosong.getId());
        //select ...
        //from reposongs_song song0_
        //where song0_.id=?

        // then
        BDDAssertions.then(result).isPresent();

        // when obtaining the instance

        Song dbSong = result.get();

        // then - database copy matches the initial pojo
        BDDAssertions.then(dbSong).isNotNull();
        BDDAssertions.then(dbSong.getArtist()).isEqualTo(pojosong.getArtist());
        BDDAssertions.then(dbSong.getTitle()).isEqualTo(pojosong.getTitle());
        // the dbSong instance  os coming from db
        // comparing SQL timestamp to java.util.Date
        BDDAssertions.then(dbSong.getReleased()).isEqualTo(pojosong.getReleased());
    }

    @Test
    void findById_not_found() {
        //given - an ID that does not exist
        int missingId = 123456;

        //when - using find for a missing ID
        Optional<Song> result = songsRepo.findById(missingId);

        //then - the optional can be benignly tested
        BDDAssertions.then(result).isNotPresent();

        //then - the optional is asserted during the get()
        Assertions.assertThatThrownBy(() -> result.get())
                .isInstanceOf(NoSuchElementException.class);
    }

    @Test
    void saveAll_entities() {
        //given - several songs persisted
        //Collection<Song> songs = dtoFactory.listBuilder().songs(3, 3).stream()
        List<Song> songs = dtoFactory.listBuilder().songs(3, 3).stream()
                .map(dto->mapper.map(dto))
                .toList();

        //when
        songsRepo.saveAll(songs);

        //then - each will exist in the DB
        songs.forEach(s->{
            songsRepo.existsById(s.getId());
        });
    }

    @Test
    void findAll_entities() {
        // given - several songs persisted
        Collection<Song> pojoSongs = dtoFactory.listBuilder().songs(3,3).stream()
                                        .map(dto->mapper.map(dto))
                                        .toList();
        
        songsRepo.saveAll(pojoSongs);
        Map<Integer,Song> pojoSongMap = pojoSongs.stream().collect(Collectors.toMap(s->s.getId(), s->s));

        // when
        Iterable<Song> result = songsRepo.findAll();

        // then
        BDDAssertions.then(result).hasSameSizeAs(pojoSongs);
        BDDAssertions.then(result).allMatch(s->pojoSongMap.containsKey(s.getId()));
        
    }


    @Test
    //@Transactional // without this, the entity will get loaded
    void delete_exists() {
        //given - a persisted entity instance
        Song existingSong = mapper.map(dtoFactory.make());
        songsRepo.save(existingSong);

        //when - deleting an existing instance
        songsRepo.delete(existingSong);
        //select ... as title4_0_0_ from reposongs_song song0_ where song0_.id=?
        //delete from reposongs_song where id=?

        //then - instance will be removed from DB
        BDDAssertions.then(songsRepo.existsById(existingSong.getId())).isFalse();
    }

    @Test
    void delete_not_exists() {
        //given - a persisted entity instance
        Song doesNotExist = mapper.map(dtoFactory.make(SongDTOFactory.oneUpId));
        BDDAssertions.then(songsRepo.existsById(doesNotExist.getId())).isFalse();

        //when - deleting a non-existing instance
        songsRepo.delete(doesNotExist);
        //select ... as title4_0_0_ from reposongs_song song0_ where song0_.id=?
        //no exception was thrown
    }

     @Test
    //@Transactional
    void deleteById_exists() {
        //given - a persisted entity instance
        Song existingSong = mapper.map(dtoFactory.make());
        songsRepo.save(existingSong);

        //when - deleting an existing instance
        songsRepo.deleteById(existingSong.getId());

        //then - instance will be removed from DB
        BDDAssertions.then(songsRepo.existsById(existingSong.getId())).isFalse();
    }

    @Test
    void deleteById_not_exists() {
        //given - an ID that does not exist
        int missingId = 123456;

        //when - deleting a non-existant instance
        //Spring Boot <= 3.0.6
        Throwable ex= Assertions.catchThrowable(()->{
            songsRepo.deleteById(missingId);
        });

        //then -- <= Spring Boot 3.0.6 exception is thrown
        //then(ex).isInstanceOf(EmptyResultDataAccessException.class);
        //then -- >= Spring Boot > 3.1.0 ignored
        BDDAssertions.then(ex).isNull();
        log.info("deleted non-existant ID {}", missingId, ex);
    }

    @Test
    void deleteAll_every() {
        //given
        Collection<Song> pojoSongs = dtoFactory.listBuilder().songs(3, 3).stream()
                .map(dto->mapper.map(dto))
                .toList();
        songsRepo.saveAll(pojoSongs);

        //when
        songsRepo.deleteAll();

        //then
        BDDAssertions.then(pojoSongs).allSatisfy(s-> BDDAssertions.then(songsRepo.existsById(s.getId())).isFalse() );
    }

    @Test
    void deleteAll_some() {
        //given
        List<Song> pojoSongs = dtoFactory.listBuilder().songs(3, 3).stream()
                .map(dto->mapper.map(dto))
                .toList();
        songsRepo.saveAll(pojoSongs);
        List<Song> toDelete = IntStream.range(0,2)
                .mapToObj(i->pojoSongs.get(i))
                .toList();

        //when - deleting a subset
        songsRepo.deleteAll(toDelete);

        //then
        BDDAssertions.then(songsRepo.existsById(pojoSongs.get(0).getId())).isFalse();
        BDDAssertions.then(songsRepo.existsById(pojoSongs.get(1).getId())).isFalse();
        BDDAssertions.then(songsRepo.existsById(pojoSongs.get(2).getId())).isTrue();
    }

    @Test
    void count() {
        //given
        List<Song> pojoSongs = dtoFactory.listBuilder().songs(8, 8).stream()
                .map(dto->mapper.map(dto))
                .toList();
        songsRepo.saveAll(pojoSongs);

        //when
        long songCount = songsRepo.count();

        //then
        BDDAssertions.then(songCount).isEqualTo(pojoSongs.size());
        log.info("song count - {}", songCount);
    }

    @Test
    @Transactional
    void save_modify_existing() {
        //given - a persisted entity instance
        Song song = mapper.map(dtoFactory.make());
        songsRepo.save(song);
        String originalTitle = song.getTitle();
        String modifiedTitle = dtoFactory.title() + "1";
        Assertions.assertThat(originalTitle).as("given titles").isNotEqualTo(modifiedTitle);

        //when - modifying song instance without saving
        song.setTitle(modifiedTitle);

        //then - DB is modified because we have a transaction active on @Test method
        Song dbSong = songsRepo.findById(song.getId()).get();
        BDDAssertions.then(dbSong.getTitle()).isEqualTo(modifiedTitle);
    }

    @Test
    void timestamp_date_compare() {
        java.util.Date utilDate = new Date();
        java.sql.Timestamp sqlTimestamp = new Timestamp(utilDate.getTime());

        //Timestamp is-a Date, but Date is-not-a Timestamp
        BDDAssertions.then(utilDate.equals(sqlTimestamp)).isTrue();
        BDDAssertions.then(sqlTimestamp.equals(utilDate)).isFalse();
        BDDAssertions.then(utilDate).isEqualTo(sqlTimestamp);
        BDDAssertions.then(sqlTimestamp).isNotEqualTo(utilDate);

        //Timestamp and Date represent same time
        BDDAssertions.then(sqlTimestamp).hasSameTimeAs(utilDate);
        BDDAssertions.then(utilDate).hasSameTimeAs(sqlTimestamp);
    }




}
