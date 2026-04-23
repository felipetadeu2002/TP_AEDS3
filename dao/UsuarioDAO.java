package dao;

import java.lang.reflect.Constructor;
import model.Usuario;
import persistencia.Arquivo;
import java.util.ArrayList;

public class UsuarioDAO {
    private Arquivo<Usuario> arquivo;
    private Constructor<Usuario> construtor;

    public UsuarioDAO() throws Exception {
        this.construtor = Usuario.class.getConstructor();
        this.arquivo = new Arquivo<>("usuario", this.construtor);
    }

    public long create(Usuario usuario) throws Exception {
        return this.arquivo.createWithAddress(usuario);
    }

    public Usuario read(int id) throws Exception {
        return this.arquivo.read(id);
    }

    public boolean update(Usuario usuario) throws Exception {
        return this.arquivo.update(usuario);
    }

    public boolean delete(int id) throws Exception {
        return this.arquivo.delete(id);
    }

    public Usuario buscarPorCpf(String cpf) throws Exception {
        ArrayList<Usuario> usuarios = arquivo.readAll();

        for (Usuario u : usuarios) {
            if (u.getCpf().equals(cpf)) {
                return u;
            }
        }
        return null;
    }
}
