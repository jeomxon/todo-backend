package backend.likelion.todos.auth.jwt;

import static java.util.concurrent.TimeUnit.DAYS;
import static java.util.concurrent.TimeUnit.MILLISECONDS;

import backend.likelion.todos.common.UnAuthorizedException;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import java.util.Date;
import org.springframework.stereotype.Service;

@Service
public class JwtService {

    private final long accessTokenExpirationDayToMills;
    private final Algorithm algorithm;

    public JwtService(JwtProperty jwtProperty) {
        this.accessTokenExpirationDayToMills =
                MILLISECONDS.convert(jwtProperty.accessTokenExpirationDay(), DAYS);
        this.algorithm = Algorithm.HMAC512(jwtProperty.secretKey());
    }

    // 회원 ID를 기반으로 JWT 토큰을 생성합니다.
    public String createToken(Long memberId) {
        final Date now = new Date();
        return JWT.create()
                .withIssuedAt(now)
                .withExpiresAt(new Date(now.getTime() + accessTokenExpirationDayToMills))
                .withClaim("memberId", memberId)
                .sign(algorithm);
    }

    // 토큰에서 회원 ID를 추출합니다.
    public Long extractMemberId(String token) {
        try {
            JWTVerifier jwtVerifier = JWT.require(algorithm).build();
            DecodedJWT decodedJwt = jwtVerifier.verify(token);
            return decodedJwt.getClaim("memberId")
                    .asLong();
        } catch (JWTVerificationException e) {
            throw new UnAuthorizedException("유효하지 않은 토큰입니다.");
        }
    }
}
