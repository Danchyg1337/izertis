package com.test.izertis.controller;

import com.test.izertis.dto.request.ClubRequestDTO;
import com.test.izertis.dto.response.ClubResponseDTO;
import com.test.izertis.service.ClubService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/club")
@RequiredArgsConstructor
public class ClubController {

    private final ClubService clubService;

    @Operation(summary = "Register a new club")
    @PostMapping
    public ResponseEntity<ClubResponseDTO> register(@Valid @RequestBody ClubRequestDTO clubRequestDTO) {
        ClubResponseDTO registeredClub = clubService.registerClub(clubRequestDTO);
        return ResponseEntity.created(URI.create("/club/" + registeredClub.getId())).body(registeredClub);
    }

    @Operation(summary = "Get all public clubs")
    @GetMapping
    public ResponseEntity<Page<ClubResponseDTO>> getPublicClubs(@RequestParam(value = "officialName", required = false) String officialName,
                                                                @RequestParam(value = "popularName", required = false) String popularName,
                                                                @RequestParam(value = "federation", required = false) String federation,
                                                                @Parameter(hidden = true) @PageableDefault(size = 5, sort = "id") Pageable pageable) {
        return ResponseEntity.ok(clubService.getPublicClubs(pageable, officialName, popularName, federation));
    }

    @Operation(summary = "Get club details")
    @GetMapping("/{clubId}")
    public ResponseEntity<ClubResponseDTO> getClubDetails(@PathVariable long clubId) {
        return ResponseEntity.ok(clubService.getClubDetails(clubId));
    }

    @Operation(summary = "Update a club")
    @PutMapping
    public ResponseEntity<ClubResponseDTO> update(@Valid @RequestBody ClubRequestDTO clubDTO) {
        return ResponseEntity.ok(clubService.updateClub(clubDTO));
    }
}
