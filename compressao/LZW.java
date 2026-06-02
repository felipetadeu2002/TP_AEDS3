package compressao;

import java.util.ArrayList;
import java.util.HashMap;

public class LZW {

    public static ArrayList<Integer> compress(byte[] dados) {

        HashMap<String, Integer> dicionario = new HashMap<>();

        for (int i = 0; i < 256; i++) {
            dicionario.put("" + (char) i, i);
        }

        int codigo = 256;

        String atual = "";

        ArrayList<Integer> saida = new ArrayList<>();

        for (byte b : dados) {

            char c = (char) (b & 0xFF);

            String combinado = atual + c;

            if (dicionario.containsKey(combinado)) {

                atual = combinado;

            } else {

                saida.add(dicionario.get(atual));

                dicionario.put(combinado, codigo++);

                atual = "" + c;
            }
        }

        if (!atual.isEmpty()) {
            saida.add(dicionario.get(atual));
        }

        return saida;
    }

    public static byte[] decompress(ArrayList<Integer> comprimido) {

    HashMap<Integer, String> dicionario =
        new HashMap<>();

    for (int i = 0; i < 256; i++) {
        dicionario.put(i, "" + (char) i);
    }

    int codigo = 256;

    String anterior =
        dicionario.get(comprimido.get(0));

    StringBuilder resultado =
        new StringBuilder(anterior);

    for (int i = 1; i < comprimido.size(); i++) {

        int atualCodigo =
            comprimido.get(i);

        String entrada;

        if (dicionario.containsKey(atualCodigo)) {

            entrada =
                dicionario.get(atualCodigo);

        } else {

            entrada =
                anterior + anterior.charAt(0);
        }

        resultado.append(entrada);

        dicionario.put(
            codigo++,
            anterior + entrada.charAt(0)
        );

        anterior = entrada;
    }

    byte[] bytes =
        new byte[resultado.length()];

    for (int i = 0; i < resultado.length(); i++) {
        bytes[i] = (byte) resultado.charAt(i);
    }

    return bytes;
    }
}