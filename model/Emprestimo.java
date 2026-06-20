package model;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Emprestimo implements Registro {
    private int id;
    private int idUsuario;
    private int idFilme;
    private int idFuncionario;
    private long dataEmprestimo;
    private long dataDevolucaoPrevista;
    private long dataDevolucaoReal;
    private String status;

    public Emprestimo() {}

    public Emprestimo(int idUsuario, int idFilme, int idFuncionario, long dataEmprestimo, long dataDevolucaoPrevista, String status) {
        this.idUsuario = idUsuario;
        this.idFilme = idFilme;
        this.idFuncionario = idFuncionario;
        this.dataEmprestimo = dataEmprestimo;
        this.dataDevolucaoPrevista = dataDevolucaoPrevista;
        this.dataDevolucaoReal = 0;
        this.status = status;
    }

    @Override public int getId()        { return id; }
    @Override public void setId(int id) { this.id = id; }
    public int getIdUsuario()           { return idUsuario; }
    public int getIdFilme()             { return idFilme; }
    public int getIdFuncionario()       { return idFuncionario; }
    public long getDataEmprestimo()     { return dataEmprestimo; }
    public long getDataDevolucaoPrevista() { return dataDevolucaoPrevista; }
    public long getDataDevolucaoReal()  { return dataDevolucaoReal; }
    public String getStatus()           { return status; }
    public void setStatus(String s)     { this.status = s; }
    public void setDataDevolucaoReal(long d) { this.dataDevolucaoReal = d; }

    public static String formatarData(long ms) {
        if (ms == 0) return "-";
        return new SimpleDateFormat("dd/MM/yyyy").format(new Date(ms));
    }

    @Override
    public byte[] toByteArray() throws IOException {
        ByteArrayOutputStream ba = new ByteArrayOutputStream();
        DataOutputStream da = new DataOutputStream(ba);
        da.writeInt(id);
        da.writeInt(idUsuario);
        da.writeInt(idFilme);
        da.writeInt(idFuncionario);
        da.writeLong(dataEmprestimo);
        da.writeLong(dataDevolucaoPrevista);
        da.writeLong(dataDevolucaoReal);
        da.writeUTF(status == null ? "" : status);
        return ba.toByteArray();
    }

    @Override
    public void fromByteArray(byte[] ba) throws IOException {
        ByteArrayInputStream bi = new ByteArrayInputStream(ba);
        DataInputStream di = new DataInputStream(bi);
        id = di.readInt();
        idUsuario = di.readInt();
        idFilme = di.readInt();
        idFuncionario = di.readInt();
        dataEmprestimo = di.readLong();
        dataDevolucaoPrevista = di.readLong();
        dataDevolucaoReal = di.readLong();
        status = di.readUTF();
    }

    @Override
    public String toString() {
        return "Emprestimo[id=" + id + ", usuario=" + idUsuario + ", filme=" + idFilme + ", status=" + status + "]";
    }
}
