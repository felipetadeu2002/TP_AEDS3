package view;

import dao.FilmeDAO;
import model.Filme;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;

public class FilmePanel extends JPanel {

    private final FilmeDAO dao;

    private final JTextField fTitulo    = new JTextField();
    private final JTextField fDiretor   = new JTextField();
    private final JTextField fAno       = new JTextField();
    private final JTextField fDuracao   = new JTextField();
    private final JTextField fGeneros   = new JTextField();
    private final JTextField fTags      = new JTextField();
    private final JTextField fBuscarId  = new JTextField();
    private final JTextField fPadraoPesquisa = new JTextField();
    private final JRadioButton rbKMP = new JRadioButton("KMP", true);
    private final JRadioButton rbBM = new JRadioButton("Boyer-Moore");
    private final DefaultTableModel tableModel;
    private final JTable tabela;
    private int idSelecionado = -1;

    public FilmePanel(FilmeDAO dao) throws Exception {
        this.dao = dao;
        setLayout(new BorderLayout(8, 8));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // ── Formulário ────────────────────────────────────────────
        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createTitledBorder("Dados do Filme"));
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(4, 4, 4, 4);
        g.fill = GridBagConstraints.HORIZONTAL;

        String[] labels = {"Título", "Diretor", "Ano", "Duração (min)", "Gêneros", "Tags"};
        JTextField[] fields = {fTitulo, fDiretor, fAno, fDuracao, fGeneros, fTags};

        for (int i = 0; i < labels.length; i++) {
            g.gridx = 0; g.gridy = i; g.weightx = 0;
            form.add(new JLabel(labels[i] + ":"), g);
            g.gridx = 1; g.weightx = 1;
            form.add(fields[i], g);
        }

        // Busca por ID
        g.gridx = 0; g.gridy = labels.length; g.weightx = 0;
        form.add(new JLabel("Buscar ID:"), g);
        g.gridx = 1; g.weightx = 1;
        form.add(fBuscarId, g);

        // Pesquisa por padrão
        g.gridx = 0;
        g.gridy = labels.length + 2;
        g.weightx = 0;
        form.add(  new JLabel("Padrão:"), g);
        g.gridx = 1;
        g.weightx = 1;
        form.add(fPadraoPesquisa, g);

        ButtonGroup grupo = new ButtonGroup();
        grupo.add(rbKMP);
        grupo.add(rbBM);

        JPanel painelBusca = new JPanel(new FlowLayout(FlowLayout.LEFT));
        painelBusca.add(rbKMP);
        painelBusca.add(rbBM);
        g.gridx = 1;
        g.gridy = labels.length + 3;
        form.add(painelBusca, g);

        // ── Botões ────────────────────────────────────────────────
        JPanel btns = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        JButton bSalvar   = botao("Salvar",   new Color(46, 125, 50));
        JButton bAtualizar = botao("Atualizar", new Color(21, 101, 192));
        JButton bExcluir  = botao("Excluir",  new Color(183, 28, 28));
        JButton bBuscar   = botao("Buscar ID", new Color(74, 20, 140));
        JButton bLimpar   = botao("Limpar",   new Color(84, 84, 84));
        JButton bListar   = botao("Listar Ordenado (B+)", new Color(0, 96, 100));
        JButton bPesquisar = botao("Pesquisar", new Color(255, 140, 0));

        btns.add(bSalvar); btns.add(bAtualizar); btns.add(bExcluir);
        btns.add(bBuscar); btns.add(bPesquisar); btns.add(bLimpar); btns.add(bListar);

        g.gridx = 0; g.gridy = labels.length + 1; g.gridwidth = 2; g.weightx = 1;
        form.add(btns, g); g.gridwidth = 1;

        // ── Tabela ────────────────────────────────────────────────
        tableModel = new DefaultTableModel(
            new String[]{"ID", "Título", "Diretor", "Ano", "Duração", "Gêneros", "Tags"}, 0) {
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
        split.setResizeWeight(0.70);
        add(split, BorderLayout.CENTER);

        // ── Ações ─────────────────────────────────────────────────
        bSalvar.addActionListener(e -> salvar());
        bAtualizar.addActionListener(e -> atualizar());
        bExcluir.addActionListener(e -> excluir());
        bBuscar.addActionListener(e -> buscarPorId());
        bLimpar.addActionListener(e -> limpar());
        bListar.addActionListener(e -> listarOrdenado());
        bPesquisar.addActionListener(e -> pesquisarPadrao());

        carregarTabela();
    }

    private void salvar() {
        try {
            if (fTitulo.getText().isBlank()) { aviso("Título obrigatório."); return; }
            Filme f = new Filme(0, fTitulo.getText().trim(), fDiretor.getText().trim(),
                parseInt(fAno, 0), parseInt(fDuracao, 0),
                fGeneros.getText().trim(), fTags.getText().trim());
            dao.create(f);
            JOptionPane.showMessageDialog(this, "Filme salvo com ID " + f.getId());
            limpar();
            carregarTabela();
        } catch (Exception ex) { erro(ex); }
    }

    private void atualizar() {
        if (idSelecionado < 0) { aviso("Selecione um filme na tabela primeiro."); return; }
        try {
            Filme f = new Filme(idSelecionado, fTitulo.getText().trim(), fDiretor.getText().trim(),
                parseInt(fAno, 0), parseInt(fDuracao, 0),
                fGeneros.getText().trim(), fTags.getText().trim());
            if (dao.update(f)) {
                JOptionPane.showMessageDialog(this, "Filme atualizado.");
                limpar(); carregarTabela();
            } else aviso("Filme não encontrado.");
        } catch (Exception ex) { erro(ex); }
    }

    private void excluir() {
        if (idSelecionado < 0) { aviso("Selecione um filme na tabela primeiro."); return; }
        int c = JOptionPane.showConfirmDialog(this,
            "Excluir filme ID " + idSelecionado + "?", "Confirmar", JOptionPane.YES_NO_OPTION);
        if (c != JOptionPane.YES_OPTION) return;
        try {
            if (dao.delete(idSelecionado)) {
                JOptionPane.showMessageDialog(this, "Filme excluído.");
                limpar(); carregarTabela();
            } else aviso("Filme não encontrado.");
        } catch (Exception ex) { erro(ex); }
    }

    private void buscarPorId() {
        try {
            int id = parseInt(fBuscarId, -1);
            if (id < 0) { aviso("Informe um ID válido."); return; }
            Filme f = dao.read(id);
            if (f == null) { aviso("Filme não encontrado."); return; }
            preencherForm(f);
            idSelecionado = f.getId();
        } catch (Exception ex) { erro(ex); }
    }

    private void listarOrdenado() {
        try {
            ArrayList<Filme> filmes = dao.listarFilmesOrdenadosPorTitulo();
            tableModel.setRowCount(0);
            for (Filme f : filmes) addRow(f);
            if (filmes.isEmpty()) aviso("Nenhum filme cadastrado.");
        } catch (Exception ex) { erro(ex); }
    }

    private void carregarTabela() {
        try {
            tableModel.setRowCount(0);
            // usar listar ordenado para demonstrar B+
            ArrayList<Filme> filmes = dao.listarFilmesOrdenadosPorTitulo();
            for (Filme f : filmes) addRow(f);
        } catch (Exception ex) { erro(ex); }
    }

    private void addRow(Filme f) {
        tableModel.addRow(new Object[]{
            f.getId(), f.getTitulo(), f.getDiretor(),
            f.getAnoLancamento(), f.getDuracao(), f.getGeneros(), f.getTags()
        });
    }

    private void preencherFormDaTabela() {
        int row = tabela.getSelectedRow();
        if (row < 0) return;
        idSelecionado = (int) tableModel.getValueAt(row, 0);
        fTitulo.setText(str(tableModel.getValueAt(row, 1)));
        fDiretor.setText(str(tableModel.getValueAt(row, 2)));
        fAno.setText(str(tableModel.getValueAt(row, 3)));
        fDuracao.setText(str(tableModel.getValueAt(row, 4)));
        fGeneros.setText(str(tableModel.getValueAt(row, 5)));
        fTags.setText(str(tableModel.getValueAt(row, 6)));
    }

    private void preencherForm(Filme f) {
        fTitulo.setText(f.getTitulo());
        fDiretor.setText(f.getDiretor());
        fAno.setText(String.valueOf(f.getAnoLancamento()));
        fDuracao.setText(String.valueOf(f.getDuracao()));
        fGeneros.setText(f.getGeneros());
        fTags.setText(f.getTags());
    }

    private void limpar() {
        fTitulo.setText(""); fDiretor.setText(""); fAno.setText("");
        fDuracao.setText(""); fGeneros.setText(""); fTags.setText("");
        fBuscarId.setText(""); idSelecionado = -1;
        tabela.clearSelection();
    }

    private JButton botao(String txt, Color cor) {
        JButton b = new JButton(txt);
        b.setBackground(cor);
        b.setForeground(Color.BLACK);
        b.setOpaque(true);
        b.setBorderPainted(false);
        b.setFocusPainted(false);
        b.setFont(b.getFont().deriveFont(java.awt.Font.BOLD));
        return b;
    }

    private int parseInt(JTextField f, int def) {
        try { return Integer.parseInt(f.getText().trim()); }
        catch (NumberFormatException e) { return def; }
    }

    private void pesquisarPadrao() {
        try {
            String padrao = fPadraoPesquisa.getText().trim();

            if (padrao.isEmpty()) {
                aviso("Informe um padrão.");
            return;
            }

            ArrayList<Filme> filmes;

            if (rbKMP.isSelected()) {
                filmes = dao.pesquisarKMP(padrao);
            } else {
            filmes = dao.pesquisarBM(padrao);
            }

            tableModel.setRowCount(0);

            for (Filme f : filmes) {
            addRow(f);
            }

            if (filmes.isEmpty()) {
            aviso("Nenhum filme encontrado.");
            }

        } catch (Exception ex) {
            erro(ex);
        }
    }

    private String str(Object o) { return o == null ? "" : o.toString(); }
    private void aviso(String msg) { JOptionPane.showMessageDialog(this, msg, "Aviso", JOptionPane.WARNING_MESSAGE); }
    private void erro(Exception ex) { JOptionPane.showMessageDialog(this, "Erro: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE); }
}
