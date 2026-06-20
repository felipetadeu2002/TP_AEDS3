package seguranca;

public class XORCipher {

    private static final char CHAVE = 'K';

    public static String encrypt(String texto) {
        if (texto == null)
            return null;

        char[] chars = texto.toCharArray();

        for (int i = 0; i < chars.length; i++) {
            chars[i] = (char) (chars[i] ^ CHAVE);
        }
        return new String(chars);
    }

    public static String decrypt(String texto) {
        return encrypt(texto);
    }
}