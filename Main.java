import dao.EmprestimoDAO;
import dao.FilmeDAO;
import dao.UsuarioDAO;
import dao.UsuarioFilmeDAO;
import view.EmprestimoPanel;
import view.FilmePanel;
import view.UsuarioPanel;

import javax.swing.*;
import java.awt.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {}
            try {
                new AppFrame().setVisible(true);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null,
                    "Erro ao iniciar: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        });
    }
}

class AppFrame extends JFrame {

    public AppFrame() throws Exception {
        super("Sistema de Biblioteca de Filmes");

        // DAOs
        FilmeDAO filmeDAO       = new FilmeDAO();
        UsuarioDAO usuarioDAO   = new UsuarioDAO();
        EmprestimoDAO empDAO    = new EmprestimoDAO();
        UsuarioFilmeDAO ufDAO   = new UsuarioFilmeDAO();

        // Panels
        FilmePanel filmePanel         = new FilmePanel(filmeDAO);
        UsuarioPanel usuarioPanel     = new UsuarioPanel(usuarioDAO);
        EmprestimoPanel empPanel      = new EmprestimoPanel(empDAO, filmeDAO, usuarioDAO, ufDAO);

        // Abas
        JTabbedPane tabs = new JTabbedPane(JTabbedPane.TOP);
        tabs.addTab("🎬  Filmes",     null, filmePanel,    "Gerenciar filmes");
        tabs.addTab("👤  Usuários",   null, usuarioPanel,  "Gerenciar usuários");
        tabs.addTab("📋  Empréstimos",null, empPanel,      "Gerenciar empréstimos (N:N)");

        // Cabeçalho
        JLabel titulo = new JLabel("Biblioteca de Filmes", SwingConstants.CENTER);
        titulo.setFont(new Font("SansSerif", Font.BOLD, 20));
        titulo.setBorder(BorderFactory.createEmptyBorder(10, 0, 6, 0));
        titulo.setOpaque(true);
        titulo.setBackground(new Color(25, 42, 86));
        titulo.setForeground(Color.WHITE);

        setLayout(new BorderLayout());
        add(titulo, BorderLayout.NORTH);
        add(tabs,   BorderLayout.CENTER);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 720);
        setMinimumSize(new Dimension(900, 600));
        setLocationRelativeTo(null);
    }
}
