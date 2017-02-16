package net.eulerframework.common.util.jwt;

/**
 * Basic Jwt Claims, Contains only the seven Registered Claim Names specified in RFC 7519
 * 
 * @author cFrost
 *
 */
public class BasicJwtClaims implements JwtClaims {

    private String iss;
    private String sub;
    private String aud;
    private Long exp;
    private Long nbf;
    private Long iat;
    private String jti;

    @Override
    public String getIss() {
        return iss;
    }

    public void setIss(String iss) {
        this.iss = iss;
    }

    @Override
    public String getSub() {
        return sub;
    }

    public void setSub(String sub) {
        this.sub = sub;
    }

    @Override
    public String getAud() {
        return aud;
    }

    public void setAud(String aud) {
        this.aud = aud;
    }

    @Override
    public Long getExp() {
        return exp;
    }

    public void setExp(Long exp) {
        this.exp = exp;
    }

    @Override
    public Long getNbf() {
        return nbf;
    }

    public void setNbf(Long nbf) {
        this.nbf = nbf;
        
    }

    @Override
    public Long getIat() {
        return iat;
    }

    public void setIat(Long iat) {
        this.iat = iat;
        
    }

    @Override
    public String getJti() {
        return jti;
    }

    public void setJti(String jti) {
        this.jti = jti;
    }

}
