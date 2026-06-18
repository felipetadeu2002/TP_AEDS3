package busca;

import dao.FilmeDAO;
import model.Filme;

import java.util.ArrayList;

public class TestePesquisa {

    public static void main(String[] args)
            throws Exception {

        FilmeDAO dao =
            new FilmeDAO();

        ArrayList<Filme> lista =
            dao.pesquisarKMP("man");

        for (Filme f : lista) {

            System.out.println(
                f.getTitulo()
            );
        }
    }
}
