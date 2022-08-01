package com.nisum.userApi.service;

import com.nisum.userApi.config.JwtUtils;
import com.nisum.userApi.exception.UserApiBussinesException;
import com.nisum.userApi.model.dto.UserDto;
import com.nisum.userApi.model.dto.UserDtoSaved;
import com.nisum.userApi.model.entity.Phone;
import com.nisum.userApi.model.entity.User;
import com.nisum.userApi.repository.UserRepository;
import com.nisum.userApi.util.RegExpression;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class UserApiServiceImpl implements UserApiService {

    /**
     * Instancia para acceso a los datos de los usuarios.
     */
    private final UserRepository userRepository;
    /**
     * Instancia de una clase utilitaria que nos permite crear y validar token para los usuarios. En este caso se usa
     * para generar y persistir en la base de datos el token asignado al usuario.
     */
    private final JwtUtils jwtUtils;
    /**
     * Instancia que nos permite encriptar el password que se guardará en la base de datos.
     * Este debe ser del mismo tipo de clase de la instacia de encoder que pasemos a la clase WebApiSecurity porque
     * será la que se le pase al UserDetailService para verificar las credenciales del usuario en el proceso de
     * Authenticación usando las propiedades de spring security.
     *
     * @see com.nisum.userApi.config.WebApiSecurity
     */
    private final PasswordEncoder passwordEncoder;

    /**
     * Constructor para injectar o inicializar todas las instancias que necesitamos para el funcionamiento de todos los
     * métodos usados.
     *
     * @param userRepository injecta el bean UserRepository.
     * @param jwtUtils       injecta el bean JwtUtils.
     */
    @Autowired
    public UserApiServiceImpl(final UserRepository userRepository, final JwtUtils jwtUtils) {
        this.userRepository = userRepository;
        this.jwtUtils = jwtUtils;
        //Se crea una nueva instancia de la implementación BCryptPasswordEncoder
        passwordEncoder = new BCryptPasswordEncoder();
    }

    /**
     * Método para obtener el usuario que corresponde a un id específico.
     *
     * @param prId de tipo UUID, contiene el id a buscar en la base de datos.
     * @return un objeto de tipo User si encuentra el id proporcionado, sino retorna una excepción de tipo
     * UserApiBussinesException indicando que no encontró el usuario.
     * @throws UserApiBussinesException checked, esta será manejada por la clase HandlerExceptionCustom la cual
     *                                  retornará un objeto de tipo ErrorDto.
     * @see com.nisum.userApi.exception.HandlerExceptionCustom
     * @see com.nisum.userApi.exception.UserApiBussinesException
     * @see com.nisum.userApi.model.dto.ErrorDto
     * @see User
     */
    public User getUserById(final UUID prId) throws UserApiBussinesException {
        return userRepository.findById(prId).orElseThrow(
                () -> new UserApiBussinesException("User not found", HttpStatus.NOT_FOUND));
    }

    /**
     * Método para buscar el usuario que corresponde a un id específico y retornar sus datos en ub objeto dto.
     *
     * @param prId de tipo UUID, contiene el id a buscar en la base de datos.
     * @return un objeto de tipo UserDto si encuentra el id proporcionado, sino retorna una excepción de tipo
     * UserApiBussinesException indicando que no encontró el usuario.
     * @throws UserApiBussinesException checked, esta será manejada por la clase HandlerExceptionCustom la cual
     *                                  retornará un objeto de tipo ErrorDto.
     * @see com.nisum.userApi.exception.HandlerExceptionCustom
     * @see com.nisum.userApi.exception.UserApiBussinesException
     * @see com.nisum.userApi.model.dto.ErrorDto
     * @see com.nisum.userApi.model.dto.UserDto
     * @see User
     */
    public UserDto getUserDtoById(final UUID prId) throws UserApiBussinesException {
        return new UserDto(this.getUserById(prId));
    }

    /**
     * Método para obtener el usuario que corresponde a un email específico.
     * Si no encuentra al usuario, no retornará una excepción.
     *
     * @param prEmail de tipo String, contiene el email a buscar en la base de datos.
     * @return un objeto de tipo User si encuentra el email proporcionado, sino retorna Optional.empty().
     * @see User
     */
    public Optional<User> getUserByEmail(final String prEmail) {
        return userRepository.findByEmail(prEmail);
    }

    /**
     * Método para obtener el usuario que corresponde a un email específico.
     * Si no encuentra al usuario, deberá retonar una excepción.
     *
     * @param prEmail de tipo String, contiene el email a buscar en la base de datos.
     * @return un objeto de tipo UserDto si encuentra el email proporcionado, sino retorna una excepción de tipo
     * UserApiBussinesException indicando que no encontró el usuario.
     * @throws UserApiBussinesException checked, esta será manejada por la clase HandlerExceptionCustom la cual
     *                                  retornará un objeto de tipo ErrorDto.
     * @see com.nisum.userApi.exception.HandlerExceptionCustom
     * @see com.nisum.userApi.exception.UserApiBussinesException
     * @see com.nisum.userApi.model.dto.ErrorDto
     * @see com.nisum.userApi.model.dto.UserDto
     * @see User
     */
    public UserDto getUserDtoByEmail(final String prEmail) throws UserApiBussinesException {
        User user = this.getUserByEmail(prEmail).orElseThrow(
                () -> new UserApiBussinesException("User not found", HttpStatus.NOT_FOUND));
        return new UserDto(user);
    }

    /**
     * Método para obtener el catálogo completo de los usuarios.
     *
     * @return lista con objetor de tipo UserDto con los datos de los usuarios registrados en la base de datos.
     * @see com.nisum.userApi.model.dto.UserDto
     */
    public List<UserDto> getAllUser() {
        List<UserDto> lstUsers = new ArrayList<>();
        userRepository.findAll().forEach(x -> lstUsers.add(new UserDto(x)));
        return lstUsers;
    }

    /**
     * Método para registrar un nuevo usuario.
     * Si el email ya existe, debe retornar una excepción de tipo UserApiBussinesException indicando que ya existe.
     * Debe encriptar el password.
     * Debe generar un token y guardarlo en el registro del usuario.
     *
     * @param prUserDto dto con los datos basicos a registrar para un nuevo usuario (name, email, password, phones).
     * @return objeto de tipo UserDtoSaved que contiene solo las propiedades que se desean retornar el nuevo usuario.
     * @throws UserApiBussinesException checked, esta será manejada por la clase HandlerExceptionCustom la cual
     *                                  retornará un objeto de tipo ErrorDto.
     * @see com.nisum.userApi.exception.HandlerExceptionCustom
     * @see com.nisum.userApi.exception.UserApiBussinesException
     * @see com.nisum.userApi.model.dto.ErrorDto
     * @see com.nisum.userApi.model.dto.UserDto
     * @see com.nisum.userApi.model.dto.UserDtoSaved
     */
    public UserDtoSaved saveUser(final UserDto prUserDto) throws UserApiBussinesException {
        try {
            User prNewUser = new User(prUserDto);
            validUser(prNewUser);
            if (userRepository.findByEmail(prNewUser.getEmail()).isPresent()) {
                throw new UserApiBussinesException("Email exist", HttpStatus.FOUND);
            }
            prNewUser.setPassword(passwordEncoder.encode(prNewUser.getPassword()));
            prNewUser.setCreated(LocalDateTime.now());
            prNewUser.setIsActive(true);
            prNewUser.setToken(jwtUtils.generateToken(prNewUser.getEmail()));
            userRepository.save(prNewUser);
            return new UserDtoSaved(prNewUser);
        } catch (Exception e) {
            if (e instanceof UserApiBussinesException) {
                throw e;
            } else {
                throw new UserApiBussinesException(e.getMessage(), HttpStatus.BAD_REQUEST);
            }
        }
    }

    /**
     * Método para actualizar los datos de un usuario específico, solamente se pueden actualizar los campos:
     * name, password, phones.
     *
     * @param prUserUpdate objeto de tipo dto con los datos a actualizar al objeto User.
     * @return objeto de tipo UserDto con los datos del usuario ya actualizado.
     * @throws UserApiBussinesException checked, se lanzará si el id proporcionado no se encuentra en la base de
     *                                  datos o cuando se intente modificar el campo email de usuario.
     *                                  Esta será manejada por la clase HandlerExceptionCustom la cual
     *                                  retornará un objeto de tipo ErrorDto.
     * @see com.nisum.userApi.exception.HandlerExceptionCustom
     * @see com.nisum.userApi.exception.UserApiBussinesException
     * @see com.nisum.userApi.model.dto.ErrorDto
     * @see com.nisum.userApi.model.dto.UserDto
     */
    public UserDto updateUser(final UserDto prUserUpdate) throws UserApiBussinesException {
        User user = this.getUserById(prUserUpdate.getId());

        if (user.getPhones() == null) {
            user.setPhones(null);
        }else{
            Set<Phone> phones = new LinkedHashSet<>();
            prUserUpdate.getPhones().forEach(x -> phones.add(new Phone(user, x)));
            user.setPhones(phones);
        }

        if (prUserUpdate.getEmail() != null && !user.getEmail().equals(prUserUpdate.getEmail())) {
            throw new UserApiBussinesException("cannot update email", HttpStatus.CONFLICT);
        }

        user.setName(prUserUpdate.getName());

        if (prUserUpdate.getPassword() != null) {
            String passNew = passwordEncoder.encode(prUserUpdate.getPassword());
            if (!BCrypt.checkpw(passNew, user.getPassword())) {
                user.setPassword(passNew);
            }
        }
        user.setModified(LocalDateTime.now());
        validUser(user);
        userRepository.save(user);
        return new UserDto(user);
    }

    /**
     * Método para guardar datos indicando que un usuario inició sesión.
     * Se guardará el token asignado y la fecha y hora en que inició sesión.
     *
     * @param prUserUpdate objeto de tipo dto con los datos a actualizar al objeto User.
     * @throws UserApiBussinesException checked, se lanzará si el id proporcionado no se encuentra en la base de datos.
     *                                  Esta será manejada por la clase HandlerExceptionCustom la cual retornará
     *                                  un objeto de tipo ErrorDto.
     * @see com.nisum.userApi.exception.HandlerExceptionCustom
     * @see com.nisum.userApi.exception.UserApiBussinesException
     * @see com.nisum.userApi.model.dto.ErrorDto
     * @see com.nisum.userApi.model.dto.UserDto
     */
    public void saveLoginUser(final UserDto prUserUpdate) throws UserApiBussinesException {
        User user = this.getUserById(prUserUpdate.getId());
        user.setToken(prUserUpdate.getToken());
        user.setLastLogin(LocalDateTime.now());
        user.setModified(LocalDateTime.now());
        userRepository.save(user);
    }

    /**
     * Método para desactivar a un usuario a partir de un id.
     *
     * @param prId de tipo UUID, contiene el id a buscar en la base de datos.
     * @return objeto de tipo UserDto con los datos del usuario ya actualizado.
     * @throws UserApiBussinesException checked,se lanzará si el id proporcionado no se encuentra en la base de datos.
     *                                  Esta será manejada por la clase HandlerExceptionCustom la cual retornará
     *                                  un objeto de tipo ErrorDto.
     * @see com.nisum.userApi.exception.HandlerExceptionCustom
     * @see com.nisum.userApi.exception.UserApiBussinesException
     * @see com.nisum.userApi.model.dto.ErrorDto
     * @see com.nisum.userApi.model.dto.UserDto
     */
    @Override
    public UserDto desactivateUser(UUID prId) throws UserApiBussinesException {
        User user = this.getUserById(prId);
        user.setIsActive(false);
        user.setModified(LocalDateTime.now());
        userRepository.save(user);
        return new UserDto(user);
    }

    /**
     * Método para activar a un usuario a partir de un id.
     *
     * @param prId de tipo UUID, contiene el id a buscar en la base de datos.
     * @return objeto de tipo UserDto con los datos del usuario ya actualizado.
     * @throws UserApiBussinesException checked,se lanzará si el id proporcionado no se encuentra en la base de datos.
     *                                  Esta será manejada por la clase HandlerExceptionCustom la cual retornará
     *                                  un objeto de tipo ErrorDto.
     * @see com.nisum.userApi.exception.HandlerExceptionCustom
     * @see com.nisum.userApi.exception.UserApiBussinesException
     * @see com.nisum.userApi.model.dto.ErrorDto
     * @see com.nisum.userApi.model.dto.UserDto
     */
    @Override
    public UserDto activateUser(UUID prId) throws UserApiBussinesException {
        User user = this.getUserById(prId);
        user.setIsActive(true);
        user.setModified(LocalDateTime.now());
        userRepository.save(user);
        return new UserDto(user);
    }

    /**
     * Método privado de la implemetanción que permite validar los datos de un usuario a registrar o de un
     * usuario a modificar.
     *
     * @param prUser objeto de tipo User que contiene los datos del usuario a enviar a la base de datos.
     * @throws UserApiBussinesException checked, retornará los mensajes de las validaciones que no se cumplan.
     *                                  Esta será manejada por la clase HandlerExceptionCustom la cual
     *                                  retornará un objeto de tipo ErrorDto.
     * @see com.nisum.userApi.exception.HandlerExceptionCustom
     * @see com.nisum.userApi.exception.UserApiBussinesException
     * @see com.nisum.userApi.model.dto.ErrorDto
     */
    private void validUser(final User prUser) throws UserApiBussinesException {
        if (prUser == null) {
            throw new UserApiBussinesException("Error getting user data", HttpStatus.PARTIAL_CONTENT);
        }
        if (prUser.getName() == null || prUser.getName().trim().isEmpty()) {
            throw new UserApiBussinesException("name: required", HttpStatus.PARTIAL_CONTENT);
        }
        if (prUser.getEmail() == null || prUser.getEmail().trim().isEmpty()) {
            throw new UserApiBussinesException("email: required", HttpStatus.PARTIAL_CONTENT);
        } else if (!validReg(prUser.getEmail(), RegExpression.regExpEmail)) {
            throw new UserApiBussinesException("email format: invalid, required format example aaaaaaa@dominio.cl",
                    HttpStatus.BAD_REQUEST);
        }
        if (prUser.getPassword() == null || prUser.getPassword().trim().isEmpty()) {
            throw new UserApiBussinesException("password: required", HttpStatus.PARTIAL_CONTENT);
        } else if (!validReg(prUser.getPassword(), RegExpression.regExpPassword)) {
            throw new UserApiBussinesException("password format: invalid, "
                    + "required minimum 1 uppercase letter, lowercase letters and minimum 2 numbers",
                    HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Método para validar expresiones regulares, espera como parámetro el string a evaluar y la expresión regular
     * a verificar.
     *
     * @param value      String a validar.
     * @param expression String con la expresión regular a aplicar.
     * @return true si el string a validar cumple los requisitos de la expresión regular,
     * de lo contrario, retornará false.
     */
    private Boolean validReg(final String value, final String expression) {
        Pattern pat = Pattern.compile(expression);
        Matcher mat = pat.matcher(value);
        return mat.find();
    }
}
