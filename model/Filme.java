package model;

import java.io.*;

public class Filme implements Registro {
    private int idFilme;
    private String titulo;
    private String diretor;
    private int anoLancamento;
    private int duracao;
    private String generos;
    private String tags;

    public Filme() {}

    public Filme(int idFilme, String titulo, String diretor, int ano, int duracao, String generos, String tags) {
        this.idFilme = idFilme;
        this.titulo = titulo;
        this.diretor = diretor;
        this.anoLancamento = ano;
        this.duracao = duracao;
        this.generos = generos;
        this.tags = tags;
    }

    @Override public void setId(int id) { this.idFilme = id; }
    @Override public int getId()        { return this.idFilme; }
    public String getTitulo()           { return titulo; }
    public String getDiretor()          { return diretor; }
    public int getAnoLancamento()       { return anoLancamento; }
    public int getDuracao()             { return duracao; }
    public String getGeneros()          { return generos; }
    public String getTags()             { return tags; }

    @Override
    public byte[] toByteArray() throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        dos.writeInt(idFilme);
        dos.writeUTF(titulo == null ? "" : titulo);
        dos.writeUTF(diretor == null ? "" : diretor);
        dos.writeInt(anoLancamento);
        dos.writeInt(duracao);
        dos.writeUTF(generos == null ? "" : generos);
        dos.writeUTF(tags == null ? "" : tags);
        return baos.toByteArray();
    }

    @Override
    public void fromByteArray(byte[] ba) throws Exception {
        ByteArrayInputStream bais = new ByteArrayInputStream(ba);
        DataInputStream dis = new DataInputStream(bais);
        this.idFilme = dis.readInt();
        this.titulo = dis.readUTF();
        this.diretor = dis.readUTF();
        this.anoLancamento = dis.readInt();
        this.duracao = dis.readInt();
        this.generos = dis.readUTF();
        this.tags = dis.readUTF();
    }

    @Override
    public String toString() {
        return titulo + " (" + anoLancamento + ")";
    }
}
