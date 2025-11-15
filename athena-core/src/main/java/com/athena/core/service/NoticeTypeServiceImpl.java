package com.athena.core.service;

import com.athena.core.dto.NoticeTypeCreateDTO;
import com.athena.core.dto.NoticeTypeResponseDTO;
import com.athena.core.dto.NoticeTypeUpdateDTO;
import com.athena.core.entity.NoticeType;
import com.athena.core.exception.DuplicateEntityException;
import com.athena.core.exception.EntityNotFoundException;
import com.athena.core.repository.NoticeTypeRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

/**
 * Implementation of NoticeTypeService.
 */
@Service
@Transactional(readOnly = true)
public class NoticeTypeServiceImpl implements NoticeTypeService {

    private final NoticeTypeRepository noticeTypeRepository;

    public NoticeTypeServiceImpl(NoticeTypeRepository noticeTypeRepository) {
        this.noticeTypeRepository = noticeTypeRepository;
    }

    @Override
    @Transactional
    public NoticeTypeResponseDTO create(NoticeTypeCreateDTO dto) {
        // Check for duplicates
        if (noticeTypeRepository.existsByCode(dto.code())) {
            throw new DuplicateEntityException("NoticeType", "code", dto.code());
        }

        // Create notice type entity
        NoticeType noticeType = new NoticeType(dto.code(), dto.name());
        noticeType.setDescription(dto.description());
        noticeType.setCategory(dto.category());

        // Save and return
        NoticeType savedNoticeType = noticeTypeRepository.save(noticeType);
        return NoticeTypeResponseDTO.fromEntity(savedNoticeType);
    }

    @Override
    public Optional<NoticeTypeResponseDTO> findById(UUID id) {
        return noticeTypeRepository.findById(id)
            .map(NoticeTypeResponseDTO::fromEntity);
    }

    @Override
    public Page<NoticeTypeResponseDTO> findAll(Pageable pageable) {
        return noticeTypeRepository.findAll(pageable)
            .map(NoticeTypeResponseDTO::fromEntity);
    }

    @Override
    @Transactional
    public NoticeTypeResponseDTO update(UUID id, NoticeTypeUpdateDTO dto) {
        NoticeType noticeType = noticeTypeRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("NoticeType", id));

        // Update fields if provided
        if (dto.name() != null) {
            noticeType.setName(dto.name());
        }

        if (dto.description() != null) {
            noticeType.setDescription(dto.description());
        }

        if (dto.category() != null) {
            noticeType.setCategory(dto.category());
        }

        if (dto.isActive() != null) {
            noticeType.setIsActive(dto.isActive());
        }

        NoticeType updatedNoticeType = noticeTypeRepository.save(noticeType);
        return NoticeTypeResponseDTO.fromEntity(updatedNoticeType);
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        NoticeType noticeType = noticeTypeRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("NoticeType", id));

        // Soft delete
        noticeType.setIsActive(false);
        noticeTypeRepository.save(noticeType);
    }

    @Override
    public Optional<NoticeTypeResponseDTO> findByCode(String code) {
        return noticeTypeRepository.findByCode(code)
            .map(NoticeTypeResponseDTO::fromEntity);
    }

    @Override
    public boolean existsByCode(String code) {
        return noticeTypeRepository.existsByCode(code);
    }
}
