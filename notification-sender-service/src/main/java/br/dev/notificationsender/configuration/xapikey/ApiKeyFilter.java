package br.dev.notificationsender.configuration.xapikey;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static br.dev.notificationsender.exceptions.ErrorMessages.INVALID_X_API_KEY;
import static br.dev.notificationsender.exceptions.ErrorMessages.MISSING_X_API_KEY;
import static java.util.Objects.isNull;

@Component
@Profile("prod")
@RequiredArgsConstructor
public class ApiKeyFilter implements Filter {

    private static final String API_KEY_HEADER = "x-api-key";

    private final ApiKeyConfiguration xApiKey;

    private final ObjectMapper objectMapper;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String apiKeyRecebida = httpRequest.getHeader(API_KEY_HEADER);

        if (isNull(apiKeyRecebida)) {
            escreverResposta(httpRequest, httpResponse, MISSING_X_API_KEY);
            return;
        }

        if (apiKeyNaoConfere(apiKeyRecebida)) {
            escreverResposta(httpRequest, httpResponse, INVALID_X_API_KEY);
            return;
        }

        chain.doFilter(request, response);
    }

    private boolean apiKeyNaoConfere(String receivedApiKey) {
        return !xApiKey.getXApiKey().equals(receivedApiKey);
    }

    private void escreverResposta(HttpServletRequest httpRequest, HttpServletResponse response, String mensagem) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("timestamp", System.currentTimeMillis());
        responseBody.put("status", HttpServletResponse.SC_UNAUTHORIZED);
        responseBody.put("error", "Unauthorized");
        responseBody.put("message", mensagem);
        responseBody.put("path", httpRequest.getPathInfo());

        response.getWriter().write(objectMapper.writeValueAsString(responseBody));
    }

}
