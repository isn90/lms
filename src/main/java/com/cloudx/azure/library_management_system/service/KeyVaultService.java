/*package com.cloudx.azure.library_management_system.service;

import com.azure.security.keyvault.secrets.SecretClient;
import com.azure.security.keyvault.secrets.models.KeyVaultSecret;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import jakarta.annotation.PostConstruct;

@Service
@Getter
@Setter
public class KeyVaultService {

    @Autowired
    private SecretClient secretClient;

    private String dbUrl;
    private String dbUsername;
    private String dbPassword;

    @PostConstruct
    public void init() {
        this.dbUrl = getSecret("db-url");
        this.dbUsername = getSecret("db-username");
        this.dbPassword = getSecret("db-password");
    }

    private String getSecret(String secretName) {
        KeyVaultSecret secret = secretClient.getSecret(secretName);
        return secret.getValue();
    }
}*/