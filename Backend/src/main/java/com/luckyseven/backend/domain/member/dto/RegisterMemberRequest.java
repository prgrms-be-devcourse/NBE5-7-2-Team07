package com.luckyseven.backend.domain.member.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;

//TODO : 잘못된 값 들어올 경우 테스트
@Builder
public record RegisterMemberRequest(

    @NotNull
    @Email
    @Schema(description = "이메일")
    @Pattern(regexp = "^[a-zA-Z0-9]+@[a-zA-Z0-9]+\\.[a-zA-Z0-9]+$")
    String email,
    @NotNull
    @Schema(description = "비밀번호")
    String password,
    @NotNull
    @Schema(description = "비밀번호 확인")
    String checkPassword,
    @NotNull
    @Schema(description = "닉네임")
    String nickname
) {
}
