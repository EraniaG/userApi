package com.nisum.userApi.service;

import com.nisum.userApi.exception.UserApiBussinesException;
import com.nisum.userApi.model.dto.UserDto;
import com.nisum.userApi.model.dto.UserDtoSaved;
import com.nisum.userApi.model.entity.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserApiService {

    List<UserDto> getAllUser();

    User getUserById(final UUID prId) throws UserApiBussinesException;

    Optional<User> getUserByEmail(final String email) throws UserApiBussinesException;

    UserDto getUserDtoById(final UUID prId) throws UserApiBussinesException;

    UserDto getUserDtoByEmail(final String email) throws UserApiBussinesException;

    UserDtoSaved saveUser(final UserDto prNewUser) throws UserApiBussinesException;

    UserDto updateUser(final UserDto prUser) throws UserApiBussinesException;

    UserDto desactivateUser(final UUID prId) throws UserApiBussinesException;

    UserDto activateUser(final UUID prId) throws UserApiBussinesException;

    void saveLoginUser(final UserDto prUserUpdate) throws UserApiBussinesException;
}
