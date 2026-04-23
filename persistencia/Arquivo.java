
package persistencia;

import java.io.*;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import model.Registro;


public class Arquivo<T extends Registro> {
    public static final int TAM_CABECALHO = 4 + 8; // int + long (último id + head free list)
    private RandomAccessFile arquivo;
    private String nomeArquivo;
    private Constructor<T> construtor;
    private IndiceDireto indice;

    public Arquivo(String nomeArquivo, Constructor<T> construtor) throws Exception {
        File diretorio = new File("./dados");
        if (!diretorio.exists())
            diretorio.mkdir();

        diretorio = new File("./dados/" + nomeArquivo);
        if (!diretorio.exists())
            diretorio.mkdir();

        this.nomeArquivo = "./dados/" + nomeArquivo + "/" + nomeArquivo + ".db";
        this.construtor = construtor;
        this.arquivo = new RandomAccessFile(this.nomeArquivo, "rw");
        this.indice = new IndiceDireto(nomeArquivo);

        if (arquivo.length() < TAM_CABECALHO) {
            arquivo.seek(0);
            arquivo.writeInt(0); // Último ID usado
            arquivo.writeLong(-1); // Lista de registros excluídos (ponteiro)
        }
    }

    // Cria e retorna o endereço físico do registro criado
    public long createWithAddress(T obj) throws Exception {
        arquivo.seek(0);
        int novoID = arquivo.readInt() + 1;
        arquivo.seek(0);
        arquivo.writeInt(novoID);
        obj.setId(novoID);
        byte[] dados = obj.toByteArray();

        long endereco = getDeleted(dados.length);
        if (endereco == -1) {
            arquivo.seek(arquivo.length());
            endereco = arquivo.getFilePointer();
        } else {
            arquivo.seek(endereco);
        }

        arquivo.writeByte(' ');
        arquivo.writeShort(dados.length);
        arquivo.write(dados);

        indice.put(obj.getId(), endereco);
        return endereco;
    }

    // Leitura por id (varredura)
    public T read(int id) throws Exception {
        Long endereco = indice.get(id);
        if (endereco == null) {
            return null;
        }
        return readAt(endereco);
    }

    // Leitura por endereço físico
    public T readAt(long endereco) throws Exception {
        arquivo.seek(endereco);
        byte lapide = arquivo.readByte();
        short tamanho = arquivo.readShort();

        if (lapide != ' ') {
            return null;
        }
        byte[] dados = new byte[tamanho];
        arquivo.read(dados);
        T obj = construtor.newInstance();
        obj.fromByteArray(dados);

        return obj;
    }


    public boolean update(T novoObj) throws Exception {
        Long endereco = indice.get(novoObj.getId());
        if (endereco == null) {
            return false;
        } 

        arquivo.seek(endereco);
        byte lapide = arquivo.readByte();
        short tamanhoAntigo = arquivo.readShort();
        byte[] novoRegistro = novoObj.toByteArray();

        if (novoRegistro.length <= tamanhoAntigo) {
            arquivo.seek(endereco + 3);
            arquivo.write(novoRegistro);

            // limpa resto (boa prática)
            for (int i = novoRegistro.length; i < tamanhoAntigo; i++) {
                arquivo.writeByte(0);
            }
        } else {
            // marca como deletado
            arquivo.seek(endereco);
            arquivo.writeByte('*');
            addDeleted(tamanhoAntigo, endereco);

            // cria novo registro
            createWithAddress(novoObj); // índice já atualizado aqui
        }
        return true;
    }

    public boolean delete(int id) throws Exception {
        Long endereco = indice.get(id);
        if (endereco == null) {
            return false;
        }
        arquivo.seek(endereco);
        byte lapide = arquivo.readByte();
        short tamanho = arquivo.readShort();
        arquivo.seek(endereco);
        arquivo.writeByte('*');
        addDeleted(tamanho, endereco);
        indice.remove(id);
        return true;
    }

    public void addDeleted(int tamanhoEspaco, long enderecoEspaco) throws Exception {
        long posicao = 4;
        arquivo.seek(posicao);
        long endereco = arquivo.readLong();
        long proximo;

        if (endereco == -1) {
            arquivo.seek(4);
            arquivo.writeLong(enderecoEspaco);
            arquivo.seek(enderecoEspaco + 3);
            arquivo.writeLong(-1);
        } else {
            do {
                arquivo.seek(endereco + 1);
                int tamanho = arquivo.readShort();
                proximo = arquivo.readLong();

                if (tamanho > tamanhoEspaco) {
                    if (posicao == 4){
                        arquivo.seek(posicao);
                    } else {
                        arquivo.seek(posicao + 3);
                    }
                    arquivo.writeLong(enderecoEspaco);
                    arquivo.seek(enderecoEspaco + 3);
                    arquivo.writeLong(endereco);
                    break;
                }
                if (proximo == -1) {
                    arquivo.seek(endereco + 3);
                    arquivo.writeLong(enderecoEspaco);
                    arquivo.seek(enderecoEspaco + 3);
                    arquivo.writeLong(-1);
                    break;
                }
                posicao = endereco;
                endereco = proximo;
            } while (endereco != -1);
        }
    }

    private long getDeleted(int tamanhoNecessario) throws Exception {
        long posicao = 4;
        arquivo.seek(posicao);
        long endereco = arquivo.readLong();
        long proximo;
        int tamanho;

        while (endereco != -1) {
            arquivo.seek(endereco + 1);
            tamanho = arquivo.readShort();
            proximo = arquivo.readLong();

            if (tamanho >= tamanhoNecessario) {
                if (posicao == 4) {
                    arquivo.seek(posicao);
                } else {
                    arquivo.seek(posicao + 3);
                }
                arquivo.writeLong(proximo);
                return endereco;
            }
            posicao = endereco;
            endereco = proximo;
        }
        return -1;
    }

    public ArrayList<T> readAll() throws Exception {
        ArrayList<T> lista = new ArrayList<>();
        arquivo.seek(TAM_CABECALHO);

        while (arquivo.getFilePointer() < arquivo.length()) {
            byte lapide = arquivo.readByte();
            short tamanho = arquivo.readShort();
            byte[] dados = new byte[tamanho];
            arquivo.read(dados);

            if (lapide == ' ') {
                T obj = construtor.newInstance();
                obj.fromByteArray(dados);
                lista.add(obj);
            }
        }
        return lista;
    }

    public void close() throws Exception {
        arquivo.close();
        indice.close();
    }
}
