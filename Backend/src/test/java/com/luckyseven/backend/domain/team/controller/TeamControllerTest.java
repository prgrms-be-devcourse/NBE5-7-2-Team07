//package com.luckyseven.backend.domain.team.controller;
//
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.luckyseven.backend.domain.budget.entity.CurrencyCode;
//import static com.luckyseven.backend.domain.expense.enums.ExpenseCategory.MEAL;
//import com.luckyseven.backend.domain.member.entity.Member;
//import com.luckyseven.backend.domain.member.service.utill.MemberDetails;
//import com.luckyseven.backend.domain.team.dto.TeamCreateRequest;
//import com.luckyseven.backend.domain.team.dto.TeamCreateResponse;
//import com.luckyseven.backend.domain.team.dto.TeamDashboardResponse;
//import com.luckyseven.backend.domain.team.dto.TeamDashboardResponse.ExpenseDto;
//import com.luckyseven.backend.domain.team.dto.TeamJoinRequest;
//import com.luckyseven.backend.domain.team.dto.TeamJoinResponse;
//import com.luckyseven.backend.domain.team.dto.TeamMemberDto;
//import com.luckyseven.backend.domain.team.service.TeamMemberService;
//import com.luckyseven.backend.domain.team.service.TeamService;
//import com.luckyseven.backend.domain.team.service.TempMemberService;
//import com.luckyseven.backend.sharedkernel.jwt.utill.JwtTokenizer;
//import java.math.BigDecimal;
//import java.time.LocalDateTime;
//import java.util.ArrayList;
//import java.util.List;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.ArgumentMatchers.anyLong;
//import static org.mockito.ArgumentMatchers.anyString;
//import static org.mockito.Mockito.doNothing;
//import static org.mockito.Mockito.when;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.context.annotation.Import;
//import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
//import org.springframework.http.MediaType;
//import org.springframework.security.test.context.support.WithMockUser;
//import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
//import org.springframework.test.context.bean.override.mockito.MockitoBean;
//import org.springframework.test.web.servlet.MockMvc;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//@WebMvcTest(TeamController.class)
//@Import(JwtTokenizer.class) // JwtTokenizer 빈을 명시적으로 가져옵니다
//class TeamControllerTest {
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    @Autowired
//    private ObjectMapper objectMapper;
//
//    @MockitoBean
//    private TeamService teamService;
//
//    @MockitoBean
//    private TempMemberService tempMemberService;
//
//    @MockitoBean
//    private TeamMemberService teamMemberService;
//
//    @MockitoBean
//    private JpaMetamodelMappingContext jpaMetamodelMappingContext;
//
//
//    private TeamCreateRequest teamCreateRequest;
//    private TeamCreateResponse teamCreateResponse;
//    private TeamJoinRequest teamJoinRequest;
//    private TeamJoinResponse teamJoinResponse;
//    private List<TeamMemberDto> teamMemberDtoList;
//    private TeamDashboardResponse teamDashboardResponse;
//
//    private Member mockMember;
//    private Member payer;
//
//    @BeforeEach
//    void setUp() {
//        // 테스트용 Member 객체 생성
//        mockMember = mockMember.builder()
//            .id(1L)
//            .email("test@example.com")
//            .nickname("테스트사용자")
//            .build();
//        payer = mockMember.builder()
//            .id(2L)
//            .email("test2@example.com")
//            .nickname("테스트사용자2")
//            .build();
//        // TeamCreateRequest 생성
//        teamCreateRequest = TeamCreateRequest.builder()
//            .name("테스트팀")
//            .teamPassword("test123")
//            .build();
//
//        // TeamCreateResponse 생성
//        teamCreateResponse = TeamCreateResponse.builder()
//            .id(1L)
//            .name("테스트팀")
//            .teamCode("ABCD1234")
//            .leaderId(1L)
//            .build();
//
//        // TeamJoinRequest 생성
//        teamJoinRequest = TeamJoinRequest.builder()
//            .teamCode("ABCD1234")
//            .teamPassword("test123")
//            .build();
//
//        // TeamJoinResponse 생성
//        teamJoinResponse = TeamJoinResponse.builder()
//            .id(1L)
//            .teamName("테스트팀")
//            .teamCode("ABCD1234")
//            .leaderId(1L)
//            .build();
//
//        // TeamMemberDto 리스트 생성
//        teamMemberDtoList = new ArrayList<>();
//        teamMemberDtoList.add(TeamMemberDto.builder()
//            .id(1L)
//            .teamId(1L)
//            .teamName("테스트팀")
//            .memberId(1L)
//            .memberNickName("테스트사용자")
//            .memberEmail("test@example.com")
//            .build());
//
//        // TeamDashboardResponse 생성
//        List<ExpenseDto> expenseList = new ArrayList<>();
//        expenseList.add(ExpenseDto.builder()
//            .description("테스트 지출")
//            .amount(BigDecimal.valueOf(10000))
//            .category(MEAL)
//            .date(LocalDateTime.now())
//            .payerNickname(payer.getNickname())
//            .build());
//
//        teamDashboardResponse = TeamDashboardResponse.builder()
//            .team_id(1L)
//            .foreignCurrency(CurrencyCode.USD)
//            .balance(new BigDecimal("900000.00"))
//            .foreignBalance(new BigDecimal("100.00"))
//            .totalAmount(new BigDecimal("1000000.00"))
//            .avgExchangeRate(new BigDecimal("1250.00"))
//            .expenseList(expenseList)
//            .build();
//
//        // 현재 로그인한 사용자 모킹
//        when(tempMemberService.getCurrentMember()).thenReturn(mockMember);
//    }
//
//
//    @Test
//    @DisplayName("팀 생성 테스트")
//    @WithMockUser
//    void createTeamTest() throws Exception {
//        // given
//        when(teamService.createTeam(any(MemberDetails.class), any(TeamCreateRequest.class)))
//            .thenReturn(teamCreateResponse);
//
//        // when & then
//        mockMvc.perform(post("/api/teams")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(objectMapper.writeValueAsString(teamCreateRequest))
//                .with(SecurityMockMvcRequestPostProcessors.csrf()))
//            .andExpect(status().isOk());
//    }
//
//    @Test
//    @DisplayName("팀 참가 테스트")
//    @WithMockUser
//    void joinTeamTest() throws Exception {
//        // given
//        // 메서드 시그니처 수정 (TeamService의 실제 메서드에 맞게)
//        when(teamService.joinTeam(any(MemberDetails.class), anyString(), anyString()))
//            .thenReturn(teamJoinResponse);
//
//        // when & then
//        mockMvc.perform(post("/api/teams/members")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(objectMapper.writeValueAsString(teamJoinRequest))
//                .with(SecurityMockMvcRequestPostProcessors.csrf()))
//            .andExpect(status().isOk());
//    }
//
//    @Test
//    @DisplayName("팀 멤버 조회 테스트")
//    @WithMockUser
//    void getTeamMembersTest() throws Exception {
//        // given
//        when(teamMemberService.getTeamMemberByTeamId(anyLong()))
//            .thenReturn(teamMemberDtoList);
//
//
//        // when & then
//        mockMvc.perform(get("/api/teams/1/members")
//                .with(SecurityMockMvcRequestPostProcessors.csrf()))
//            .andExpect(status().isOk());
//    }
//
//    @Test
//    @DisplayName("팀 멤버 삭제 테스트")
//    @WithMockUser
//    void removeTeamMemberTest() throws Exception {
//        // given
//        doNothing().when(teamMemberService).removeTeamMember(any(MemberDetails.class),anyLong(), anyLong());
//
//        // when & then
//        mockMvc.perform(delete("/api/teams/1/members/2")
//                .with(SecurityMockMvcRequestPostProcessors.csrf()))
//            .andExpect(status().isNoContent());
//    }
//
//    @Test
//    @DisplayName("팀 대시보드 조회 테스트")
//    @WithMockUser
//    void getTeamDashboardTest() throws Exception {
//        // given
//        when(teamService.getTeamDashboard(anyLong()))
//            .thenReturn(teamDashboardResponse);
//
//        // when & then
//        mockMvc.perform(get("/api/teams/1/dashboard")
//                .with(SecurityMockMvcRequestPostProcessors.csrf()))
//            .andExpect(status().isOk());
//    }
//}