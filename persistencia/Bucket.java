package persistencia;

import java.io.*;

public class Bucket {
    public int profundidadeLocal;
    public int quantidade;
    public int[] ids;
    public long[] enderecos;
    public static final int TAM_MAX = 4;

    public Bucket(int p) {
        profundidadeLocal = p;
        quantidade = 0;
        ids = new int[TAM_MAX];
        enderecos = new long[TAM_MAX];
    }

    public boolean cheio() {
        return quantidade == TAM_MAX;
    }

    public void add(int id, long endereco) {
        ids[quantidade] = id;
        enderecos[quantidade] = endereco;
        quantidade++;
    }
}