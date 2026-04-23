package dao;

import java.lang.reflect.Constructor;
import model.Filme;
import persistencia.Arquivo;

public class FilmeDAO {
   private Arquivo<Filme> arquivo;
   private Constructor<Filme> construtor;

   public FilmeDAO() throws Exception {
      this.construtor = Filme.class.getConstructor();
      this.arquivo = new Arquivo<>("Filme", this.construtor);
   }

   public long create(Filme filme) throws Exception {
    return this.arquivo.createWithAddress(filme);
   }

   public Filme read(int id) throws Exception {
      return (Filme)this.arquivo.read(id);
   }

   public boolean update(Filme filme) throws Exception {
      return this.arquivo.update(filme);
   }

   public boolean delete(int id) throws Exception {
      return this.arquivo.delete(id);
   }
}
