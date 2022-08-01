package com.nisum.userApi.config;

import com.nisum.userApi.service.UserApiDetailsSecurityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.BeanIds;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Clase que nos permite acceder a la capa de seguridad de spring, y nos permite customizar ciertos comportamientos.
 */
@Configuration
@EnableWebSecurity
public class WebApiSecurity extends WebSecurityConfigurerAdapter {

    /**
     * Instancia de la implementación customizada de UserDetailService, para que verifique los datos de authenticación
     * en la base de datos de la app.
     */
    @Autowired
    private UserApiDetailsSecurityService userApiDetailsSecurityService;

    /**
     * Instancia del Filtro customizado que tenemos para trabajar con token JWT.
     */
    @Autowired
    private JwtFilter jwtFilter;

    /**
     * Instancia para controlar la excepción de nuestro filtro.
     */
    @Autowired
    private JwtAuthenticationEntryPoint unauthorizedHandler;

    /**
     * Instancia del encoder a aplicar a la contraseña recibida a ser comparada con la contraseña del usuario en la
     * base de datos.
     *
     * @return implementación BCryptPasswordEncoder, esta debe coincidir con el encoder que se usa para guardar la
     * contraseña encriptada en la base de datos.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Indicamos a spring security los paths a los que se puede acceder publicamente, los path que requieres
     * authenticación, el filtro de token customizado que queremos aplicar, y el controlador de la
     * excepción de nuestro filtro para manejar la excepción de no autorización.
     *
     * @param http .
     * @throws Exception unchecked.
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable().httpBasic().and()
                .authorizeRequests()
                .antMatchers("/", "/login", "/init").permitAll()
                .antMatchers("/admin/**").hasRole("ADMIN")
                .anyRequest().authenticated()
                .and()
                .exceptionHandling().authenticationEntryPoint(unauthorizedHandler).and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
    }

    /**
     * Le asignamos al AuthenticationManager nuestra implementación customizada de UserDetailService para que el
     * manager use esta implementación para validar si los datos de authenticación del usuario son correctos.
     *
     * @param auth .
     * @throws Exception unchecked.
     */
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(this.userApiDetailsSecurityService).passwordEncoder(passwordEncoder());
    }

    /**
     * Injectamos el bean de AuthenticationManager para que nuestro controllador lo pueda usar.
     *
     * @return el bean creado.
     * @throws Exception unchecked.
     */
    @Bean(name = BeanIds.AUTHENTICATION_MANAGER)
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }
}
