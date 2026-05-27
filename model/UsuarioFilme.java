package model;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class UsuarioFilme implements Registro {
    private int id;
    private int idUsuario;
    private int idFilme;

    public UsuarioFilme() {
    }

    public UsuarioFilme(int idUsuario, int idFilme) {
        this.idUsuario = idUsuario;
        this.idFilme = idFilme;
        this.id = composeId(idUsuario, idFilme);
    }

    public static int composeId(int idUsuario, int idFilme) {
        if (idUsuario < 0 || idUsuario > 0xFFFF || idFilme < 0 || idFilme > 0xFFFF) {
            throw new IllegalArgumentException("IDs para chave composta devem estar entre 0 e 65535");
        }
        return (idUsuario << 16) | (idFilme & 0xFFFF);
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public int getIdFilme() {
        return idFilme;
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
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        dos.writeInt(id);
        dos.writeInt(idUsuario);
        dos.writeInt(idFilme);
        return baos.toByteArray();
    }

    @Override
    public void fromByteArray(byte[] ba) throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(ba);
        DataInputStream dis = new DataInputStream(bais);
        id = dis.readInt();
        idUsuario = dis.readInt();
        idFilme = dis.readInt();
    }
}
