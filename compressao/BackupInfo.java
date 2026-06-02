package compressao;

public class BackupInfo {

    public long tamanhoOriginal;

    public long tamanhoComprimido;

    public double taxaCompressao;

    public BackupInfo(
            long tamanhoOriginal,
            long tamanhoComprimido,
            double taxaCompressao) {

        this.tamanhoOriginal = tamanhoOriginal;
        this.tamanhoComprimido = tamanhoComprimido;
        this.taxaCompressao = taxaCompressao;
    }
}