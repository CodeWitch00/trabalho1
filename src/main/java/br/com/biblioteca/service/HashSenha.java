package br.com.biblioteca.service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Base64;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

final class HashSenha {
    private static final String ALGORITMO = "PBKDF2WithHmacSHA256";
    private static final int ITERACOES = 210_000;
    private static final int TAMANHO_SAL = 16;
    private static final int TAMANHO_HASH_BITS = 256;

    private HashSenha() {
    }

    static String gerar(String senha) {
        byte[] sal = new byte[TAMANHO_SAL];
        new SecureRandom().nextBytes(sal);
        byte[] hash = derivar(senha.toCharArray(), sal, ITERACOES);
        Base64.Encoder encoder = Base64.getEncoder();
        return "pbkdf2-sha256$" + ITERACOES + "$" + encoder.encodeToString(sal)
            + "$" + encoder.encodeToString(hash);
    }

    static boolean verificar(String senha, String valorArmazenado) {
        try {
            String[] partes = valorArmazenado.split("\\$", -1);
            if (partes.length != 4 || !"pbkdf2-sha256".equals(partes[0])) {
                return false;
            }
            int iteracoes = Integer.parseInt(partes[1]);
            byte[] sal = Base64.getDecoder().decode(partes[2]);
            byte[] esperado = Base64.getDecoder().decode(partes[3]);
            byte[] obtido = derivar(senha.toCharArray(), sal, iteracoes);
            return MessageDigest.isEqual(esperado, obtido);
        } catch (IllegalArgumentException excecao) {
            return false;
        }
    }

    private static byte[] derivar(char[] senha, byte[] sal, int iteracoes) {
        try {
            KeySpec spec = new PBEKeySpec(senha, sal, iteracoes, TAMANHO_HASH_BITS);
            return SecretKeyFactory.getInstance(ALGORITMO).generateSecret(spec).getEncoded();
        } catch (NoSuchAlgorithmException | InvalidKeySpecException excecao) {
            throw new IllegalStateException("Algoritmo de hash de senha indisponivel", excecao);
        }
    }
}
