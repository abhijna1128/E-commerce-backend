//package com.ecommerce.config;
//
//import com.ecommerce.repository.UserRepository;
//import com.ecommerce.utils.JwtUtil;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
//import org.springframework.stereotype.Component;
//import org.springframework.web.filter.OncePerRequestFilter;
//import jakarta.servlet.FilterChain;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import java.io.IOException;
//import java.util.Collections;
//
//@Component
//public class JwtFilter extends OncePerRequestFilter {
//
//    private final JwtUtil jwtUtil;
//    private final UserRepository userRepository;
//
//    public JwtFilter(JwtUtil jwtUtil, UserRepository userRepository) {
//        this.jwtUtil = jwtUtil;
//        this.userRepository = userRepository;
//    }
//
//    @Override
//    protected void doFilterInternal(HttpServletRequest request,
//                                    HttpServletResponse response,
//                                    FilterChain filterChain)
//            throws ServletException, IOException {
//
//        String path = request.getServletPath();
//
//        // ✅ Skip authentication for public endpoints
//        if (path.startsWith("/api/auth") || 
//            path.startsWith("/swagger-ui") || 
//            path.startsWith("/v3/api-docs")) {
//            filterChain.doFilter(request, response);
//            return;
//        }
//
//        final String authHeader = request.getHeader("Authorization");
//
//        // ✅ Proceed without authentication if no Bearer token
//        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
//            filterChain.doFilter(request, response);
//            return;
//        }
//
//        try {
//            String token = authHeader.substring(7);
//
//            if (jwtUtil.validateToken(token)) {
//                String email = jwtUtil.extractEmail(token);
//
//                var userOpt = userRepository.findByEmail(email);
//                if (userOpt.isPresent()) {
//                    UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
//                            userOpt.get(), null, Collections.emptyList());
//                    auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
//                    SecurityContextHolder.getContext().setAuthentication(auth);
//                }
//            }
//        } catch (Exception e) {
//            System.out.println("⚠️ JWT validation failed: " + e.getMessage());
//        }
//
//        filterChain.doFilter(request, response);
//    }
//}
package com.ecommerce.config;

import com.ecommerce.entity.User;
import com.ecommerce.repository.UserRepository;
import com.ecommerce.utils.JwtUtil;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;

@Component
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    public JwtFilter(JwtUtil jwtUtil, UserRepository userRepository) {
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getServletPath();

        // Skip authentication for public endpoints
        if (path.startsWith("/api/auth") || 
            path.startsWith("/swagger-ui") || 
            path.startsWith("/v3/api-docs")) {
            filterChain.doFilter(request, response);
            return;
        }

        final String authHeader = request.getHeader("Authorization");

        // Proceed without authentication if no Bearer token
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            String token = authHeader.substring(7);

            if (jwtUtil.validateToken(token)) {
                String email = jwtUtil.extractEmail(token);

                var userOpt = userRepository.findByEmail(email);
                if (userOpt.isPresent()) {
                    User user = userOpt.get();

                    // ✅ Fix: Convert enum to string
                    SimpleGrantedAuthority authority = new SimpleGrantedAuthority(user.getRole().name());

                    UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                            user, null, Collections.singletonList(authority));

                    auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(auth);
                }
            }
        } catch (Exception e) {
            System.out.println("⚠️ JWT validation failed: " + e.getMessage());
        }

        filterChain.doFilter(request, response);
    }
}
