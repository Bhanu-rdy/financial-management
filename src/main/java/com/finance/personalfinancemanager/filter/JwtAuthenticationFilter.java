package com.finance.personalfinancemanager.filter;

import com.finance.personalfinancemanager.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.util.ArrayList;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    protected  void doFilterInternal(HttpServletRequest request,
                                     HttpServletResponse response,
                                     FilterChain filterChain) throws ServletException, IOException {

        String token = null;
        String userName = null;

        //Get token from cookie
        Cookie[] cookies = request.getCookies();
        if(cookies != null) {
            for(Cookie cookie : cookies) {
                if("token".equals(cookie.getName())) {
                    token = cookie.getValue();
                    break;
                }
            }
        } else {
            System.out.println("No cookies found in request");
        }

        // Extract username from token
        if (token != null) {
            try {
                userName = jwtUtil.extractUserName(token);
                System.out.println("Username from cookies : " + userName);
            } catch (Exception e) {
                System.out.println("Invalid token: " + e.getMessage());
                System.out.println("Invalid token");
            }
        }

        //Validate token and set authentication
        if(userName != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            if(jwtUtil.validateToken(token)) {
                System.out.println("Token validated successfully");
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userName, null, new ArrayList<>());
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authToken);

                request.setAttribute("userId", jwtUtil.extractUserId(token));
            } else {
                System.out.println("Token validation failed");
            }
        }

        filterChain.doFilter(request, response);
    }
}
