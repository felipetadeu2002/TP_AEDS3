package compressao;

public class HuffmanNode
        implements Comparable<HuffmanNode> {

    byte simbolo;

    int frequencia;

    HuffmanNode esquerda;

    HuffmanNode direita;

    public HuffmanNode(byte simbolo,
                       int frequencia) {

        this.simbolo = simbolo;
        this.frequencia = frequencia;
    }

    public HuffmanNode(int frequencia,
                       HuffmanNode esquerda,
                       HuffmanNode direita) {

        this.frequencia = frequencia;
        this.esquerda = esquerda;
        this.direita = direita;
    }

    public boolean folha() {

        return esquerda == null
            && direita == null;
    }

    @Override
    public int compareTo(HuffmanNode outro) {

        return Integer.compare(
            this.frequencia,
            outro.frequencia
        );
    }
}