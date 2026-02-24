package com.gindevp.meeting.service.mapper;

import com.gindevp.meeting.domain.Department;
import com.gindevp.meeting.domain.Meeting;
import com.gindevp.meeting.domain.MeetingLevel;
import com.gindevp.meeting.domain.MeetingType;
import com.gindevp.meeting.domain.Room;
import com.gindevp.meeting.domain.User;
import com.gindevp.meeting.service.dto.DepartmentDTO;
import com.gindevp.meeting.service.dto.MeetingDTO;
import com.gindevp.meeting.service.dto.MeetingLevelDTO;
import com.gindevp.meeting.service.dto.MeetingTypeDTO;
import com.gindevp.meeting.service.dto.RoomDTO;
import com.gindevp.meeting.service.dto.UserDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Meeting} and its DTO {@link MeetingDTO}.
 */
@Mapper(componentModel = "spring")
public interface MeetingMapper extends EntityMapper<MeetingDTO, Meeting> {
    @Mapping(target = "type", source = "type", qualifiedByName = "meetingTypeName")
    @Mapping(target = "level", source = "level", qualifiedByName = "meetingLevelName")
    @Mapping(target = "organizerDepartment", source = "organizerDepartment", qualifiedByName = "departmentName")
    @Mapping(target = "room", source = "room", qualifiedByName = "roomName")
    @Mapping(target = "requester", source = "requester", qualifiedByName = "userLogin")
    @Mapping(target = "host", source = "host", qualifiedByName = "userLogin")
    MeetingDTO toDto(Meeting s);

    @Named("meetingTypeName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    MeetingTypeDTO toDtoMeetingTypeName(MeetingType meetingType);

    @Named("meetingLevelName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    MeetingLevelDTO toDtoMeetingLevelName(MeetingLevel meetingLevel);

    @Named("departmentName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    DepartmentDTO toDtoDepartmentName(Department department);

    @Named("roomName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    RoomDTO toDtoRoomName(Room room);

    @Named("userLogin")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "login", source = "login")
    UserDTO toDtoUserLogin(User user);
}
