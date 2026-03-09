package com.gindevp.meeting.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link com.gindevp.meeting.domain.Equipment} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class EquipmentDTO implements Serializable {

    private Long id;

    @NotNull
    private String code;

    @NotNull
    private String name;

    private String description;

    @Min(value = 0)
    private Integer totalQuantity = 999;

    private String equipmentType;

    @Size(max = 20)
    private String status;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getTotalQuantity() {
        return totalQuantity;
    }

    public void setTotalQuantity(Integer totalQuantity) {
        this.totalQuantity = totalQuantity;
    }

    public String getEquipmentType() {
        return equipmentType;
    }

    public void setEquipmentType(String equipmentType) {
        this.equipmentType = equipmentType;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof EquipmentDTO)) {
            return false;
        }

        EquipmentDTO equipmentDTO = (EquipmentDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, equipmentDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "EquipmentDTO{" +
            "id=" + getId() +
            ", code='" + getCode() + "'" +
            ", name='" + getName() + "'" +
            ", description='" + getDescription() + "'" +
            ", equipmentType='" + getEquipmentType() + "'" +
            ", status='" + getStatus() + "'" +
            "}";
    }
}
