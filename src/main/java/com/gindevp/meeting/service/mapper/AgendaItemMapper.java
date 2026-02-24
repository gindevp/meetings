package com.gindevp.meeting.service.mapper;

import com.gindevp.meeting.domain.AgendaItem;
import com.gindevp.meeting.domain.Meeting;
import com.gindevp.meeting.service.dto.AgendaItemDTO;
import com.gindevp.meeting.service.dto.MeetingDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link AgendaItem} and its DTO {@link AgendaItemDTO}.
 */
@Mapper(componentModel = "spring")
public interface AgendaItemMapper extends EntityMapper<AgendaItemDTO, AgendaItem> {
    @Mapping(target = "meeting", source = "meeting", qualifiedByName = "meetingId")
    AgendaItemDTO toDto(AgendaItem s);

    @Named("meetingId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    MeetingDTO toDtoMeetingId(Meeting meeting);
}
