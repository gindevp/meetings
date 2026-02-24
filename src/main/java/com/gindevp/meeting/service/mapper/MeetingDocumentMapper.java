package com.gindevp.meeting.service.mapper;

import com.gindevp.meeting.domain.Meeting;
import com.gindevp.meeting.domain.MeetingDocument;
import com.gindevp.meeting.domain.User;
import com.gindevp.meeting.service.dto.MeetingDTO;
import com.gindevp.meeting.service.dto.MeetingDocumentDTO;
import com.gindevp.meeting.service.dto.UserDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link MeetingDocument} and its DTO {@link MeetingDocumentDTO}.
 */
@Mapper(componentModel = "spring")
public interface MeetingDocumentMapper extends EntityMapper<MeetingDocumentDTO, MeetingDocument> {
    @Mapping(target = "uploadedBy", source = "uploadedBy", qualifiedByName = "userLogin")
    @Mapping(target = "meeting", source = "meeting", qualifiedByName = "meetingId")
    MeetingDocumentDTO toDto(MeetingDocument s);

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
