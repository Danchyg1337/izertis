package com.test.izertis.service.validator;

import com.test.izertis.entity.Player;
import com.test.izertis.exception.InsufficientAuthoritiesException;
import com.test.izertis.exception.ResourceNotFoundException;
import com.test.izertis.repository.PlayerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PlayerValidator {
    private final PlayerRepository playerRepository;

    public void validatePlayerBelongsToClub(long clubId, long playerId) {
        Player player = playerRepository.findById(playerId)
                .orElseThrow(() -> new ResourceNotFoundException("{errors.ServicePlayerService.playerNotFound}"));
        Long playerClubId = player.getClub().getId();

        if (!playerClubId.equals(clubId)) {
            throw new InsufficientAuthoritiesException(HttpStatus.BAD_REQUEST, "{errors.ServicePlayerService.playerDoesNotBelongToRequestedClub}");
        }
    }
}
