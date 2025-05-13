package com.test.izertis;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.test.izertis.dto.request.ClubRequestDTO;
import com.test.izertis.repository.ClubRepository;
import com.test.izertis.repository.PlayerRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class ClubApiTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ClubRepository clubRepository;

    @Autowired
    private PlayerRepository playerRepository;

    @BeforeEach
    public void clearDatabase() {
        playerRepository.deleteAll();
        clubRepository.deleteAll();
    }

    @Test
    public void testPerformUnauthorizedAccess() throws Exception {
        log.info("Test unauthorized access started");

        fillDatabaseWithSampleData();

        log.info("Logging in with first club credentials");

        String firstClubJson = objectMapper.writeValueAsString(getSampleClub());

        MvcResult loginResult = mvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(firstClubJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andReturn();

        String token = objectMapper.readTree(loginResult.getResponse().getContentAsString()).get("token").asText();

        log.info("Getting all public clubs");

        MvcResult allPublicClubsResult = mvc.perform(get("/club")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page.totalElements").value(3))
                .andReturn();

        long thirdClubId = objectMapper.readTree(allPublicClubsResult.getResponse().getContentAsString()).get("content").get(2).path("id").asLong();

        log.info("Logged in. Token: {}", token);

        log.info("Getting unauthorized private club details");

        mvc.perform(get(String.format("/club/%d", thirdClubId + 1))
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isForbidden());

        log.info("Test unauthorized access passed");
    }

    @Test
    public void testClubApiWorkflow() throws Exception {
        log.info("Test api workflow started");

        fillDatabaseWithSampleData();

        log.info("Logging in with first club credentials");

        String firstClubJson = objectMapper.writeValueAsString(getSampleClub());

        MvcResult loginResult = mvc.perform(post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(firstClubJson))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.token").exists())
                    .andReturn();

        String token = objectMapper.readTree(loginResult.getResponse().getContentAsString()).get("token").asText();

        log.info("Logged in. Token: {}", token);

        log.info("Getting all public clubs");

        MvcResult allPublicClubsResult = mvc.perform(get("/club")
                                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page.totalElements").value(3))
                .andReturn();

        long firstClubId = objectMapper.readTree(allPublicClubsResult.getResponse().getContentAsString()).get("content").get(0).path("id").asLong();
        long secondClubId = objectMapper.readTree(allPublicClubsResult.getResponse().getContentAsString()).get("content").get(1).path("id").asLong();
        long thirdClubId = objectMapper.readTree(allPublicClubsResult.getResponse().getContentAsString()).get("content").get(2).path("id").asLong();

        log.info("Getting all public clubs filtered by federation");

        mvc.perform(get("/club")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .param("federation", "FIFA"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page.totalElements").value(2));

        log.info("Getting authorized club details");

        mvc.perform(get( String.format("/club/%d", firstClubId))
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isPublic").value(true))
                .andExpect(jsonPath("$.numberOfPlayers").value(0));

        log.info("Getting public club details");

        mvc.perform(get(String.format("/club/%d", secondClubId))
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isPublic").value(true))
                .andExpect(jsonPath("$.numberOfPlayers").value(0));

        log.info("Putting club details");

        ClubRequestDTO clubToPut = getSampleClub();
        clubToPut.setPopularName("Putted name");
        clubToPut.setOfficialName("Putted official name");
        clubToPut.setFederation("1234");

        String clubJson = objectMapper.writeValueAsString(clubToPut);

        mvc.perform(put("/club")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(clubJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").doesNotExist())
                .andExpect(jsonPath("$.password").doesNotExist())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.officialName").value("Putted official name"))
                .andExpect(jsonPath("$.popularName").value("Putted name"))
                .andExpect(jsonPath("$.federation").value("1234"));

        log.info("Test api workflow passed");
    }

    @Test
    public void testClubDuplicate() throws Exception {
        log.info("Test club duplicate started");

        log.info("Posting initial club");

        String clubJson = objectMapper.writeValueAsString(getSampleClub());

        mvc.perform(post("/club")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(clubJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").doesNotExist())
                .andExpect(jsonPath("$.password").doesNotExist())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.officialName").value("Messi"))
                .andExpect(jsonPath("$.federation").value("FIFA"));

        log.info("Posting duplicate club");

        mvc.perform(post("/club")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(clubJson))
                .andExpect(status().isConflict());

        log.info("Test club duplicate passed");
    }

    @Test
    public void testClubValidation() throws Exception {
        log.info("Test club validation started");

        ClubRequestDTO dto = getSampleClub();
        dto.setUsername("invalid-email");
        dto.setPassword("inv-ps");
        dto.setFederation("invalid-too-long-federation");
        dto.setIsPublic(null);

        String invalidClubJson = objectMapper.writeValueAsString(dto);

        mvc.perform(post("/club")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidClubJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message.username").exists())
                .andExpect(jsonPath("$.message.password").exists())
                .andExpect(jsonPath("$.message.federation").exists())
                .andExpect(jsonPath("$.message.isPublic").exists());

        log.info("Test club validation passed");
    }

    private ClubRequestDTO getSampleClub() {
        ClubRequestDTO dto = new ClubRequestDTO();
        dto.setUsername("messi@football.com");
        dto.setPassword("secure123");
        dto.setOfficialName("Messi");
        dto.setPopularName("The Messinho");
        dto.setFederation("FIFA");
        dto.setIsPublic(true);

        return dto;
    }

    private void fillDatabaseWithSampleData() throws Exception {
        log.info("Posting first club");

        String firstClubJson = objectMapper.writeValueAsString(getSampleClub());

        mvc.perform(post("/club")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(firstClubJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").doesNotExist())
                .andExpect(jsonPath("$.password").doesNotExist())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.officialName").value("Messi"))
                .andExpect(jsonPath("$.popularName").value("The Messinho"))
                .andExpect(jsonPath("$.federation").value("FIFA"));

        log.info("Posting second club");

        ClubRequestDTO secondClubDto = getSampleClub();
        secondClubDto.setUsername("secondClub@example.com");
        secondClubDto.setFederation("UEFA");

        String secondClubJson = objectMapper.writeValueAsString(secondClubDto);

        mvc.perform(post("/club")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(secondClubJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").doesNotExist())
                .andExpect(jsonPath("$.password").doesNotExist())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.officialName").exists())
                .andExpect(jsonPath("$.popularName").exists())
                .andExpect(jsonPath("$.federation").value("UEFA"));

        log.info("Posting third club");

        ClubRequestDTO thirdClubDto = getSampleClub();
        thirdClubDto.setUsername("thirdClub@example.com");
        thirdClubDto.setPopularName("The MESS");
        thirdClubDto.setFederation("FIFA");

        String thirdClubJson = objectMapper.writeValueAsString(thirdClubDto);

        mvc.perform(post("/club")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(thirdClubJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").doesNotExist())
                .andExpect(jsonPath("$.password").doesNotExist())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.officialName").exists())
                .andExpect(jsonPath("$.popularName").value("The MESS"))
                .andExpect(jsonPath("$.federation").value("FIFA"));

        log.info("Posting fourth club");

        ClubRequestDTO fourthClubDtoPrivate = getSampleClub();
        fourthClubDtoPrivate.setUsername("fourthClub@fromTennisToFootballEZ.com");
        fourthClubDtoPrivate.setOfficialName("Rafael Nadal");
        fourthClubDtoPrivate.setFederation("FIFA");
        fourthClubDtoPrivate.setIsPublic(false);

        String fourthClubJson = objectMapper.writeValueAsString(fourthClubDtoPrivate);

        mvc.perform(post("/club")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(fourthClubJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").doesNotExist())
                .andExpect(jsonPath("$.password").doesNotExist())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.officialName").value("Rafael Nadal"))
                .andExpect(jsonPath("$.popularName").exists())
                .andExpect(jsonPath("$.federation").value("FIFA"));
    }
}
