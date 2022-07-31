package com.nisum.userApi.controller;

import com.nisum.userApi.exception.UserApiBussinesException;
import com.nisum.userApi.model.dto.UserDto;
import com.nisum.userApi.model.dto.UserDtoSaved;
import com.nisum.userApi.service.UserApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping( "/user")
public class UserController {

    private final UserApiService userService;

    @Autowired
    public UserController(UserApiService userService) {
        this.userService = userService;
    }

    @GetMapping(value = "/list",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<UserDto>> getAllUser() {
        return ResponseEntity.ok(userService.getAllUser());
    }

    @RequestMapping(value = "/{prId}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserDto> getUser(final @PathVariable UUID prId) throws UserApiBussinesException {
        return ResponseEntity.ok(userService.getUserDtoById(prId));
    }

    @RequestMapping(method = RequestMethod.POST, value = "/save",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserDtoSaved> saveUser(final @RequestBody UserDto prUserDto) throws UserApiBussinesException {
        return ResponseEntity.ok(userService.saveUser(prUserDto));
    }

    @RequestMapping(method = RequestMethod.PUT, value = "/{prId}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserDto> update(final @PathVariable UUID prId, final @RequestBody UserDto prUserDto)
            throws UserApiBussinesException {
        if (prUserDto.getId() != null && !prId.equals(prUserDto.getId())) {
            throw new UserApiBussinesException("cannot update id", HttpStatus.CONFLICT);
        }
        if (prUserDto.getEmail() != null && !prUserDto.getEmail().equals(prUserDto.getEmail())) {
            throw new UserApiBussinesException("cannot update email", HttpStatus.CONFLICT);
        }
        return ResponseEntity.ok(userService.updateUser(prUserDto));
    }

    @RequestMapping(method = RequestMethod.PUT, value = "/desactivate/{prId}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserDto> activate(final @PathVariable UUID prId) throws UserApiBussinesException {
        return ResponseEntity.ok(userService.desactivateUser(prId));
    }

    @RequestMapping(method = RequestMethod.PUT, value = "/activate/{prId}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserDto> desactivate(final @PathVariable UUID prId) throws UserApiBussinesException {
        return ResponseEntity.ok(userService.activateUser(prId));
    }
}
