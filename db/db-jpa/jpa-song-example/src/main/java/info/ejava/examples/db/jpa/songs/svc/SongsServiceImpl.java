package info.ejava.examples.db.jpa.songs.svc;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import info.ejava.examples.common.exceptions.ClientErrorException;
import info.ejava.examples.db.jpa.songs.bo.Song;
import info.ejava.examples.db.jpa.songs.dto.SongDTO;
import info.ejava.examples.db.jpa.songs.repo.SongsRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class SongsServiceImpl implements SongsService {

    private final SongsMapper mapper;
    private final SongsRepository songsRepo;


    /*  Transaction propagations:
        REQUIRES_NEW: Always starts a new transaction, suspending any existing one.
        MANDATORY: Requires an existing transaction, throws an exception if none exists.
        SUPPORTS: Runs with a transaction if one exists, otherwise runs non-transactionally.
        REQUIRED: @Transactional(propagation = Propagation.REQUIRED) means “run this method inside a transaction; if one exists, 
                  join it, otherwise start a new one.”
    */
    @Override
    @Transactional(propagation = Propagation.REQUIRED) 
    // run this method inside a transaction; if one exists, join it, otherwise start a new one.
    public SongDTO createSong(SongDTO songDTO) {
        Song songBO = mapper.map(songDTO);

        // managed instance
        songsRepo.save(songBO);

        return mapper.map(songBO);

    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    // Runs with a transaction if one exists, otherwise runs non-transactionally.
    public SongDTO getSong(int id) {
        // this way leverages Optional features
        return songsRepo.findById(id).map(songBO -> mapper.map(songBO))
                        .orElseThrow(()-> new ClientErrorException.NotFoundException("Song id-[%d] not found", id));
    }

    @Override
    public SongDTO getRandomSong() {
        
        // This way manually checks optional
        Optional<Song> songBo = songsRepo.random();
        if(!songBo.isPresent()){
            throw new ClientErrorException.NotFoundException("No random song found");
        }

        return mapper.map(songBo.get());
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    // run this method inside a transaction; if one exists, join it, otherwise start a new one.
    public void updateSong(int id, SongDTO songDTO) {
        songDTO.setId(id);
        Song songBO = mapper.map(songDTO);

        songsRepo.save(songBO);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    // run this method inside a transaction; if one exists, join it, otherwise start a new one.
    public void deleteSong(int id) {
        songsRepo.deleteById(id);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    // run this method inside a transaction; if one exists, join it, otherwise start a new one.
    public void deleteAllSongs() {
        songsRepo.deleteAll();
    }

    @Override
    public Page<SongDTO> getSongs(Pageable pageable) {
        Page<Song> songs = songsRepo.findAll(pageable);
        return mapper.map(songs);
    }

    @Override
    public Page<SongDTO> findReleasedAfter(LocalDate afterDate, Pageable pageable) {
        Page<Song> songs = songsRepo.findByReleasedAfter(afterDate, pageable);
        return mapper.map(songs);
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    // Runs with a transaction if one exists, otherwise runs non-transactionally.
    public Page<SongDTO> findSongsMatchingAll(SongDTO probeDTO, Pageable pageable) {
        Song probe = mapper.map(probeDTO);
        ExampleMatcher matcher = ExampleMatcher.matchingAll().withIgnorePaths("id");
        Page<Song> songs = songsRepo.findAll(Example.of(probe, matcher), pageable);
        return mapper.map(songs);
    }
    
}
