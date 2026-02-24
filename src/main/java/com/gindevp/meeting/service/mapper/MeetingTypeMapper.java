package com.gindevp.meeting.service.mapper;

import com.gindevp.meeting.domain.MeetingType;
import com.gindevp.meeting.service.dto.MeetingTypeDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link MeetingType} and its DTO {@link MeetingTypeDTO}.
 */
@Mapper(componentModel = "spring")
public interface MeetingTypeMapper extends EntityMapper<MeetingTypeDTO, MeetingType> {}
