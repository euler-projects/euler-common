/*
 * Copyright 2013-2024 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.eulerframework.common.util.jwt;

import java.util.Date;

import org.eulerframework.common.util.jwt.springcode.JwtHelper;
import org.eulerframework.common.util.jwt.springcode.crypto.sign.RsaSigner;
import org.eulerframework.common.util.jwt.springcode.crypto.sign.RsaVerifier;
import org.eulerframework.common.util.jwt.springcode.crypto.sign.SignatureVerifier;
import org.eulerframework.common.util.jwt.springcode.crypto.sign.Signer;
import org.eulerframework.common.base.log.LogSupport;
import org.eulerframework.common.util.Assert;
import org.eulerframework.common.util.DateUtils;
import org.eulerframework.common.util.json.JacksonUtils;

/**
 * Json Web Token 加密器/验证器
 *
 * <p>使用加密器功能需提供RSA私钥作为加密密钥
 * <p>使用验证器功能需提供RSA公钥作为校验密钥
 *
 * @author cFrost
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
        } else {
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
     *
     * @param claims Claims对象
     * @return JWT
     */
    public Jwt encode(JwtClaims claims) {
        if (signer == null)
            throw new RuntimeException("signingKey is null, cannot encode claims");

        return JwtHelper.encode(JacksonUtils.writeValueAsString(claims), signer);
    }

    /**
     * 解码并校验JWT字符串
     *
     * @param jwtStr JWT字符串
     * @return 解码后的Jwt对象
     * @throws InvalidJwtException 验证不通过
     */
    public Jwt decode(String jwtStr) throws InvalidJwtException {
        if (verifier == null)
            throw new RuntimeException("verifierKey is null, cannot dencode jwt");

        try {
            Jwt jwt = JwtHelper.decode(jwtStr);

            jwt.verifySignature(verifier);

            String claims = jwt.getClaims();

            Long exp = JacksonUtils.readKeyValue(claims, "exp", Long.class);
            Long nbf = JacksonUtils.readKeyValue(claims, "nbf", Long.class);

            Date now = new Date();

            if (exp != null) {
                Date expireTime = DateUtils.parseDateFromUnixTimestamp(exp);
                if (expireTime != null && expireTime.compareTo(now) <= 0)
                    throw new InvalidJwtException("token has expired");
            }

            if (nbf != null) {
                Date effectiveTime = DateUtils.parseDateFromUnixTimestamp(nbf);
                if (effectiveTime != null && effectiveTime.compareTo(now) > 0)
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
     * @param <T>        JwtClaims
     * @param jwtStr     JWT字符串
     * @param claimsType claims对应的对象类型
     * @return claims对象
     * @throws InvalidJwtException 验证不通过
     */
    public <T extends JwtClaims> T decode(String jwtStr, Class<T> claimsType) throws InvalidJwtException {
        Jwt jwt = this.decode(jwtStr);
        return JacksonUtils.readValue(jwt.getClaims(), claimsType);
    }
}
