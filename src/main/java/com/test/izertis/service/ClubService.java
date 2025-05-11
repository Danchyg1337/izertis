package com.test.izertis.service;

import com.test.izertis.dtos.request.NewClubDTO;
import com.test.izertis.dtos.response.ClubDTO;
import com.test.izertis.dtos.response.ClubDetailsDTO;
import com.test.izertis.exception.InsufficientAuthoritiesException;
import com.test.izertis.exception.NotFoundException;
import com.test.izertis.mappers.ClubMapper;
import com.test.izertis.model.Club;
import com.test.izertis.model.Player;
import com.test.izertis.repository.ClubRepository;
import com.test.izertis.repository.PlayerRepository;
import com.test.izertis.service.auth.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
@Transactional
public class ClubService {
    private final PlayerRepository playerRepository;
    private final ClubRepository clubRepository;
    private final ClubMapper clubMapper;
    private final AuthService authService;
    private final PasswordEncoder passwordEncoder;

    public ClubDTO registerClub(NewClubDTO newClubDTO) {
        String newClubUsername = newClubDTO.getUsername();

        if (clubRepository.findByUsername(newClubUsername).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Username " + newClubUsername + " already exists");
        }

        Club clubEntity = clubMapper.toEntity(newClubDTO);
        clubEntity.setPassword(passwordEncoder.encode(newClubDTO.getPassword()));
        Club savedClub = clubRepository.save(clubEntity);

        return clubMapper.toClubDTO(savedClub);
    }

    @Transactional(readOnly = true)
    public ClubDetailsDTO getClubDetails(long id) {
        Long currentClubId = authService.getCurrentClubId();

        Club requestedClub = clubRepository.findById(id)
                .orElseThrow( () -> new NotFoundException("Requested club not found"));

        if (!requestedClub.getIsPublic() && !currentClubId.equals(requestedClub.getId())) {
            throw new InsufficientAuthoritiesException(HttpStatus.BAD_REQUEST, "You are not allowed to get details of this player");
        }

        ClubDetailsDTO clubDetailsDTO = clubMapper.toClubDetailsDTO(requestedClub);

        long numberOfPlayers = playerRepository.countByClubId(id);
        clubDetailsDTO.setNumberOfPlayers(numberOfPlayers);

        return clubDetailsDTO;
    }

    @Transactional(readOnly = true)
    public Page<ClubDTO> getPublicClubs(Pageable pageable) {
        Page<Club> clubs = clubRepository.findAllByIsPublicTrue(pageable);
        return clubs.map(clubMapper::toClubDTO);
    }

    @Transactional(readOnly = true)
    public Page<ClubDTO> getPublicClubsWithFederation(String federation, Pageable pageable) {
        Page<Club> clubs = clubRepository.findAllByIsPublicTrueAndFederation(federation, pageable);
        return clubs.map(clubMapper::toClubDTO);
    }

    public ClubDTO updateClub(ClubDTO clubDTO) {
        Long currentClubId = authService.getCurrentClubId();

        Club currentClub = clubRepository.findById(currentClubId)
                .orElseThrow( () -> new NotFoundException("Club not found for provided token"));

        currentClub.setFederation(clubDTO.getFederation());
        currentClub.setOfficialName(clubDTO.getOfficialName());
        currentClub.setPopularName(clubDTO.getPopularName());
        currentClub.setIsPublic(clubDTO.getIsPublic());

        Club savedClub = clubRepository.save(currentClub);

        return clubMapper.toClubDTO(savedClub);
    }
}
