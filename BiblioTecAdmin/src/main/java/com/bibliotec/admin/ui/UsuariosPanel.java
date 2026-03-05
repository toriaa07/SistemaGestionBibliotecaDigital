package com.bibliotec.admin.ui;

import com.bibliotec.admin.model.UsuarioDto;
import com.bibliotec.admin.service.ApiService;
import com.bibliotec.admin.util.UIUtils;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class UsuariosPanel extends JPanel {

    private JTable table;
    private DefaultTableModel model;
    private JTextField searchField;
    private JComboBox<String> rolBox, estadoBox;
    private List<UsuarioDto> usuarios;

    private static final String[] COLS = {
        "#", "Nombre", "Correo", "Rol", "Estado", "Registro", "Acciones"
    };

    public UsuariosPanel() {
        setLayout(new BorderLayout());
        setBackground(UIUtils.BG_LIGHT);
        buildUI();
        load();
    }

    private void buildUI() {
        // ── Header ───────────────────────────────────
        JPanel header = UIUtils.headerPanel("👥  Gestión de Usuarios",
                "Crea, edita y administra los usuarios del sistema");

        JPanel btnBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        btnBar.setOpaque(false);
        btnBar.setBorder(BorderFactory.createEmptyBorder(0,0,0,24));

        JButton btnNuevo = UIUtils.primaryButton("＋  Nuevo Usuario");
        btnNuevo.addActionListener(e -> dialogCrear());
        btnBar.add(btnNuevo);
        header.add(btnBar, BorderLayout.EAST);

        // ── Filter bar ───────────────────────────────
        JPanel filter = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        filter.setBackground(Color.WHITE);
        filter.setBorder(BorderFactory.createMatteBorder(0,0,1,0, new Color(224,224,224)));

        searchField = UIUtils.styledField("");
        searchField.setPreferredSize(new Dimension(240, 34));
        searchField.putClientProperty("JTextField.placeholderText", "Buscar nombre o correo…");
        searchField.addActionListener(e -> load());

        rolBox   = new JComboBox<>(new String[]{"Todos los roles","ADMIN","USUARIO"});
        estadoBox= new JComboBox<>(new String[]{"Todos los estados","ACTIVO","SUSPENDIDO"});
        for (JComboBox<?> cb : new JComboBox[]{rolBox, estadoBox})
            cb.setPreferredSize(new Dimension(160, 34));

        JButton btnBuscar = UIUtils.ghostButton("Buscar");
        btnBuscar.addActionListener(e -> load());

        filter.add(new JLabel("🔍")); filter.add(searchField);
        filter.add(rolBox); filter.add(estadoBox); filter.add(btnBuscar);

        // ── Table ─────────────────────────────────────
        model = new DefaultTableModel(COLS, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(model);
        UIUtils.styleTable(table);
        table.getColumnModel().getColumn(0).setPreferredWidth(40);
        table.getColumnModel().getColumn(1).setPreferredWidth(200);
        table.getColumnModel().getColumn(2).setPreferredWidth(220);
        table.getColumnModel().getColumn(3).setPreferredWidth(80);
        table.getColumnModel().getColumn(4).setPreferredWidth(90);
        table.getColumnModel().getColumn(5).setPreferredWidth(100);
        table.getColumnModel().getColumn(6).setPreferredWidth(160);

        // Color estado column
        table.getColumnModel().getColumn(4).setCellRenderer(new javax.swing.table.DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(JTable t, Object v,
                    boolean sel, boolean foc, int r, int c) {
                super.getTableCellRendererComponent(t, v, sel, foc, r, c);
                String s = v != null ? v.toString() : "";
                setForeground("ACTIVO".equals(s) ? UIUtils.SUCCESS : UIUtils.DANGER);
                setFont(getFont().deriveFont(Font.BOLD));
                return this;
            }
        });

        // Action buttons column
        table.getColumnModel().getColumn(6).setCellRenderer(new ButtonRenderer());
        table.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseClicked(java.awt.event.MouseEvent e) {
                int row = table.rowAtPoint(e.getPoint());
                int col = table.columnAtPoint(e.getPoint());
                if (row < 0 || col != 6 || usuarios == null) return;
                UsuarioDto u = usuarios.get(row);
                String action = detectActionButton(e.getX(), table.getCellRect(row, col, true).x);
                handleAction(action, u);
            }
        });

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(224,224,224)));

        JPanel center = new JPanel(new BorderLayout());
        center.setBackground(UIUtils.BG_LIGHT);
        center.setBorder(BorderFactory.createEmptyBorder(16, 24, 24, 24));
        center.add(scroll, BorderLayout.CENTER);

        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        top.add(header, BorderLayout.NORTH);
        top.add(filter, BorderLayout.CENTER);

        add(top, BorderLayout.NORTH);
        add(center, BorderLayout.CENTER);
    }

    private String detectActionButton(int mouseX, int cellX) {
        int rel = mouseX - cellX;
        if (rel < 55)  return "editar";
        if (rel < 110) return "estado";
        return "eliminar";
    }

    private void load() {
        String q      = searchField.getText().trim();
        String rol    = rolBox.getSelectedIndex()   == 0 ? null : (String)rolBox.getSelectedItem();
        String estado = estadoBox.getSelectedIndex()== 0 ? null : (String)estadoBox.getSelectedItem();

        new SwingWorker<List<UsuarioDto>, Void>() {
            @Override protected List<UsuarioDto> doInBackground() throws Exception {
                return ApiService.get().getUsuarios(q, rol, estado);
            }
            @Override protected void done() {
                try {
                    usuarios = get();
                    model.setRowCount(0);
                    int i = 1;
                    for (UsuarioDto u : usuarios) {
                        model.addRow(new Object[]{
                            i++, u.getNombre(), u.getCorreo(),
                            u.getRol(), u.getEstado(), u.getFechaRegistroStr(), "•••"
                        });
                    }
                } catch (Exception e) {
                    UIUtils.showError(UsuariosPanel.this, "Error: " + e.getMessage());
                }
            }
        }.execute();
    }

    private void handleAction(String action, UsuarioDto u) {
        switch (action) {
            case "editar"  -> dialogEditar(u);
            case "estado"  -> toggleEstado(u);
            case "eliminar" -> confirmarEliminar(u);
        }
    }

    private void dialogCrear() {
        JDialog dlg = new JDialog(
        SwingUtilities.getWindowAncestor(this),
        "Nuevo Usuario",
        java.awt.Dialog.ModalityType.APPLICATION_MODAL
);
        dlg.setSize(420, 390);
        dlg.setLocationRelativeTo(this);

        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBorder(BorderFactory.createEmptyBorder(20, 28, 20, 28));
        p.setBackground(Color.WHITE);

        JTextField nomF  = UIUtils.styledField(""); nomF.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        JTextField corF  = UIUtils.styledField(""); corF.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        JPasswordField pssF = UIUtils.styledPasswordField(); pssF.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        JComboBox<String> rolC = new JComboBox<>(new String[]{"USUARIO","ADMIN"});
        rolC.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));

        p.add(new JLabel("Crear Nuevo Usuario") {{ setFont(UIUtils.FONT_HEADER); setForeground(UIUtils.PRIMARY); }});
        p.add(Box.createVerticalStrut(18));
        p.add(UIUtils.fieldLabel("Nombre completo")); p.add(Box.createVerticalStrut(4)); p.add(nomF);
        p.add(Box.createVerticalStrut(12));
        p.add(UIUtils.fieldLabel("Correo electrónico")); p.add(Box.createVerticalStrut(4)); p.add(corF);
        p.add(Box.createVerticalStrut(12));
        p.add(UIUtils.fieldLabel("Contraseña")); p.add(Box.createVerticalStrut(4)); p.add(pssF);
        p.add(Box.createVerticalStrut(12));
        p.add(UIUtils.fieldLabel("Rol")); p.add(Box.createVerticalStrut(4)); p.add(rolC);
        p.add(Box.createVerticalStrut(20));

        JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        btns.setOpaque(false);
        JButton cancel = UIUtils.ghostButton("Cancelar");
        JButton save   = UIUtils.primaryButton("Crear");
        cancel.addActionListener(e -> dlg.dispose());
        save.addActionListener(e -> {
            String nom = nomF.getText().trim();
            String cor = corF.getText().trim();
            String pas = new String(pssF.getPassword());
            String rol = (String)rolC.getSelectedItem();
            if (nom.isEmpty() || cor.isEmpty() || pas.isEmpty()) {
                UIUtils.showError(dlg, "Todos los campos son obligatorios."); return;
            }
            new SwingWorker<Void,Void>() {
                @Override protected Void doInBackground() throws Exception {
                    ApiService.get().createUsuario(nom, cor, pas, rol); return null;
                }
                @Override protected void done() {
                    try { get(); dlg.dispose(); load(); UIUtils.showInfo(UsuariosPanel.this, "Usuario creado."); }
                    catch (Exception ex) { UIUtils.showError(dlg, ex.getMessage()); }
                }
            }.execute();
        });
        btns.add(cancel); btns.add(save);
        p.add(btns);

        dlg.setContentPane(p);
        dlg.setVisible(true);
    }

    private void dialogEditar(UsuarioDto u) {
        JDialog dlg = new JDialog(
        SwingUtilities.getWindowAncestor(this),
        "Algo",
        java.awt.Dialog.ModalityType.APPLICATION_MODAL);
        
        dlg.setSize(400, 280);
        dlg.setLocationRelativeTo(this);

        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBorder(BorderFactory.createEmptyBorder(20, 28, 20, 28));
        p.setBackground(Color.WHITE);

        JTextField nomF = UIUtils.styledField(""); nomF.setText(u.getNombre()); nomF.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        JTextField corF = UIUtils.styledField(""); corF.setText(u.getCorreo()); corF.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));

        p.add(new JLabel("Editar Usuario") {{ setFont(UIUtils.FONT_HEADER); setForeground(UIUtils.PRIMARY); }});
        p.add(Box.createVerticalStrut(16));
        p.add(UIUtils.fieldLabel("Nombre")); p.add(Box.createVerticalStrut(4)); p.add(nomF);
        p.add(Box.createVerticalStrut(12));
        p.add(UIUtils.fieldLabel("Correo")); p.add(Box.createVerticalStrut(4)); p.add(corF);
        p.add(Box.createVerticalStrut(20));

        JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        btns.setOpaque(false);
        JButton cancel = UIUtils.ghostButton("Cancelar");
        JButton save   = UIUtils.primaryButton("Guardar");
        cancel.addActionListener(e -> dlg.dispose());
        save.addActionListener(e -> {
            new SwingWorker<Void,Void>() {
                @Override protected Void doInBackground() throws Exception {
                    ApiService.get().updateUsuario(u.getIdUsuario(), nomF.getText().trim(), corF.getText().trim()); return null;
                }
                @Override protected void done() {
                    try { get(); dlg.dispose(); load(); }
                    catch (Exception ex) { UIUtils.showError(dlg, ex.getMessage()); }
                }
            }.execute();
        });
        btns.add(cancel); btns.add(save); p.add(btns);
        dlg.setContentPane(p); dlg.setVisible(true);
    }

    private void toggleEstado(UsuarioDto u) {
        String nuevo = "ACTIVO".equals(u.getEstado()) ? "SUSPENDIDO" : "ACTIVO";
        if (!UIUtils.confirm(this, "¿Cambiar estado de " + u.getNombre() + " a " + nuevo + "?")) return;
        new SwingWorker<Void,Void>() {
            @Override protected Void doInBackground() throws Exception {
                ApiService.get().updateEstadoUsuario(u.getIdUsuario(), nuevo); return null;
            }
            @Override protected void done() {
                try { get(); load(); } catch (Exception e) { UIUtils.showError(UsuariosPanel.this, e.getMessage()); }
            }
        }.execute();
    }

    private void confirmarEliminar(UsuarioDto u) {
        if (!UIUtils.confirm(this, "¿Eliminar al usuario \"" + u.getNombre() + "\"?\nEsta acción no se puede deshacer.")) return;
        new SwingWorker<Void,Void>() {
            @Override protected Void doInBackground() throws Exception {
                ApiService.get().deleteUsuario(u.getIdUsuario()); return null;
            }
            @Override protected void done() {
                try { get(); load(); UIUtils.showInfo(UsuariosPanel.this, "Usuario eliminado."); }
                catch (Exception e) { UIUtils.showError(UsuariosPanel.this, e.getMessage()); }
            }
        }.execute();
    }

    // Renderer that paints 3 small buttons in the actions cell
    static class ButtonRenderer extends javax.swing.table.DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable t, Object v,
                boolean sel, boolean foc, int row, int col) {
            JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 4));
            p.setBackground(sel ? new Color(232,234,246) : Color.WHITE);

            JButton e = new JButton("Editar");
            e.setFont(new Font("Segoe UI", Font.BOLD, 11));
            e.setBackground(new Color(227, 242, 253));
            e.setForeground(new Color(21, 101, 192));
            e.setBorderPainted(false); e.setFocusPainted(false);
            e.setPreferredSize(new Dimension(52, 24));

            JButton s = new JButton("Estado");
            s.setFont(new Font("Segoe UI", Font.BOLD, 11));
            s.setBackground(new Color(232, 245, 233));
            s.setForeground(new Color(46, 125, 50));
            s.setBorderPainted(false); s.setFocusPainted(false);
            s.setPreferredSize(new Dimension(52, 24));

            JButton d = new JButton("Eliminar");
            d.setFont(new Font("Segoe UI", Font.BOLD, 11));
            d.setBackground(new Color(255, 235, 238));
            d.setForeground(new Color(183, 28, 28));
            d.setBorderPainted(false); d.setFocusPainted(false);
            d.setPreferredSize(new Dimension(56, 24));

            p.add(e); p.add(s); p.add(d);
            return p;
        }
    }
}
