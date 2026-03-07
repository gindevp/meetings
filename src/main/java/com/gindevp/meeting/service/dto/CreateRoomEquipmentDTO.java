package com.gindevp.meeting.service.dto;

import jakarta.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * DTO for creating a RoomEquipment - accepts only room and equipment IDs.
 */
public class CreateRoomEquipmentDTO implements Serializable {

    @NotNull
    private Long roomId;

    @NotNull
    private Long equipmentId;

    private Integer quantity = 1;

    public Long getRoomId() {
        return roomId;
    }

    public void setRoomId(Long roomId) {
        this.roomId = roomId;
    }

    public Long getEquipmentId() {
        return equipmentId;
    }

    public void setEquipmentId(Long equipmentId) {
        this.equipmentId = equipmentId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}
