package tcc.impl.security.controller.dto;

public record LoginResponse(String accessToken, Long expiresIn) {
}
