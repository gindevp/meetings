package com.gindevp.meeting.service.mapper;

import com.gindevp.meeting.domain.Equipment;
import com.gindevp.meeting.service.dto.EquipmentDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Equipment} and its DTO {@link EquipmentDTO}.
 */
@Mapper(componentModel = "spring")
public interface EquipmentMapper extends EntityMapper<EquipmentDTO, Equipment> {}
