package dao;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import model.Filme;
import persistencia.Arquivo;
import persistencia.ArvoreBMais;

public class FilmeDAO {
   private Arquivo<Filme> arquivo;
   private Constructor<Filme> construtor;
   private ArvoreBMais indiceTitulo;

   public FilmeDAO() throws Exception {
      this.construtor = Filme.class.getConstructor();
      this.arquivo = new Arquivo<>("Filme", this.construtor);
      this.indiceTitulo = new ArvoreBMais("Filme", "indice_titulo_bmais");
   }

   public long create(Filme filme) throws Exception {
      long endereco = this.arquivo.createWithAddress(filme);
      this.indiceTitulo.put(chaveTitulo(filme), endereco);
      return endereco;
   }

   public Filme read(int id) throws Exception {
      return this.arquivo.read(id);
   }

   public boolean update(Filme filme) throws Exception {
      Filme antigo = this.arquivo.read(filme.getId());
      boolean atualizado = this.arquivo.update(filme);
      if (atualizado) {
         if (antigo != null) {
            this.indiceTitulo.remove(chaveTitulo(antigo));
         }
         Long novoEndereco = this.arquivo.getEndereco(filme.getId());
         if (novoEndereco != null) {
            this.indiceTitulo.put(chaveTitulo(filme), novoEndereco);
         }
      }
      return atualizado;
   }

   public boolean delete(int id) throws Exception {
      Filme antigo = this.arquivo.read(id);
      boolean removido = this.arquivo.delete(id);
      if (removido && antigo != null) {
         this.indiceTitulo.remove(chaveTitulo(antigo));
      }
      return removido;
   }

   public ArrayList<Filme> listarFilmesOrdenadosPorTitulo() throws Exception {
      ArrayList<Filme> filmes = new ArrayList<>();
      ArrayList<Long> enderecos = this.indiceTitulo.listAllInOrder();
      for (long endereco : enderecos) {
         Filme filme = this.arquivo.readAt(endereco);
         if (filme != null) {
            filmes.add(filme);
         }
      }
      return filmes;
   }

   private String chaveTitulo(Filme filme) {
      return filme.getId() + "|" + filme.getTitulo().toLowerCase();
   }
}
