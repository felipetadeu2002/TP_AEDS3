package view;

import compressao.BackupInfo;
import compressao.BackupManager;

import javax.swing.*;
import java.awt.*;

public class BackupPanel extends JPanel {

    private JTextArea resultado;

    public BackupPanel() {

        setLayout(new BorderLayout());

        JPanel botoes =
            new JPanel();

        JButton btnLZW =
            new JButton("Gerar Backup LZW");

        JButton btnHuffman =
            new JButton("Gerar Backup Huffman");

        botoes.add(btnLZW);
        botoes.add(btnHuffman);

        resultado =
            new JTextArea();

        resultado.setEditable(false);

        add(botoes, BorderLayout.NORTH);

        add(
            new JScrollPane(resultado),
            BorderLayout.CENTER
        );

        btnLZW.addActionListener(e -> {

            try {

                BackupInfo info =
                    BackupManager
                        .gerarBackupLZW();

                resultado.setText(
                    "=== LZW ===\n\n" +
                    "Original: "
                    + info.tamanhoOriginal
                    + " bytes\n\n" +

                    "Comprimido: "
                    + info.tamanhoComprimido
                    + " bytes\n\n" +

                    "Taxa: "
                    + String.format(
                        "%.2f",
                        info.taxaCompressao
                    )
                    + "%"
                );

            } catch (Exception ex) {

                JOptionPane.showMessageDialog(
                    this,
                    ex.getMessage()
                );
            }
        });

        btnHuffman.addActionListener(e -> {

            try {

                BackupInfo info =
                    BackupManager
                        .gerarBackupHuffman();

                resultado.setText(
                    "=== HUFFMAN ===\n\n" +
                    "Original: "
                    + info.tamanhoOriginal
                    + " bytes\n\n" +

                    "Comprimido: "
                    + info.tamanhoComprimido
                    + " bytes\n\n" +

                    "Taxa: "
                    + String.format(
                        "%.2f",
                        info.taxaCompressao
                    )
                    + "%"
                );

            } catch (Exception ex) {

                JOptionPane.showMessageDialog(
                    this,
                    ex.getMessage()
                );
            }
        });
    }
}
