package compressao;

import java.util.HashMap;

public class HuffmanResultado {

    public String bits;

    public HuffmanNode raiz;

    public HashMap<Byte,String> codigos;

    public HuffmanResultado(
            String bits,
            HuffmanNode raiz,
            HashMap<Byte,String> codigos) {

        this.bits = bits;
        this.raiz = raiz;
        this.codigos = codigos;
    }
}