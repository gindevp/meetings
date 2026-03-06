package com.gindevp.meeting.service.mapper;

import com.gindevp.meeting.domain.Department;
import com.gindevp.meeting.domain.Meeting;
import com.gindevp.meeting.domain.MeetingParticipant;
import com.gindevp.meeting.domain.User;
import com.gindevp.meeting.service.dto.DepartmentDTO;
import com.gindevp.meeting.service.dto.MeetingDTO;
import com.gindevp.meeting.service.dto.MeetingParticipantDTO;
import com.gindevp.meeting.service.dto.UserDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link MeetingParticipant} and its DTO {@link MeetingParticipantDTO}.
 */
@Mapper(componentModel = "spring")
public interface MeetingParticipantMapper extends EntityMapper<MeetingParticipantDTO, MeetingParticipant> {
    @Mapping(target = "user", source = "user", qualifiedByName = "userLogin")
    @Mapping(target = "department", source = "department", qualifiedByName = "departmentName")
    @Mapping(target = "meeting", source = "meeting", qualifiedByName = "meetingId")
    MeetingParticipantDTO toDto(MeetingParticipant s);

    @Named("userLogin")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "login", source = "login")
    UserDTO toDtoUserLogin(User user);

    @Named("departmentName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    DepartmentDTO toDtoDepartmentName(Department department);

    @Named("meetingId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    MeetingDTO toDtoMeetingId(Meeting meeting);
}
