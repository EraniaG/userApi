package com.nisum.userApi.controller;

import com.nisum.userApi.config.JwtUtils;
import com.nisum.userApi.exception.UserApiBussinesException;
import com.nisum.userApi.model.dto.UserDto;
import com.nisum.userApi.model.dto.UserDtoSaved;
import com.nisum.userApi.model.dto.UserLoginDto;
import com.nisum.userApi.model.entity.User;
import com.nisum.userApi.service.UserApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

/**
 * Controller para el inicio de sesión.
 */
@RestController
public class AuthController {

    /**
     * Instancia que nos permite modificar los datos del usuario que inicia sesión, para registrar la última fecha de
     * inicio de sesión así como el token asignado.
     */
    private final UserApiService userApiService;
    /**
     * Instancia que nos brinda métodos para generar y validar los token de los usuarios.
     */
    private final JwtUtils jwtUtils;
    private final AuthenticationManager authenticationManager;

    @Autowired
    public AuthController(final UserApiService userApiService,
                          final JwtUtils jwtUtils,
                          final AuthenticationManager authenticationManagerBuilder) {
        this.userApiService = userApiService;
        this.jwtUtils = jwtUtils;
        this.authenticationManager = authenticationManagerBuilder;
    }

    @GetMapping("/")
    public String welcome() {
        return "Welcome to my first api";
    }

    /**
     * Endpoint para solicitar el inicio de sesión usando email y password.
     * Se usa Spring Security para la validación de los datos y posteriormente se le genera un token al
     * usuario. Dicho token se persistirá en los datos del usuario, y le permitirá acceder a los otros endpoints.
     *
     * @param user tipo UserLoginDto contiene email y password del usuario.
     * @return status 200 si fue exitoso y el token asignado al usuario. Si los datos del usuario son incorrectos,
     * el authenticate de Spring lanzará una excepción.
     * @throws UserApiBussinesException checked.
     */
    @PostMapping(value = "/login", produces = MediaType.APPLICATION_JSON_VALUE)
    private ResponseEntity<String> login(final @RequestBody UserLoginDto user) throws UserApiBussinesException {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            user.getEmail(), user.getPassword()));
        } catch (Exception e) {
            if (e instanceof UserApiBussinesException) {
                throw e;
            } else {
                throw new UserApiBussinesException("email or password incorrect", HttpStatus.BAD_REQUEST);
            }
        }
        String token = jwtUtils.generateToken(user.getEmail());
        UserDto userDto = userApiService.getUserDtoByEmail(user.getEmail());
        userDto.setToken(token);
        userApiService.saveLoginUser(userDto);
        return ResponseEntity.ok("{ \"token\": \"" + token + "\" }");
    }

    @RequestMapping(method = RequestMethod.POST, value = "/init",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserDto> saveUser() throws UserApiBussinesException {
        User user = userApiService.getUserByEmail("useradmin@gmail.com").orElse(null);
        if (user == null) {
            UserDto userAdmin = new UserDto();
            userAdmin.setName("Usuario Administrador");
            userAdmin.setEmail("useradmin@gmail.com");
            userAdmin.setPassword("Useradmin159");
            UserDtoSaved userDtoSaved = userApiService.saveUser(userAdmin);
            user = userApiService.getUserById(userDtoSaved.getId());
        }
        return ResponseEntity.ok(new UserDto(user));
    }
}
