package com.test.izertis.mappers;

import com.test.izertis.dtos.request.NewPlayerDTO;
import com.test.izertis.dtos.response.PlayerDTO;
import com.test.izertis.dtos.response.PlayerDetailsDTO;
import com.test.izertis.model.Player;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PlayerMapper {
    PlayerDTO toPlayerDTO(Player player);

    PlayerDetailsDTO toPlayerDetailsDTO(Player player);

    Player toEntity(NewPlayerDTO dto);

    List<PlayerDTO> toPlayerDTOList(List<Player> players);
}
