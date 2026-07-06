//package com.example.e_commerce.security;
////let's implement the JWT authentication filter, which will intercept incoming requests,
//// extract the JWT token, validate it, and set the authentication context.
////JwtUtil is the token expert: verifies signature, expiration, and retrieves username.
////JwtAuthenticationFilter is the security gatekeeper: takes the token info, loads user info,
//// and sets the authentication so Spring Security knows who the user is for this request.
//import jakarta.servlet.FilterChain;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.core.userdetails.UserDetailsService;
//import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
//import org.springframework.stereotype.Component;
//import org.springframework.web.filter.OncePerRequestFilter;
//
//import java.io.IOException;
//
//@Component
//public class JwtAuthenticationFilter extends OncePerRequestFilter {
//
//    @Autowired
//    private JwtUtil jwtUtil;
//
//    @Autowired
//    private UserDetailsService userDetailsService;
//
//    @Override
//    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
//            throws ServletException, IOException {
//
//        String jwt = parseJwt(request);
//        if(jwt != null && jwtUtil.validateJwtToken(jwt)) {
//            String username = jwtUtil.getUsernameFromJwtToken(jwt);
//            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
//
//            UsernamePasswordAuthenticationToken authentication =
//                    new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
//
//            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
//            SecurityContextHolder.getContext().setAuthentication(authentication);
//        }
//        filterChain.doFilter(request, response);
//    }
//
//    private String parseJwt(HttpServletRequest request) {
//        String headerAuth = request.getHeader("Authorization");
//        if(headerAuth != null && headerAuth.startsWith("Bearer ")) {
//            return headerAuth.substring(7);
//        }
//        return null;
//    }
//}
//The JwtAuthenticationFilter.java class is a custom Spring Security filter that runs once per HTTP request. Its main responsibilities are:
//Extract the JWT token from the Authorization header in incoming HTTP requests.
//Validate the extracted JWT token using the JwtUtil service to ensure it’s correctly signed and not expired.
//If valid, retrieve the username from the token.
//Use the username to load user details from the user service
//Create an Authentication object representing the authenticated user with their authorities.
//Set this Authentication object into the Spring Security context, making the user’s identity available throughout the request processing.
//Allow the request to continue through the filter chain with the user authenticated if the token is valid, or continue without authentication if not.
//In short, this filter secures your APIs by verifying JWT tokens for each request and establishing the user’s identity in your application’s security context,
// enabling role-based access control and secure endpoint protection.
//This is key for enabling stateless, token-based authentication in your Spring Boot backend.

//JwtUtil.java
//This class handles JWT-specific logic: generating tokens, parsing tokens to extract the username, and validating the token’s integrity and expiration.
// It’s focused on the token itself without knowledge of Spring Security’s authentication system or user details.
//
//JwtAuthenticationFilter.java
//This class acts as a Spring Security filter that intercepts HTTP requests. It uses JwtUtil to validate and parse the token, but its main task
// is to integrate JWT validation with Spring Security’s security context. After JwtUtil confirms the token is valid and extracts the username, the
// filter loads user details from your user service and constructs an Authentication object which it sets in the SecurityContext.
package com.example.e_commerce.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * JWT filter that:
 *  - SKIPS preflight OPTIONS requests (so CORS works)
 *  - SKIPS public auth endpoints (e.g. /api/auth/**)
 *  - Validates JWT from Authorization header and sets Authentication in SecurityContext when valid
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        // --- 1) Skip preflight OPTIONS immediately ---
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            filterChain.doFilter(request, response);
            return;
        }

        // --- 2) Skip only the specific public auth endpoints; /api/auth/me still needs the token parsed ---
        String path = request.getRequestURI();
        if (path != null && (path.equals("/api/auth/login") || path.equals("/api/auth/register") || path.equals("/api/auth/refresh"))) {
            filterChain.doFilter(request, response);
            return;
        }

        // --- 3) Normal JWT processing (wrapped safely) ---
        try {
            String jwt = parseJwt(request);
            if (jwt != null && jwtUtil.validateJwtToken(jwt)) {
                String username = jwtUtil.getUsernameFromJwtToken(jwt);
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception ex) {
            // Don't fail the request because of JWT parsing/validation errors.
            // Let the filter chain continue — Spring Security will return 401 for protected endpoints if no auth set.
            // You can log here if you have a logger; keep simple for now:
            System.out.println("JwtAuthenticationFilter - token processing failed: " + ex.getMessage());
        }

        // Continue chain
        filterChain.doFilter(request, response);
    }

    private String parseJwt(HttpServletRequest request) {
        String headerAuth = request.getHeader("Authorization");
        if (headerAuth != null && headerAuth.startsWith("Bearer ")) {
            return headerAuth.substring(7);
        }
        return null;
    }
}
