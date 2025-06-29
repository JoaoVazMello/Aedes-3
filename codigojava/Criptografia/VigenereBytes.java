package codigojava.Criptografia;

import java.nio.charset.StandardCharsets;

public class VigenereBytes {

    /**
     * Criptografa um vetor de bytes com a cifra de Vigenère.
     *
     * @param data Os bytes do arquivo.
     * @param key  A chave de criptografia.
     * @return Os bytes criptografados.
     */
    public static byte[] encrypt(byte[] data, String key) {
        byte[] keyBytes = key.getBytes(StandardCharsets.UTF_8);
        byte[] result = new byte[data.length];

        for (int i = 0; i < data.length; i++) {
            result[i] = (byte) ((data[i] + keyBytes[i % keyBytes.length]) & 0xFF);
        }

        return result;
    }

    /**
     * Descriptografa um vetor de bytes criptografados com a cifra de Vigenère.
     *
     * @param data Os bytes criptografados.
     * @param key  A chave de criptografia.
     * @return Os bytes originais.
     */
    public static byte[] decrypt(byte[] data, String key) {
        byte[] keyBytes = key.getBytes(StandardCharsets.UTF_8);
        byte[] result = new byte[data.length];

        for (int i = 0; i < data.length; i++) {
            result[i] = (byte) ((data[i] - keyBytes[i % keyBytes.length] + 256) & 0xFF);
        }

        return result;
    }
}
