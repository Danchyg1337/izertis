package com.test.izertis.controller;

import com.test.izertis.dtos.request.NewClubDTO;
import com.test.izertis.dtos.response.ClubDTO;
import com.test.izertis.dtos.response.ClubDetailsDTO;
import com.test.izertis.model.Club;
import com.test.izertis.service.ClubService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/club")
@RequiredArgsConstructor
public class ClubController {

    private final ClubService clubService;

    @Operation(summary = "Register a new club")
    @PostMapping
    public ClubDTO register(@Valid @RequestBody NewClubDTO newClubDTO) {
        return clubService.registerClub(newClubDTO);
    }

    @Operation(summary = "Get all public clubs")
    @GetMapping
    public Page<ClubDTO> getPublicClubs(@RequestParam(required = false) String federation,
                                        @RequestParam(defaultValue = "0") @Min(0) int page,
                                        @RequestParam(defaultValue = "5") @Min(1) @Max(100) int size) {
        Pageable pageable = PageRequest.of(page, size);
        if (federation != null) {
            return clubService.getPublicClubsWithFederation(federation, pageable);
        }
        return clubService.getPublicClubs(pageable);
    }

    @Operation(summary = "Get club details")
    @GetMapping("/{clubId}")
    public ClubDetailsDTO getClubDetails(@PathVariable long clubId) {
        return clubService.getClubDetails(clubId);
    }

    @Operation(summary = "Update a club")
    @PutMapping
    public ClubDTO update(@Valid @RequestBody ClubDTO clubDTO) {
        return clubService.updateClub(clubDTO);
    }
}
