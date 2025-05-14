package com.luckyseven.backend.sharedkernel.jwt.utill;

import com.luckyseven.backend.domain.member.service.CustomMemberDetailsService;
import com.luckyseven.backend.domain.member.service.utill.memberDetails;
import com.luckyseven.backend.sharedkernel.exception.CustomLogicException;
import com.luckyseven.backend.sharedkernel.exception.ExceptionCode;
import com.luckyseven.backend.sharedkernel.jwt.entity.BlackListToken;
import com.luckyseven.backend.sharedkernel.jwt.entity.RefreshToken;

import com.luckyseven.backend.sharedkernel.jwt.repository.BlackListTokenRepository;
import com.luckyseven.backend.sharedkernel.jwt.repository.RefreshTokenRepository;

import io.jsonwebtoken.Claims;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;

import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import java.security.Key;

import java.util.Base64;
import java.util.Date;
import javax.crypto.SecretKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class JwtTokenizer {
  private final RefreshTokenRepository refreshTokenRepository;
  private final BlackListTokenRepository blackListTokenRepository;
  private final CustomMemberDetailsService customMemberDetailsService;
  private final byte[] accessSecret;
  private final byte[] refreshSecret;
  public static final Long ACCESS_TOKEN_EXPIRE = 24*60*60*1000L; //1일
  public static final Long REFRESH_TOKEN_EXPIRE = 7*24*60*60*1000L; //1주일


  public JwtTokenizer(
      RefreshTokenRepository refreshTokenRepository,
      BlackListTokenRepository blackListTokenRepository,
      CustomMemberDetailsService customMemberDetailsService,
      @Value("213948109238490182309481asdkasdfkajsdlf19023840921")String accessSecret,
      @Value("213948109238490182309asdfasdfasdf4819023840921")String refreshSecret) {
    this.refreshTokenRepository = refreshTokenRepository;
    this.blackListTokenRepository = blackListTokenRepository;
    this.customMemberDetailsService = customMemberDetailsService;
    this.accessSecret = Base64.getDecoder().decode(accessSecret);
    this.refreshSecret = Base64.getDecoder().decode(refreshSecret);

  }

  public String reissueTokenPair(HttpServletResponse response, memberDetails memberDetails) {
    String accessToken = createToken(
        memberDetails,
        ACCESS_TOKEN_EXPIRE,
        getSigningKey(accessSecret)
    );
    String refreshToken = createToken(
        memberDetails,
        REFRESH_TOKEN_EXPIRE,
        getSigningKey(refreshSecret)

    );

    RefreshToken refreshTokenEntity = refreshTokenRepository.findByUserId(memberDetails.getId())
        .orElseGet(() -> {
          return RefreshToken.builder()
              .userId(memberDetails.getId())
              .tokenValue(refreshToken)
              .build();
        });
    refreshTokenRepository.save(refreshTokenEntity);

    addTokenCookie(response,"refreshToken",refreshToken,REFRESH_TOKEN_EXPIRE);


    return accessToken;
  }

  private String createToken(memberDetails userDetails,Long expire, Key signingKey) {
    return Jwts.builder()
        .subject(userDetails.getId().toString())
        .claim("email", userDetails.getEmail())
        .claim("nickname", userDetails.getUsername())
        .issuedAt(new Date())
        .expiration(new Date(new Date().getTime() + expire))
        .signWith(signingKey)
        .compact();
  }

  private void addTokenCookie(HttpServletResponse response ,String tokenName ,  String tokenValue, Long expirationTime){
    Cookie tokenCookie = new Cookie(tokenName, tokenValue);
    tokenCookie.setPath("/");
    tokenCookie.setHttpOnly(true);
    tokenCookie.setMaxAge(Math.toIntExact(expirationTime/1000));
    tokenCookie.setSecure(false);
    tokenCookie.setAttribute("SameSite","None");
    response.addCookie(tokenCookie);
  }


  public Claims parseAccessToken(String accessToken){
    return parseToken(accessToken, accessSecret);
  }

  //TODO <QNA> : RefreshTokenEntity에 조회를 하지않고 , 직접 파싱을 하게 된다면 굳이 RefreshEntity가 필요한가?
  public Claims parseRefreshToken(String refreshToken){
    return parseToken(refreshToken,refreshSecret);}

  public Claims parseToken(String token, byte[] signingKey) {
    try{
      Jws<Claims> parsed = Jwts.parser()
          .verifyWith(getSigningKey(signingKey))
          .build()
          .parseSignedClaims(token);

      return parsed.getPayload();
    } catch (ExpiredJwtException e){
      throw new CustomLogicException(ExceptionCode.JWT_EXPIRED_TOKEN,e.getMessage());
    } catch (UnsupportedJwtException | MalformedJwtException |
             SecurityException | IllegalArgumentException e){
      throw new CustomLogicException(ExceptionCode.JWT_INVALID_TOKEN,e.getMessage());
    }
  }


  private SecretKey getSigningKey(byte[] signingKey) {
    return Keys.hmacShaKeyFor(signingKey);
  }


   public String validateRefreshToken(String refreshToken, HttpServletResponse response){
    if(blackListTokenRepository.existsByTokenValue(refreshToken)){
      throw new CustomLogicException(ExceptionCode.JWT_BLACKLISTED_TOKEN);
    }
    Claims claims = parseRefreshToken(refreshToken);
    Long memberId = Long.parseLong(claims.getSubject());
    memberDetails user = customMemberDetailsService.loadUserById(memberId);
    blackListTokenRepository.save(
        BlackListToken.builder()
            .tokenValue(refreshToken)
            .expirationTime(claims.getExpiration().toInstant())
            .build()
    );
    return reissueTokenPair(response, user);
  }

}
