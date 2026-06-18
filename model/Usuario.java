package model;

import java.io.*;
import seguranca.XORCipher;

public class Usuario implements Registro {
    private int id;
    private String nome;
    private String email;
    private String telefone;
    private String cpf;

    public Usuario() {}

    public Usuario(int id, String nome, String email, String telefone, String cpf) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.telefone = telefone;
        this.cpf = cpf;
    }

    @Override public int getId()        { return id; }
    @Override public void setId(int id) { this.id = id; }
    public String getNome()             { return nome; }
    public String getEmail()            { return email; }
    public String getTelefone()         { return telefone; }
    public String getCpf()              { return cpf; }

    @Override
    public byte[] toByteArray() throws IOException {
        ByteArrayOutputStream ba = new ByteArrayOutputStream();
        DataOutputStream da = new DataOutputStream(ba);
        da.writeInt(id);
        da.writeUTF(nome == null ? "" : nome);
        da.writeUTF(email == null ? "" : email);
        da.writeUTF(telefone == null ? "" : telefone);
        String cpfCriptografado = XORCipher.encrypt(cpf == null ? "" : cpf);
        da.writeUTF(cpfCriptografado);
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
        String cpfCriptografado = di.readUTF();
        cpf = XORCipher.decrypt(cpfCriptografado);
    }

    @Override
    public String toString() {
        return id + " - " + nome;
    }
}
