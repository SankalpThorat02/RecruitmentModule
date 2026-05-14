package com.sankalp.prototype.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class RoleValidationFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        // TODO Auto-generated method stub
        String path = request.getRequestURI();

        // skip auth APIs
        if (path.startsWith("/auth") || path.startsWith("/register")) {
            filterChain.doFilter(request, response);
            return;
        }

        HttpSession session = request.getSession(false);

        if (session == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("No session");
            return;
        }

        Integer roleId = (Integer) session.getAttribute("ROLE_ID");

        if (roleId == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Role not found");
            return;
        }

        //Example RBAC rules

        String uri = request.getRequestURI();

        // Admin only
        if (uri.startsWith("/admin") && roleId != 1) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.getWriter().write("Access denied");
            return;
        }

        // Admin + Manager
        if (uri.startsWith("/manager") && !(roleId == 1 || roleId == 2)) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.getWriter().write("Access denied");
            return;
        }

        // Manager,evaluator, Admin
        if (uri.startsWith("/manager") && !(roleId == 1 || roleId == 2 || roleId == 3)) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.getWriter().write("Access denied");
            return;
        }
        if (uri.startsWith("/manager") && !(roleId != 4)) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.getWriter().write("Access denied");
            return;
        }

        // allow
        filterChain.doFilter(request, response);
    }
}


