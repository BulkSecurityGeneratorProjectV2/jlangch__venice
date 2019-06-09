/*   __    __         _
 *   \ \  / /__ _ __ (_) ___ ___ 
 *    \ \/ / _ \ '_ \| |/ __/ _ \
 *     \  /  __/ | | | | (_|  __/
 *      \/ \___|_| |_|_|\___\___|
 *
 *
 * Copyright 2017-2019 Venice
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.jlangch.venice.impl.util.crypt;

import java.util.Base64;

import javax.crypto.Cipher;


/**
 * Encrypts and decrypts text messages using a passphrase based on a 
 * specific cypher
 * 
 * <p>Note: <tt>CipherSuite</tt> is THREAD SAFE</p>
 */
public class CipherSuite {
	
	/**
	 * Constructor.
	 * 
	 * @param encrypt An encrypt cipher
	 * @param decrypt An decrypt cipher
	 * @param cipherPrefix A cipher prefix (E.g. {DES})
	 */
	public CipherSuite(final Cipher encrypt, final Cipher decrypt, final String cipherPrefix) {
		if (encrypt == null) {
			throw new IllegalArgumentException("An encrypt cipher must not be null");
		}
		if (decrypt == null) {
			throw new IllegalArgumentException("A decrypt cipher must not be null");
		}
		if (cipherPrefix == null || cipherPrefix.length() == 0) {
			throw new IllegalArgumentException("A cipherPrefix must not be null or empty");
		}

        this.cipherEncrypt = encrypt;
        this.cipherDecrypt = decrypt;
        this.cipherPrefix = cipherPrefix;
	}
	
	/**
	 * Encrypts a text message.
	 * 
	 * @param message A clear-text message
	 * @return A Base64 encoded and encryted string
	 */
	public synchronized String encrypt(final String message) {
		if (message == null) return null;
		if (message.length() == 0) return "";
		
		try {
			// Encode the string into bytes using utf-8
			final byte[] utf8 = message.getBytes("UTF-8");
						
			// Encrypt
			final byte[] enc = this.cipherEncrypt.doFinal(utf8);
			
			// Encode bytes to base64 to get a string (no chunks and URL safe)
			return Base64.getEncoder().encodeToString(enc);
		}
		catch(Exception ex) {
			throw new EncryptionException("Failed to encrypt message.", ex);
		}
    }

	/**
	 * Decrypts a Base64 encrypted string.
	 * 
	 * @param message A Base64 encoded and encrypted string
	 * @return The decrypted clear-text message
	 */
	public synchronized String decrypt(final String message) {
		if (message == null) return null;
		if (message.length() == 0) return "";

		try {
			// Remove the option encryption marker and decode base64 to get bytes
			final byte[] dec = Base64.getDecoder().decode(message.getBytes("UTF-8"));

			// Decrypt
			return new String(cipherDecrypt.doFinal(dec), "UTF-8");
		}
		catch(Exception ex) {
			throw new EncryptionException("Failed to decrypt message.", ex);
		} 
    }

	/**
	 * @return this cipher's name
	 */
	public String getCipherAlgorithm() {
		return cipherEncrypt.getAlgorithm();
	}

	/**
	 * @return this cipher's prefix
	 */
	public String getCipherPrefix() {
		return cipherPrefix;
	}
	

    private final Cipher cipherEncrypt;
	private final Cipher cipherDecrypt;	
	private final String cipherPrefix;
}
