package com.bibliotec.admin.ui;

import com.bibliotec.admin.model.CategoriaDto;
import com.bibliotec.admin.service.ApiService;
import com.bibliotec.admin.util.UIUtils;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class CategoriasPanel extends JPanel {

    private JTable table;
    private DefaultTableModel model;
    private List<CategoriaDto> categorias;

    public CategoriasPanel() {
        setLayout(new BorderLayout());
        setBackground(UIUtils.BG_LIGHT);
        buildUI();
        load();
    }

    private void buildUI() {
        JPanel header = UIUtils.headerPanel("🏷  Gestión de Categorías",
                "Administra las categorías del catálogo de libros");

        JPanel btnBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        btnBar.setOpaque(false);
        btnBar.setBorder(BorderFactory.createEmptyBorder(0,0,0,24));
        JButton btnNuevo = UIUtils.primaryButton("＋  Nueva Categoría");
        btnNuevo.addActionListener(e -> dialogCategoria(null));
        btnBar.add(btnNuevo);
        header.add(btnBar, BorderLayout.EAST);

        model = new DefaultTableModel(new String[]{"#","Nombre","Acciones"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(model);
        UIUtils.styleTable(table);
        table.getColumnModel().getColumn(0).setPreferredWidth(50);
        table.getColumnModel().getColumn(1).setPreferredWidth(400);
        table.getColumnModel().getColumn(2).setPreferredWidth(160);

        table.getColumnModel().getColumn(2).setCellRenderer(new javax.swing.table.DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(JTable t, Object v,
                    boolean sel, boolean foc, int row, int col) {
                JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 5));
                p.setBackground(sel ? new Color(232,234,246) : Color.WHITE);
                JButton e = makeBtn("✏ Editar","#e3f2fd","#1565c0");
                JButton d = makeBtn("🗑 Eliminar","#ffebee","#b71c1c");
                p.add(e); p.add(d);
                return p;
            }
            JButton makeBtn(String t, String bg, String fg) {
                JButton b = new JButton(t);
                b.setFont(new Font("Segoe UI",Font.BOLD,11));
                b.setBackground(Color.decode(bg)); b.setForeground(Color.decode(fg));
                b.setBorderPainted(false); b.setFocusPainted(false);
                b.setPreferredSize(new Dimension(72,24)); return b;
            }
        });

        table.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseClicked(java.awt.event.MouseEvent e) {
                int row = table.rowAtPoint(e.getPoint());
                int col = table.columnAtPoint(e.getPoint());
                if (row < 0 || col != 2 || categorias == null) return;
                CategoriaDto c = categorias.get(row);
                int rel = e.getX() - table.getCellRect(row,col,true).x;
                if (rel < 80) dialogCategoria(c);
                else          confirmarEliminar(c);
            }
        });

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(224,224,224)));

        // Right side form / hint
        JPanel hint = new JPanel();
        hint.setBackground(Color.WHITE);
        hint.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(224,224,224),1,true),
                BorderFactory.createEmptyBorder(20,20,20,20)));
        hint.setLayout(new BoxLayout(hint, BoxLayout.Y_AXIS));
        hint.setPreferredSize(new Dimension(280,0));

        JLabel hTitle = new JLabel("ℹ  Categorías");
        hTitle.setFont(UIUtils.FONT_HEADER); hTitle.setForeground(UIUtils.PRIMARY);
        JTextArea hText = new JTextArea(
            "Las categorías permiten organizar y filtrar los libros del catálogo.\n\n" +
            "Puedes asignar múltiples categorías a un mismo libro desde la sección de Libros.\n\n" +
            "Al eliminar una categoría, los libros ya no aparecerán bajo ella, " +
            "pero no serán eliminados.");
        hText.setEditable(false); hText.setOpaque(false);
        hText.setFont(UIUtils.FONT_BODY); hText.setForeground(UIUtils.TEXT_MUTED);
        hText.setWrapStyleWord(true); hText.setLineWrap(true);

        hint.add(hTitle); hint.add(Box.createVerticalStrut(12)); hint.add(hText);

        JPanel center = new JPanel(new BorderLayout(16,0));
        center.setBackground(UIUtils.BG_LIGHT);
        center.setBorder(BorderFactory.createEmptyBorder(16,24,24,24));
        center.add(scroll, BorderLayout.CENTER);
        center.add(hint, BorderLayout.EAST);

        add(header, BorderLayout.NORTH);
        add(center, BorderLayout.CENTER);
    }

    private void load() {
        new SwingWorker<List<CategoriaDto>,Void>() {
            @Override protected List<CategoriaDto> doInBackground() throws Exception {
                return ApiService.get().getCategorias();
            }
            @Override protected void done() {
                try {
                    categorias = get(); model.setRowCount(0); int i=1;
                    for (CategoriaDto c : categorias)
                        model.addRow(new Object[]{i++, c.getNombre(), "•••"});
                } catch (Exception e) { UIUtils.showError(CategoriasPanel.this, e.getMessage()); }
            }
        }.execute();
    }

    private void dialogCategoria(CategoriaDto existing) {
        boolean edit = existing != null;
        String titulo = edit ? "Editar Categoría" : "Nueva Categoría";
        String input = (String) JOptionPane.showInputDialog(this,
                "Nombre de la categoría:", titulo,
                JOptionPane.PLAIN_MESSAGE, null, null,
                edit ? existing.getNombre() : "");
        if (input == null || input.isBlank()) return;
        new SwingWorker<Void,Void>() {
            @Override protected Void doInBackground() throws Exception {
                if (edit) ApiService.get().updateCategoria(existing.getIdCategoria(), input.trim());
                else      ApiService.get().createCategoria(input.trim());
                return null;
            }
            @Override protected void done() {
                try { get(); load(); }
                catch (Exception e) { UIUtils.showError(CategoriasPanel.this, e.getMessage()); }
            }
        }.execute();
    }

    private void confirmarEliminar(CategoriaDto c) {
        if (!UIUtils.confirm(this, "¿Eliminar la categoría \"" + c.getNombre() + "\"?")) return;
        new SwingWorker<Void,Void>() {
            @Override protected Void doInBackground() throws Exception {
                ApiService.get().deleteCategoria(c.getIdCategoria()); return null;
            }
            @Override protected void done() {
                try { get(); load(); }
                catch (Exception e) { UIUtils.showError(CategoriasPanel.this, e.getMessage()); }
            }
        }.execute();
    }
}
