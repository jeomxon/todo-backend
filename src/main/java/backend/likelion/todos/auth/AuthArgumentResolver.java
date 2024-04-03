package backend.likelion.todos.auth;

import backend.likelion.todos.auth.jwt.JwtService;
import backend.likelion.todos.common.UnAuthorizedException;
import lombok.RequiredArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@RequiredArgsConstructor
@Component
public class AuthArgumentResolver implements HandlerMethodArgumentResolver {

    private final JwtService jwtService;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        boolean isCorrectParameterType = parameter.getParameterType().equals(Long.class);
        boolean hasAnnotation = parameter.hasParameterAnnotation(Auth.class);
        return isCorrectParameterType && hasAnnotation;
    }

    @Override
    public Object resolveArgument(
            MethodParameter parameter,
            ModelAndViewContainer mavContainer,
            NativeWebRequest webRequest,
            WebDataBinderFactory binderFactory
    ) {
        String accessToken = extractAccessToken(webRequest);
        Long memberId = jwtService.extractMemberId(accessToken);
        return memberId;
    }

    private static String extractAccessToken(NativeWebRequest request) {
        String token = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (StringUtils.hasText(token) && token.startsWith("BEARER ")) {
            return token.split(" ")[1];
        }
        throw new UnAuthorizedException("로그인 후 접근할 수 있습니다.");
    }

}
