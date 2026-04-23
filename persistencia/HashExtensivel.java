package persistencia;

import java.io.*;
import java.util.ArrayList;

public class HashExtensivel {
    private RandomAccessFile diretorio;
    private RandomAccessFile buckets;
    private int profundidadeGlobal;

    public HashExtensivel(String nome) throws Exception {
        diretorio = new RandomAccessFile("./dados/" + nome + "/diretorio.db", "rw");
        buckets = new RandomAccessFile("./dados/" + nome + "/buckets.db", "rw");

        if (diretorio.length() == 0) {
            profundidadeGlobal = 1;
            diretorio.writeInt(profundidadeGlobal);

            // cria 2 buckets iniciais
            for (int i = 0; i < 2; i++) {
                long pos = criarBucket(1);
                diretorio.writeLong(pos);
            }
        } else {
            diretorio.seek(0);
            profundidadeGlobal = diretorio.readInt();
        }
    }

    private long criarBucket(int profundidade) throws Exception {
        long pos = buckets.length();
        buckets.seek(pos);
        buckets.writeInt(profundidade); // profundidade local
        buckets.writeInt(0); // quantidade

        for (int i = 0; i < Bucket.TAM_MAX; i++) {
            buckets.writeInt(-1);
            buckets.writeLong(-1);
        }
        return pos;
    }

    private int hash(int chave) {
        return chave & ((1 << profundidadeGlobal) - 1);
    }

    private void inserir(long posBucket, int id, long endereco, int qtd) throws Exception {
        buckets.seek(posBucket + 8 + qtd * 12);
        buckets.writeInt(id);
        buckets.writeLong(endereco);
        buckets.seek(posBucket + 4);
        buckets.writeInt(qtd + 1);
    }

    private int getQtd(long posBucket) throws Exception {
        buckets.seek(posBucket + 4);
        return buckets.readInt();
    }

    private void duplicarDiretorio() throws Exception {
        int tamanho = 1 << profundidadeGlobal;
        diretorio.seek(4);
        long[] ponteiros = new long[tamanho];

        for (int i = 0; i < tamanho; i++) {
            ponteiros[i] = diretorio.readLong();
        }
        profundidadeGlobal++;
        diretorio.setLength(0);
        diretorio.seek(0);
        diretorio.writeInt(profundidadeGlobal);

        for (int i = 0; i < tamanho * 2; i++) {
            diretorio.writeLong(ponteiros[i % tamanho]);
        }
    }

    private void atualizarDiretorio(long bucketAntigo, long bucketNovo, int pLocal) throws Exception {
        int tamanho = 1 << profundidadeGlobal;

        for (int i = 0; i < tamanho; i++) {
            diretorio.seek(4 + i * 8);
            long ptr = diretorio.readLong();

            if (ptr == bucketAntigo) {
                if ((i & (1 << (pLocal - 1))) != 0) {
                    diretorio.seek(4 + i * 8);
                    diretorio.writeLong(bucketNovo);
                }
            }
        }
    }

    private void split(int posDir, long posBucket) throws Exception {
        buckets.seek(posBucket);
        int pLocal = buckets.readInt();
        int qtd = buckets.readInt();
        int[] ids = new int[qtd];
        long[] ends = new long[qtd];

        for (int i = 0; i < qtd; i++) {
            ids[i] = buckets.readInt();
            ends[i] = buckets.readLong();
        }
        pLocal++;

        if (pLocal > profundidadeGlobal) {
            duplicarDiretorio();
        }
        long novoBucket = criarBucket(pLocal);

        // Limpa bucket antigo
        buckets.seek(posBucket);
        buckets.writeInt(pLocal);
        buckets.writeInt(0);

        // Redistribui
        for (int i = 0; i < qtd; i++) {
            int h = hash(ids[i]);
            diretorio.seek(4 + h * 8);
            long destino = diretorio.readLong();

            if ((hash(ids[i]) & (1 << (pLocal - 1))) == 0) {
                inserir(posBucket, ids[i], ends[i], getQtd(posBucket));
            } else {
                inserir(novoBucket, ids[i], ends[i], getQtd(novoBucket));
            }
        }
        atualizarDiretorio(posBucket, novoBucket, pLocal);
    }

    public void add(int id, long endereco) throws Exception {
        int posDir = hash(id);
        diretorio.seek(4 + posDir * 8);
        long posBucket = diretorio.readLong();
        buckets.seek(posBucket);
        int pLocal = buckets.readInt();
        int qtd = buckets.readInt();

        if (qtd < Bucket.TAM_MAX) {
            inserir(posBucket, id, endereco, qtd);
        } else {
            split(posDir, posBucket);
            add(id, endereco); // tenta de novo após split
        }
    }

    public ArrayList<Long> get(int id) throws Exception {
        ArrayList<Long> lista = new ArrayList<>();
        int posDir = hash(id);
        diretorio.seek(4 + posDir * 8);
        long posBucket = diretorio.readLong();
        buckets.seek(posBucket);
        int pLocal = buckets.readInt();
        int qtd = buckets.readInt();

        for (int i = 0; i < qtd; i++) {
            int idLido = buckets.readInt();
            long end = buckets.readLong();

            if (idLido == id) {
                lista.add(end);
            }
        }
        return lista;
    }
}
