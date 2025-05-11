package com.test.izertis.service;

import com.test.izertis.dtos.request.NewPlayerDTO;
import com.test.izertis.dtos.response.PlayerDTO;
import com.test.izertis.dtos.response.PlayerDetailsDTO;
import com.test.izertis.exception.InsufficientAuthoritiesException;
import com.test.izertis.exception.NotFoundException;
import com.test.izertis.mappers.PlayerMapper;
import com.test.izertis.model.Club;
import com.test.izertis.model.Player;
import com.test.izertis.repository.ClubRepository;
import com.test.izertis.repository.PlayerRepository;
import com.test.izertis.service.auth.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
@Transactional
public class PlayerService {

    private final AuthService authService;
    private final PlayerMapper playerMapper;
    private final PlayerRepository playerRepository;
    private final ClubRepository clubRepository;
    public PlayerDTO registerPlayer(NewPlayerDTO newPlayerDTO, long clubId) {
        Long currentClubId = authService.getCurrentClubId();

        if (!currentClubId.equals(clubId)) {
            throw new InsufficientAuthoritiesException(HttpStatus.BAD_REQUEST, "You are not allowed to register player for this club");
        }

        Club currentClub = clubRepository.findById(currentClubId)
                .orElseThrow( () -> new NotFoundException("Club not found for provided token"));

        Player newPlayer = playerMapper.toEntity(newPlayerDTO);
        newPlayer.setClub(currentClub);
        Player savedPlayer = playerRepository.save(newPlayer);

        return playerMapper.toPlayerDTO(savedPlayer);
    }

    @Transactional(readOnly = true)
    public Page<PlayerDTO> getAllPlayers(long clubId,
                                         Pageable pageable) {
        Long currentClubId = authService.getCurrentClubId();

        Club requestedClub = clubRepository.findById(clubId)
                .orElseThrow( () -> new NotFoundException("Requested club not found"));

        if (!requestedClub.getIsPublic() && !currentClubId.equals(requestedClub.getId())) {
            throw new InsufficientAuthoritiesException(HttpStatus.BAD_REQUEST, "You are not allowed to get players of this club");
        }

        Page<Player> players = playerRepository.findByClubId(clubId, pageable);

        return players.map(playerMapper::toPlayerDTO);
    }

    @Transactional(readOnly = true)
    public PlayerDetailsDTO getPlayerDetails(long clubId, long playerId) {
        Long currentClubId = authService.getCurrentClubId();

        Club requestedClub = clubRepository.findById(clubId)
                .orElseThrow( () -> new NotFoundException("Requested club not found"));

        if (!requestedClub.getIsPublic() && !currentClubId.equals(requestedClub.getId())) {
            throw new InsufficientAuthoritiesException(HttpStatus.BAD_REQUEST, "You are not allowed to get details of this player");
        }

        Player requestedPlayer = playerRepository.findById(playerId)
                .orElseThrow( () -> new NotFoundException("Requested player not found"));

        return playerMapper.toPlayerDetailsDTO(requestedPlayer);
    }

    public PlayerDTO updatePlayer(NewPlayerDTO playerDTO, long clubId, long playerId) {
        Long currentClubId = authService.getCurrentClubId();

        if (!currentClubId.equals(clubId)) {
            throw new InsufficientAuthoritiesException(HttpStatus.BAD_REQUEST, "You are not allowed to update player for this club");
        }

        Player requestedPlayer = playerRepository.findById(playerId)
                .orElseThrow( () -> new NotFoundException("Requested player not found"));

        requestedPlayer.setGivenName(playerDTO.getGivenName());
        requestedPlayer.setFamilyName(playerDTO.getFamilyName());
        requestedPlayer.setNationality(playerDTO.getNationality());
        requestedPlayer.setEmail(playerDTO.getEmail());
        requestedPlayer.setDateOfBirth(playerDTO.getDateOfBirth());

        Player savedPlayer = playerRepository.save(requestedPlayer);

        return playerMapper.toPlayerDTO(savedPlayer);
    }

    public void deletePlayer(Long clubId, Long playerId) {
        Long currentClubId = authService.getCurrentClubId();

        if (!currentClubId.equals(clubId)) {
            throw new InsufficientAuthoritiesException(HttpStatus.BAD_REQUEST, "You are not allowed to delete player for this club");
        }

        playerRepository.deleteById(playerId);
    }
}
