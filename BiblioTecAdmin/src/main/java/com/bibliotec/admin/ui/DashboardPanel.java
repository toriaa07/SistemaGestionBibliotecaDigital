package com.bibliotec.admin.ui;

import com.bibliotec.admin.service.ApiService;
import com.bibliotec.admin.util.UIUtils;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class DashboardPanel extends JPanel {

    // Stat panels (mutable references)
    private JPanel statUsuarios, statLibros, statPrestActivos, statPrestVencidos;
    private JTextArea activityArea;

    public DashboardPanel() {
        setLayout(new BorderLayout());
        setBackground(UIUtils.BG_LIGHT);
        buildUI();
        loadData();
    }

    private void buildUI() {
        // Header
        JPanel header = UIUtils.headerPanel("📊  Dashboard", "Resumen general del sistema BiblioTec");

        JPanel inner = new JPanel(new BorderLayout());
        inner.setBackground(UIUtils.BG_LIGHT);
        inner.setBorder(BorderFactory.createEmptyBorder(20, 24, 24, 24));

        // ── Stat cards row ────────────────────────────
        JPanel cards = new JPanel(new GridLayout(1, 4, 16, 0));
        cards.setOpaque(false);

        statUsuarios       = UIUtils.statCard("Usuarios Activos",    "—", UIUtils.ACCENT);
        statLibros         = UIUtils.statCard("Libros en Catálogo",  "—", UIUtils.SUCCESS);
        statPrestActivos   = UIUtils.statCard("Préstamos Activos",   "—", UIUtils.WARNING);
        statPrestVencidos  = UIUtils.statCard("Préstamos Vencidos",  "—", UIUtils.DANGER);

        cards.add(statUsuarios);
        cards.add(statLibros);
        cards.add(statPrestActivos);
        cards.add(statPrestVencidos);

        // ── Quick actions ─────────────────────────────
        JPanel actionsRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        actionsRow.setOpaque(false);
        actionsRow.setBorder(BorderFactory.createEmptyBorder(16, 0, 0, 0));

        JButton btnVencidos = UIUtils.dangerButton("⚠  Marcar Vencidos");
        btnVencidos.addActionListener(e -> marcarVencidos());
        JButton btnRefresh  = UIUtils.ghostButton("↻  Actualizar");
        btnRefresh.addActionListener(e -> loadData());

        actionsRow.add(btnVencidos);
        actionsRow.add(btnRefresh);

        // ── Activity log area ─────────────────────────
        JPanel logCard = UIUtils.cardPanel();
        logCard.setLayout(new BorderLayout());
        logCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(224,224,224),1,true),
                BorderFactory.createEmptyBorder(14,18,14,18)));

        JLabel logTitle = new JLabel("📋  Actividad Reciente del Sistema");
        logTitle.setFont(UIUtils.FONT_HEADER);
        logTitle.setForeground(UIUtils.PRIMARY);
        logTitle.setBorder(BorderFactory.createEmptyBorder(0,0,10,0));

        activityArea = new JTextArea();
        activityArea.setEditable(false);
        activityArea.setFont(new Font("Consolas", Font.PLAIN, 12));
        activityArea.setForeground(new Color(55, 71, 79));
        activityArea.setBackground(new Color(250, 251, 252));
        activityArea.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        activityArea.setText("Cargando información del sistema…");

        JScrollPane scroll = new JScrollPane(activityArea);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(224,224,224)));
        scroll.setPreferredSize(new Dimension(0, 260));

        logCard.add(logTitle, BorderLayout.NORTH);
        logCard.add(scroll, BorderLayout.CENTER);

        JPanel center = new JPanel();
        center.setOpaque(false);
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.add(cards);
        center.add(actionsRow);
        center.add(Box.createVerticalStrut(20));
        center.add(logCard);

        inner.add(center, BorderLayout.NORTH);
        add(header, BorderLayout.NORTH);
        add(inner, BorderLayout.CENTER);
    }

    private void loadData() {
        new SwingWorker<int[], Void>() {
            int[] counts = new int[4];
            StringBuilder log = new StringBuilder();

            @Override
            protected int[] doInBackground() throws Exception {
                var usuarios   = ApiService.get().getUsuarios(null, "USUARIO", "ACTIVO");
                var libros     = ApiService.get().getLibros(null, true);
                var activos    = ApiService.get().getPrestamos("ACTIVO", null);
                var vencidos   = ApiService.get().getPrestamos("VENCIDO", null);

                counts[0] = usuarios.size();
                counts[1] = libros.size();
                counts[2] = activos.size();
                counts[3] = vencidos.size();

                log.append("✔  Usuarios activos en el sistema:        ").append(counts[0]).append("\n");
                log.append("✔  Libros activos en catálogo:            ").append(counts[1]).append("\n");
                log.append("✔  Préstamos activos en curso:            ").append(counts[2]).append("\n");
                log.append("⚠  Préstamos vencidos pendientes:         ").append(counts[3]).append("\n\n");

                // Recent active loans
                log.append("── Últimos préstamos activos ───────────────────────────────\n");
                activos.stream().limit(5).forEach(p ->
                    log.append(String.format("   %-28s → %-30s [%s]\n",
                            p.getNombreUsuario(), p.getTituloLibro(), p.getFechaVencimientoStr())));

                if (vencidos.size() > 0) {
                    log.append("\n── Préstamos vencidos ──────────────────────────────────────\n");
                    vencidos.stream().limit(5).forEach(p ->
                        log.append(String.format("   ⚠ %-26s → %-30s [venció: %s]\n",
                                p.getNombreUsuario(), p.getTituloLibro(), p.getFechaVencimientoStr())));
                }
                return counts;
            }

            @Override
            protected void done() {
                try {
                    get();
                    updateStat(statUsuarios,      String.valueOf(counts[0]));
                    updateStat(statLibros,         String.valueOf(counts[1]));
                    updateStat(statPrestActivos,   String.valueOf(counts[2]));
                    updateStat(statPrestVencidos,  String.valueOf(counts[3]));
                    activityArea.setText(log.toString());
                    activityArea.setCaretPosition(0);
                } catch (Exception e) {
                    activityArea.setText("Error al cargar datos: " + e.getMessage());
                }
            }
        }.execute();
    }

    private void updateStat(JPanel card, String value) {
        // The value label is inside inner VBox which is in CENTER
        for (Component c : card.getComponents()) {
            if (c instanceof JPanel inner) {
                for (Component ic : inner.getComponents()) {
                    if (ic instanceof JLabel lbl && lbl.getFont().getSize() == 30) {
                        lbl.setText(value);
                    }
                }
            }
        }
        card.revalidate(); card.repaint();
    }

    private void marcarVencidos() {
        new SwingWorker<Void,Void>() {
            @Override protected Void doInBackground() throws Exception {
                ApiService.get().marcarVencidos(); return null;
            }
            @Override protected void done() {
                try { get(); UIUtils.showInfo(DashboardPanel.this, "Préstamos vencidos actualizados."); loadData(); }
                catch (Exception e) { UIUtils.showError(DashboardPanel.this, e.getMessage()); }
            }
        }.execute();
    }
}
