package com.luckyseven.backend.domain.team.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class TeamCreateRequest {

  @NotBlank(message = "팀 이름은 필수입니다")
  @Pattern(regexp = "^[가-힣a-zA-Z0-9\\s]*$", message = "팀 이름은 한글, 영문, 숫자만 사용 가능합니다")
  private String name;

  @NotBlank(message = "팀 비밀번호는 필수입니다")
  @Size(min = 6, max = 20, message = "팀 비밀번호는 6자 이상 20자 이하여야 합니다")
  @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d@$!%*#?&]{6,}$",
      message = "비밀번호는 최소 하나의 문자와 숫자를 포함해야 합니다")

  private String teamPassword;
}
