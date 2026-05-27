package dao;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import model.Usuario;
import persistencia.Arquivo;

public class UsuarioDAO {
    private Arquivo<Usuario> arquivo;

    public UsuarioDAO() throws Exception {
        Constructor<Usuario> c = Usuario.class.getConstructor();
        this.arquivo = new Arquivo<>("usuario", c);
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

    public ArrayList<Usuario> readAll() throws Exception {
        return this.arquivo.readAll();
    }

    public Usuario buscarPorCpf(String cpf) throws Exception {
        for (Usuario u : arquivo.readAll()) {
            if (u.getCpf().equals(cpf)) return u;
        }
        return null;
    }
}
