package busca;

public class TesteKMP {

    public static void main(String[] args) {

        String texto =
            "BATMAN SUPERMAN IRON MAN";

        String padrao =
            "MAN";

        System.out.println(
            KMP.buscar(
                texto,
                padrao
            )
        );
    }
}