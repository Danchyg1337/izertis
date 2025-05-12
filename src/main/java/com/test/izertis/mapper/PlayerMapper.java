package com.test.izertis.mapper;

import com.test.izertis.dto.request.PlayerRequestDTO;
import com.test.izertis.dto.response.PlayerResponseDTO;
import com.test.izertis.entity.Player;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface PlayerMapper {

    void fromDto(PlayerRequestDTO dto, @MappingTarget Player entity);

    PlayerResponseDTO toDto(Player entity);


    @Mapping(target = "email", ignore = true)
    @Mapping(target = "nationality", ignore = true)
    @Mapping(target = "dateOfBirth", ignore = true)
    PlayerResponseDTO toDtoWithoutDetails(Player entity);
}
