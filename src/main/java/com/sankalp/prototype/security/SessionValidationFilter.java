package com.sankalp.prototype.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;

@Component
public class SessionValidationFilter extends OncePerRequestFilter {
    @Autowired
    private UserSessionRepository sessionRepo;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getRequestURI();
        String method = request.getMethod();

        if ("OPTIONS".equalsIgnoreCase(method)) {
            filterChain.doFilter(request, response);
            return;
        }
        // Skip auth APIs
        if (path.contains("/auth") || path.contains("/register")) {
            filterChain.doFilter(request, response);
            return;
        }

        // SKIP PRE-FLIGHT REQUESTS



        HttpSession session = request.getSession(false);
        if (session == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("No active session");
            return;
        }

        String sessionId = session.getId();

        // DB check
        Optional<UserSession> dbSession =
                sessionRepo.findActiveBySessionId(sessionId);

        if (dbSession.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Session expired or logged in elsewhere");
            return;
        }

        //  valid → continue
        filterChain.doFilter(request, response);
    }
}

