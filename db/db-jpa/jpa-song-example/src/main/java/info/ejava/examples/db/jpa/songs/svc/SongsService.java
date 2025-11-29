package info.ejava.examples.db.jpa.songs.svc;

import java.time.LocalDate;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import info.ejava.examples.db.jpa.songs.dto.SongDTO;

public interface SongsService {
    
    SongDTO createSong(SongDTO songDTO);
    SongDTO getSong(int id);
    SongDTO getRandomSong();
    void updateSong(int id, SongDTO songDTO);
    void deleteSong(int id);
    void deleteAllSongs();

    Page<SongDTO> getSongs(Pageable pageable);
    Page<SongDTO> findReleasedAfter(LocalDate afterDate, Pageable pageable);
    Page<SongDTO> findSongsMatchingAll(SongDTO probeDTO,Pageable pageable);
}
