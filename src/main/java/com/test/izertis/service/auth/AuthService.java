package com.test.izertis.service.auth;

import com.test.izertis.config.JwtProvider;
import com.test.izertis.dtos.response.JwtTokenDTO;
import com.test.izertis.exception.InsufficientAuthoritiesException;
import com.test.izertis.model.Club;
import com.test.izertis.repository.ClubRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final ClubRepository clubRepository;
    private final JwtProvider jwtProvider;
    private final PasswordEncoder passwordEncoder;

    public JwtTokenDTO login(String username, String password) {
        Club club = clubRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Invalid username or password"));

        if (!passwordEncoder.matches(password, club.getPassword())) {
            throw new RuntimeException("Invalid username or password");
        }

        String token = jwtProvider.generateToken(club.getId());
        return new JwtTokenDTO(token);
    }

    public Long getCurrentClubId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return (Long) auth.getPrincipal();
    }

    public Club getClubAndCheckIfUserHasAuthoritiesToAccess(Long clubId, String errorMessage) {
        Long currentClubId = getCurrentClubId();

        Club requestedClub = clubRepository.findById(clubId)
                .orElseThrow( () -> new InsufficientAuthoritiesException(HttpStatus.NOT_FOUND, "Requested club not found"));

        if (!requestedClub.getIsPublic() && !currentClubId.equals(requestedClub.getId())) {
            throw new InsufficientAuthoritiesException(HttpStatus.BAD_REQUEST, errorMessage);
        }

        return requestedClub;
    }
}
