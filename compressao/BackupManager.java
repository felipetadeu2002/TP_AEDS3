package compressao;

import java.io.*;
import java.util.ArrayList;

public class BackupManager {

    public static BackupInfo gerarBackupLZW() throws Exception {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        File pasta = new File("./dados");

        adicionarArquivos(pasta, buffer);

        byte[] original = buffer.toByteArray();
        ArrayList<Integer> comprimido = LZW.compress(original);
        DataOutputStream out = new DataOutputStream(new FileOutputStream("backup_lzw.bin"));

        for (int codigo : comprimido) {
            out.writeShort(codigo);
        }

        out.close();

        long tamanhoOriginal = original.length;
        long tamanhoComprimido = new File("backup_lzw.bin").length();
        double taxa = (1.0 - ((double) tamanhoComprimido / tamanhoOriginal))* 100.0;

        return new BackupInfo(tamanhoOriginal, tamanhoComprimido, taxa);
    }

    public static BackupInfo gerarBackupHuffman() throws Exception {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        File pasta = new File("./dados");

        adicionarArquivos(pasta, buffer);

        byte[] original = buffer.toByteArray();
        HuffmanResultado resultado = Huffman.compress(original);
        DataOutputStream out = new DataOutputStream(new FileOutputStream("backup_huffman.bin"));
        byte[] compactado = bitsParaBytes(resultado.bits);

        out.writeInt(resultado.bits.length());
        out.write(compactado);
        out.close();

        long tamanhoOriginal = original.length;

        long tamanhoComprimido = new File("backup_huffman.bin").length();
        double taxa =(1.0 - ((double) tamanhoComprimido / tamanhoOriginal))* 100.0;

        return new BackupInfo(tamanhoOriginal, tamanhoComprimido, taxa);
    }

    private static void adicionarArquivos(File pasta, ByteArrayOutputStream buffer) throws Exception {
        File[] arquivos = pasta.listFiles();

        if (arquivos == null)
            return;

        for (File arquivo : arquivos) {
            if (arquivo.isDirectory()) {
                adicionarArquivos(arquivo, buffer);
            } else {
                FileInputStream in = new FileInputStream(arquivo);
                byte[] dados = in.readAllBytes();
                buffer.write(dados);
                in.close();
            }
        }
    }

    private static byte[] bitsParaBytes(String bits) {
        int tamanho = (bits.length() + 7) / 8;
        byte[] bytes = new byte[tamanho];

        for (int i = 0; i < bits.length(); i++) {
            if (bits.charAt(i) == '1') {
                bytes[i / 8] |= (1 << (7 - (i % 8)));
            }
        }
    return bytes;
    }
}