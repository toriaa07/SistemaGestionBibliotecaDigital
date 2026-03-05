package com.bibliotec.admin.ui;

import com.bibliotec.admin.model.PrestamoDto;
import com.bibliotec.admin.service.ApiService;
import com.bibliotec.admin.util.UIUtils;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class PrestamosPanel extends JPanel {

    private JTable table;
    private DefaultTableModel model;
    private JComboBox<String> estadoBox;
    private List<PrestamoDto> prestamos;

    private static final String[] COLS = {
        "#", "Usuario", "Libro", "Autor", "F. Préstamo", "F. Vencimiento", "F. Devolución", "Estado", "Acciones"
    };

    public PrestamosPanel() {
        setLayout(new BorderLayout());
        setBackground(UIUtils.BG_LIGHT);
        buildUI();
        load();
    }

    private void buildUI() {
        JPanel header = UIUtils.headerPanel("🔖  Gestión de Préstamos",
                "Consulta y administra todos los préstamos del sistema");

        JPanel btnBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        btnBar.setOpaque(false);
        btnBar.setBorder(BorderFactory.createEmptyBorder(0,0,0,24));
        JButton btnVenc = UIUtils.dangerButton("⚠  Marcar Vencidos");
        btnVenc.addActionListener(e -> marcarVencidos());
        btnBar.add(btnVenc);
        header.add(btnBar, BorderLayout.EAST);

        // Filter
        JPanel filter = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        filter.setBackground(Color.WHITE);
        filter.setBorder(BorderFactory.createMatteBorder(0,0,1,0, new Color(224,224,224)));

        estadoBox = new JComboBox<>(new String[]{"Todos los estados","ACTIVO","VENCIDO","DEVUELTO"});
        estadoBox.setPreferredSize(new Dimension(180, 34));

        JButton btnFiltrar = UIUtils.ghostButton("Filtrar");
        btnFiltrar.addActionListener(e -> load());
        JButton btnRefresh = UIUtils.ghostButton("↻ Actualizar");
        btnRefresh.addActionListener(e -> load());

        filter.add(UIUtils.fieldLabel("Estado:")); filter.add(estadoBox);
        filter.add(btnFiltrar); filter.add(btnRefresh);

        // Table
        model = new DefaultTableModel(COLS, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(model);
        UIUtils.styleTable(table);
        int[] widths = {35, 160, 200, 140, 120, 130, 120, 90, 130};
        for (int i = 0; i < widths.length; i++)
            table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);

        // Estado cell color
        table.getColumnModel().getColumn(7).setCellRenderer(new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(JTable t, Object v,
                    boolean sel, boolean foc, int r, int c) {
                super.getTableCellRendererComponent(t, v, sel, foc, r, c);
                String s = v != null ? v.toString() : "";
                setForeground(switch (s) {
                    case "ACTIVO"   -> UIUtils.ACCENT;
                    case "VENCIDO"  -> UIUtils.DANGER;
                    case "DEVUELTO" -> UIUtils.SUCCESS;
                    default -> Color.BLACK;
                });
                setFont(getFont().deriveFont(Font.BOLD));
                setHorizontalAlignment(CENTER);
                return this;
            }
        });

        table.getColumnModel().getColumn(8).setCellRenderer(new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(JTable t, Object v,
                    boolean sel, boolean foc, int row, int col) {
                JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 5));
                p.setBackground(sel ? new Color(232,234,246) : Color.WHITE);
                if (prestamos != null && row < prestamos.size()) {
                    PrestamoDto pr = prestamos.get(row);
                    boolean active = "ACTIVO".equals(pr.getEstado()) || "VENCIDO".equals(pr.getEstado());
                    if (active) {
                        JButton devolver = btn("Devolver", "#e8f5e9","#2e7d32");
                        p.add(devolver);
                    }
                    JButton del = btn("Eliminar","#ffebee","#b71c1c");
                    p.add(del);
                }
                return p;
            }
            JButton btn(String t, String bg, String fg) {
                JButton b = new JButton(t);
                b.setFont(new Font("Segoe UI",Font.BOLD,11));
                b.setBackground(Color.decode(bg)); b.setForeground(Color.decode(fg));
                b.setBorderPainted(false); b.setFocusPainted(false);
                b.setPreferredSize(new Dimension(62,24)); return b;
            }
        });

        table.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseClicked(java.awt.event.MouseEvent e) {
                int row = table.rowAtPoint(e.getPoint());
                int col = table.columnAtPoint(e.getPoint());
                if (row < 0 || col != 8 || prestamos == null) return;
                PrestamoDto p = prestamos.get(row);
                int rel = e.getX() - table.getCellRect(row,col,true).x;
                boolean active = "ACTIVO".equals(p.getEstado()) || "VENCIDO".equals(p.getEstado());
                if (active && rel < 68) devolver(p);
                else confirmarEliminar(p);
            }
        });

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(224,224,224)));

        JPanel center = new JPanel(new BorderLayout());
        center.setBackground(UIUtils.BG_LIGHT);
        center.setBorder(BorderFactory.createEmptyBorder(16,24,24,24));
        center.add(scroll, BorderLayout.CENTER);

        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        top.add(header, BorderLayout.NORTH);
        top.add(filter, BorderLayout.CENTER);

        add(top, BorderLayout.NORTH);
        add(center, BorderLayout.CENTER);
    }

    private void load() {
        String estado = estadoBox.getSelectedIndex() == 0 ? null : (String)estadoBox.getSelectedItem();
        new SwingWorker<List<PrestamoDto>,Void>() {
            @Override protected List<PrestamoDto> doInBackground() throws Exception {
                return ApiService.get().getPrestamos(estado, null);
            }
            @Override protected void done() {
                try {
                    prestamos = get(); model.setRowCount(0); int i=1;
                    for (PrestamoDto p : prestamos) {
                        model.addRow(new Object[]{
                            i++, p.getNombreUsuario(), p.getTituloLibro(), p.getAutorLibro(),
                            p.getFechaPrestamoStr(), p.getFechaVencimientoStr(),
                            p.getFechaDevolucionStr(), p.getEstado(), "•••"
                        });
                    }
                    table.repaint();
                } catch (Exception e) { UIUtils.showError(PrestamosPanel.this, e.getMessage()); }
            }
        }.execute();
    }

    private void marcarVencidos() {
        new SwingWorker<Void,Void>() {
            @Override protected Void doInBackground() throws Exception {
                ApiService.get().marcarVencidos(); return null;
            }
            @Override protected void done() {
                try { get(); UIUtils.showInfo(PrestamosPanel.this,"Préstamos vencidos marcados correctamente."); load(); }
                catch (Exception e) { UIUtils.showError(PrestamosPanel.this, e.getMessage()); }
            }
        }.execute();
    }

    private void devolver(PrestamoDto p) {
        if (!UIUtils.confirm(this, "¿Marcar como DEVUELTO el préstamo de\n\"" + p.getTituloLibro() + "\"?")) return;
        new SwingWorker<Void,Void>() {
            @Override protected Void doInBackground() throws Exception {
                ApiService.get().updateEstadoPrestamo(p.getIdPrestamo(), "DEVUELTO"); return null;
            }
            @Override protected void done() {
                try { get(); load(); } catch (Exception e) { UIUtils.showError(PrestamosPanel.this, e.getMessage()); }
            }
        }.execute();
    }

    private void confirmarEliminar(PrestamoDto p) {
        if (!UIUtils.confirm(this, "¿Eliminar este registro de préstamo?\nEsta acción no se puede deshacer.")) return;
        new SwingWorker<Void,Void>() {
            @Override protected Void doInBackground() throws Exception {
                ApiService.get().deletePrestamo(p.getIdPrestamo()); return null;
            }
            @Override protected void done() {
                try { get(); load(); } catch (Exception e) { UIUtils.showError(PrestamosPanel.this, e.getMessage()); }
            }
        }.execute();
    }
}
