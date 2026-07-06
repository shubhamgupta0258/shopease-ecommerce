//package com.example.e_commerce.security;
//
//import io.jsonwebtoken.*;
//import io.jsonwebtoken.security.Keys;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Component;
//
//import javax.crypto.SecretKey;
//import java.util.Date;
//
//@Component
//public class JwtUtil {
//
////    //First part defines class and reads JWT secret and expiration from properties.)
//        @Value("${app.jwtSecret}")
////        private String jwtSecret;
//        SecretKey key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
//
//        @Value("${app.jwtExpirationMs}")
//        private int jwtExpirationMs;
//
//        //Explanation: This creates a signed JWT token with username, issue time, and expiration.
//    //add the method to generate a JWT token:
//    public String generateJwtToken(org.springframework.security.core.Authentication authentication) {
//        org.springframework.security.core.userdetails.User userPrincipal =
//                (org.springframework.security.core.userdetails.User) authentication.getPrincipal();
//
//        return Jwts.builder()
//                .setSubject(userPrincipal.getUsername())
//                .setIssuedAt(new Date())
//                .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
//                .signWith(key, SignatureAlgorithm.HS256)
//                .compact();
//    }
//    //Now add the method to get username from token:
//    public String getUsernameFromJwtToken(String token) {
//        return Jwts.parser()
//                .setSigningKey(key)
//                .parseClaimsJws(token)
//                .getBody()
//                .getSubject();
//    }
//    //Finally, add the validation method:
//    public boolean validateJwtToken(String authToken) {
//        try {
//            Jwts.parser().setSigningKey(key).parseClaimsJws(authToken);
//            return true;
//        } catch (JwtException | IllegalArgumentException e) {
//            // You can add logging here for invalid token
//        }
//        return false;
//    }
//}
//

//package com.example.e_commerce.security;
//
//import io.jsonwebtoken.*;
//import io.jsonwebtoken.security.Keys;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Component;
//
//import javax.crypto.SecretKey;
//import java.util.Date;
//import io.jsonwebtoken.io.Decoders;
//import jakarta.annotation.PostConstruct;
//
//
//@Component
//public class JwtUtil {
//
//    // Use the secret string read from properties, decoded as bytes, to create a key
//    @Value("${app.jwtSecret}")
//    private String jwtSecret;
//
//    private SecretKey key;
//
//
//
//    @Value("${app.jwtExpirationMs}")
//    private int jwtExpirationMs;
//
//    // Initialize the SecretKey from the secret string after bean creation
//    public JwtUtil(@Value("${app.jwtSecret}") String jwtSecret,
//                   @Value("${app.jwtExpirationMs}") int jwtExpirationMs) {
//        byte[] keyBytes = Decoders.BASE64.decode(jwtSecret);
//        this.key = Keys.hmacShaKeyFor(keyBytes);
//        this.jwtExpirationMs = jwtExpirationMs;
//
//    // Generate JWT token signed with the SecretKey
//    public String generateJwtToken(org.springframework.security.core.Authentication authentication) {
//        org.springframework.security.core.userdetails.User userPrincipal =
//                (org.springframework.security.core.userdetails.User) authentication.getPrincipal();
//
//        return Jwts.builder()
//                .setSubject(userPrincipal.getUsername())
//                .setIssuedAt(new Date())
//                .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
//                .signWith(key, SignatureAlgorithm.HS256)
//                .compact();
//    }
//
//    // Extract username (subject) from token
//    public String getUsernameFromJwtToken(String token) {
//        return Jwts.parserBuilder()
//                .setSigningKey(key)
//                .build()
//                .parseClaimsJws(token)
//                .getBody()
//                .getSubject();
//    }
//
//    // Validate JWT token
//    public boolean validateJwtToken(String authToken) {
//        try {
//            Jwts.parserBuilder()
//                    .setSigningKey(key)
//                    .build()
//                    .parseClaimsJws(authToken);
//            return true;
//        } catch (JwtException | IllegalArgumentException e) {
//            // log token validation errors if needed
//        }
//        return false;
//    }
//}

package com.example.e_commerce.security;

//import com.example.e_commerce.models.User;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.security.core.Authentication;

//import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.security.core.userdetails.User;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtUtil {

    private final SecretKey key;
    private final int jwtExpirationMs;
    private final int refreshExpirationsMs;

    public JwtUtil(@Value("${app.jwtSecret}") String jwtSecret,
                   @Value("${app.jwtExpirationMs}") int jwtExpirationMs,
                   @Value("${app.refreshExpirationsMs}") int refreshExpirationsMs) {
        byte[] keyBytes = Decoders.BASE64.decode(jwtSecret);
        this.key = Keys.hmacShaKeyFor(keyBytes);
        this.jwtExpirationMs = jwtExpirationMs;
        this.refreshExpirationsMs=refreshExpirationsMs;
    }

    public String generateJwtToken(org.springframework.security.core.Authentication authentication) {
        org.springframework.security.core.userdetails.User userPrincipal =
                (org.springframework.security.core.userdetails.User) authentication.getPrincipal();

        return Jwts.builder()
                .setSubject(userPrincipal.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }
    public String generateRefreshToken(Authentication authentication) {
        User userPrincipal = (User) authentication.getPrincipal();
        return createToken(userPrincipal.getUsername(), refreshExpirationsMs);
    }

    private String createToken(String Username, int expirationsMs){
        return Jwts.builder()
                .setSubject(Username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis()+expirationsMs))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        }

    public String getUsernameFromJwtToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public String generateJwtTokenFromUsername(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public String generateRefreshTokenFromUsername(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + refreshExpirationsMs))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean validateJwtToken(String authToken) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(authToken);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            // You can add logging here if needed to track invalid tokens
        }
        return false;
    }
}

