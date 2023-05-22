package com.financeit.web.configurations;

import com.financeit.web.models.Client;
import com.financeit.web.repositories.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.GlobalAuthenticationConfigurerAdapter;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class WebAuthentication extends GlobalAuthenticationConfigurerAdapter {

    @Autowired
    ClientRepository clientRepository;

    //init: It overrides the default init, we set it to use Client and to find by a given email
    @Override
    public void init(AuthenticationManagerBuilder auth) throws  Exception{

        auth.userDetailsService(inputClient->{
            Client client = clientRepository.findByEmail(inputClient);
            if(client.getEmail().equals("admin@admin.com")){
                return new User(client.getEmail(),client.getPassword(),
                        AuthorityUtils.createAuthorityList("ADMIN"));
            }
            if(client != null){
                return new User(client.getEmail(),client.getPassword(),
                        AuthorityUtils.createAuthorityList("CLIENT"));
            }else{
                throw new UsernameNotFoundException("Unknown client: " + inputClient);
            }
        });
    }

    //Password encoder: this encode a given password
    @Bean
    public PasswordEncoder passwordEncoder(){
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

}
