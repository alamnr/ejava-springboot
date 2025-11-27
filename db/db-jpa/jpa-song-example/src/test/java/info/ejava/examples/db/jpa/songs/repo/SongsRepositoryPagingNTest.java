package info.ejava.examples.db.jpa.songs.repo;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.assertj.core.api.BDDAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;

import info.ejava.examples.db.jpa.songs.NTestConfiguration;
import info.ejava.examples.db.jpa.songs.bo.Song;
import info.ejava.examples.db.jpa.songs.dto.SongDTO;
import info.ejava.examples.db.jpa.songs.dto.SongDTOFactory;
import info.ejava.examples.db.jpa.songs.svc.SongsMapper;
import lombok.extern.slf4j.Slf4j;

@SpringBootTest(classes = NTestConfiguration.class, properties = "db.populate=false")
//@ActiveProfiles(resolver = TestProfileResolver.class)
@Tag("springboot")
@DisplayName("Repository Sorting/Paging")
@Slf4j
public class SongsRepositoryPagingNTest {
    
    @Autowired
    private SongsRepository songsRepository;
    @Autowired
    private SongDTOFactory dtoFactory;
    @Autowired
    private SongsMapper mapper;

    private String titlePrefix = "123";

    private UnaryOperator<SongDTO> addTitlePrefix = s->{
        s.setTitle(titlePrefix + s.getTitle() );
        return s;
    };

    private List<Song> savedSongs = new ArrayList<>();

    @BeforeEach
    void populate() {
        songsRepository.deleteAll();
        IntStream.range(0, 10).forEach(i->{
            Song song = mapper.map(dtoFactory.make(SongDTOFactory.nextDate,addTitlePrefix));
            savedSongs.add(song);
        });

        songsRepository.saveAll(savedSongs);
    }

    @Test
    void findAll_sorted() {

        // when
        List<Song> byReleased = songsRepository.findAll(Sort.by("released").descending().and(Sort.by("id").ascending()));
        //select ... from reposongs_song song0_ order by song0_.released desc, song0_.id asc
        log.info("ordered by released date DSC found {}", byReleased);

        //then
        LocalDate previous = null;
        for (Song s: byReleased) {
            if (previous!=null) {
                BDDAssertions.then(previous).isAfterOrEqualTo(s.getReleased()); //DESC order
            }
            previous=s.getReleased();
        }

    }

    @Test
    void findAll_sorted_and_paged() {
        // given
        int offset = 0;
        int pageSize = 3;

        Pageable pageable = PageRequest.of(offset/pageSize, pageSize, Sort.by("released"));

        // Pageable next = pageable.next();
        // Pageable previous = pageable.previousOrFirst();
        // Pageable first = pageable.first();


        // when 
        Page<Song> songPage = songsRepository.findAll(pageable);

        // then
        Slice songSlice = songPage;
        log.info("songSlice - {} ", songSlice.toString());
        BDDAssertions.then(songSlice).isNotNull();
        BDDAssertions.then(songSlice.isEmpty()).isFalse();
        BDDAssertions.then(songSlice.getNumber()).isEqualTo(0);
        BDDAssertions.then(songSlice.getSize()).isEqualTo(pageSize);
        BDDAssertions.then(songSlice.getNumberOfElements()).isEqualTo(pageSize);

        BDDAssertions.then(songPage.getTotalElements()).isEqualTo(savedSongs.size()); // unique to page

        List<Song> songList = songSlice.getContent();
        BDDAssertions.then(songList).hasSize(pageSize);

        for (int i = 1; songSlice.hasNext(); i++) {
             pageable = pageable.next();
             songSlice = songsRepository.findAll(pageable);

             songList = songSlice.getContent();

             BDDAssertions.then(songSlice).isNotNull();
             BDDAssertions.then(songSlice.getNumber()).isEqualTo(i);
             BDDAssertions.then(songSlice.getSize()).isEqualTo(pageSize);
             BDDAssertions.then(songSlice.getNumberOfElements()).isLessThanOrEqualTo(pageSize);
             BDDAssertions.then(((Page)songSlice).getTotalElements()).isEqualTo(savedSongs.size()); // unique to page

         }

         BDDAssertions.then(songSlice.hasNext()).isFalse();
         BDDAssertions.then(songSlice.getNumber()).isEqualTo(songsRepository.count()/pageSize);
    }

    @Test
    void sorting() {
        // when
        List<Integer> dbIdsByTitleAsc = songsRepository.findByTitleStartingWith(titlePrefix, Sort.by("released").ascending())
                                            .stream()
                                            .map(s->s.getId())
                                            .toList();
        log.info("ordered by released date ASC found - {}", dbIdsByTitleAsc);

        // then
        List<Integer> idByTitleAsc = savedSongs.stream()
                                        .sorted(Comparator.comparing(s->s.getReleased()))
                                        .map(s->s.getId())
                                        .toList();
        BDDAssertions.then(dbIdsByTitleAsc).isEqualTo(idByTitleAsc);

        // when
        List<Integer> dbIdsByTitleDsc = songsRepository.findByTitleStartingWith(titlePrefix, Sort.by("released").descending())
                                            .stream()
                                            .map(s->s.getId())
                                            .toList();
        log.info("ordered by released date descending found - {} ", dbIdsByTitleDsc);

        // then
        List<Integer> idByTitleDsc = savedSongs.stream()
                                        .sorted(Comparator.comparing(s->s.getReleased(),Comparator.reverseOrder()))
                                        .map(s->s.getId())
                                        .toList();

        BDDAssertions.then(dbIdsByTitleDsc).isEqualTo(idByTitleDsc);
                                    

    }

    @Test
    void paging_slice() {
        //  given
        int offset = 0;
        int pageSize = 3;

        Pageable pageable  = PageRequest.of(offset/pageSize, pageSize, Sort.by("released"));

        Set<Integer> ids = savedSongs.stream().map(s->s.getId()).collect(Collectors.toSet());
        log.info("ids - {}", ids);

        // when
        Slice<Song> songPage =  songsRepository.findByTitleStartingWith(titlePrefix, pageable);

        log.info("slice - {}", songPage.getContent());
        // then
        BDDAssertions.then(songPage).isNotNull();
        BDDAssertions.then(songPage.isEmpty()).isFalse();
        BDDAssertions.then(songPage.getNumber()).isEqualTo(0);
        BDDAssertions.then(songPage.getSize()).isEqualTo(pageSize);
        BDDAssertions.then(songPage.getNumberOfElements()).isEqualTo(pageSize);

        List<Song> songsList = songPage.getContent();
        BDDAssertions.then(songsList.size()).isEqualTo(pageSize);
        BDDAssertions.then(songsList).allMatch(s->ids.remove(s.getId()));

         for (int i=1; songPage.hasNext(); i++) {
            pageable = pageable.next();
            songPage = songsRepository.findByTitleStartingWith(titlePrefix, pageable);
            songsList = songPage.getContent();
            BDDAssertions.then(songPage).isNotNull();
            BDDAssertions.then(songPage.getNumber()).isEqualTo(i);
            BDDAssertions.then(songPage.getSize()).isLessThanOrEqualTo(pageSize);
            BDDAssertions.then(songPage.getNumberOfElements()).isLessThanOrEqualTo(pageSize);
            BDDAssertions.then(songsList).allMatch(s->ids.remove(s.getId()));
        }
        BDDAssertions.then(songPage.hasNext()).isFalse();
        BDDAssertions.then(songPage.getNumber()).isEqualTo(songsRepository.count() / pageSize);
        BDDAssertions.then(ids).isEmpty();



    }

    @Test
    void paging_pageable() {
        //given
        int offset = 0;
        int pageSize = 3;
        Pageable pageable = PageRequest.of(offset / pageSize, pageSize, Sort.by("released"));
        Set<Integer> ids = savedSongs.stream().map(s -> s.getId()).collect(Collectors.toSet());

        //when
        Page<Song> songPage = songsRepository.findPageByTitleStartingWith(titlePrefix, pageable);

        //then
        BDDAssertions.then(songPage).isNotNull();
        BDDAssertions.then(songPage.isEmpty()).isFalse();
        BDDAssertions.then(songPage.getNumber()).isEqualTo(0);
        BDDAssertions.then(songPage.getSize()).isEqualTo(pageSize);
        BDDAssertions.then(songPage.getNumberOfElements()).isEqualTo(pageSize);
        BDDAssertions.then(songPage.hasNext()).isTrue();

        BDDAssertions.then(songPage.getTotalElements()).isEqualTo(savedSongs.size());
        BDDAssertions.then(songPage.getTotalPages()).isEqualTo(savedSongs.size() / pageSize + (savedSongs.size() % pageSize==0?0:1));
    }
    @Test
    void query_annotation_can_paging() {
        //given
        Pageable pageable = PageRequest.of(0,1);
        List<LocalDate> releasedDates = savedSongs.stream().map(s -> s.getReleased()).sorted().toList();
        List<LocalDate> desiredDates = releasedDates.subList(releasedDates.size()/2 -3, releasedDates.size()/2 +3)
                .stream().sorted().toList();
        LocalDate min = desiredDates.get(0);
        LocalDate max = desiredDates.get(desiredDates.size() - 1);

        //when
        Page<Song> songPage = songsRepository.findByReleasedBetween(min, max, pageable);
        //then
        BDDAssertions.then(songPage).hasSize(pageable.getPageSize());
        BDDAssertions.then(songPage.getTotalElements()).isEqualTo(desiredDates.size());
    }


}
