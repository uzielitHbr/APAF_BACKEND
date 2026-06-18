package app.apaf.backend.core.security;



import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.List;


/*
Clase de Servicio donde se encarga de generar los tokens JWT

@Uziel Abraham
@Version 1.0
 */

@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String jwtKey;

    @Value("${jwt.expiration-time}")
    private long expirationTime;


    private SecretKey key;
    private SecretKey secretKey (){
        if (key == null) {
            key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtKey));
        }
        return key;
    }

    //get from header
    public String getJwtTokenFromHeader(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            return header.substring(7);
        }
        return null;
    }

    // Generate token , I used a IdUsuario is more secure
    public String generateJwtToken(Long idUsuario, String role) {
        return Jwts.builder()
                .subject(String.valueOf(idUsuario))
                .claim("role", role)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(secretKey())
                .compact();
    }


    // Get idUsuario from JWT
    public String getIdUsuarioFromJwtToken(String token) {
    String IdString = Jwts.parser()
            .verifyWith(secretKey())
            .build()
            .parseSignedClaims(token)
            .getPayload()
            .getSubject();
    return IdString;

    }

    //Validate JWT
    public boolean validateJwtToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(secretKey())
                    .build()
                    .parseSignedClaims(token);

            return true;
        } catch (SecurityException e) {
            System.out.println("Invalid JWT signature: " + e.getMessage());
        } catch (MalformedJwtException e) {
            System.out.println("Invalid JWT token: " + e.getMessage());
        } catch (ExpiredJwtException e) {
            System.out.println("JWT token is expired: " + e.getMessage());
        } catch (UnsupportedJwtException e) {
            System.out.println("JWT token is unsupported: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.out.println("JWT claims string is empty: " + e.getMessage());
        }

        return false;
    }

}
