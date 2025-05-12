package com.test.izertis.service.auth;

import com.test.izertis.config.JwtProvider;
import com.test.izertis.dto.response.JwtTokenDTO;
import com.test.izertis.entity.Club;
import com.test.izertis.repository.ClubRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final ClubRepository clubRepository;
    private final JwtProvider jwtProvider;
    private final PasswordEncoder passwordEncoder;

    public JwtTokenDTO login(String username, String password) {
        Club club = clubRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("{errors.ServiceAuthService.invalidCredentials}"));

        if (!passwordEncoder.matches(password, club.getPassword())) {
            throw new RuntimeException("{errors.ServiceAuthService.invalidCredentials}");
        }

        String token = jwtProvider.generateToken(club.getId());
        return new JwtTokenDTO(token);
    }

    public Long getCurrentClubId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return (Long) auth.getPrincipal();
    }
}
