package persistencia;

import java.io.RandomAccessFile;
import java.util.HashMap;

public class IndiceDireto {

    private HashMap<Integer, Long> mapa;
    private RandomAccessFile arquivo;

    public IndiceDireto(String nome) throws Exception {
        mapa = new HashMap<>();
        arquivo = new RandomAccessFile("dados/" + nome + "/indice.db", "rw");
        carregar();
    }

    public void put(int id, long endereco) throws Exception {
        mapa.put(id, endereco);
        salvar();
    }

    public Long get(int id) {
        return mapa.get(id);
    }

    public void remove(int id) throws Exception {
        mapa.remove(id);
        salvar();
    }

    private void salvar() throws Exception {
        arquivo.setLength(0);
        for (Integer id : mapa.keySet()) {
            arquivo.writeInt(id);
            arquivo.writeLong(mapa.get(id));
        }
    }

    private void carregar() throws Exception {
        arquivo.seek(0);
        while (arquivo.getFilePointer() < arquivo.length()) {
            int id = arquivo.readInt();
            long endereco = arquivo.readLong();
            mapa.put(id, endereco);
        }
    }

    public void close() throws Exception {
        arquivo.close();
    }
}
