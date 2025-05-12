package com.test.izertis.service.validator;

import com.test.izertis.entity.Club;
import com.test.izertis.exception.InsufficientAuthoritiesException;
import com.test.izertis.service.auth.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ClubValidator {
    private final AuthService authService;

    public void validateThatUserHasReadAccessToClub(Club club) {
        Long currentClubId = authService.getCurrentClubId();

        if (!club.getIsPublic() && !currentClubId.equals(club.getId())) {
            throw new InsufficientAuthoritiesException(HttpStatus.BAD_REQUEST, "{errors.ServiceClubService.notAllowedToReadResource}");
        }
    }

    public void validateThatUserHasWriteAccessToClub(long clubId) {
        Long currentClubId = authService.getCurrentClubId();

        if (!currentClubId.equals(clubId)) {
            throw new InsufficientAuthoritiesException(HttpStatus.BAD_REQUEST, "{errors.ServiceClubService.notAllowedToWriteToResource}");
        }
    }
}
