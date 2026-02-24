package com.gindevp.meeting.service.mapper;

import com.gindevp.meeting.domain.Meeting;
import com.gindevp.meeting.domain.MeetingApproval;
import com.gindevp.meeting.domain.User;
import com.gindevp.meeting.service.dto.MeetingApprovalDTO;
import com.gindevp.meeting.service.dto.MeetingDTO;
import com.gindevp.meeting.service.dto.UserDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link MeetingApproval} and its DTO {@link MeetingApprovalDTO}.
 */
@Mapper(componentModel = "spring")
public interface MeetingApprovalMapper extends EntityMapper<MeetingApprovalDTO, MeetingApproval> {
    @Mapping(target = "decidedBy", source = "decidedBy", qualifiedByName = "userLogin")
    @Mapping(target = "meeting", source = "meeting", qualifiedByName = "meetingId")
    MeetingApprovalDTO toDto(MeetingApproval s);

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
