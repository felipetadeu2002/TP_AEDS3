package dao;

import model.Emprestimo;
import persistencia.Arquivo;
import persistencia.HashExtensivel;
import java.util.ArrayList;

public class EmprestimoDAO {
    private Arquivo<Emprestimo> arquivo;
    private HashExtensivel hashUsuario;
    private HashExtensivel hashFilme;

    public EmprestimoDAO() throws Exception {
        arquivo     = new Arquivo<>("emprestimo", Emprestimo.class.getConstructor());
        hashUsuario = new HashExtensivel("emprestimo_usuario");
        hashFilme   = new HashExtensivel("emprestimo_filme");
    }

    public void create(Emprestimo emp) throws Exception {
        long endereco = arquivo.createWithAddress(emp);
        hashUsuario.add(emp.getIdUsuario(), endereco);
        hashFilme.add(emp.getIdFilme(), endereco);
    }

    public Emprestimo read(int id) throws Exception {
        return arquivo.read(id);
    }

    public boolean update(Emprestimo emp) throws Exception {
        return arquivo.update(emp);
    }

    public boolean delete(int id) throws Exception {
        return arquivo.delete(id);
    }

    public ArrayList<Emprestimo> readAll() throws Exception {
        return arquivo.readAll();
    }

    public ArrayList<Emprestimo> getEmprestimosDoUsuario(int idUsuario) throws Exception {
        ArrayList<Long> enderecos = hashUsuario.get(idUsuario);
        ArrayList<Emprestimo> lista = new ArrayList<>();
        for (long end : enderecos) {
            Emprestimo e = arquivo.readAt(end);
            if (e != null) lista.add(e);
        }
        return lista;
    }

    public ArrayList<Emprestimo> getEmprestimosDoFilme(int idFilme) throws Exception {
        ArrayList<Long> enderecos = hashFilme.get(idFilme);
        ArrayList<Emprestimo> lista = new ArrayList<>();
        for (long end : enderecos) {
            Emprestimo e = arquivo.readAt(end);
            if (e != null) lista.add(e);
        }
        return lista;
    }
}
