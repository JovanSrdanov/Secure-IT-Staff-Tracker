package jass.security.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.servlet.http.HttpServletRequest;
import jass.security.exception.TokenExpiredException;
import org.jose4j.jwa.AlgorithmConstraints;
import org.jose4j.jwk.JsonWebKey;
import org.jose4j.jwk.RsaJsonWebKey;
import org.jose4j.jws.AlgorithmIdentifiers;
import org.jose4j.jwt.consumer.InvalidJwtException;
import org.jose4j.keys.RsaKeyUtil;
import org.jose4j.lang.JoseException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import org.jose4j.jwt.JwtClaims;
import org.jose4j.jwt.consumer.JwtConsumer;
import org.jose4j.jwt.consumer.JwtConsumerBuilder;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.Map;

// Utility klasa za rad sa JSON Web Tokenima
@Component
public class TokenUtils {

    private static final String AUDIENCE_WEB = "web";
    // Algoritam za potpisivanje JWT
    private final SignatureAlgorithm SIGNATURE_ALGORITHM = SignatureAlgorithm.HS512;
    // Tajna koju samo backend aplikacija treba da zna kako bi mogla da generise i proveri JWT https://jwt.io/
    @Value("${tokenSecretKey}")
    public String SECRET;
    // Public key used in token keycloak token validations
    @Value("${keycloakRealmPublicKey}")
    public String KEYCLOAK_REALM_PUBLIC_KEY;

    // Izdavac tokena
    @Value("spring-security-example")
    private String APP_NAME;
    // Period vazenja tokena - 15 minuta
    @Value("${tokenExpiration}")
    private int EXPIRES_IN;
    @Value("${refreshTokenExpiration}")
    private int REFRESH_EXPIRES_IN;
    // Moguce je generisati JWT za razlicite klijente (npr. web i mobilni klijenti nece imati isto trajanje JWT,
    // JWT za mobilne klijente ce trajati duze jer se mozda aplikacija redje koristi na taj nacin)
    // Radi jednostavnosti primera, necemo voditi racuna o uređaju sa kojeg zahtev stiže.
    //	private static final String AUDIENCE_UNKNOWN = "unknown";
    //	private static final String AUDIENCE_MOBILE = "mobile";
    //	private static final String AUDIENCE_TABLET = "tablet";
    // Naziv headera kroz koji ce se prosledjivati JWT u komunikaciji server-klijent
    @Value("Authorization")
    private String AUTH_HEADER;

    // ============= Funkcije za generisanje JWT tokena =============

    /**
     * Funkcija za generisanje JWT tokena.
     *
     * @param username Korisničko ime korisnika kojem se token izdaje
     * @return JWT token
     */
    public String generateToken(String username, String role) {
        return Jwts.builder()
                .setIssuer(APP_NAME)
                .setSubject(username)
                .setAudience(generateAudience())
                .setIssuedAt(new Date())
                .setExpiration(generateExpirationDate())
                .claim("isRefresh", false)
                .claim("role", role)
                .signWith(SIGNATURE_ALGORITHM, SECRET).compact();


        // moguce je postavljanje proizvoljnih podataka u telo JWT tokena pozivom funkcije .claim("key", value), npr. .claim("role", user.getRole())
    }

    public String generateRefreshToken(String email) {
        return Jwts.builder()
                .setIssuer(APP_NAME)
                .setSubject(email)
                .setAudience(generateAudience())
                .setIssuedAt(new Date())
                .setExpiration(generateRefreshExpirationDate())
                .claim("isRefresh", true)
                .signWith(SIGNATURE_ALGORITHM, SECRET).compact();
    }

    /**
     * Funkcija za utvrđivanje tipa uređaja za koji se JWT kreira.
     *
     * @return Tip uređaja.
     */
    private String generateAudience() {

        //	Moze se iskoristiti org.springframework.mobile.device.Device objekat za odredjivanje tipa uredjaja sa kojeg je zahtev stigao.
        //	https://spring.io/projects/spring-mobile

        //	String audience = AUDIENCE_UNKNOWN;
        //		if (device.isNormal()) {
        //			audience = AUDIENCE_WEB;
        //		} else if (device.isTablet()) {
        //			audience = AUDIENCE_TABLET;
        //		} else if (device.isMobile()) {
        //			audience = AUDIENCE_MOBILE;
        //		}

        return AUDIENCE_WEB;
    }

    /**
     * Funkcija generiše datum do kog je JWT token validan.
     *
     * @return Datum do kojeg je JWT validan.
     */
    private Date generateExpirationDate() {
        return new Date(new Date().getTime() + EXPIRES_IN);
    }

    private Date generateRefreshExpirationDate() {
        return new Date(new Date().getTime() + REFRESH_EXPIRES_IN);
    }

    // =================================================================

    // ============= Funkcije za citanje informacija iz JWT tokena =============

    /**
     * Funkcija za preuzimanje JWT tokena iz zahteva.
     *
     * @param request HTTP zahtev koji klijent šalje.
     * @return JWT token ili null ukoliko se token ne nalazi u odgovarajućem zaglavlju HTTP zahteva.
     */
    public String getToken(HttpServletRequest request) {
        String authHeader = getAuthHeaderFromHeader(request);

        // JWT se prosledjuje kroz header 'Authorization' u formatu:
        // Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7); // preuzimamo samo token (vrednost tokena je nakon "Bearer " prefiksa)
        }

        return null;
    }

    /**
     * Funkcija za preuzimanje vlasnika tokena (korisničko ime).
     *
     * @param token JWT token.
     * @return Korisničko ime iz tokena ili null ukoliko ne postoji.
     */
    public String getUsernameFromToken(String token) {
        String username;

        try {
            final Claims claims = this.getAllClaimsFromToken(token);
            username = claims.getSubject();
            if (!isEmail(username)) {
                username = claims.get("preferred_username", String.class);
            }
        } catch (ExpiredJwtException ex) {
            throw ex;
        } catch (Exception e) {
            username = null;
        }

        return username;
    }

    private Boolean isEmail(String username) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
        return username.matches(emailRegex);
    }

    /**
     * Funkcija za preuzimanje datuma kreiranja tokena.
     *
     * @param token JWT token.
     * @return Datum kada je token kreiran.
     */
    public Date getIssuedAtDateFromToken(String token) throws TokenExpiredException {
        Date issueAt;
        try {
            final Claims claims = this.getAllClaimsFromToken(token);
            issueAt = claims.getIssuedAt();
        } catch (ExpiredJwtException ex) {
            throw new TokenExpiredException();
        } catch (Exception e) {
            issueAt = null;
        }
        return issueAt;
    }

    /**
     * Funkcija za preuzimanje informacije o uređaju iz tokena.
     *
     * @param token JWT token.
     * @return Tip uredjaja.
     */
    public String getAudienceFromToken(String token) {
        String audience;
        try {
            final Claims claims = this.getAllClaimsFromToken(token);
            audience = claims.getAudience();
        } catch (ExpiredJwtException ex) {
            throw ex;
        } catch (Exception e) {
            audience = null;
        }
        return audience;
    }

    /**
     * Funkcija za preuzimanje datuma do kada token važi.
     *
     * @param token JWT token.
     * @return Datum do kojeg token važi.
     */
    public Date getExpirationDateFromToken(String token) throws TokenExpiredException {
        Date expiration;
        try {
            final Claims claims = this.getAllClaimsFromToken(token);
            expiration = claims.getExpiration();
        } catch (ExpiredJwtException ex) {
            throw new TokenExpiredException();
        } catch (Exception e) {
            expiration = null;
        }

        return expiration;
    }

    /**
     * Funkcija za čitanje svih podataka iz JWT tokena
     *
     * @param token JWT token.
     * @return Podaci iz tokena.
     */
    private Claims getAllClaimsFromToken(String token) {
        Claims claims;
        try {
            claims = Jwts.parser()
                    .setSigningKey(SECRET)
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException ex) {
            throw ex;
        } catch (Exception e) {
            //claims = null;
            return parseKeycloakToken(token);
        }

        // Preuzimanje proizvoljnih podataka je moguce pozivom funkcije claims.get(key)

        return claims;
    }

    private Claims parseKeycloakToken(String token) {
        try {
            PublicKey keycloakKey = getPublicKeyFromString(KEYCLOAK_REALM_PUBLIC_KEY);

            return Jwts.parser()
                    .setSigningKey(keycloakKey)
                    .parseClaimsJws(token)
                    .getBody();
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new RuntimeException(e);
        }
    }

    private PublicKey getPublicKeyFromString(String keyString) throws NoSuchAlgorithmException, InvalidKeySpecException {
        byte[] publicKeyBytes = Base64.getDecoder().decode(keyString);
        KeyFactory factory = KeyFactory.getInstance("RSA");
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicKeyBytes);
        return factory.generatePublic(keySpec);
    }

    // =================================================================

    // ============= Funkcije za validaciju JWT tokena =============

    /**
     * Funkcija za validaciju JWT tokena.
     *
     * @param token       JWT token.
     * @param userDetails Informacije o korisniku koji je vlasnik JWT tokena.
     * @return Informacija da li je token validan ili ne.
     */
    public Boolean validateToken(String token, UserDetails userDetails) throws TokenExpiredException {
        final String username = getUsernameFromToken(token);
        final Date created = getIssuedAtDateFromToken(token);

        Claims claims;
        try {
            claims = Jwts.parser()
                    .setSigningKey(SECRET)
                    .parseClaimsJws(token)
                    .getBody();
            return (
                    username != null // korisnicko ime nije null
                            && username.equals(userDetails.getUsername())
                            && !claims.get("isRefresh", Boolean.class)
            );
        } catch (Exception e) {
            // If regular jwt token parsing fails, tries to parse a keycloak token
            try {
                PublicKey keycloakKey = getPublicKeyFromString(KEYCLOAK_REALM_PUBLIC_KEY);
                claims = Jwts.parser()
                        .setSigningKey(keycloakKey)
                        .parseClaimsJws(token)
                        .getBody();
                return (
                        username != null // korisnicko ime nije null
                                && username.equals(userDetails.getUsername())
                        && !claims.get("typ").equals("Refresh")
                );
            }
            catch (Exception e1) {
                throw new RuntimeException(e1);
            }
        }
    }

    public Boolean validateRefreshToken(String token) throws TokenExpiredException {
        final String username = getUsernameFromToken(token);
        final Date created = getIssuedAtDateFromToken(token);

        Claims claims;
        try {
            claims = Jwts.parser()
                .setSigningKey(SECRET)
                .parseClaimsJws(token)
                .getBody();
            return (
                    username != null // korisnicko ime nije null
                            && claims.get("isRefresh", Boolean.class)
            );
        } catch (Exception e) {
            try {
                // TODO Stefan: ovo ovako ne treba, keycloak razlicito tretira access i refresh tokene,
                // access tokene potipiuje sa RSA algoritmom, dok refresh sa HS256, ali sa invalidnim kljucem,
                // jer je klijent public posto je frotnend aplikacija pa nema secret
                // resenje je implementirati PKSE, ali bi to zahtevalo da se menja spring security konfiguracija
                return (
//                        username != null // korisnicko ime nije null
//                                && !jwtClaims.get("typ").equals("Bearer")
                        false
                );
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    /**
     * Funkcija proverava da li je lozinka korisnika izmenjena nakon izdavanja tokena.
     *
     * @param created           Datum kreiranja tokena.
     * @param lastPasswordReset Datum poslednje izmene lozinke.
     * @return Informacija da li je token kreiran pre poslednje izmene lozinke ili ne.
     */
    private Boolean isCreatedBeforeLastPasswordReset(Date created, Date lastPasswordReset) {
        return (lastPasswordReset != null && created.before(lastPasswordReset));
    }

    // =================================================================

    /**
     * Funkcija za preuzimanje perioda važenja tokena.
     *
     * @return Period važenja tokena.
     */
    public int getExpiredIn() {
        return EXPIRES_IN;
    }

    /**
     * Funkcija za preuzimanje sadržaja AUTH_HEADER-a iz zahteva.
     *
     * @param request HTTP zahtev.
     * @return Sadrzaj iz AUTH_HEADER-a.
     */
    public String getAuthHeaderFromHeader(HttpServletRequest request) {
        return request.getHeader(AUTH_HEADER);
    }

    public static String extractRoleFromAuthenticationHeader(Authentication principal) {
        List<String> authorities = principal.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        return authorities.get(0);
    }
}
