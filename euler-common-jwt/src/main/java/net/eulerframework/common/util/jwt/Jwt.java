package net.eulerframework.common.util.jwt;

import net.eulerframework.common.util.jwt.springcode.BinaryFormat;
import net.eulerframework.common.util.jwt.springcode.crypto.sign.SignatureVerifier;

/**
 * @author Luke Taylor
 */
public interface Jwt extends BinaryFormat {
	String getClaims();

	String getEncoded();

	void verifySignature(SignatureVerifier verifier);
}
