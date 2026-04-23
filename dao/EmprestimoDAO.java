package dao;

import model.Emprestimo;
import persistencia.Arquivo;
import persistencia.HashExtensivel;
import java.util.ArrayList;

public class EmprestimoDAO {
    private Arquivo<Emprestimo> arquivo;
    private HashExtensivel hash;

    public EmprestimoDAO() throws Exception {
        arquivo = new Arquivo<>("emprestimo", Emprestimo.class.getConstructor());
        hash = new HashExtensivel("emprestimo");
    }

    public void create(Emprestimo emp) throws Exception {
        long endereco = arquivo.createWithAddress(emp);

        // RELACIONAMENTO 1:N
        hash.add(emp.getIdUsuario(), endereco);
    }

    public ArrayList<Emprestimo> getEmprestimosDoUsuario(int idUsuario) throws Exception {
        ArrayList<Long> enderecos = hash.get(idUsuario);
        ArrayList<Emprestimo> lista = new ArrayList<>();

        for (long end : enderecos) {
            Emprestimo e = arquivo.readAt(end);
            if (e != null) {
                lista.add(e);
            }
        }
        return lista;
    }
}