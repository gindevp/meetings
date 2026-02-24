package com.gindevp.meeting.service.mapper;

import com.gindevp.meeting.domain.Incident;
import com.gindevp.meeting.domain.Meeting;
import com.gindevp.meeting.domain.User;
import com.gindevp.meeting.service.dto.IncidentDTO;
import com.gindevp.meeting.service.dto.MeetingDTO;
import com.gindevp.meeting.service.dto.UserDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Incident} and its DTO {@link IncidentDTO}.
 */
@Mapper(componentModel = "spring")
public interface IncidentMapper extends EntityMapper<IncidentDTO, Incident> {
    @Mapping(target = "reportedBy", source = "reportedBy", qualifiedByName = "userLogin")
    @Mapping(target = "meeting", source = "meeting", qualifiedByName = "meetingId")
    IncidentDTO toDto(Incident s);

    @Named("userLogin")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "login", source = "login")
    UserDTO toDtoUserLogin(User user);

    @Named("meetingId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    MeetingDTO toDtoMeetingId(Meeting meeting);
}
