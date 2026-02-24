package com.gindevp.meeting.service.mapper;

import com.gindevp.meeting.domain.Equipment;
import com.gindevp.meeting.domain.Room;
import com.gindevp.meeting.domain.RoomEquipment;
import com.gindevp.meeting.service.dto.EquipmentDTO;
import com.gindevp.meeting.service.dto.RoomDTO;
import com.gindevp.meeting.service.dto.RoomEquipmentDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link RoomEquipment} and its DTO {@link RoomEquipmentDTO}.
 */
@Mapper(componentModel = "spring")
public interface RoomEquipmentMapper extends EntityMapper<RoomEquipmentDTO, RoomEquipment> {
    @Mapping(target = "room", source = "room", qualifiedByName = "roomCode")
    @Mapping(target = "equipment", source = "equipment", qualifiedByName = "equipmentCode")
    RoomEquipmentDTO toDto(RoomEquipment s);

    @Named("roomCode")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "code", source = "code")
    RoomDTO toDtoRoomCode(Room room);

    @Named("equipmentCode")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "code", source = "code")
    EquipmentDTO toDtoEquipmentCode(Equipment equipment);
}
