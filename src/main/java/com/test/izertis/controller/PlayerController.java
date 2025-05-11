package com.test.izertis.controller;

import com.test.izertis.dtos.request.NewPlayerDTO;
import com.test.izertis.dtos.response.PlayerDTO;
import com.test.izertis.dtos.response.PlayerDetailsDTO;
import com.test.izertis.mappers.PlayerMapper;
import com.test.izertis.model.Player;
import com.test.izertis.service.PlayerService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/club/{clubId}/player")
public class PlayerController {

    private final PlayerService playerService;
    private final PlayerMapper playerMapper;

    @Operation(summary = "Register player")
    @PostMapping
    private PlayerDTO registerPlayer(@Valid @RequestBody NewPlayerDTO newPlayerDTO,
                                     @PathVariable long clubId) {
        return playerService.registerPlayer(newPlayerDTO, clubId);
    }

    @Operation(summary = "Get all players of the club")
    @GetMapping
    private Page<PlayerDTO> listPlayers(@PathVariable long clubId,
                                        @RequestParam(defaultValue = "0") @Min(0) int page,
                                        @RequestParam(defaultValue = "5") @Min(1) @Max(100) int size,
                                        @RequestParam(defaultValue = "id") String sortBy,
                                        @RequestParam(defaultValue = "true") boolean ascending) {
        Sort sort = ascending ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        return playerService.getAllPlayers(clubId, pageable);
    }

    @Operation(summary = "Get player details")
    @GetMapping("/{playerId}")
    private PlayerDetailsDTO listPlayers(@PathVariable long clubId,
                                         @PathVariable long playerId) {
        return playerService.getPlayerDetails(clubId, playerId);
    }

    @Operation(summary = "Update player")
    @PutMapping("/{playerId}")
    public PlayerDTO updatePlayer(@Valid @RequestBody NewPlayerDTO newPlayerDTO,
                                  @PathVariable long clubId,
                                  @PathVariable long playerId) {
        return playerService.updatePlayer(newPlayerDTO, clubId, playerId);
    }

    @Operation(summary = "Delete player")
    @DeleteMapping("/{playerId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePlayer(@PathVariable Long clubId, @PathVariable Long playerId) {
        playerService.deletePlayer(clubId, playerId);
    }
}
