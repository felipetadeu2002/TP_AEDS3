package view;

import dao.UsuarioDAO;
import model.Usuario;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;

public class UsuarioPanel extends JPanel {

    private final UsuarioDAO dao;

    private final JTextField fNome      = new JTextField();
    private final JTextField fEmail     = new JTextField();
    private final JTextField fTelefone  = new JTextField();
    private final JTextField fCpf       = new JTextField();
    private final JTextField fBuscarId  = new JTextField();

    private final DefaultTableModel tableModel;
    private final JTable tabela;
    private int idSelecionado = -1;

    public UsuarioPanel(UsuarioDAO dao) throws Exception {
        this.dao = dao;
        setLayout(new BorderLayout(8, 8));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // ── Formulário ────────────────────────────────────────────
        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createTitledBorder("Dados do Usuário"));
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(4, 4, 4, 4);
        g.fill = GridBagConstraints.HORIZONTAL;

        String[] labels = {"Nome", "Email", "Telefone", "CPF"};
        JTextField[] fields = {fNome, fEmail, fTelefone, fCpf};

        for (int i = 0; i < labels.length; i++) {
            g.gridx = 0; g.gridy = i; g.weightx = 0;
            form.add(new JLabel(labels[i] + ":"), g);
            g.gridx = 1; g.weightx = 1;
            form.add(fields[i], g);
        }

        g.gridx = 0; g.gridy = labels.length; g.weightx = 0;
        form.add(new JLabel("Buscar ID:"), g);
        g.gridx = 1; g.weightx = 1;
        form.add(fBuscarId, g);

        // ── Botões ────────────────────────────────────────────────
        JPanel btns = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        JButton bSalvar    = botao("Salvar",    new Color(46, 125, 50));
        JButton bAtualizar = botao("Atualizar", new Color(21, 101, 192));
        JButton bExcluir   = botao("Excluir",   new Color(183, 28, 28));
        JButton bBuscar    = botao("Buscar ID", new Color(74, 20, 140));
        JButton bLimpar    = botao("Limpar",    new Color(84, 84, 84));
        JButton bListar    = botao("Listar Todos", new Color(0, 96, 100));

        btns.add(bSalvar); btns.add(bAtualizar); btns.add(bExcluir);
        btns.add(bBuscar); btns.add(bLimpar);    btns.add(bListar);

        g.gridx = 0; g.gridy = labels.length + 1; g.gridwidth = 2; g.weightx = 1;
        form.add(btns, g);

        // ── Tabela ────────────────────────────────────────────────
        tableModel = new DefaultTableModel(
            new String[]{"ID", "Nome", "Email", "Telefone", "CPF"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tabela = new JTable(tableModel);
        tabela.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabela.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) preencherFormDaTabela();
        });
        tabela.setRowHeight(22);

        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
            form, new JScrollPane(tabela));
        split.setResizeWeight(0.45);
        add(split, BorderLayout.CENTER);

        bSalvar.addActionListener(e -> salvar());
        bAtualizar.addActionListener(e -> atualizar());
        bExcluir.addActionListener(e -> excluir());
        bBuscar.addActionListener(e -> buscarPorId());
        bLimpar.addActionListener(e -> limpar());
        bListar.addActionListener(e -> carregarTabela());

        carregarTabela();
    }

    private void salvar() {
        try {
            if (fNome.getText().isBlank()) { aviso("Nome obrigatório."); return; }
            Usuario u = new Usuario(0, fNome.getText().trim(),
                fEmail.getText().trim(), fTelefone.getText().trim(), fCpf.getText().trim());
            dao.create(u);
            JOptionPane.showMessageDialog(this, "Usuário salvo com ID " + u.getId());
            limpar(); carregarTabela();
        } catch (Exception ex) { erro(ex); }
    }

    private void atualizar() {
        if (idSelecionado < 0) { aviso("Selecione um usuário na tabela primeiro."); return; }
        try {
            Usuario u = new Usuario(idSelecionado, fNome.getText().trim(),
                fEmail.getText().trim(), fTelefone.getText().trim(), fCpf.getText().trim());
            if (dao.update(u)) {
                JOptionPane.showMessageDialog(this, "Usuário atualizado.");
                limpar(); carregarTabela();
            } else aviso("Usuário não encontrado.");
        } catch (Exception ex) { erro(ex); }
    }

    private void excluir() {
        if (idSelecionado < 0) { aviso("Selecione um usuário na tabela primeiro."); return; }
        int c = JOptionPane.showConfirmDialog(this,
            "Excluir usuário ID " + idSelecionado + "?", "Confirmar", JOptionPane.YES_NO_OPTION);
        if (c != JOptionPane.YES_OPTION) return;
        try {
            if (dao.delete(idSelecionado)) {
                JOptionPane.showMessageDialog(this, "Usuário excluído.");
                limpar(); carregarTabela();
            } else aviso("Usuário não encontrado.");
        } catch (Exception ex) { erro(ex); }
    }

    private void buscarPorId() {
        try {
            int id = parseInt(fBuscarId);
            if (id < 0) { aviso("Informe um ID válido."); return; }
            Usuario u = dao.read(id);
            if (u == null) { aviso("Usuário não encontrado."); return; }
            preencherForm(u);
            idSelecionado = u.getId();
        } catch (Exception ex) { erro(ex); }
    }

    public void carregarTabela() {
        try {
            tableModel.setRowCount(0);
            ArrayList<Usuario> lista = dao.readAll();
            for (Usuario u : lista) {
                tableModel.addRow(new Object[]{
                    u.getId(), u.getNome(), u.getEmail(), u.getTelefone(), u.getCpf()
                });
            }
        } catch (Exception ex) { erro(ex); }
    }

    private void preencherFormDaTabela() {
        int row = tabela.getSelectedRow();
        if (row < 0) return;
        idSelecionado = (int) tableModel.getValueAt(row, 0);
        fNome.setText(str(tableModel.getValueAt(row, 1)));
        fEmail.setText(str(tableModel.getValueAt(row, 2)));
        fTelefone.setText(str(tableModel.getValueAt(row, 3)));
        fCpf.setText(str(tableModel.getValueAt(row, 4)));
    }

    private void preencherForm(Usuario u) {
        fNome.setText(u.getNome());
        fEmail.setText(u.getEmail());
        fTelefone.setText(u.getTelefone());
        fCpf.setText(u.getCpf());
    }

    private void limpar() {
        fNome.setText(""); fEmail.setText(""); fTelefone.setText("");
        fCpf.setText(""); fBuscarId.setText(""); idSelecionado = -1;
        tabela.clearSelection();
    }

    public ArrayList<Usuario> getUsuarios() {
        try { return dao.readAll(); }
        catch (Exception e) { return new ArrayList<>(); }
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

    private String str(Object o) { return o == null ? "" : o.toString(); }
    private void aviso(String msg) { JOptionPane.showMessageDialog(this, msg, "Aviso", JOptionPane.WARNING_MESSAGE); }
    private void erro(Exception ex) { JOptionPane.showMessageDialog(this, "Erro: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE); }
}
