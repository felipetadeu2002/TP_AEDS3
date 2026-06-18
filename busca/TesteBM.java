package busca;

public class TesteBM {

    public static void main(String[] args) {

        String texto =
            "BATMAN SUPERMAN IRON MAN";

        String padrao =
            "MAN";

        System.out.println(
            BoyerMoore.buscar(
                texto,
                padrao
            )
        );
    }
}
