package com.gindevp.meeting.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;

/**
 * A Room.
 */
@Entity
@Table(name = "room")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Room implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "code", nullable = false, unique = true)
    private String code;

    @NotNull
    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "location")
    private String location;

    @NotNull
    @Min(value = 1)
    @Column(name = "capacity", nullable = false)
    private Integer capacity;

    @NotNull
    @Column(name = "active", nullable = false)
    private Boolean active;

    @Size(max = 512)
    @Column(name = "image_url", length = 512)
    private String imageUrl;

    @Size(max = 2000)
    @Column(name = "description", length = 2000)
    private String description;

    @Size(max = 20)
    @Column(name = "status", length = 20)
    private String status;

    @Column(name = "image_content_type", length = 100)
    private String imageContentType;

    @Lob
    @Column(name = "image_data")
    private byte[] imageData;

    @Size(max = 50)
    @Column(name = "room_type", length = 50)
    private String roomType;

    @Column(name = "layout_data", columnDefinition = "longtext")
    private String layoutData;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Room id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return this.code;
    }

    public Room code(String code) {
        this.setCode(code);
        return this;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return this.name;
    }

    public Room name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return this.location;
    }

    public Room location(String location) {
        this.setLocation(location);
        return this;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Integer getCapacity() {
        return this.capacity;
    }

    public Room capacity(Integer capacity) {
        this.setCapacity(capacity);
        return this;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    public Boolean getActive() {
        return this.active;
    }

    public Room active(Boolean active) {
        this.setActive(active);
        return this;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public String getImageUrl() {
        return this.imageUrl;
    }

    public Room imageUrl(String imageUrl) {
        this.setImageUrl(imageUrl);
        return this;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getDescription() {
        return this.description;
    }

    public Room description(String description) {
        this.setDescription(description);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return this.status;
    }

    public Room status(String status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getImageContentType() {
        return this.imageContentType;
    }

    public Room imageContentType(String imageContentType) {
        this.setImageContentType(imageContentType);
        return this;
    }

    public void setImageContentType(String imageContentType) {
        this.imageContentType = imageContentType;
    }

    public byte[] getImageData() {
        return this.imageData;
    }

    public Room imageData(byte[] imageData) {
        this.setImageData(imageData);
        return this;
    }

    public void setImageData(byte[] imageData) {
        this.imageData = imageData;
    }

    public String getRoomType() {
        return this.roomType;
    }

    public Room roomType(String roomType) {
        this.setRoomType(roomType);
        return this;
    }

    public void setRoomType(String roomType) {
        this.roomType = roomType;
    }

    public String getLayoutData() {
        return this.layoutData;
    }

    public Room layoutData(String layoutData) {
        this.setLayoutData(layoutData);
        return this;
    }

    public void setLayoutData(String layoutData) {
        this.layoutData = layoutData;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Room)) {
            return false;
        }
        return getId() != null && getId().equals(((Room) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Room{" +
            "id=" + getId() +
            ", code='" + getCode() + "'" +
            ", name='" + getName() + "'" +
            ", location='" + getLocation() + "'" +
            ", capacity=" + getCapacity() +
            ", active='" + getActive() + "'" +
            ", imageUrl='" + getImageUrl() + "'" +
            ", description='" + getDescription() + "'" +
            ", status='" + getStatus() + "'" +
            ", imageContentType='" + getImageContentType() + "'" +
            ", roomType='" + getRoomType() + "'" +
            ", layoutData='" + getLayoutData() + "'" +
            "}";
    }
}
