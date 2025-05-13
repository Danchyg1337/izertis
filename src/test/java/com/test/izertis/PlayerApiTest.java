package com.test.izertis;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.test.izertis.dto.request.ClubRequestDTO;
import com.test.izertis.dto.request.PlayerRequestDTO;
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

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class PlayerApiTest {

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

        objectMapper.registerModule(new JavaTimeModule());
    }

    @Test
    public void testPlayerApiWorkflow() throws Exception {
        log.info("Test player api workflow started");

        log.info("Posting club");

        ClubRequestDTO clubDto = new ClubRequestDTO();
        clubDto.setUsername("messi@football.com");
        clubDto.setPassword("secure123");
        clubDto.setOfficialName("Messi");
        clubDto.setPopularName("The Messinho");
        clubDto.setFederation("FIFA");
        clubDto.setIsPublic(true);
        String firstClubJson = objectMapper.writeValueAsString(clubDto);

        MvcResult clubPostResult = mvc.perform(post("/club")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(firstClubJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").doesNotExist())
                .andExpect(jsonPath("$.password").doesNotExist())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.officialName").value("Messi"))
                .andExpect(jsonPath("$.popularName").value("The Messinho"))
                .andExpect(jsonPath("$.federation").value("FIFA"))
                .andReturn();

        long clubId = objectMapper.readTree(clubPostResult.getResponse().getContentAsString()).get("id").asLong();

        log.info("Logging in with first club credentials");

        MvcResult loginResult = mvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(firstClubJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andReturn();

        String token = objectMapper.readTree(loginResult.getResponse().getContentAsString()).get("token").asText();

        log.info("Logged in. Token: {}", token);

        log.info("Posting player");

        PlayerRequestDTO playerDto = getSamplePlayer();
        String playerJson = objectMapper.writeValueAsString(playerDto);

        MvcResult playerOnePostResult =mvc.perform(post(String.format("/club/%d/player", clubId))
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(playerJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.email").value("ricrocardo@football.com"))
                .andExpect(jsonPath("$.givenName").value("Ricardinho"))
                .andExpect(jsonPath("$.familyName").value("Ricardo"))
                .andExpect(jsonPath("$.nationality").value("Spanish"))
                .andExpect(jsonPath("$.dateOfBirth").exists())
                .andReturn();

        long firstPlayerId = objectMapper.readTree(playerOnePostResult.getResponse().getContentAsString()).get("id").asLong();

        log.info("Posting another player");

        PlayerRequestDTO playerTwoDto = getSamplePlayer();
        playerTwoDto.setGivenName("Pele");
        playerTwoDto.setFamilyName("Edson");
        playerTwoDto.setNationality("Brazilian");
        String playerTwoJson = objectMapper.writeValueAsString(playerTwoDto);

        MvcResult playerTwoPostResult = mvc.perform(post(String.format("/club/%d/player", clubId))
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(playerTwoJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.email").value("ricrocardo@football.com"))
                .andExpect(jsonPath("$.givenName").value("Pele"))
                .andExpect(jsonPath("$.familyName").value("Edson"))
                .andExpect(jsonPath("$.nationality").value("Brazilian"))
                .andExpect(jsonPath("$.dateOfBirth").exists())
                .andReturn();

        long secondPlayerId = objectMapper.readTree(playerTwoPostResult.getResponse().getContentAsString()).get("id").asLong();

        log.info("Getting second player details");

        mvc.perform(get(String.format("/club/%d/player/%d", clubId, secondPlayerId))
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.givenName").value("Pele"))
                .andExpect(jsonPath("$.familyName").value("Edson"))
                .andExpect(jsonPath("$.nationality").exists())
                .andExpect(jsonPath("$.dateOfBirth").exists());

        log.info("Getting all players");

        mvc.perform(get(String.format("/club/%d/player", clubId))
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page.totalElements").value(2))
                .andExpect(jsonPath("$.content[0].nationality").doesNotExist())
                .andExpect(jsonPath("$.content[0].dateOfBirth").doesNotExist());

        log.info("Getting all players by family name");

        mvc.perform(get(String.format("/club/%d/player", clubId))
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .param("familyName", "Edson"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page.totalElements").value(1));

        log.info("Deleting player 1");

        mvc.perform(delete(String.format("/club/%d/player/%d", clubId, firstPlayerId))
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isNoContent());

        log.info("Deleting player 2");

        mvc.perform(delete(String.format("/club/%d/player/%d", clubId, secondPlayerId))
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isNoContent());

        log.info("Test player api workflow passed");
    }

    private PlayerRequestDTO getSamplePlayer() throws JsonProcessingException {
        PlayerRequestDTO dto = new PlayerRequestDTO();
        dto.setEmail("ricrocardo@football.com");
        dto.setGivenName("Ricardinho");
        dto.setFamilyName("Ricardo");
        dto.setNationality("Spanish");
        dto.setDateOfBirth(LocalDate.now().minusYears(30));

        return dto;
    }
}
