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
import org.springframework.data.repository.query.Param;

import info.ejava.examples.db.jpa.songs.bo.Song;

public interface SongsRepository extends JpaRepository<Song,Integer>, SongsRepositoryCustom {
    
    Optional<Song> getByTitle(String title);

    //Optional<Song> findByTitle(String title);
    Optional<Song> queryByTitle(String title);
    Optional<Song> searchByTitle(String title);
    Optional<Song> readByTitle(String title);
    Optional<Song> streamByTitle(String title);

    List<Song> findByTitle(String title);
    List<Song> findByTitleNot(String title);
    List<Song> findByTitleContaining(String title);
    List<Song> findByTitleNotContaining(String title);
    List<Song> findByTitleLike(String title);
    List<Song> findByTitleNotLike(String title);


    List<Song> findByReleasedAfter(LocalDate date);
    Page<Song> findByReleasedAfter(LocalDate date, Pageable pageable);
    List<Song> findByReleasedGreaterThanEqual(LocalDate date);
    List<Song> findByReleasedBetween(LocalDate starting, LocalDate ending);



    @Query(value = "select s from Song s where released between :starting and :ending order by id ASC")
    Page<Song> findByReleasedBetween(LocalDate starting, LocalDate ending, Pageable pageable) ;

    List<Song> findByTitleNullAndReleasedAfter(LocalDate date);
    Slice<Song> findByTitleNullAndReleasedAfter(LocalDate date, Pageable pageable);
    Page<Song> findPageByTitleNullAndReleasedAfter(LocalDate date, Pageable pageable);

    @Query(value = "select s.title from REPOSONGS_SONG s where length(s.title) >= :length", nativeQuery = true)
    List<String> getTitlesGESizeNative(@Param("length") int length);

    @Query("select s from Song s where length(s.title) >= :length")
    List<Song> findByTitleGESize(@Param("length") int length);

    //  Repository Interface Methods can Automatically Invoke Matching @NamedQueries
    //see @NamedQuery(name="Song.findByArtistGESize" in Song class
    List<Song> findByArtistGESize(@Param("length") int length);


    List<Song> findByTitleStartingWith(String string, Sort sort);
    Slice<Song> findByTitleStartingWith(String string, Pageable pageable);
    Page<Song> findPageByTitleStartingWith(String string, Pageable pageable);
}
