package info.ejava.examples.db.jpa.songs.repo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.assertj.core.api.Assertions;
import org.assertj.core.api.BDDAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;

import info.ejava.examples.db.jpa.songs.NTestConfiguration;
import info.ejava.examples.db.jpa.songs.bo.Song;
import info.ejava.examples.db.jpa.songs.dto.SongDTOFactory;
import info.ejava.examples.db.jpa.songs.svc.SongsMapper;
import lombok.extern.slf4j.Slf4j;

@SpringBootTest(classes = {NTestConfiguration.class},
        properties = "db.populate=false")
//@ActiveProfiles(resolver = TestProfileResolver.class)
@Tag("springboot")
@DisplayName("Repository Derived Query Methods")
@Slf4j
public class SongsDerivedQueryNTest {
   
     @Autowired
    private SongsRepository songsRepository;
    @Autowired
    private SongDTOFactory dtoFactory;
    @Autowired
    private SongsMapper mapper;

    private static List<Song> savedSongs = new ArrayList<>();

    @BeforeEach
    void populate() {
        if (savedSongs.isEmpty()) {
            songsRepository.deleteAll();
            IntStream.range(0, 3).forEach(i -> {
                Song song = mapper.map(dtoFactory.make(SongDTOFactory.nextDate));
                if (i == 2) {
                    song.setTitle(null);
                }
                songsRepository.save(song);
                savedSongs.add(song);
                //log.info("{}", song);
            });
        }
    }

    @Test
    void optional_exists() {
        // given
        Song song = savedSongs.stream().filter(s->s.getTitle()==null).findFirst().get();

        log.info("song - {}", song);

        // when
        Optional<Song> result = songsRepository.streamByTitle(song.getTitle());
        //select ...
        // from reposongs_song song0_
        // where song0_.title=?
        log.info("title with '{}' {} find instance", song.getTitle(), result.isPresent()?"did":"did not");

        // then
        BDDAssertions.then(result.isPresent()).isTrue();
        Song foundSong = result.get();
        BDDAssertions.then(foundSong.getId()).isEqualTo(song.getId());
    }

    @Test
    void optional_does_not_exists() {
        // given
        String nonExistantTitle = "1234567890";
        // when
        Optional<Song> result = songsRepository.getByTitle(nonExistantTitle);
        log.info("title with '{}' {} find instance", nonExistantTitle, result.isPresent()?"did":"did not");

        //then
        BDDAssertions.then(result.isPresent()).isFalse();
        Assertions.assertThatThrownBy(() -> result.get())
                .isInstanceOf(NoSuchElementException.class);

        //when
        Song foundSong = result.orElse(null);

        //then
        BDDAssertions.then(foundSong).isNull();

    }

    private Map.Entry<String, Long> getStartsWith() {
        Map<String, Long> counts = savedSongs.stream()
                .filter(s -> s.getTitle() != null)
                .collect(Collectors.groupingBy(s -> s.getTitle().substring(0, 1), Collectors.counting()));
        long maxCount = counts.values().stream().mapToLong(v->v).max().orElse(0);
        Map.Entry<String, Long> startsWith = counts.entrySet().stream()
                .filter(e -> maxCount == e.getValue())
                .findFirst().orElse(null);
        Assertions.assertThat(startsWith).isNotNull().describedAs("no song found");
        Assertions.assertThat(startsWith.getKey()).isNotNull();
        Assertions.assertThat(startsWith.getValue()).isNotZero();
        return startsWith;
    }

    @Test
    void findBy_list() {
        //given
        Map.Entry<String, Long> startsWith = getStartsWith();
        String startingWith = startsWith.getKey();
        long expectedCount = startsWith.getValue();

        //when
        Sort sort = Sort.by("id").ascending();
        List<Song> songs = songsRepository.findByTitleStartingWith(startingWith, sort);
        //select ...
        // from reposongs_song song0_
        // where song0_.title like ? escape ?
        // order by song0_.id asc

        //then
        BDDAssertions.then(songs.size()).isEqualTo(expectedCount);
    }

    @Test
    void findBy_pagable_slice() {
        //given
        Map.Entry<String, Long> startsWith = getStartsWith();
        String startingWith = startsWith.getKey();

        //when
        PageRequest pageable = PageRequest.of(0, 1, Sort.by("id").ascending());
        Slice<Song> songsSlice = songsRepository.findByTitleStartingWith(startingWith, pageable);
        // select s1_0.id,s1_0.artist,s1_0.released,s1_0.title 
        // from reposongs_song s1_0 
        // where s1_0.title like ? escape '\' 
        // order by s1_0.id fetch first ? rows only

        //then
        BDDAssertions.then(songsSlice.getNumberOfElements()).isEqualTo(pageable.getPageSize());
    }

     @Test
    void findBy_pagable_page() {
        //given
        Map.Entry<String, Long> startsWith = getStartsWith();
        String startingWith = startsWith.getKey();
        long expectedCount = startsWith.getValue();

        //when
        PageRequest pageable = PageRequest.of(0, 1, Sort.by("id").ascending());
        Page<Song> songsPage = songsRepository.findPageByTitleStartingWith(startingWith, pageable);
        // select s1_0.id,s1_0.artist,s1_0.released,s1_0.title 
        // from reposongs_song s1_0 
        // where s1_0.title like ? escape '\' 
        // order by s1_0.id fetch first ? rows only

        // select count(s1_0.id) 
        // from reposongs_song s1_0 
        // where s1_0.title like ? escape '\'

        //then
        BDDAssertions.then(songsPage.getNumberOfElements()).isEqualTo(pageable.getPageSize());
        BDDAssertions.then(songsPage.getTotalElements()).isEqualTo(expectedCount);
    }

    @Test
    void findBy_property_value() {
        //given
        Song song = savedSongs.stream().filter(s->s.getTitle()!=null).findFirst().get();

        //when
        List<Song> foundSongs = songsRepository.findByTitle(song.getTitle());
        log.info("title with '{}' found {}", song.getTitle(), foundSongs);

        //then
        BDDAssertions.then(foundSongs).hasSize(1);
        Song foundSong = foundSongs.get(0);
        BDDAssertions.then(foundSong.getId()).isEqualTo(song.getId());
    }

     @Test
    void findBy_property_not_value() {
        //given
        Song song = savedSongs.stream().filter(s->s.getTitle()!=null).findFirst().get();
        Set<Integer> expectedIds = savedSongs.stream()
                .filter(s->s.getTitle()!=null && !song.getTitle().equals(s.getTitle()))
                .map(s->s.getId())
                .collect(Collectors.toSet());

        //when
        List<Song> foundSongs = songsRepository.findByTitleNot(song.getTitle());
        // select s1_0.id,s1_0.artist,s1_0.released,s1_0.title 
        // from reposongs_song s1_0 
        // where s1_0.title<>?

        log.info("title not '{}' found {}", song.getTitle(), foundSongs);

        //then
        Set<Integer> foundIds = foundSongs.stream().map(s->s.getId()).collect(Collectors.toSet());
        BDDAssertions.then(foundIds).isEqualTo(expectedIds);
    }

    @Test
    void findBy_property_null_value() {
        //given - a song with a null title
        Song song = savedSongs.stream().filter(s->s.getTitle()==null).findFirst().get();

        //when - query will look for "is null"
        List<Song> foundSongs = songsRepository.findByTitle(song.getTitle());
        // select s1_0.id,s1_0.artist,s1_0.released,s1_0.title 
        // from reposongs_song s1_0 
        // where s1_0.title is null
        log.info("title with '{}' found {}", song.getTitle(), foundSongs);

        //then - song with null title is found
        Song foundSong = foundSongs.get(0);
        BDDAssertions.then(foundSong.getId()).isEqualTo(song.getId());
    }

    @Test
    void findBy_contains() {
        //given
        Song song = savedSongs.stream().filter(s->s.getTitle()!=null).findFirst().get();
        String substring = song.getTitle().substring(1,5);
        Set<Integer> expectedIds = savedSongs.stream()
                .filter(s->s.getTitle()!=null && s.getTitle().contains(substring))
                .map(s->s.getId())
                .collect(Collectors.toSet());

        //when
        List<Song> foundSongs = songsRepository.findByTitleContaining(substring);
        
        log.info("title containing '{}' found {}", substring, foundSongs);

        //then
        Set<Integer> foundIds = foundSongs.stream().map(s->s.getId()).collect(Collectors.toSet());
        BDDAssertions.then(foundIds).isEqualTo(expectedIds);
    }





}
