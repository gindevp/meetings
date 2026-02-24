package com.gindevp.meeting.service.mapper;

import com.gindevp.meeting.domain.Meeting;
import com.gindevp.meeting.domain.MeetingTask;
import com.gindevp.meeting.domain.User;
import com.gindevp.meeting.service.dto.MeetingDTO;
import com.gindevp.meeting.service.dto.MeetingTaskDTO;
import com.gindevp.meeting.service.dto.UserDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link MeetingTask} and its DTO {@link MeetingTaskDTO}.
 */
@Mapper(componentModel = "spring")
public interface MeetingTaskMapper extends EntityMapper<MeetingTaskDTO, MeetingTask> {
    @Mapping(target = "assignee", source = "assignee", qualifiedByName = "userLogin")
    @Mapping(target = "assignedBy", source = "assignedBy", qualifiedByName = "userLogin")
    @Mapping(target = "meeting", source = "meeting", qualifiedByName = "meetingId")
    MeetingTaskDTO toDto(MeetingTask s);

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
