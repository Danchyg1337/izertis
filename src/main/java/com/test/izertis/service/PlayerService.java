package com.test.izertis.service;

import com.test.izertis.dto.request.PlayerRequestDTO;
import com.test.izertis.dto.response.PlayerResponseDTO;
import com.test.izertis.entity.Club;
import com.test.izertis.entity.Player;
import com.test.izertis.exception.ResourceNotFoundException;
import com.test.izertis.mapper.PlayerMapper;
import com.test.izertis.repository.ClubRepository;
import com.test.izertis.repository.PlayerRepository;
import com.test.izertis.service.validator.ClubValidator;
import com.test.izertis.service.validator.PlayerValidator;
import com.test.izertis.specification.PlayerSpecifications;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class PlayerService {
    private final PlayerMapper playerMapper;
    private final PlayerRepository playerRepository;
    private final ClubRepository clubRepository;
    private final ClubValidator clubValidator;
    private final PlayerValidator playerValidator;

    @Transactional
    public PlayerResponseDTO registerPlayer(PlayerRequestDTO playerRequestDTO, long clubId) {
        clubValidator.validateThatUserHasWriteAccessToClub(clubId);

        Club parentClub = clubRepository.findById(clubId)
                .orElseThrow(() -> new ResourceNotFoundException("{errors.ServiceClubService.clubNotFound}"));

        Player newPlayer = new Player();
        playerMapper.fromDto(playerRequestDTO, newPlayer);

        newPlayer.setClub(parentClub);

        Player savedPlayer = playerRepository.save(newPlayer);

        return playerMapper.toDto(savedPlayer);
    }

    @Transactional(readOnly = true)
    public Page<PlayerResponseDTO> getAllPlayers(long clubId,
                                                 String givenName,
                                                 String familyName,
                                                 //String nationality,
                                                 Pageable pageable) {
        Club requestedClub = clubRepository.findById(clubId)
                .orElseThrow(() -> new ResourceNotFoundException("{errors.ServiceClubService.clubNotFound}"));

        clubValidator.validateThatUserHasReadAccessToClub(requestedClub);

        Specification<Player> spec = Specification.where(PlayerSpecifications.byClubId(clubId))
                .and(PlayerSpecifications.byGivenName(givenName))
                //.and(PlayerSpecifications.byNationality(nationality))
                .and(PlayerSpecifications.byFamilyName(familyName));

        Page<Player> players = playerRepository.findAll(spec, pageable);

        return players.map(playerMapper::toDtoWithoutDetails);
    }

    @Transactional(readOnly = true)
    public PlayerResponseDTO getPlayerDetails(long clubId, long playerId) {
        Club requestedClub = clubRepository.findById(clubId)
                .orElseThrow(() -> new ResourceNotFoundException("{errors.ServiceClubService.clubNotFound}"));

        clubValidator.validateThatUserHasReadAccessToClub(requestedClub);
        playerValidator.validatePlayerBelongsToClub(clubId, playerId);

        Player requestedPlayer = playerRepository.findById(playerId)
                .orElseThrow(() -> new ResourceNotFoundException("{errors.ServicePlayerService.playerNotFound}"));

        return playerMapper.toDto(requestedPlayer);
    }

    @Transactional
    public PlayerResponseDTO updatePlayer(PlayerRequestDTO playerDTO, long clubId, long playerId) {
        clubValidator.validateThatUserHasWriteAccessToClub(clubId);
        playerValidator.validatePlayerBelongsToClub(clubId, playerId);

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
    public void deletePlayer(long clubId, long playerId) {
        clubValidator.validateThatUserHasWriteAccessToClub(clubId);
        playerValidator.validatePlayerBelongsToClub(clubId, playerId);
        playerRepository.deleteById(playerId);
    }
}
