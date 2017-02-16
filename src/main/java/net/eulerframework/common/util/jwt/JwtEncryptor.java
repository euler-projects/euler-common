package net.eulerframework.common.util.jwt;

import java.util.Date;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.jwt.Jwt;
import org.springframework.security.jwt.JwtHelper;
import org.springframework.security.jwt.crypto.sign.RsaSigner;
import org.springframework.security.jwt.crypto.sign.RsaVerifier;
import org.springframework.security.jwt.crypto.sign.SignatureVerifier;
import org.springframework.security.jwt.crypto.sign.Signer;

import net.eulerframework.common.util.Assert;
import net.eulerframework.common.util.DateUtils;
import net.eulerframework.common.util.json.JsonConvertException;
import net.eulerframework.common.util.json.JsonUtils;

public class JwtEncryptor {

    protected final Logger logger = LogManager.getLogger(this.getClass());
    
    private String signingKey;
    private String verifierKey;
    private Signer signer;
    private SignatureVerifier verifier;
    
    public JwtEncryptor() {
    }
    
    public void setSigningKey(String rsaPrivateKey) {
        Assert.hasText(rsaPrivateKey);

        this.signingKey = rsaPrivateKey.trim();

        //true if the key has a public verifier
        if (this.signingKey.startsWith("-----BEGIN")) {
            signer = new RsaSigner(this.signingKey);
            logger.info("Configured with RSA signing key");
        }
        else {
            throw new IllegalArgumentException(
                    "SigningKey property must be a RSA private key");
        }
    }
    
    public void setVerifierKey(String rsaPublicKey) {
        Assert.hasText(rsaPublicKey);

        this.verifierKey = rsaPublicKey.trim();
        
        try {
            new RsaSigner(this.verifierKey);
            throw new IllegalArgumentException(
                    "Private key cannot be set as verifierKey property");
        } catch (Exception expected) {
            // Expected
        }
        
        this.verifier = new RsaVerifier(this.verifierKey);
    }
    
    /**
     * 生成JWT
     * @param claims Claims对象
     * @return JWT
     */
    public Jwt encode (BasicJwtClaims claims) {
        try {
            return JwtHelper.encode(JsonUtils.toJsonStr(claims), signer);
        } catch (JsonConvertException e) {
            throw new RuntimeException("Cannot convert object to JSON");
        }
    }
    
    /**
     * 解码token而不校验
     * 
     * @param jwtStr token
     * @return 解码后的Jwt对象
     * @throws JwtDecodeException 解码错误
     */
    private Jwt decodeIngoreVerify(String jwtStr) throws JwtDecodeException {
        try {
            return JwtHelper.decode(jwtStr);
        } catch (Exception e) {
            throw new JwtDecodeException("Cannot decode jwt string", e);
        }
    }
    
    /**
     * 解码并校验token签名
     * 
     * @param jwtStr token
     * @return 解码后的Jwt对象
     * @throws InvalidJwtException 验证不通过
     */
    public Jwt decode(String jwtStr) throws InvalidJwtException {
        try {
            Jwt jwt = this.decodeIngoreVerify(jwtStr);

            jwt.verifySignature(verifier);
            
            return jwt;
        } catch (Exception e) {
            throw new InvalidJwtException("Invalid jwt string", e);
        }
    }
    
    /**
     * 解码token的claims并校验claims的过期时间和生效时间
     * 
     * @param jwtStr token
     * @param claimsType claims对应的对象类型
     * @return claims对象
     * @throws InvalidJwtException 验证不通过
     */
    public <T extends JwtClaims> T decodeClaims(String jwtStr, Class<T> claimsType) throws InvalidJwtException {
        Jwt jwt = this.decode(jwtStr);
        try {
            T claims = JsonUtils.toObject(jwt.getClaims(), claimsType);
            
            Date now = new Date();
            
            Date expireTime = DateUtils.parseDateFromUnixTimestamp(claims.getExp() * 1000);
            if(expireTime != null && expireTime.compareTo(now) <= 0)
                throw new InvalidJwtException("token expired");
            
            Date effectiveTime = DateUtils.parseDateFromUnixTimestamp(claims.getNbf() * 1000);
            if(effectiveTime != null && effectiveTime.compareTo(now) > 0)
                throw new InvalidJwtException("token has not effective yet"); 
            
            return claims;
        } catch (JsonConvertException e) {
            throw new InvalidJwtException("Invalid jwt claims", e);
        }
    }
}
