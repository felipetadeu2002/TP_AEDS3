package compressao;

import java.io.*;
import java.util.ArrayList;

public class BackupManager {

    public static BackupInfo gerarBackupLZW()
            throws Exception {

        ByteArrayOutputStream buffer =
            new ByteArrayOutputStream();

        File pasta =
            new File("./dados");

        adicionarArquivos(
            pasta,
            buffer
        );

        byte[] original =
            buffer.toByteArray();

        ArrayList<Integer> comprimido =
            LZW.compress(original);

        DataOutputStream out =
            new DataOutputStream(
                new FileOutputStream(
                    "backup_lzw.bin"
                )
            );

        for (int codigo : comprimido) {
            out.writeInt(codigo);
        }

        out.close();

        long tamanhoOriginal =
            original.length;

        long tamanhoComprimido =
            new File(
                "backup_lzw.bin"
            ).length();

        double taxa =
            (1.0 - ((double) tamanhoComprimido
            / tamanhoOriginal))
            * 100.0;

        return new BackupInfo(
            tamanhoOriginal,
            tamanhoComprimido,
            taxa
        );
    }

    public static BackupInfo gerarBackupHuffman()
        throws Exception {

        ByteArrayOutputStream buffer =
            new ByteArrayOutputStream();

        File pasta =
            new File("./dados");

        adicionarArquivos(
            pasta,
            buffer
        );

        byte[] original =
            buffer.toByteArray();

        HuffmanResultado resultado =
            Huffman.compress(original);

        DataOutputStream out =
            new DataOutputStream(
                new FileOutputStream(
                    "backup_huffman.bin"
                )
            );

        out.writeUTF(
            resultado.bits
        );

        out.close();

        long tamanhoOriginal =
            original.length;

        long tamanhoComprimido =
            new File(
                "backup_huffman.bin"
            ).length();

        double taxa =
            (1.0
            - ((double) tamanhoComprimido
            / tamanhoOriginal))
            * 100.0;

        return new BackupInfo(
            tamanhoOriginal,
            tamanhoComprimido,
            taxa
        );
    }

    private static void adicionarArquivos(
            File pasta,
            ByteArrayOutputStream buffer)
            throws Exception {

        File[] arquivos =
            pasta.listFiles();

        if (arquivos == null)
            return;

        for (File arquivo : arquivos) {

            if (arquivo.isDirectory()) {

                adicionarArquivos(
                    arquivo,
                    buffer
                );

            } else {

                FileInputStream in =
                    new FileInputStream(
                        arquivo
                    );

                byte[] dados =
                    in.readAllBytes();

                buffer.write(dados);

                in.close();
            }
        }
    }

}