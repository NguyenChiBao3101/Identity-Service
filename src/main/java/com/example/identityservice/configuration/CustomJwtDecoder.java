package com.example.identityservice.configuration;
import com.example.identityservice.dto.request.IntrospectRequest;
import com.example.identityservice.repository.InvalidatedTokenRepository;
import com.example.identityservice.service.AuthenticationService;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;
import java.text.ParseException;
import java.util.Date;
import java.util.Objects;

@Component
public class CustomJwtDecoder implements JwtDecoder {
    @Value("${jwt.signerKey}")
    private String signerKey;

    @Autowired
    private InvalidatedTokenRepository invalidatedTokenRepository;


    @Override
    public Jwt decode(String token) throws JwtException {

        try {
            //Parse token
            SignedJWT signedJWT = SignedJWT.parse(token);
            //verify signer key
            JWSVerifier jwsVerifier = new MACVerifier(signerKey.getBytes());
            if (!signedJWT.verify(jwsVerifier)) {
                throw new JwtException("Invalid JWT Signature");
            }

            // expired token checked
            JWTClaimsSet claims = signedJWT.getJWTClaimsSet();
            Date expirationTime = claims.getExpirationTime();
            if (expirationTime == null || expirationTime.before(new Date())) {
                throw new JwtException("token expired");
            }

            // token logout checked
            String jti = claims.getJWTID();
            if(jti != null && invalidatedTokenRepository.existsById(jti)) {
                throw new JwtException("Token has been invalidated (logout)");
            }
            // validated, decode token into JWT object for use in
            SecretKeySpec secretKeySpec = new SecretKeySpec(signerKey.getBytes(), "HmacSHA512");
            NimbusJwtDecoder nimbusJwtDecoder = NimbusJwtDecoder.withSecretKey(secretKeySpec)
                    .macAlgorithm(MacAlgorithm.HS512)
                    .build();

            return nimbusJwtDecoder.decode(token);

        } catch (JOSEException | ParseException e) {
            throw new JwtException("Failed to decode JWT: " + e.getMessage(), e);
        }
    }
}
