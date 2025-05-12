package com.test.izertis.controller;

import com.test.izertis.dto.request.PlayerRequestDTO;
import com.test.izertis.dto.response.PlayerResponseDTO;
import com.test.izertis.service.PlayerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/club/{clubId}/player")
public class PlayerController {

    private final PlayerService playerService;

    @Operation(summary = "Register player")
    @PostMapping
    public ResponseEntity<PlayerResponseDTO> registerPlayer(@Valid @RequestBody PlayerRequestDTO playerRequestDTO,
                                                             @PathVariable long clubId) {
        return ResponseEntity.ok(playerService.registerPlayer(playerRequestDTO, clubId));
    }

    @Operation(summary = "Get all players of the club")
    @GetMapping
    public ResponseEntity<Page<PlayerResponseDTO>> listPlayers(@PathVariable long clubId,
                                                                @RequestParam(value = "givenName", required = false) String givenName,
                                                                @RequestParam(value = "familyName", required = false) String familyName,
                                                                //@RequestParam(value = "nationality", required = false) String federation,
                                                                @Parameter(hidden = true) @PageableDefault(size = 5, sort = "id", direction = Direction.ASC) Pageable pageable) {
        return ResponseEntity.ok(playerService.getAllPlayers(clubId, givenName, familyName, pageable));
    }

    @Operation(summary = "Get player details")
    @GetMapping("/{playerId}")
    public ResponseEntity<PlayerResponseDTO> listPlayers(@PathVariable long clubId,
                                                          @PathVariable long playerId) {
        return ResponseEntity.ok(playerService.getPlayerDetails(clubId, playerId));
    }

    @Operation(summary = "Update player")
    @PutMapping("/{playerId}")
    public ResponseEntity<PlayerResponseDTO> updatePlayer(@Valid @RequestBody PlayerRequestDTO playerRequestDTO,
                                                          @PathVariable long clubId,
                                                          @PathVariable long playerId) {
        return ResponseEntity.ok(playerService.updatePlayer(playerRequestDTO, clubId, playerId));
    }

    @Operation(summary = "Delete player")
    @DeleteMapping("/{playerId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePlayer(@PathVariable Long clubId, @PathVariable Long playerId) {
        playerService.deletePlayer(clubId, playerId);
    }
}
