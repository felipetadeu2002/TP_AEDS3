package busca;

import java.util.ArrayList;

public class KMP {
    private static int[] construirLPS(String padrao) {
        int[] lps = new int[padrao.length()];
        int tamanho = 0;
        int i = 1;

        while (i < padrao.length()) {
            if (padrao.charAt(i) == padrao.charAt(tamanho)) {
                tamanho++;
                lps[i] = tamanho;
                i++;
            } else {
                if (tamanho != 0) {
                    tamanho = lps[tamanho - 1];
                } else {
                    lps[i] = 0;
                    i++;
                }
            }
        }
        return lps;
    }

    public static ArrayList<Integer> buscar(String texto, String padrao) {
        ArrayList<Integer> ocorrencias = new ArrayList<>();
        if (padrao.isEmpty()) {
            return ocorrencias;
        }

        int[] lps = construirLPS(padrao);
        int i = 0;
        int j = 0;

        while (i < texto.length()) {
            if (texto.charAt(i) == padrao.charAt(j)) {
                i++;
                j++;
            }

            if (j == padrao.length()) {
                ocorrencias.add(i - j);
                j = lps[j - 1];
            }

            else if (i < texto.length() && texto.charAt(i) != padrao.charAt(j)) {
                if (j != 0) {
                    j = lps[j - 1];
                } else {
                    i++;
                }
            }
        }
        return ocorrencias;
    }
}