package com.bibliotec.admin.ui;

import com.bibliotec.admin.model.CategoriaDto;
import com.bibliotec.admin.model.LibroDto;
import com.bibliotec.admin.service.ApiService;
import com.bibliotec.admin.util.UIUtils;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class LibrosPanel extends JPanel {

    private JTable table;
    private DefaultTableModel model;
    private JTextField searchField;
    private JComboBox<String> estadoBox;
    private List<LibroDto> libros;

    private static final String[] COLS = {
        "#", "Título", "Autor", "Editorial", "Año", "Total", "Disp.", "Estado", "Categorías", "Acciones"
    };

    public LibrosPanel() {
        setLayout(new BorderLayout());
        setBackground(UIUtils.BG_LIGHT);
        buildUI();
        load();
    }

    private void buildUI() {
        JPanel header = UIUtils.headerPanel("📖  Gestión de Libros",
                "Administra el catálogo completo de libros");

        JPanel btnBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        btnBar.setOpaque(false);
        btnBar.setBorder(BorderFactory.createEmptyBorder(0,0,0,24));
        JButton btnNuevo = UIUtils.primaryButton("＋  Nuevo Libro");
        btnNuevo.addActionListener(e -> dialogLibro(null));
        btnBar.add(btnNuevo);
        header.add(btnBar, BorderLayout.EAST);

        // Filter bar
        JPanel filter = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        filter.setBackground(Color.WHITE);
        filter.setBorder(BorderFactory.createMatteBorder(0,0,1,0, new Color(224,224,224)));

        searchField = UIUtils.styledField("");
        searchField.setPreferredSize(new Dimension(260, 34));
        searchField.putClientProperty("JTextField.placeholderText", "Buscar título o autor…");
        searchField.addActionListener(e -> load());

        estadoBox = new JComboBox<>(new String[]{"Todos","Activos","Inactivos"});
        estadoBox.setPreferredSize(new Dimension(140, 34));

        JButton btnBuscar = UIUtils.ghostButton("Buscar");
        btnBuscar.addActionListener(e -> load());

        filter.add(new JLabel("🔍")); filter.add(searchField);
        filter.add(estadoBox); filter.add(btnBuscar);

        // Table
        model = new DefaultTableModel(COLS, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(model);
        UIUtils.styleTable(table);
        int[] widths = {35, 230, 160, 130, 50, 55, 50, 75, 170, 140};
        for (int i = 0; i < widths.length; i++)
            table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);

        // Estado color
        table.getColumnModel().getColumn(7).setCellRenderer(new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(JTable t, Object v,
                    boolean sel, boolean foc, int r, int c) {
                super.getTableCellRendererComponent(t, v, sel, foc, r, c);
                String s = v != null ? v.toString() : "";
                setForeground("Activo".equals(s) ? UIUtils.SUCCESS : UIUtils.DANGER);
                setFont(getFont().deriveFont(Font.BOLD)); setHorizontalAlignment(CENTER);
                return this;
            }
        });
        // Disponibles color
        table.getColumnModel().getColumn(6).setCellRenderer(new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(JTable t, Object v,
                    boolean sel, boolean foc, int r, int c) {
                super.getTableCellRendererComponent(t, v, sel, foc, r, c);
                int disp = v != null ? (int)v : 0;
                setForeground(disp > 0 ? UIUtils.SUCCESS : UIUtils.DANGER);
                setFont(getFont().deriveFont(Font.BOLD)); setHorizontalAlignment(CENTER);
                return this;
            }
        });

        table.getColumnModel().getColumn(9).setCellRenderer(new UsuariosPanel.ButtonRenderer() {
            @Override public Component getTableCellRendererComponent(JTable t, Object v,
                    boolean sel, boolean foc, int row, int col) {
                JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 4));
                p.setBackground(sel ? new Color(232,234,246) : Color.WHITE);
                for (String[] lbl : new String[][]{
                        {"Editar","#e3f2fd","#1565c0"},
                        {"Activar","#e8f5e9","#2e7d32"},
                        {"Eliminar","#ffebee","#b71c1c"}}) {
                    JButton b = new JButton(lbl[0]);
                    b.setFont(new Font("Segoe UI",Font.BOLD,11));
                    b.setBackground(Color.decode(lbl[1]));
                    b.setForeground(Color.decode(lbl[2]));
                    b.setBorderPainted(false); b.setFocusPainted(false);
                    b.setPreferredSize(new Dimension(56, 24)); p.add(b);
                }
                return p;
            }
        });

        table.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseClicked(java.awt.event.MouseEvent e) {
                int row = table.rowAtPoint(e.getPoint());
                int col = table.columnAtPoint(e.getPoint());
                if (row < 0 || col != 9 || libros == null) return;
                LibroDto l = libros.get(row);
                int rel = e.getX() - table.getCellRect(row,col,true).x;
                if      (rel < 62)  dialogLibro(l);
                else if (rel < 122) toggleEstado(l);
                else                confirmarEliminar(l);
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
        String q = searchField.getText().trim();
        Boolean activo = switch (estadoBox.getSelectedIndex()) {
            case 1 -> true; case 2 -> false; default -> null;
        };
        new SwingWorker<List<LibroDto>,Void>() {
            @Override protected List<LibroDto> doInBackground() throws Exception {
                return ApiService.get().getLibros(q, activo);
            }
            @Override protected void done() {
                try {
                    libros = get(); model.setRowCount(0); int i=1;
                    for (LibroDto l : libros) {
                        model.addRow(new Object[]{
                            i++, l.getTitulo(), l.getAutor(), l.getEditorial(),
                            l.getAnio(), l.getTotalEjemplares(), l.getDisponibles(),
                            l.getActivoStr(), l.getCategoriasStr(), "•••"
                        });
                    }
                } catch (Exception e) { UIUtils.showError(LibrosPanel.this, e.getMessage()); }
            }
        }.execute();
    }

    private void dialogLibro(LibroDto existing) {
        boolean editar = existing != null;
        JDialog dlg = new JDialog(
        SwingUtilities.getWindowAncestor(this),
        editar ? "Editar Libro" : "Nuevo Libro",
        java.awt.Dialog.ModalityType.APPLICATION_MODAL);
        dlg.setSize(520, 520);
        dlg.setLocationRelativeTo(this);

        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBorder(BorderFactory.createEmptyBorder(20, 28, 20, 28));
        p.setBackground(Color.WHITE);

        JTextField titF = field(editar ? existing.getTitulo() : "");
        JTextField autF = field(editar ? existing.getAutor()  : "");
        JTextField edtF = field(editar ? existing.getEditorial() : "");
        JTextField anyF = field(editar && existing.getAnio() != null ? existing.getAnio().toString() : "");
        JTextField urlF = field(editar ? existing.getRutaPdf() : "");
        JTextField ejF  = field(editar ? String.valueOf(existing.getTotalEjemplares()) : "1");

        // Categories checklist
        JPanel catPanel = new JPanel();
        catPanel.setLayout(new BoxLayout(catPanel, BoxLayout.Y_AXIS));
        catPanel.setBackground(new Color(248, 249, 250));
        List<JCheckBox> catChecks = new ArrayList<>();
        new SwingWorker<List<CategoriaDto>,Void>() {
            @Override protected List<CategoriaDto> doInBackground() throws Exception {
                return ApiService.get().getCategorias();
            }
            @Override protected void done() {
                try {
                    for (CategoriaDto c : get()) {
                        JCheckBox cb = new JCheckBox(c.getNombre());
                        cb.putClientProperty("catId", c.getIdCategoria());
                        cb.setOpaque(false);
                        cb.setFont(UIUtils.FONT_BODY);
                        if (editar && existing.getCategorias() != null)
                            cb.setSelected(existing.getCategorias().contains(c.getNombre()));
                        catChecks.add(cb);
                        catPanel.add(cb);
                    }
                    catPanel.revalidate();
                } catch (Exception ignored) {}
            }
        }.execute();

        p.add(title(editar ? "Editar Libro" : "Nuevo Libro"));
        p.add(Box.createVerticalStrut(14));
        p.add(UIUtils.fieldLabel("Título *")); p.add(Box.createVerticalStrut(3)); p.add(titF);
        p.add(Box.createVerticalStrut(10));
        p.add(UIUtils.fieldLabel("Autor *")); p.add(Box.createVerticalStrut(3)); p.add(autF);
        p.add(Box.createVerticalStrut(10));

        JPanel row1 = new JPanel(new GridLayout(1,2,10,0)); row1.setOpaque(false);
        JPanel edtBox = col("Editorial", edtF); JPanel anyBox = col("Año", anyF);
        row1.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70)); row1.add(edtBox); row1.add(anyBox);
        p.add(row1);

        p.add(Box.createVerticalStrut(10));
        p.add(UIUtils.fieldLabel("URL del PDF *")); p.add(Box.createVerticalStrut(3)); p.add(urlF);
        p.add(Box.createVerticalStrut(10));
        p.add(UIUtils.fieldLabel("Total Ejemplares *")); p.add(Box.createVerticalStrut(3)); p.add(ejF);
        p.add(Box.createVerticalStrut(12));
        p.add(UIUtils.fieldLabel("Categorías"));
        p.add(Box.createVerticalStrut(5));
        JScrollPane catScroll = new JScrollPane(catPanel);
        catScroll.setPreferredSize(new Dimension(0, 100));
        catScroll.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));
        p.add(catScroll);
        p.add(Box.createVerticalStrut(16));

        JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        btns.setOpaque(false);
        JButton cancel = UIUtils.ghostButton("Cancelar");
        JButton save   = UIUtils.primaryButton(editar ? "Guardar" : "Crear");
        cancel.addActionListener(e -> dlg.dispose());
        save.addActionListener(e -> {
            String tit = titF.getText().trim(), aut = autF.getText().trim();
            String url = urlF.getText().trim();
            if (tit.isEmpty() || aut.isEmpty() || url.isEmpty()) {
                UIUtils.showError(dlg, "Título, Autor y URL son obligatorios."); return;
            }
            int ej; try { ej = Integer.parseInt(ejF.getText().trim()); } catch (Exception ex){ UIUtils.showError(dlg,"Ejemplares debe ser un número."); return; }
            Integer anio = null; try { if(!anyF.getText().isBlank()) anio = Integer.parseInt(anyF.getText().trim()); } catch (Exception ignored){}
            Integer anioFinal = anio; int ejFinal = ej;
            new SwingWorker<LibroDto,Void>() {
                @Override protected LibroDto doInBackground() throws Exception {
                    LibroDto l;
                    if (editar)
                        l = ApiService.get().updateLibro(existing.getLibroId(), tit, aut, edtF.getText().trim(), anioFinal, url, ejFinal);
                    else
                        l = ApiService.get().createLibro(tit, aut, edtF.getText().trim(), anioFinal, url, ejFinal);
                    List<Integer> ids = new ArrayList<>();
                    for (JCheckBox cb : catChecks) if (cb.isSelected()) ids.add((int)cb.getClientProperty("catId"));
                    if (!ids.isEmpty()) ApiService.get().asignarCategorias(l.getLibroId(), ids);
                    return l;
                }
                @Override protected void done() {
                    try { get(); dlg.dispose(); load(); }
                    catch (Exception ex) { UIUtils.showError(dlg, ex.getMessage()); }
                }
            }.execute();
        });
        btns.add(cancel); btns.add(save); p.add(btns);

        JScrollPane dlgScroll = new JScrollPane(p);
        dlgScroll.setBorder(null);
        dlg.setContentPane(dlgScroll);
        dlg.setVisible(true);
    }

    private void toggleEstado(LibroDto l) {
        boolean nuevo = !l.isActivo();
        if (!UIUtils.confirm(this, "¿" + (nuevo?"Activar":"Desactivar") + " el libro \"" + l.getTitulo() + "\"?")) return;
        new SwingWorker<Void,Void>() {
            @Override protected Void doInBackground() throws Exception {
                ApiService.get().updateEstadoLibro(l.getLibroId(), nuevo); return null;
            }
            @Override protected void done() {
                try { get(); load(); } catch (Exception e) { UIUtils.showError(LibrosPanel.this, e.getMessage()); }
            }
        }.execute();
    }

    private void confirmarEliminar(LibroDto l) {
        if (!UIUtils.confirm(this, "¿Eliminar el libro \"" + l.getTitulo() + "\"?")) return;
        new SwingWorker<Void,Void>() {
            @Override protected Void doInBackground() throws Exception {
                ApiService.get().deleteLibro(l.getLibroId()); return null;
            }
            @Override protected void done() {
                try { get(); load(); } catch (Exception e) { UIUtils.showError(LibrosPanel.this, e.getMessage()); }
            }
        }.execute();
    }

    private JTextField field(String val) {
        JTextField f = UIUtils.styledField(""); f.setText(val);
        f.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36)); return f;
    }
    private JLabel title(String t) {
        JLabel l = new JLabel(t); l.setFont(UIUtils.FONT_HEADER); l.setForeground(UIUtils.PRIMARY); return l;
    }
    private JPanel col(String lbl, JComponent field) {
        JPanel p = new JPanel(); p.setOpaque(false);
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.add(UIUtils.fieldLabel(lbl)); p.add(Box.createVerticalStrut(3)); p.add(field); return p;
    }
}
