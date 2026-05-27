package dao;

import java.util.ArrayList;

import model.UsuarioFilme;
import persistencia.Arquivo;
import persistencia.HashExtensivel;

public class UsuarioFilmeDAO {
    private final Arquivo<UsuarioFilme> arquivo;
    private final HashExtensivel indiceUsuario;
    private final HashExtensivel indiceFilme;

    public UsuarioFilmeDAO() throws Exception {
        this.arquivo = new Arquivo<>("usuario_filme", UsuarioFilme.class.getConstructor());
        this.indiceUsuario = new HashExtensivel("usuario_filme_usuario");
        this.indiceFilme = new HashExtensivel("usuario_filme_filme");
    }

    public void vincular(int idUsuario, int idFilme) throws Exception {
        UsuarioFilme relacao = new UsuarioFilme(idUsuario, idFilme);
        if (arquivo.read(relacao.getId()) != null) {
            return;
        }
        long endereco = arquivo.createWithAddress(relacao);
        indiceUsuario.add(idUsuario, endereco);
        indiceFilme.add(idFilme, endereco);
    }

    public ArrayList<Integer> listarFilmesDoUsuario(int idUsuario) throws Exception {
        ArrayList<Integer> filmes = new ArrayList<>();
        ArrayList<Long> enderecos = indiceUsuario.get(idUsuario);

        for (long endereco : enderecos) {
            UsuarioFilme relacao = arquivo.readAt(endereco);
            if (relacao != null) {
                filmes.add(relacao.getIdFilme());
            }
        }
        return filmes;
    }

    public ArrayList<Integer> listarUsuariosDoFilme(int idFilme) throws Exception {
        ArrayList<Integer> usuarios = new ArrayList<>();
        ArrayList<Long> enderecos = indiceFilme.get(idFilme);

        for (long endereco : enderecos) {
            UsuarioFilme relacao = arquivo.readAt(endereco);
            if (relacao != null) {
                usuarios.add(relacao.getIdUsuario());
            }
        }
        return usuarios;
    }
}
