package info.ejava.examples.db.jpa.songs.repo;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import info.ejava.examples.db.jpa.songs.bo.Song;

public interface SongsRepository extends JpaRepository<Song,Integer> {
    
    Optional<Song> getByTitle(String title);

    //Optional<Song> findByTitle(String title);
    Optional<Song> queryByTitle(String title);
    Optional<Song> searchByTitle(String title);
    Optional<Song> readByTitle(String title);
    Optional<Song> streamByTitle(String title);

    List<Song> findByTitle(String title);
    List<Song> findByTitleNot(String title);
    List<Song> findByTitleContaining(String title);

    @Query(value = "select s from Song s where released between :starting and :ending order by id ASC")
    Page<Song> findByReleasedBetween(LocalDate starting, LocalDate ending, Pageable pageable) ;

    List<Song> findByTitleStartingWith(String string, Sort sort);
    Slice<Song> findByTitleStartingWith(String string, Pageable pageable);
    Page<Song> findPageByTitleStartingWith(String string, Pageable pageable);
}
