package com.unaj.detectface.role.service;

import com.unaj.detectface.role.dto.RoleDto;
import java.util.List;

public interface RoleService {
    List<RoleDto> findAll();
    RoleDto findById(Long id);
    RoleDto findByNombre(String nombre);
    RoleDto create(RoleDto roleDto);
    RoleDto update(Long id, RoleDto roleDto);
    void delete(Long id);
}
