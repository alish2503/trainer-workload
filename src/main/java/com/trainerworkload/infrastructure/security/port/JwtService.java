package com.trainerworkload.infrastructure.security.port;

import java.util.List;

public interface JwtService {
    boolean isValidToken(String token);
    List<String> getRoles(String token);
}
