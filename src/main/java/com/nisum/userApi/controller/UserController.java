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
@RequestMapping("/user")
public class UserController {

    /**
     * Instancia para acceder a un implementación de UserApiService,
     * que permite el acceso a la capa de negocio y base de datos.
     */
    private final UserApiService userService;

    /**
     * Método constructor para injectar los beans necesarios.
     *
     * @param userService injectar el bean UserApiService.
     */
    @Autowired
    public UserController(final UserApiService userService) {
        this.userService = userService;
    }

    /**
     * Método de tipo GET, que permite obtener el catálogo de los usuarios.
     *
     * @return JSON con el catálogo de los usuarios
     */
    @GetMapping(value = "/list",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<UserDto>> getAllUser() {
        return ResponseEntity.ok(userService.getAllUser());
    }

    /**
     * Metodo de tipo GET, permite obtener los datos de un usuario específico a partir de su id.
     * Retorna un objeto JSON.
     *
     * @param prId parámetro de tipo Path, objeto de tipo UUID con el id del usuario a buscar.
     * @return si encuentra al usuario retornará un objeto de tipo UserDto con los datos del usuario encontrado,
     * sino retornará una excepción de tipo UserApiBussinesException indicando que no se encontró al usuario.
     * @throws UserApiBussinesException checked, será manejada por la clase HandlerExceptionCustom.
     */
    @GetMapping(value = "/{prId}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserDto> getUser(final @PathVariable UUID prId) throws UserApiBussinesException {
        return ResponseEntity.ok(userService.getUserDtoById(prId));
    }

    /**
     * Método de tipo POST que permite registrar un nuevo usuario.
     * Espera un objeto JSON y retorna un objeto JSON.
     *
     * @param prUserDto parámetro de tipo Body, objeto dto con los datos basicos a registrar para un
     *                  nuevo usuario (name, email, password, phones).
     * @return objeto de tipo UserDtoSaved que contiene solo las propiedades que se desean retornar el nuevo usuario.
     * @throws UserApiBussinesException checked, esta será manejada por la clase HandlerExceptionCustom.
     */
    @RequestMapping(method = RequestMethod.POST, value = "/save",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserDtoSaved> saveUser(final @RequestBody UserDto prUserDto) throws UserApiBussinesException {
        return ResponseEntity.ok(userService.saveUser(prUserDto));
    }

    /**
     * Método de tipo PUT que permite actualizar los datos de usuario.
     * Espera un objeto JSON y retorna un objeto JSON.
     *
     * @param prId      parámetro de tipo Path, objeto de tipo UUID con el id del usuario a buscar.
     * @param prUserDto parámetro de tipo Body, objeto dto con los datos basicos a actualizar (name, password, phones).
     * @return objeto de tipo UserDto que contiene los datos del usuario ya actualizado.
     * @throws UserApiBussinesException checked, esta será manejada por la clase HandlerExceptionCustom.
     */
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

    /**
     * Método de tipo PUT que permite desactivar a un usuario específico.
     * Retorna un objeto JSON.
     *
     * @param prId parámetro de tipo Path, objeto de tipo UUID con el id del usuario a buscar.
     * @return objeto de tipo UserDto que contiene los datos del usuario ya actualizado.
     * @throws UserApiBussinesException checked, esta será manejada por la clase HandlerExceptionCustom.
     */
    @RequestMapping(method = RequestMethod.PUT, value = "/desactivate/{prId}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserDto> activate(final @PathVariable UUID prId) throws UserApiBussinesException {
        return ResponseEntity.ok(userService.desactivateUser(prId));
    }

    /**
     * Método de tipo PUT que permite activar a un usuario específico.
     * Retorna un objeto JSON.
     *
     * @param prId parámetro de tipo Path, objeto de tipo UUID con el id del usuario a buscar.
     * @return objeto de tipo UserDto que contiene los datos del usuario ya actualizado.
     * @throws UserApiBussinesException checked, esta será manejada por la clase HandlerExceptionCustom.
     */
    @RequestMapping(method = RequestMethod.PUT, value = "/activate/{prId}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserDto> desactivate(final @PathVariable UUID prId) throws UserApiBussinesException {
        return ResponseEntity.ok(userService.activateUser(prId));
    }
}
