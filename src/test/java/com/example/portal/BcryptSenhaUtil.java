package com.example.portal;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * Rode como aplicação Java (Run) para imprimir um {@code senha_hash} BCrypt.
 * Argumento opcional: senha em texto (padrão {@code 123}).
 */
public final class BcryptSenhaUtil {

	private BcryptSenhaUtil() {}

	public static void main(String[] args) {
		String plain = args.length > 0 ? args[0] : "123";
		System.out.println(new BCryptPasswordEncoder().encode(plain));
	}
}
