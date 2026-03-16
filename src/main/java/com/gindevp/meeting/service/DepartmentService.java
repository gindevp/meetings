package com.gindevp.meeting.service;

import com.gindevp.meeting.domain.Department;
import com.gindevp.meeting.repository.DepartmentRepository;
import com.gindevp.meeting.repository.UserRepository;
import com.gindevp.meeting.service.dto.DepartmentDTO;
import com.gindevp.meeting.service.mapper.DepartmentMapper;
import com.gindevp.meeting.web.rest.errors.BadRequestAlertException;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.gindevp.meeting.domain.Department}.
 */
@Service
@Transactional
public class DepartmentService {

    private static final Logger LOG = LoggerFactory.getLogger(DepartmentService.class);

    private final DepartmentRepository departmentRepository;

    private final DepartmentMapper departmentMapper;

    private final UserRepository userRepository;

    public DepartmentService(DepartmentRepository departmentRepository, DepartmentMapper departmentMapper, UserRepository userRepository) {
        this.departmentRepository = departmentRepository;
        this.departmentMapper = departmentMapper;
        this.userRepository = userRepository;
    }

    /**
     * Save a department.
     *
     * @param departmentDTO the entity to save.
     * @return the persisted entity.
     */
    public DepartmentDTO save(DepartmentDTO departmentDTO) {
        LOG.debug("Request to save Department : {}", departmentDTO);
        if (isBanLanhDao(departmentDTO)) {
            // Ensure Ban Lãnh Đạo is unique by code and name
            departmentRepository
                .findAll()
                .stream()
                .filter(d -> isBanLanhDaoDto(toDtoWithManagerLogin(d)))
                .findAny()
                .ifPresent(d -> {
                    throw new BadRequestAlertException("Ban Lãnh Đạo already exists", "department", "banlanhdaoexists");
                });
        }
        Department department = departmentMapper.toEntity(departmentDTO);
        if (departmentDTO.getManagerId() != null) {
            department.setManager(userRepository.getReferenceById(departmentDTO.getManagerId()));
        }
        department = departmentRepository.save(department);
        return toDtoWithManagerLogin(department);
    }

    /**
     * Update a department.
     *
     * @param departmentDTO the entity to save.
     * @return the persisted entity.
     */
    public DepartmentDTO update(DepartmentDTO departmentDTO) {
        LOG.debug("Request to update Department : {}", departmentDTO);
        if (isBanLanhDao(departmentDTO)) {
            // Do not allow renaming other departments into Ban Lãnh Đạo
            departmentRepository
                .findAll()
                .stream()
                .filter(d -> !d.getId().equals(departmentDTO.getId()))
                .map(this::toDtoWithManagerLogin)
                .filter(this::isBanLanhDaoDto)
                .findAny()
                .ifPresent(d -> {
                    throw new BadRequestAlertException("Ban Lãnh Đạo already exists", "department", "banlanhdaoexists");
                });
        }
        Department department = departmentMapper.toEntity(departmentDTO);
        if (departmentDTO.getManagerId() != null) {
            department.setManager(userRepository.getReferenceById(departmentDTO.getManagerId()));
        } else {
            department.setManager(null);
        }
        department = departmentRepository.save(department);
        return toDtoWithManagerLogin(department);
    }

    private DepartmentDTO toDtoWithManagerLogin(Department department) {
        DepartmentDTO dto = departmentMapper.toDto(department);
        if (department.getManager() != null) {
            dto.setManagerLogin(department.getManager().getLogin());
        }
        return dto;
    }

    /**
     * Partially update a department.
     *
     * @param departmentDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<DepartmentDTO> partialUpdate(DepartmentDTO departmentDTO) {
        LOG.debug("Request to partially update Department : {}", departmentDTO);

        if (departmentDTO.getId() != null) {
            departmentRepository
                .findById(departmentDTO.getId())
                .ifPresent(existing -> {
                    DepartmentDTO existingDto = toDtoWithManagerLogin(existing);
                    if (isBanLanhDaoDto(existingDto)) {
                        throw new BadRequestAlertException("Cannot modify Ban Lãnh Đạo department", "department", "banlanhdaolocked");
                    }
                });
        }

        return departmentRepository
            .findById(departmentDTO.getId())
            .map(existingDepartment -> {
                departmentMapper.partialUpdate(existingDepartment, departmentDTO);
                if (departmentDTO.getManagerId() != null) {
                    existingDepartment.setManager(userRepository.getReferenceById(departmentDTO.getManagerId()));
                } else if (departmentDTO.getManagerId() == null && departmentDTO.getManagerLogin() == null) {
                    existingDepartment.setManager(null);
                }
                return existingDepartment;
            })
            .map(departmentRepository::save)
            .map(this::toDtoWithManagerLogin);
    }

    /**
     * Get all the departments.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<DepartmentDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all Departments");
        return departmentRepository.findAll(pageable).map(this::toDtoWithManagerLogin);
    }

    /**
     * Get all departments with optional status filter.
     */
    @Transactional(readOnly = true)
    public Page<DepartmentDTO> findAll(Pageable pageable, String status) {
        LOG.debug("Request to get all Departments with filters");
        if (status == null || status.isBlank()) {
            return departmentRepository.findAll(pageable).map(this::toDtoWithManagerLogin);
        }
        Specification<Department> spec = (root, query, cb) -> cb.equal(root.get("status"), status);
        return departmentRepository.findAll(spec, pageable).map(this::toDtoWithManagerLogin);
    }

    /**
     * Get one department by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<DepartmentDTO> findOne(Long id) {
        LOG.debug("Request to get Department : {}", id);
        return departmentRepository.findById(id).map(this::toDtoWithManagerLogin);
    }

    /**
     * Delete the department by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete Department : {}", id);
        departmentRepository
            .findById(id)
            .ifPresent(existing -> {
                DepartmentDTO dto = toDtoWithManagerLogin(existing);
                if (isBanLanhDaoDto(dto)) {
                    throw new BadRequestAlertException("Cannot delete Ban Lãnh Đạo department", "department", "banlanhdaolocked");
                }
            });
        departmentRepository.deleteById(id);
    }

    private boolean isBanLanhDao(DepartmentDTO dto) {
        if (dto == null) {
            return false;
        }
        return isBanLanhDaoDto(dto);
    }

    private boolean isBanLanhDaoDto(DepartmentDTO dto) {
        if (dto == null) {
            return false;
        }
        String code = dto.getCode() != null ? dto.getCode().trim().toUpperCase() : "";
        String name = dto.getName() != null ? dto.getName().trim().toLowerCase() : "";
        return "PB003".equals(code) || "ban lãnh đạo".equals(name) || "ban lanh dao".equals(name);
    }
}
