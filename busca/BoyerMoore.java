package busca;

import java.util.ArrayList;

public class BoyerMoore {

    private static final int TAM_ALFABETO = 256;

    private static int[] construirBadChar(
            String padrao) {

        int[] badChar =
            new int[TAM_ALFABETO];

        for (int i = 0;
             i < TAM_ALFABETO;
             i++) {

            badChar[i] = -1;
        }

        for (int i = 0;
             i < padrao.length();
             i++) {

            badChar[
                padrao.charAt(i)
            ] = i;
        }

        return badChar;
    }

    public static ArrayList<Integer> buscar(
            String texto,
            String padrao) {

        ArrayList<Integer> ocorrencias =
            new ArrayList<>();

        int[] badChar =
            construirBadChar(padrao);

        int m = padrao.length();
        int n = texto.length();

        int deslocamento = 0;

        while (deslocamento <= (n - m)) {

            int j = m - 1;

            while (
                j >= 0 &&
                padrao.charAt(j)
                    ==
                texto.charAt(
                    deslocamento + j
                )
            ) {

                j--;
            }

            if (j < 0) {

                ocorrencias.add(
                    deslocamento
                );

                deslocamento +=
                    (deslocamento + m < n)
                    ?
                    m - badChar[
                        texto.charAt(
                            deslocamento + m
                        )
                    ]
                    :
                    1;

            } else {

                deslocamento +=
                    Math.max(
                        1,
                        j - badChar[
                            texto.charAt(
                                deslocamento + j
                            )
                        ]
                    );
            }
        }

        return ocorrencias;
    }
}