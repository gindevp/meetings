package com.gindevp.meeting.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link com.gindevp.meeting.domain.RoomEquipment} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class RoomEquipmentDTO implements Serializable {

    private Long id;

    @NotNull
    @Min(value = 0)
    private Integer quantity;

    @NotNull
    private RoomDTO room;

    @NotNull
    private EquipmentDTO equipment;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public RoomDTO getRoom() {
        return room;
    }

    public void setRoom(RoomDTO room) {
        this.room = room;
    }

    public EquipmentDTO getEquipment() {
        return equipment;
    }

    public void setEquipment(EquipmentDTO equipment) {
        this.equipment = equipment;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof RoomEquipmentDTO)) {
            return false;
        }

        RoomEquipmentDTO roomEquipmentDTO = (RoomEquipmentDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, roomEquipmentDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "RoomEquipmentDTO{" +
            "id=" + getId() +
            ", quantity=" + getQuantity() +
            ", room=" + getRoom() +
            ", equipment=" + getEquipment() +
            "}";
    }
}
