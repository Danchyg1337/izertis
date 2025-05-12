package com.test.izertis.service.security;

import com.test.izertis.entity.Club;
import com.test.izertis.repository.ClubRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final ClubRepository clubRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Club user = clubRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("{errors.ServiceAuthService.invalidCredentials}"));

        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .roles("CLUB_OWNER")
                .build();
    }
}
