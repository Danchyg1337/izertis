package com.test.izertis.mappers;

import com.test.izertis.dtos.request.NewClubDTO;
import com.test.izertis.dtos.response.ClubDTO;
import com.test.izertis.dtos.response.ClubDetailsDTO;
import com.test.izertis.model.Club;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ClubMapper {
    ClubDTO toClubDTO(Club club);

    ClubDetailsDTO toClubDetailsDTO(Club club);

    Club toEntity(NewClubDTO dto);

    List<ClubDTO> toClubDTOList(List<Club> clubs);
}
