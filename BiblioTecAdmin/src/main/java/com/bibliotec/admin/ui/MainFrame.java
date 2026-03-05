package com.bibliotec.admin.ui;

import com.bibliotec.admin.Main;
import com.bibliotec.admin.util.SessionManager;
import com.bibliotec.admin.util.UIUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.LinkedHashMap;
import java.util.Map;

public class MainFrame extends JFrame {

    private JPanel contentArea;
    private final Map<String, JPanel> navButtons = new LinkedHashMap<>();
    private JPanel activeBtn = null;
    private JPanel currentPanel = null;

    public MainFrame() {
        setTitle("BiblioTec — Panel de Administración");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1280, 780);
        setMinimumSize(new Dimension(1100, 680));
        setLocationRelativeTo(null);
        buildUI();
    }

    private void buildUI() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(UIUtils.BG_LIGHT);

        root.add(buildSidebar(), BorderLayout.WEST);

        contentArea = new JPanel(new BorderLayout());
        contentArea.setBackground(UIUtils.BG_LIGHT);
        root.add(contentArea, BorderLayout.CENTER);

        setContentPane(root);
        navigate("Dashboard", new DashboardPanel());
    }

    // ── Sidebar ───────────────────────────────────────
    private JPanel buildSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setBackground(UIUtils.SIDEBAR_BG);
        sidebar.setPreferredSize(new Dimension(230, 0));
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));

        // Brand header
        JPanel header = new JPanel();
        header.setBackground(UIUtils.SIDEBAR_ITEM);
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setAlignmentX(Component.LEFT_ALIGNMENT);
        header.setMaximumSize(new Dimension(230, 100));
        header.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel icon = new JLabel("📚  BiblioTec");
        icon.setFont(new Font("Segoe UI", Font.BOLD, 17));
        icon.setForeground(Color.WHITE);

        JLabel user = new JLabel("👤  " + SessionManager.getInstance().getNombre());
        user.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        user.setForeground(new Color(144, 164, 174));

        JLabel role = new JLabel("   ADMINISTRADOR");
        role.setFont(new Font("Segoe UI", Font.BOLD, 10));
        role.setForeground(new Color(100, 181, 246));

        header.add(icon);
        header.add(Box.createVerticalStrut(10));
        header.add(user);
        header.add(Box.createVerticalStrut(2));
        header.add(role);

        sidebar.add(header);
        sidebar.add(spacer(10));

        // Section label
        sidebar.add(sectionLabel("MENÚ PRINCIPAL"));

        addNavItem(sidebar, "📊  Dashboard",        "Dashboard",       () -> new DashboardPanel());
        addNavItem(sidebar, "👥  Usuarios",          "Usuarios",        () -> new UsuariosPanel());
        addNavItem(sidebar, "📖  Libros",            "Libros",          () -> new LibrosPanel());
        addNavItem(sidebar, "🔖  Préstamos",         "Préstamos",       () -> new PrestamosPanel());

        sidebar.add(spacer(4));
        sidebar.add(sectionLabel("CATÁLOGO"));
        addNavItem(sidebar, "🏷  Categorías",        "Categorías",      () -> new CategoriasPanel());

        sidebar.add(spacer(4));
        sidebar.add(sectionLabel("SISTEMA"));
        addNavItem(sidebar, "🔔  Notificaciones",    "Notificaciones",  () -> new NotificacionesPanel());
        addNavItem(sidebar, "⚙  Configuración",     "Configuración",   () -> new ConfiguracionPanel());

        // Spacer + Logout
        sidebar.add(Box.createVerticalGlue());
        sidebar.add(buildLogoutBtn());
        sidebar.add(spacer(10));

        return sidebar;
    }

    private void addNavItem(JPanel sidebar, String label, String key, java.util.function.Supplier<JPanel> factory) {
        JPanel btn = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        btn.setBackground(UIUtils.SIDEBAR_BG);
        btn.setMaximumSize(new Dimension(230, 42));
        btn.setPreferredSize(new Dimension(230, 42));
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        btn.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lbl.setForeground(new Color(176, 190, 197));
        lbl.setBorder(BorderFactory.createEmptyBorder(11, 22, 11, 22));

        btn.add(lbl);
        navButtons.put(key, btn);

        btn.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                navigate(key, factory.get());
            }
            @Override public void mouseEntered(MouseEvent e) {
                if (btn != activeBtn) btn.setBackground(new Color(38, 50, 65));
            }
            @Override public void mouseExited(MouseEvent e) {
                if (btn != activeBtn) btn.setBackground(UIUtils.SIDEBAR_BG);
            }
        });

        sidebar.add(btn);
    }

    private JPanel buildLogoutBtn() {
        JPanel btn = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        btn.setBackground(UIUtils.SIDEBAR_BG);
        btn.setMaximumSize(new Dimension(230, 42));
        btn.setPreferredSize(new Dimension(230, 42));
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        JLabel lbl = new JLabel("⬅  Cerrar Sesión");
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lbl.setForeground(new Color(239, 154, 154));
        lbl.setBorder(BorderFactory.createEmptyBorder(11, 22, 11, 22));
        btn.add(lbl);

        btn.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                if (UIUtils.confirm(MainFrame.this, "¿Deseas cerrar sesión?"))
                    Main.showLogin();
            }
            @Override public void mouseEntered(MouseEvent e) { btn.setBackground(new Color(80, 30, 30)); }
            @Override public void mouseExited(MouseEvent e)  { btn.setBackground(UIUtils.SIDEBAR_BG); }
        });
        return btn;
    }

    private JLabel sectionLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Segoe UI", Font.BOLD, 10));
        l.setForeground(new Color(84, 110, 122));
        l.setBorder(BorderFactory.createEmptyBorder(6, 22, 4, 0));
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        return l;
    }

    private Component spacer(int h) {
        JPanel s = new JPanel();
        s.setOpaque(false);
        s.setMaximumSize(new Dimension(230, h));
        s.setPreferredSize(new Dimension(230, h));
        return s;
    }

    // ── Navigation ────────────────────────────────────
    public void navigate(String key, JPanel panel) {
        // Update active button
        if (activeBtn != null) {
            activeBtn.setBackground(UIUtils.SIDEBAR_BG);
            activeBtn.getComponents()[0].setFont(new Font("Segoe UI", Font.PLAIN, 13));
            ((JLabel) activeBtn.getComponent(0)).setForeground(new Color(176, 190, 197));
        }
        JPanel btn = navButtons.get(key);
        if (btn != null) {
            btn.setBackground(new Color(52, 73, 99));
            btn.setBorder(BorderFactory.createMatteBorder(0, 3, 0, 0, UIUtils.ACCENT));
            JLabel lbl = (JLabel) btn.getComponent(0);
            lbl.setForeground(Color.WHITE);
            lbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
            activeBtn = btn;
        }

        // Swap content
        contentArea.removeAll();
        contentArea.add(panel, BorderLayout.CENTER);
        contentArea.revalidate();
        contentArea.repaint();
        currentPanel = panel;
    }
}
