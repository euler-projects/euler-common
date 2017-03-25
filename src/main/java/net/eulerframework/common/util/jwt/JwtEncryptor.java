package net.eulerframework.common.util.jwt;

import java.util.Date;

import org.springframework.security.jwt.Jwt;
import org.springframework.security.jwt.JwtHelper;
import org.springframework.security.jwt.crypto.sign.RsaSigner;
import org.springframework.security.jwt.crypto.sign.RsaVerifier;
import org.springframework.security.jwt.crypto.sign.SignatureVerifier;
import org.springframework.security.jwt.crypto.sign.Signer;

import net.eulerframework.common.base.log.LogSupport;
import net.eulerframework.common.util.Assert;
import net.eulerframework.common.util.DateUtils;
import net.eulerframework.common.util.json.JsonConvertException;
import net.eulerframework.common.util.json.JsonUtils;

/**
 * Json Web Token 加密器/验证器
 * 
 * <p>使用加密器功能需提供RSA私钥作为加密密钥
 * <p>使用验证器功能需提供RSA公钥作为校验密钥
 * 
 * @author cFrost
 *
 */
public class JwtEncryptor extends LogSupport {
    
    private String signingKey;
    private String verifierKey;
    private Signer signer;
    private SignatureVerifier verifier;
    
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
    public Jwt encode (JwtClaims claims) {
        if(signer == null)
            throw new RuntimeException("signingKey is null, cannot encode claims");
        
        try {
            return JwtHelper.encode(JsonUtils.toJsonStr(claims), signer);
        } catch (JsonConvertException e) {
            throw new RuntimeException("Cannot convert object to JSON");
        }
    }
    
    /**
     * 解码并校验JWT字符串
     * 
     * @param jwtStr JWT字符串
     * @return 解码后的Jwt对象
     * @throws InvalidJwtException 验证不通过
     */
    public Jwt decode(String jwtStr) throws InvalidJwtException {
        if(verifier == null)
            throw new RuntimeException("verifierKey is null, cannot dencode jwt");
        
        try {
            Jwt jwt = JwtHelper.decode(jwtStr);

            jwt.verifySignature(verifier);
            
            String claims = jwt.getClaims();
            
            Long exp = JsonUtils.readKeyValue(claims, "exp", Long.class);
            Long nbf = JsonUtils.readKeyValue(claims, "nbf", Long.class);
            
            Date now = new Date();
            
            if(exp != null) {
                Date expireTime = DateUtils.parseDateFromUnixTimestamp(exp);
                if(expireTime != null && expireTime.compareTo(now) <= 0)
                    throw new InvalidJwtException("token has expired");                
            }
            
            if(nbf != null) {
                Date effectiveTime = DateUtils.parseDateFromUnixTimestamp(nbf);
                if(effectiveTime != null && effectiveTime.compareTo(now) > 0)
                    throw new InvalidJwtException("token has not effective yet");                
            }             
            
            return jwt;
        } catch (InvalidJwtException e) {
            throw e;
        } catch (Exception e) {
            throw new InvalidJwtException("Invalid jwt string", e);
        }
    }
    
    /**
     * 解码并校验JWT字符串,并把JWT的Claims以对象形式返回
     * 
     * @param T JwtClaims
     * @param jwtStr JWT字符串
     * @param claimsType claims对应的对象类型
     * @return claims对象
     * @throws InvalidJwtException 验证不通过
     */
    public <T extends JwtClaims> T decode(String jwtStr, Class<T> claimsType) throws InvalidJwtException {
        Jwt jwt = this.decode(jwtStr);
        try {
            T claims = JsonUtils.toObject(jwt.getClaims(), claimsType);
            return claims;
        } catch (JsonConvertException e) {
            throw new InvalidClaimsException("Invalid jwt claims", e);
        }
    }
}
