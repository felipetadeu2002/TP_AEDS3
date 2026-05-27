package view;

import dao.EmprestimoDAO;
import dao.FilmeDAO;
import dao.UsuarioDAO;
import dao.UsuarioFilmeDAO;
import model.Emprestimo;
import model.Filme;
import model.Usuario;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class EmprestimoPanel extends JPanel {

    private final EmprestimoDAO empDAO;
    private final FilmeDAO filmeDAO;
    private final UsuarioDAO usuarioDAO;
    private final UsuarioFilmeDAO ufDAO;

    // Campos de criação
    private final JTextField fIdUsuario    = new JTextField();
    private final JTextField fIdFilme      = new JTextField();
    private final JTextField fDataEmp      = new JTextField(new SimpleDateFormat("dd/MM/yyyy").format(new Date()), 10);
    private final JTextField fDataPrev     = new JTextField();

    // Campos de consulta
    private final JTextField fConsultaUsu  = new JTextField();
    private final JTextField fConsultaFilme= new JTextField();

    private final DefaultTableModel tableModel;
    private final JTable tabela;
    private int idSelecionado = -1;

    private static final SimpleDateFormat SDF = new SimpleDateFormat("dd/MM/yyyy");

    public EmprestimoPanel(EmprestimoDAO empDAO, FilmeDAO filmeDAO,
                           UsuarioDAO usuarioDAO, UsuarioFilmeDAO ufDAO) throws Exception {
        this.empDAO    = empDAO;
        this.filmeDAO  = filmeDAO;
        this.usuarioDAO = usuarioDAO;
        this.ufDAO     = ufDAO;

        setLayout(new BorderLayout(8, 8));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // ── Painel superior (formulário + consultas lado a lado) ──
        JPanel topo = new JPanel(new GridLayout(1, 2, 10, 0));
        topo.add(buildFormCriar());
        topo.add(buildFormConsultar());

        // ── Tabela ────────────────────────────────────────────────
        tableModel = new DefaultTableModel(
            new String[]{"ID", "ID Usuário", "Usuário", "ID Filme", "Filme",
                         "Data Emprést.", "Devolução Prev.", "Devolução Real", "Status"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tabela = new JTable(tableModel);
        tabela.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabela.setRowHeight(22);
        tabela.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int row = tabela.getSelectedRow();
                if (row >= 0) idSelecionado = (int) tableModel.getValueAt(row, 0);
            }
        });

        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, topo, new JScrollPane(tabela));
        split.setResizeWeight(0.38);
        add(split, BorderLayout.CENTER);

        carregarTabela(empDAO.readAll());
    }

    private JPanel buildFormCriar() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBorder(BorderFactory.createTitledBorder("Novo Empréstimo"));
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(4, 4, 4, 4);
        g.fill = GridBagConstraints.HORIZONTAL;

        String[] labels = {"ID Usuário", "ID Filme", "Data Empréstimo (dd/MM/aaaa)", "Devolução Prevista (dd/MM/aaaa)"};
        JTextField[] fields = {fIdUsuario, fIdFilme, fDataEmp, fDataPrev};

        for (int i = 0; i < labels.length; i++) {
            g.gridx = 0; g.gridy = i; g.weightx = 0; form(p, g, new JLabel(labels[i] + ":"));
            g.gridx = 1; g.weightx = 1; form(p, g, fields[i]);
        }

        JPanel btns = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        JButton bCriar   = botao("Criar Empréstimo", new Color(46, 125, 50));
        JButton bDevolver= botao("Devolver",          new Color(21, 101, 192));
        JButton bExcluir = botao("Excluir",           new Color(183, 28, 28));
        JButton bLimpar  = botao("Limpar",            new Color(84, 84, 84));
        btns.add(bCriar); btns.add(bDevolver); btns.add(bExcluir); btns.add(bLimpar);

        g.gridx = 0; g.gridy = labels.length; g.gridwidth = 2;
        p.add(btns, g);

        bCriar.addActionListener(e -> criarEmprestimo());
        bDevolver.addActionListener(e -> devolver());
        bExcluir.addActionListener(e -> excluir());
        bLimpar.addActionListener(e -> limpar());

        return p;
    }

    private JPanel buildFormConsultar() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBorder(BorderFactory.createTitledBorder("Consultas N:N"));
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(4, 4, 4, 4);
        g.fill = GridBagConstraints.HORIZONTAL;

        g.gridx = 0; g.gridy = 0; g.weightx = 0; p.add(new JLabel("ID Usuário:"), g);
        g.gridx = 1; g.weightx = 1; p.add(fConsultaUsu, g);

        JButton bFilmesUsu = botao("Filmes do Usuário →", new Color(0, 96, 100));
        g.gridx = 0; g.gridy = 1; g.gridwidth = 2; p.add(bFilmesUsu, g);

        g.gridwidth = 1;
        g.gridx = 0; g.gridy = 2; g.weightx = 0; p.add(new JLabel("ID Filme:"), g);
        g.gridx = 1; g.weightx = 1; p.add(fConsultaFilme, g);

        JButton bUsuFilme = botao("← Usuários do Filme", new Color(0, 96, 100));
        g.gridx = 0; g.gridy = 3; g.gridwidth = 2; p.add(bUsuFilme, g);

        JButton bListarTodos = botao("Listar Todos", new Color(84, 84, 84));
        g.gridx = 0; g.gridy = 4; g.gridwidth = 2; p.add(bListarTodos, g);

        bFilmesUsu.addActionListener(e -> consultarFilmesDoUsuario());
        bUsuFilme.addActionListener(e -> consultarUsuariosDoFilme());
        bListarTodos.addActionListener(e -> listarTodos());

        return p;
    }

    private void criarEmprestimo() {
        try {
            int idUsu = parseInt(fIdUsuario);
            int idFilme = parseInt(fIdFilme);
            if (idUsu < 0 || idFilme < 0) { aviso("Informe IDs válidos."); return; }

            if (usuarioDAO.read(idUsu) == null) { aviso("Usuário ID " + idUsu + " não encontrado."); return; }
            if (filmeDAO.read(idFilme) == null)  { aviso("Filme ID " + idFilme + " não encontrado."); return; }

            long dataEmp  = parseData(fDataEmp);
            long dataPrev = parseData(fDataPrev);
            if (dataEmp < 0)  { aviso("Data de empréstimo inválida (use dd/MM/aaaa)."); return; }
            if (dataPrev < 0) { aviso("Data de devolução prevista inválida (use dd/MM/aaaa)."); return; }

            Emprestimo emp = new Emprestimo(idUsu, idFilme, 0, dataEmp, dataPrev, "ATIVO");
            empDAO.create(emp);

            // Registra também na tabela N:N (UsuarioFilme)
            ufDAO.vincular(idUsu, idFilme);

            JOptionPane.showMessageDialog(this, "Empréstimo criado com ID " + emp.getId());
            limpar();
            listarTodos();
        } catch (Exception ex) { erro(ex); }
    }

    private void devolver() {
        if (idSelecionado < 0) { aviso("Selecione um empréstimo na tabela."); return; }
        try {
            Emprestimo e = empDAO.read(idSelecionado);
            if (e == null) { aviso("Empréstimo não encontrado."); return; }
            e.setStatus("DEVOLVIDO");
            e.setDataDevolucaoReal(System.currentTimeMillis());
            empDAO.update(e);
            JOptionPane.showMessageDialog(this, "Devolução registrada.");
            listarTodos();
        } catch (Exception ex) { erro(ex); }
    }

    private void excluir() {
        if (idSelecionado < 0) { aviso("Selecione um empréstimo na tabela."); return; }
        int c = JOptionPane.showConfirmDialog(this,
            "Excluir empréstimo ID " + idSelecionado + "?", "Confirmar", JOptionPane.YES_NO_OPTION);
        if (c != JOptionPane.YES_OPTION) return;
        try {
            if (empDAO.delete(idSelecionado))
                JOptionPane.showMessageDialog(this, "Excluído.");
            else aviso("Empréstimo não encontrado.");
            idSelecionado = -1;
            listarTodos();
        } catch (Exception ex) { erro(ex); }
    }

    private void consultarFilmesDoUsuario() {
        try {
            int id = parseInt(fConsultaUsu);
            if (id < 0) { aviso("Informe um ID de usuário."); return; }
            ArrayList<Emprestimo> lista = empDAO.getEmprestimosDoUsuario(id);
            carregarTabela(lista);
            if (lista.isEmpty()) aviso("Nenhum empréstimo encontrado para usuário " + id);
        } catch (Exception ex) { erro(ex); }
    }

    private void consultarUsuariosDoFilme() {
        try {
            int id = parseInt(fConsultaFilme);
            if (id < 0) { aviso("Informe um ID de filme."); return; }
            ArrayList<Emprestimo> lista = empDAO.getEmprestimosDoFilme(id);
            carregarTabela(lista);
            if (lista.isEmpty()) aviso("Nenhum empréstimo encontrado para filme " + id);
        } catch (Exception ex) { erro(ex); }
    }

    private void listarTodos() {
        try { carregarTabela(empDAO.readAll()); }
        catch (Exception ex) { erro(ex); }
    }

    private void carregarTabela(ArrayList<Emprestimo> lista) throws Exception {
        tableModel.setRowCount(0);
        for (Emprestimo e : lista) {
            String nomeUsu = "";
            String nomeFilme = "";
            try {
                Usuario u = usuarioDAO.read(e.getIdUsuario());
                if (u != null) nomeUsu = u.getNome();
                Filme f = filmeDAO.read(e.getIdFilme());
                if (f != null) nomeFilme = f.getTitulo();
            } catch (Exception ignored) {}

            tableModel.addRow(new Object[]{
                e.getId(), e.getIdUsuario(), nomeUsu,
                e.getIdFilme(), nomeFilme,
                Emprestimo.formatarData(e.getDataEmprestimo()),
                Emprestimo.formatarData(e.getDataDevolucaoPrevista()),
                Emprestimo.formatarData(e.getDataDevolucaoReal()),
                e.getStatus()
            });
        }
    }

    private void limpar() {
        fIdUsuario.setText(""); fIdFilme.setText("");
        fDataPrev.setText(""); idSelecionado = -1;
        tabela.clearSelection();
    }

    private long parseData(JTextField f) {
        try { return SDF.parse(f.getText().trim()).getTime(); }
        catch (ParseException e) { return -1; }
    }

    private JButton botao(String txt, Color cor) {
        JButton b = new JButton(txt);
        b.setBackground(cor); b.setForeground(Color.BLACK); b.setOpaque(true); b.setBorderPainted(false); b.setFocusPainted(false); b.setFont(b.getFont().deriveFont(java.awt.Font.BOLD));
        return b;
    }

    private int parseInt(JTextField f) {
        try { return Integer.parseInt(f.getText().trim()); }
        catch (NumberFormatException e) { return -1; }
    }

    private void form(JPanel p, GridBagConstraints g, Component c) { p.add(c, g); }
    private void aviso(String msg) { JOptionPane.showMessageDialog(this, msg, "Aviso", JOptionPane.WARNING_MESSAGE); }
    private void erro(Exception ex) { JOptionPane.showMessageDialog(this, "Erro: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE); }
}
