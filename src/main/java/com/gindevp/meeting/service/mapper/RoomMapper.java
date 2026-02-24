package com.gindevp.meeting.service.mapper;

import com.gindevp.meeting.domain.Room;
import com.gindevp.meeting.service.dto.RoomDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Room} and its DTO {@link RoomDTO}.
 */
@Mapper(componentModel = "spring")
public interface RoomMapper extends EntityMapper<RoomDTO, Room> {}
