package com.gindevp.meeting.service.mapper;

import com.gindevp.meeting.domain.MeetingLevel;
import com.gindevp.meeting.service.dto.MeetingLevelDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link MeetingLevel} and its DTO {@link MeetingLevelDTO}.
 */
@Mapper(componentModel = "spring")
public interface MeetingLevelMapper extends EntityMapper<MeetingLevelDTO, MeetingLevel> {}
