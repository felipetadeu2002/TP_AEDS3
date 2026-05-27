package model;

import java.io.*;

public class Funcionario implements Registro {
    private int id;
    private String nome;
    private String cargo;

    public Funcionario() {}

    public Funcionario(int id, String nome, String cargo) {
        this.id = id;
        this.nome = nome;
        this.cargo = cargo;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public void setId(int id) {
        this.id = id;
    }

    @Override
    public byte[] toByteArray() throws IOException {
        ByteArrayOutputStream ba = new ByteArrayOutputStream();
        DataOutputStream da = new DataOutputStream(ba);

        da.writeInt(id);
        da.writeUTF(nome);
        da.writeUTF(cargo);

        return ba.toByteArray();
    }

    @Override
    public void fromByteArray(byte[] ba) throws IOException {
        ByteArrayInputStream bi = new ByteArrayInputStream(ba);
        DataInputStream di = new DataInputStream(bi);

        id = di.readInt();
        nome = di.readUTF();
        cargo = di.readUTF();
    }

    @Override
    public String toString() {
        return "Funcionario [id=" + id + ", nome=" + nome + ", cargo=" + cargo + "]";
    }
}
