package com.test.izertis.mapper;

import com.test.izertis.dto.request.ClubRequestDTO;
import com.test.izertis.dto.response.ClubResponseDTO;
import com.test.izertis.entity.Club;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ClubMapper {
    @Mapping(target = "id", ignore = true)
    void fromDto(ClubRequestDTO dto, @MappingTarget Club entity);

    ClubResponseDTO toDto(Club entity);

    @Mapping(target = "numberOfPlayers", ignore = true)
    ClubResponseDTO toDtoWithoutDetails(Club entity);
}
