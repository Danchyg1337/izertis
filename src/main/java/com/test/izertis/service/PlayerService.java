package com.test.izertis.service;

import com.test.izertis.dto.request.PlayerRequestDTO;
import com.test.izertis.dto.response.PlayerResponseDTO;
import com.test.izertis.entity.Club;
import com.test.izertis.entity.Player;
import com.test.izertis.exception.InsufficientAuthoritiesException;
import com.test.izertis.exception.ResourceNotFoundException;
import com.test.izertis.mapper.PlayerMapper;
import com.test.izertis.repository.ClubRepository;
import com.test.izertis.repository.PlayerRepository;
import com.test.izertis.service.auth.AuthService;
import com.test.izertis.specification.PlayerSpecifications;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class PlayerService {

    private final AuthService authService;
    private final PlayerMapper playerMapper;
    private final PlayerRepository playerRepository;
    private final ClubRepository clubRepository;

    @Transactional
    public PlayerResponseDTO registerPlayer(PlayerRequestDTO playerRequestDTO, long clubId) {
        Long currentClubId = authService.getCurrentClubId();

        if (!currentClubId.equals(clubId)) {
            throw new InsufficientAuthoritiesException(HttpStatus.BAD_REQUEST, "{errors.ServicePlayerService.notAllowedToRegisterPlayer}");
        }

        Club currentClub = clubRepository.findById(currentClubId)
                .orElseThrow(() -> new ResourceNotFoundException("{errors.ServiceClubService.clubAlreadyExists}"));

        Player newPlayer = new Player();
        playerMapper.fromDto(playerRequestDTO, newPlayer);

        newPlayer.setClub(currentClub);

        Player savedPlayer = playerRepository.save(newPlayer);

        return playerMapper.toDtoWithoutDetails(savedPlayer);
    }

    @Transactional(readOnly = true)
    public Page<PlayerResponseDTO> getAllPlayers(long clubId,
                                                 String givenName,
                                                 String familyName,
                                                 //String nationality,
                                                 Pageable pageable) {
        Long currentClubId = authService.getCurrentClubId();

        Club requestedClub = clubRepository.findById(clubId)
                .orElseThrow(() -> new ResourceNotFoundException("{errors.ServiceClubService.clubNotFound}"));

        if (!requestedClub.getIsPublic() && !currentClubId.equals(requestedClub.getId())) {
            throw new InsufficientAuthoritiesException(HttpStatus.BAD_REQUEST, "{errors.ServicePlayerService.notAllowedToGetPlayers}");
        }

        Specification<Player> spec = Specification.where(PlayerSpecifications.byClubId(clubId))
                .and(PlayerSpecifications.byGivenName(givenName))
                //.and(PlayerSpecifications.byNationality(nationality))
                .and(PlayerSpecifications.byFamilyName(familyName));

        Page<Player> players = playerRepository.findAll(spec, pageable);

        return players.map(playerMapper::toDtoWithoutDetails);
    }

    @Transactional(readOnly = true)
    public PlayerResponseDTO getPlayerDetails(long clubId, long playerId) {
        Long currentClubId = authService.getCurrentClubId();

        Club requestedClub = clubRepository.findById(clubId)
                .orElseThrow(() -> new ResourceNotFoundException("{errors.ServiceClubService.clubNotFound}"));

        if (!requestedClub.getIsPublic() && !currentClubId.equals(requestedClub.getId())) {
            throw new InsufficientAuthoritiesException(HttpStatus.BAD_REQUEST, "{errors.ServicePlayerService.notAllowedToGetPlayerDetails}");
        }

        Player requestedPlayer = playerRepository.findById(playerId)
                .orElseThrow(() -> new ResourceNotFoundException("{errors.ServicePlayerService.playerNotFound}"));

        return playerMapper.toDto(requestedPlayer);
    }

    @Transactional
    public PlayerResponseDTO updatePlayer(PlayerRequestDTO playerDTO, long clubId, long playerId) {
        Long currentClubId = authService.getCurrentClubId();

        if (!currentClubId.equals(clubId)) {
            throw new InsufficientAuthoritiesException(HttpStatus.BAD_REQUEST, "{errors.ServicePlayerService.notAllowedToUpdatePlayer}");
        }

        Player requestedPlayer = playerRepository.findById(playerId)
                .orElseThrow(() -> new ResourceNotFoundException("{errors.ServicePlayerService.playerNotFound}"));

        requestedPlayer.setGivenName(playerDTO.getGivenName());
        requestedPlayer.setFamilyName(playerDTO.getFamilyName());
        requestedPlayer.setNationality(playerDTO.getNationality());
        requestedPlayer.setEmail(playerDTO.getEmail());
        requestedPlayer.setDateOfBirth(playerDTO.getDateOfBirth());

        Player savedPlayer = playerRepository.save(requestedPlayer);

        return playerMapper.toDtoWithoutDetails(savedPlayer);
    }

    @Transactional
    public void deletePlayer(Long clubId, Long playerId) {
        Long currentClubId = authService.getCurrentClubId();

        if (!currentClubId.equals(clubId)) {
            throw new InsufficientAuthoritiesException(HttpStatus.BAD_REQUEST, "{errors.ServicePlayerService.notAllowedToDeletePlayer}");
        }

        playerRepository.deleteById(playerId);
    }
}
