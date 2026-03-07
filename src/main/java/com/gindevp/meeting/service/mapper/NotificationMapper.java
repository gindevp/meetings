package com.gindevp.meeting.service.mapper;

import com.gindevp.meeting.domain.Notification;
import com.gindevp.meeting.service.dto.NotificationDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface NotificationMapper extends EntityMapper<NotificationDTO, Notification> {}
