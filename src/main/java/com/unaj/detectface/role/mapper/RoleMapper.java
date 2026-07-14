package com.unaj.detectface.role.mapper;

import com.unaj.detectface.role.entity.Role;
import com.unaj.detectface.role.dto.RoleDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface RoleMapper {
    RoleDto toDto(Role role);
    Role toEntity(RoleDto roleDto);
}
