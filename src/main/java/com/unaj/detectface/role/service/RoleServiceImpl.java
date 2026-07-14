package com.unaj.detectface.role.service;

import com.unaj.detectface.exception.BadRequestException;
import com.unaj.detectface.exception.ResourceNotFoundException;
import com.unaj.detectface.role.dto.RoleDto;
import com.unaj.detectface.role.entity.Role;
import com.unaj.detectface.role.mapper.RoleMapper;
import com.unaj.detectface.role.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;
    private final RoleMapper roleMapper;

    @Override
    @Transactional(readOnly = true)
    public List<RoleDto> findAll() {
        return roleRepository.findAll().stream()
                .map(roleMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public RoleDto findById(Long id) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Rol no encontrado con ID: " + id));
        return roleMapper.toDto(role);
    }

    @Override
    @Transactional(readOnly = true)
    public RoleDto findByNombre(String nombre) {
        Role role = roleRepository.findByNombre(nombre)
                .orElseThrow(() -> new ResourceNotFoundException("Rol no encontrado con nombre: " + nombre));
        return roleMapper.toDto(role);
    }

    @Override
    public RoleDto create(RoleDto roleDto) {
        if (roleRepository.findByNombre(roleDto.getNombre()).isPresent()) {
            throw new BadRequestException("El rol con nombre '" + roleDto.getNombre() + "' ya existe");
        }
        Role role = roleMapper.toEntity(roleDto);
        Role savedRole = roleRepository.save(role);
        return roleMapper.toDto(savedRole);
    }

    @Override
    public RoleDto update(Long id, RoleDto roleDto) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Rol no encontrado con ID: " + id));
        
        roleRepository.findByNombre(roleDto.getNombre())
                .ifPresent(existing -> {
                    if (!existing.getId().equals(id)) {
                        throw new BadRequestException("El rol con nombre '" + roleDto.getNombre() + "' ya existe");
                    }
                });

        role.setNombre(roleDto.getNombre());
        Role updatedRole = roleRepository.save(role);
        return roleMapper.toDto(updatedRole);
    }

    @Override
    public void delete(Long id) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Rol no encontrado con ID: " + id));
        roleRepository.delete(role);
    }
}
