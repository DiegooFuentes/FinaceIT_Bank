package com.financeit.web.service;

import com.financeit.web.dtos.ClientDTO;
import org.springframework.security.core.Authentication;

import java.util.List;

public interface ClientService {

    public List<ClientDTO> getClients();

    public ClientDTO getClient(Long id);

    public ClientDTO getCurrentClient(Authentication authentication);

    public String register(String firstName, String lastName,String email, String password);
}
