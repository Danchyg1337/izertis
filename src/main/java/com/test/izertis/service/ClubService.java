package com.test.izertis.service;

import com.test.izertis.dto.request.ClubRequestDTO;
import com.test.izertis.dto.response.ClubResponseDTO;
import com.test.izertis.entity.Club;
import com.test.izertis.exception.ConflictException;
import com.test.izertis.exception.ResourceNotFoundException;
import com.test.izertis.mapper.ClubMapper;
import com.test.izertis.repository.ClubRepository;
import com.test.izertis.repository.PlayerRepository;
import com.test.izertis.service.auth.AuthService;
import com.test.izertis.service.validator.ClubValidator;
import com.test.izertis.specification.ClubSpecifications;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class ClubService {
    private final PlayerRepository playerRepository;
    private final ClubRepository clubRepository;
    private final ClubMapper clubMapper;
    private final ClubValidator clubValidator;
    private final AuthService authService;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public ClubResponseDTO registerClub(ClubRequestDTO clubRequestDTO) {
        String newClubUsername = clubRequestDTO.getUsername();

        if (clubRepository.findByUsername(newClubUsername).isPresent()) {
            throw new ConflictException("{errors.ServiceClubService.clubAlreadyExists}" + newClubUsername);
        }

        Club clubEntity = new Club();
        clubMapper.fromDto(clubRequestDTO, clubEntity);

        clubEntity.setPassword(passwordEncoder.encode(clubRequestDTO.getPassword()));

        Club savedClub = clubRepository.save(clubEntity);

        return clubMapper.toDto(savedClub);
    }

    @Transactional(readOnly = true)
    public ClubResponseDTO getClubDetails(long id) {
        Club requestedClub = clubRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("{errors.ServiceClubService.clubNotFound}"));

        clubValidator.validateThatUserHasReadAccessToClub(requestedClub);

        ClubResponseDTO clubResponseDTO = clubMapper.toDto(requestedClub);

        long numberOfPlayers = playerRepository.countByClubId(id);
        clubResponseDTO.setNumberOfPlayers(numberOfPlayers);

        return clubResponseDTO;
    }

    @Transactional(readOnly = true)
    public Page<ClubResponseDTO> getPublicClubs(Pageable pageable, String officialName, String popularName, String federation) {
        Specification<Club> spec = Specification.where(ClubSpecifications.isPublic())
                .and(ClubSpecifications.byOfficialName(officialName))
                .and(ClubSpecifications.byPopularName(popularName))
                .and(ClubSpecifications.byFederation(federation));

        Page<Club> clubs = clubRepository.findAll(spec, pageable);
        return clubs.map(clubMapper::toDtoWithoutDetails);
    }

    @Transactional
    public ClubResponseDTO updateClub(ClubRequestDTO clubDTO) {
        Long currentClubId = authService.getCurrentClubId();

        Club currentClub = clubRepository.findById(currentClubId)
                .orElseThrow(() -> new ResourceNotFoundException("{errors.ServiceClubService.clubNotFoundForToken}"));

        clubMapper.fromDto(clubDTO, currentClub);

        Club savedClub = clubRepository.save(currentClub);

        return clubMapper.toDtoWithoutDetails(savedClub);
    }
}
