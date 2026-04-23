package model;

import java.io.*;

public class Usuario implements Registro {
    private int id;
    private String nome;
    private String email;
    private String telefone;

    public Usuario() {}

    public Usuario(int id, String nome, String email, String telefone) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.telefone = telefone;
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
        da.writeUTF(email);
        da.writeUTF(telefone);

        return ba.toByteArray();
    }

    @Override
    public void fromByteArray(byte[] ba) throws IOException {
        ByteArrayInputStream bi = new ByteArrayInputStream(ba);
        DataInputStream di = new DataInputStream(bi);

        id = di.readInt();
        nome = di.readUTF();
        email = di.readUTF();
        telefone = di.readUTF();
    }

    @Override
    public String toString() {
        return "Usuario [id=" + id + ", nome=" + nome + ", email=" + email + ", telefone=" + telefone + "]";
    }
}