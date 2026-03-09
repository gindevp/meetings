package com.gindevp.meeting.web.rest;

import com.gindevp.meeting.domain.User;
import com.gindevp.meeting.repository.UserRepository;
import com.gindevp.meeting.service.UserService;
import com.gindevp.meeting.service.dto.UserDTO;
import java.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.PaginationUtil;

@RestController
@RequestMapping("/api")
public class PublicUserResource {

    private static final List<String> ALLOWED_ORDERED_PROPERTIES = Collections.unmodifiableList(
        Arrays.asList("id", "login", "firstName", "lastName", "email", "activated", "langKey")
    );

    private static final Logger LOG = LoggerFactory.getLogger(PublicUserResource.class);

    private final UserService userService;

    private final UserRepository userRepository;

    public PublicUserResource(UserService userService, UserRepository userRepository) {
        this.userService = userService;
        this.userRepository = userRepository;
    }

    /**
     * {@code GET /users} : get all users with only public information - calling this method is allowed for anyone.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body all users.
     */
    @GetMapping("/users")
    public ResponseEntity<List<UserDTO>> getAllPublicUsers(@org.springdoc.core.annotations.ParameterObject Pageable pageable) {
        LOG.debug("REST request to get all public User names");
        if (!onlyContainsAllowedProperties(pageable)) {
            return ResponseEntity.badRequest().build();
        }

        final Page<UserDTO> page = userService.getAllPublicUsers(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    private boolean onlyContainsAllowedProperties(Pageable pageable) {
        return pageable.getSort().stream().map(Sort.Order::getProperty).allMatch(ALLOWED_ORDERED_PROPERTIES::contains);
    }

    /**
     * {@code GET /users/department/:departmentId} : get all users by department.
     */
    @GetMapping("/users/department/{departmentId}")
    public ResponseEntity<List<UserDTO>> getUsersByDepartment(@PathVariable Long departmentId) {
        LOG.debug("REST request to get all users by department: {}", departmentId);
        List<UserDTO> users = userService.getUsersByDepartment(departmentId);
        return ResponseEntity.ok(users);
    }

    /**
     * GET /users/:id/avatar : get a user's profile image (for display in lists). Authenticated users only.
     */
    @GetMapping("/users/{id}/avatar")
    public ResponseEntity<byte[]> getUserAvatar(@PathVariable Long id) {
        return userRepository
            .findById(id)
            .filter(u -> u.getImageData() != null && u.getImageData().length > 0)
            .map(u -> {
                String contentType = u.getImageContentType() != null ? u.getImageContentType() : "image/jpeg";
                return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .cacheControl(org.springframework.http.CacheControl.noStore().mustRevalidate())
                    .body(u.getImageData());
            })
            .orElse(ResponseEntity.noContent().build());
    }
}
