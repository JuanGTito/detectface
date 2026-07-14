package com.unaj.detectface.user.mapper;

import com.unaj.detectface.user.dto.UserDto;
import com.unaj.detectface.user.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(source = "role.id", target = "roleId")
    @Mapping(source = "role.nombre", target = "roleNombre")
    UserDto toDto(User user);
}
