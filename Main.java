import dao.FilmeDAO;
import dao.UsuarioDAO;
import dao.EmprestimoDAO;
import dao.UsuarioDAO;
import java.util.Scanner;
import model.Filme;
import model.Usuario;
import model.Emprestimo;


public class Main {
    public static void main(String[] args) throws Exception {
        FilmeDAO filmeDAO = new FilmeDAO();
        EmprestimoDAO emprestimoDAO = new EmprestimoDAO();
        UsuarioDAO usuarioDAO = new UsuarioDAO();
        Scanner sc = new Scanner(System.in);

        while (true) {
            System.out.println("\n Sistema de Gerenciamento de Biblioteca de Filmes");
            System.out.println("1) Cadastrar Filme");
            System.out.println("2) Consultar Filme");
            System.out.println("3) Atualizar Filme");
            System.out.println("4) Excluir Filme");
            System.out.println("5) Cadastrar Usuário");
            System.out.println("6) Criar Empréstimo");
            System.out.println("7) Listar Empréstimos de um Usuário");
            System.out.println("0) Sair");
            System.out.print("Escolha uma opção: ");
            
            int op = Integer.parseInt(sc.nextLine());
            if (op == 0) break;

            try {
                // Filme
                if (op == 1) { 
                    System.out.print("Título: ");
                    String titulo = sc.nextLine();
                    System.out.print("Diretor: ");
                    String diretor = sc.nextLine();
                    System.out.print("Ano de Lançamento: ");
                    int ano = Integer.parseInt(sc.nextLine());
                    System.out.print("Duração (em minutos): ");
                    int duracao = Integer.parseInt(sc.nextLine());
                    System.out.print("Gêneros: ");
                    String generos = sc.nextLine();
                    System.out.print("Tags: ");
                    String tags = sc.nextLine();

                    Filme f = new Filme(0, titulo, diretor, ano, duracao, generos, tags);
                    filmeDAO.create(f);
                    System.out.println("Filme cadastrado com sucesso! ID gerado: " + f.getId());

                } else if (op == 2) { 
                    System.out.print("Digite o ID do Filme: ");
                    int id = Integer.parseInt(sc.nextLine());
                    Filme f = filmeDAO.read(id);
                    if (f != null) {
                        System.out.println("Filme: " + f);
                    } else {
                        System.out.println("Filme não encontrado.");
                    }

                } else if (op == 3) { 
                    System.out.print("Digite o ID do Filme para atualizar: ");
                    int id = Integer.parseInt(sc.nextLine());
                    Filme fExistente = filmeDAO.read(id);
                    
                    if (fExistente != null) {
                        System.out.println("Dados atuais: " + fExistente);
                        System.out.println("\n Novos dados");
                        System.out.print("Título: ");
                        String titulo = sc.nextLine();
                        System.out.print("Diretor: ");
                        String diretor = sc.nextLine();
                        System.out.print("Ano: ");
                        int ano = Integer.parseInt(sc.nextLine());
                        System.out.print("Duração (em minutos): ");
                        int duracao = Integer.parseInt(sc.nextLine());
                        System.out.print("Gêneros: ");
                        String generos = sc.nextLine();
                        System.out.print("Tags: ");
                        String tags = sc.nextLine();

                        Filme novoFilme = new Filme(id, titulo, diretor, ano, duracao, generos, tags);
                        if (filmeDAO.update(novoFilme)) {
                            System.out.println("Filme atualizado com sucesso!");
                        }
                    } else {
                        System.out.println("Filme não encontrado para atualização.");
                    }

                } else if (op == 4) { 
                    System.out.print("Digite o ID do Filme para excluir: ");
                    int id = Integer.parseInt(sc.nextLine());
                    if (filmeDAO.delete(id)) {
                        System.out.println("Filme excluído com sucesso!");
                    } else {
                        System.out.println("Não foi possível excluir (ID não encontrado).");
                    }
                
                // Usuario
                } else if (op == 5) {
                    System.out.print("Nome: ");
                    String nome = sc.nextLine();
                    System.out.print("Email: ");
                    String email = sc.nextLine();
                    System.out.print("Telefone: ");
                    String telefone = sc.nextLine();
                    System.out.print("CPF: ");
                    String cpf = sc.nextLine();
                    Usuario u = new Usuario(0, nome, email, telefone, cpf);
                    usuarioDAO.create(u);
                    System.out.println("Usuário cadastrado com sucesso! ID gerado: " + u.getId());

                // Emprestimo
                } else if (op == 6) {
                    System.out.print("CPF do Usuario: ");
                    String cpf = sc.nextLine();
                    Usuario usuario = usuarioDAO.buscarPorCpf(cpf);

                    if (usuario == null) {
                        System.out.println("Usuário não encontrado.");
                    } else {
                        System.out.println("Usuário encontrado: " + usuario.getNome());
                        System.out.print("ID do Filme: ");
                        int idFilme = Integer.parseInt(sc.nextLine());
                        System.out.print("ID do Funcionario: ");
                        int idFuncionario = Integer.parseInt(sc.nextLine());
                        Emprestimo e = new Emprestimo(usuario.getId(), idFilme, idFuncionario, System.currentTimeMillis(), 0, "ABERTO" );
                        emprestimoDAO.create(e);
                        System.out.println("Empréstimo criado com sucesso!");
                    }

                } else if (op == 7) {
                    System.out.print("ID do Usuario: ");
                    int idUsuario = Integer.parseInt(sc.nextLine());

                    var lista = emprestimoDAO.getEmprestimosDoUsuario(idUsuario);

                    if (lista.isEmpty()) {
                        System.out.println("Nenhum empréstimo encontrado.");
                    } else {
                        System.out.println("Empréstimos do usuário:");
                        for (Emprestimo e : lista) {
                            System.out.println(e);
                        }
                    }
                }

            } catch (Exception ex) {
                System.out.println("Erro: " + ex.getMessage());
            }
        }
            
        System.out.println("Encerrado.");
        sc.close();
    }
}
