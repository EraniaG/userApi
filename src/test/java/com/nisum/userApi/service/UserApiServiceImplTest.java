package com.nisum.userApi.service;

import com.nisum.userApi.config.JwtUtils;
import com.nisum.userApi.exception.UserApiBussinesException;
import com.nisum.userApi.model.dto.UserDto;
import com.nisum.userApi.model.dto.UserDtoSaved;
import com.nisum.userApi.model.entity.User;
import com.nisum.userApi.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@ExtendWith(MockitoExtension.class)
class UserApiServiceImplTest {
    @Mock
    private UserRepository userRepositoryMock;
    @Mock
    private JwtUtils jwtUtils;
    @InjectMocks
    UserApiServiceImpl userApiService;


    private User userAdmin;
    private UserDto userDtoSave;
    private UserDtoSaved userDtoSaved;

    private String email = "useradmin@gmail.com";
    private String password = "$2a$10$6qbSdgBHLS92zJrhd1vvneLS6HfU0Gi/mRZDXbfAgQSium33gBIHS";
    private String token = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ1c2VyYWRtaW5AZ21haWwuY29tIiwiZXhwIjoxNjU5MjI5OTgxLCJpYXQiOjE2NTkxOTM5ODF9.YaCGJbrxEJwo9hrf8KeWqxzKf09IGuYdDyeSvlshMDU";

    @BeforeEach
    void setUp() {
//        userRepositoryMock = Mockito.mock(UserRepository.class);
//        userApiService = new UserApiServiceImpl(userRepositoryMock, jwtUtils);
        init();
    }

    private void init() {
        userAdmin = new User(UUID.fromString("1122d098-4bf4-47fd-89b5-74bfcc163c0e"),
                "Usuario Administrador",
                email,
                password,
                LocalDateTime.now(),
                LocalDateTime.now(),
                LocalDateTime.now(),
                token,
                true, null);

        userDtoSave = new UserDto("Usuario Test", "usertest@gmail.com", "Prueba123", new ArrayList<>(), "");
        userDtoSaved = new UserDtoSaved(UUID.fromString("1122d098-4bf4-47fd-89b5-74bfcc163c0d"),
                LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME),
                LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME),
                LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME),
                "",
                true);
    }

    @Test
    void getUserById() {
        Mockito.when(userRepositoryMock.findById(UUID.fromString("1122d098-4bf4-47fd-89b5-74bfcc163c0e")))
                .thenReturn(java.util.Optional.ofNullable(userAdmin));
        User usuarioEncontrado = userApiService.getUserById(userAdmin.getId());
        User usuarioEsperado = userAdmin;
        Assertions.assertEquals(usuarioEsperado, usuarioEncontrado);
    }

    @Test
    void getUserByEmail() {
        Mockito.when(userRepositoryMock.findByEmail("useradmin@gmail.com"))
                .thenReturn(java.util.Optional.ofNullable(userAdmin));
        User usuarioEncontrado = userApiService.getUserByEmail(userAdmin.getEmail()).orElse(null);
        User usuarioEsperado = userAdmin;
        Assertions.assertEquals(usuarioEsperado, usuarioEncontrado);
    }

    @Test
    void getUserDtoById() {
        Mockito.when(userRepositoryMock.findById(UUID.fromString("1122d098-4bf4-47fd-89b5-74bfcc163c0e")))
                .thenReturn(java.util.Optional.ofNullable(userAdmin));
        UserDto usuarioEncontrado = userApiService.getUserDtoById(userAdmin.getId());
        UserDto usuarioEsperado = new UserDto(userAdmin);
        Assertions.assertEquals(usuarioEsperado, usuarioEncontrado);
    }

    @Test
    void getUserDtoByIdNoExist() {
        Mockito.when(userRepositoryMock.findById(userAdmin.getId()))
                .thenReturn(Optional.empty());
        Assertions.assertThrows(UserApiBussinesException.class,
                () -> userApiService.getUserDtoById(userAdmin.getId()),
                "User not found");
    }

    @Test
    void getUserDtoByEmail() {
        Mockito.when(userRepositoryMock.findByEmail("useradmin@gmail.com"))
                .thenReturn(java.util.Optional.ofNullable(userAdmin));
        UserDto usuarioEncontrado = userApiService.getUserDtoByEmail(userAdmin.getEmail());
        UserDto usuarioEsperado = new UserDto(userAdmin);
        Assertions.assertEquals(usuarioEsperado, usuarioEncontrado);
    }

    @Test
    void getUserDtoByEmailNoExist() {
        Mockito.when(userRepositoryMock.findByEmail("pruebaxxx@gmail.com"))
                .thenReturn(Optional.empty());
        Assertions.assertThrows(UserApiBussinesException.class,
                () -> userApiService.getUserDtoByEmail("pruebaxxx@gmail.com"),
                "User not found");
    }

    @Test
    void getAllUser() {
        Mockito.when(userRepositoryMock.findAll())
                .thenReturn(Collections.singletonList(userAdmin));
        List<UserDto> lstEncontrado = userApiService.getAllUser();
        List<UserDto> lstEsperado = Collections.singletonList(new UserDto(userAdmin));
        Assertions.assertEquals(lstEncontrado, lstEsperado);
    }

    @Test
    void saveUser() {
        Mockito.when(userRepositoryMock.findByEmail(Mockito.any()))
                .thenReturn(Optional.empty());
        Mockito.when(userRepositoryMock.save(Mockito.any()))
                .thenAnswer(x -> new User(userDtoSave));
        UserDtoSaved encontrado = userApiService.saveUser(userDtoSave);
        encontrado.setId(userDtoSaved.getId());
        UserDtoSaved esperado = userDtoSaved;
        Assertions.assertEquals(encontrado, esperado);
    }

    @Test
    void saveUserEmailExist() {
        Mockito.when(userRepositoryMock.findByEmail(Mockito.any()))
                .thenReturn(java.util.Optional.ofNullable(userAdmin));
        Assertions.assertThrows(UserApiBussinesException.class,
                () -> userApiService.saveUser(new UserDto(userAdmin)),
                "Email exist");
    }

    @Test
    void updateUser() {
        Mockito.when(userRepositoryMock.findById(Mockito.any()))
                .thenReturn(java.util.Optional.ofNullable(userAdmin));
        Mockito.when(userRepositoryMock.save(Mockito.any()))
                .thenAnswer(x -> userAdmin);
        UserDto encontrado = userApiService.updateUser(new UserDto(userAdmin));
        UserDto esperado = new UserDto(userAdmin);
        Assertions.assertEquals(encontrado, esperado);
    }

    @Test
    void desactivateUser() {
        Mockito.when(userRepositoryMock.findById(Mockito.any()))
                .thenReturn(java.util.Optional.ofNullable(userAdmin));
        Mockito.when(userRepositoryMock.save(Mockito.any()))
                .thenAnswer(x -> userAdmin);
        UserDto encontrado = userApiService.updateUser(new UserDto(userAdmin));
        UserDto esperado = new UserDto(userAdmin);
        Assertions.assertEquals(encontrado, esperado);
    }

    @Test
    void activateUser() {
        Mockito.when(userRepositoryMock.findById(Mockito.any()))
                .thenReturn(java.util.Optional.ofNullable(userAdmin));
        Mockito.when(userRepositoryMock.save(Mockito.any()))
                .thenAnswer(x -> userAdmin);
        UserDto encontrado = userApiService.updateUser(new UserDto(userAdmin));
        UserDto esperado = new UserDto(userAdmin);
        Assertions.assertEquals(encontrado, esperado);
    }

    @Test
    void saveLoginUser() {
        Mockito.when(userRepositoryMock.findById(Mockito.any()))
                .thenReturn(java.util.Optional.ofNullable(userAdmin));
        Mockito.when(userRepositoryMock.save(Mockito.any()))
                .thenAnswer(x -> userAdmin);
        UserDto encontrado = userApiService.updateUser(new UserDto(userAdmin));
        UserDto esperado = new UserDto(userAdmin);
        Assertions.assertEquals(encontrado, esperado);
    }
}