package compressao;

import java.util.HashMap;
import java.util.PriorityQueue;

public class Huffman {

    private static void gerarCodigos(HuffmanNode no, String codigo, HashMap<Byte, String> mapa) {
        if (no == null)
            return;

        if (no.folha()) {
            mapa.put(no.simbolo, codigo);
            return;
        }

        gerarCodigos(no.esquerda, codigo + "0", mapa);
        gerarCodigos(no.direita, codigo + "1", mapa);
    }

    public static HuffmanResultado compress(byte[] dados) {
        int[] frequencias = new int[256];

        for (byte b : dados) {
            frequencias[b & 0xFF]++;
        }

        PriorityQueue<HuffmanNode> fila = new PriorityQueue<>();

        for (int i = 0; i < 256; i++) {
            if (frequencias[i] > 0) {
                fila.add(new HuffmanNode((byte) i,frequencias[i]));
            }
        }

        while (fila.size() > 1) {
            HuffmanNode esquerda = fila.poll();
            HuffmanNode direita = fila.poll();
            HuffmanNode pai = new HuffmanNode(esquerda.frequencia + direita.frequencia, esquerda, direita);
            fila.add(pai);
        }

        HuffmanNode raiz = fila.poll();
        HashMap<Byte, String> codigos = new HashMap<>();
        gerarCodigos(raiz, "", codigos);
        StringBuilder bits = new StringBuilder();

        for (byte b : dados) {
            bits.append(codigos.get(b));
        }

        return new HuffmanResultado(bits.toString(), raiz, codigos);
    }

    public static byte[] decompress(String bits, HuffmanNode raiz) {
        StringBuilder texto = new StringBuilder();
        HuffmanNode atual = raiz;

        for (int i = 0; i < bits.length(); i++) {
            if (bits.charAt(i) == '0') {
                atual = atual.esquerda;
            } else {
                atual = atual.direita;
            }

            if (atual.folha()) {
                texto.append((char)(atual.simbolo & 0xFF));
                atual = raiz;
            }
        }

        byte[] resultado = new byte[texto.length()];

        for (int i = 0; i < texto.length(); i++) {
            resultado[i] = (byte) texto.charAt(i);
        }

        return resultado;
    }
}