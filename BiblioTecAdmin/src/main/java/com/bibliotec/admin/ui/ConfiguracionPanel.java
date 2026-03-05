package com.bibliotec.admin.ui;

import com.bibliotec.admin.model.ConfiguracionDto;
import com.bibliotec.admin.service.ApiService;
import com.bibliotec.admin.util.UIUtils;

import javax.swing.*;
import java.awt.*;

public class ConfiguracionPanel extends JPanel {

    private JSpinner diasSpinner;
    private JSpinner maxSpinner;
    private JCheckBox notifCheck;
    private JLabel statusLabel;

    public ConfiguracionPanel() {
        setLayout(new BorderLayout());
        setBackground(UIUtils.BG_LIGHT);
        buildUI();
        loadConfig();
    }

    private void buildUI() {
        JPanel header = UIUtils.headerPanel("⚙  Configuración del Sistema",
                "Parámetros globales de préstamos y notificaciones");

        JPanel center = new JPanel(new GridBagLayout());
        center.setBackground(UIUtils.BG_LIGHT);
        center.setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));

        JPanel card = new JPanel();
        card.setBackground(Color.WHITE);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(224,224,224),1,true),
                BorderFactory.createEmptyBorder(28,30,28,30)));
        card.setMaximumSize(new Dimension(560, Integer.MAX_VALUE));

        JLabel ct = new JLabel("⚙  Parámetros del Sistema");
        ct.setFont(UIUtils.FONT_HEADER); ct.setForeground(UIUtils.PRIMARY);
        ct.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel cs = new JLabel("Estos valores aplican globalmente a todos los préstamos.");
        cs.setFont(UIUtils.FONT_SMALL); cs.setForeground(UIUtils.TEXT_MUTED);
        cs.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Dias préstamo
        diasSpinner = new JSpinner(new SpinnerNumberModel(10, 1, 90, 1));
        diasSpinner.setFont(UIUtils.FONT_BODY);
        diasSpinner.setMaximumSize(new Dimension(120, 36));
        diasSpinner.setPreferredSize(new Dimension(120, 36));

        // Max prestamos
        maxSpinner = new JSpinner(new SpinnerNumberModel(3, 1, 20, 1));
        maxSpinner.setFont(UIUtils.FONT_BODY);
        maxSpinner.setMaximumSize(new Dimension(120, 36));
        maxSpinner.setPreferredSize(new Dimension(120, 36));

        // Notificaciones
        notifCheck = new JCheckBox("Activar envío de notificaciones automáticas");
        notifCheck.setFont(UIUtils.FONT_BODY);
        notifCheck.setOpaque(false);
        notifCheck.setSelected(true);
        notifCheck.setAlignmentX(Component.LEFT_ALIGNMENT);

        statusLabel = new JLabel(" ");
        statusLabel.setFont(UIUtils.FONT_SMALL);
        statusLabel.setForeground(UIUtils.SUCCESS);
        statusLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton save = UIUtils.primaryButton("💾  Guardar Cambios");
        save.setPreferredSize(new Dimension(180, 40));
        save.setAlignmentX(Component.LEFT_ALIGNMENT);
        save.addActionListener(e -> guardar());

        // Separator
        JSeparator sep = new JSeparator();
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        sep.setForeground(new Color(224,224,224));

        // Info panel
        JPanel infoBox = new JPanel();
        infoBox.setBackground(new Color(232, 244, 253));
        infoBox.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(100, 181, 246),1,true),
                BorderFactory.createEmptyBorder(12,14,12,14)));
        infoBox.setLayout(new BoxLayout(infoBox, BoxLayout.Y_AXIS));
        infoBox.setAlignmentX(Component.LEFT_ALIGNMENT);
        infoBox.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));

        JLabel iTitle = new JLabel("ℹ  ¿Cómo funcionan estos parámetros?");
        iTitle.setFont(UIUtils.FONT_LABEL); iTitle.setForeground(new Color(21, 101, 192));
        JTextArea iText = new JTextArea(
            "• Días de préstamo: tiempo máximo que un usuario puede mantener un libro.\n" +
            "• Máx. préstamos activos: límite de préstamos simultáneos por usuario.\n" +
            "• Notificaciones: si está activo, el sistema enviará avisos automáticos de vencimiento.");
        iText.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        iText.setEditable(false); iText.setOpaque(false);
        iText.setForeground(new Color(21, 101, 192));
        iText.setWrapStyleWord(true); iText.setLineWrap(true);

        infoBox.add(iTitle); infoBox.add(Box.createVerticalStrut(6)); infoBox.add(iText);

        card.add(ct); card.add(Box.createVerticalStrut(4)); card.add(cs);
        card.add(Box.createVerticalStrut(24));

        card.add(rowField("Días de préstamo", diasSpinner,
                "Número de días que un usuario puede conservar un libro prestado."));
        card.add(Box.createVerticalStrut(16));
        card.add(rowField("Máx. préstamos activos", maxSpinner,
                "Número máximo de préstamos activos simultáneos por usuario."));
        card.add(Box.createVerticalStrut(20));
        card.add(sep); card.add(Box.createVerticalStrut(16));
        card.add(notifCheck);
        card.add(Box.createVerticalStrut(24));
        card.add(infoBox);
        card.add(Box.createVerticalStrut(24));
        card.add(save);
        card.add(Box.createVerticalStrut(10));
        card.add(statusLabel);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.NORTH;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1; gbc.weighty = 1;
        center.add(card, gbc);

        add(header, BorderLayout.NORTH);
        add(center, BorderLayout.CENTER);
    }

    private JPanel rowField(String label, JComponent field, String hint) {
        JPanel p = new JPanel();
        p.setOpaque(false);
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel l = UIUtils.fieldLabel(label); l.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel h = new JLabel(hint); h.setFont(UIUtils.FONT_SMALL); h.setForeground(UIUtils.TEXT_MUTED);
        h.setAlignmentX(Component.LEFT_ALIGNMENT);
        p.add(l); p.add(Box.createVerticalStrut(4));
        p.add(field);
        p.add(Box.createVerticalStrut(3)); p.add(h);
        return p;
    }

    private void loadConfig() {
        new SwingWorker<ConfiguracionDto,Void>() {
            @Override protected ConfiguracionDto doInBackground() throws Exception {
                return ApiService.get().getConfiguracion();
            }
            @Override protected void done() {
                try {
                    ConfiguracionDto c = get();
                    diasSpinner.setValue(c.getDiasPrestamo());
                    maxSpinner.setValue(c.getMaxPrestamosActivos());
                    notifCheck.setSelected(c.isNotificacionesActivas());
                } catch (Exception e) {
                    statusLabel.setForeground(UIUtils.DANGER);
                    statusLabel.setText("Error al cargar: " + e.getMessage());
                }
            }
        }.execute();
    }

    private void guardar() {
        int dias = (int) diasSpinner.getValue();
        int max  = (int) maxSpinner.getValue();
        boolean notifs = notifCheck.isSelected();

        statusLabel.setForeground(UIUtils.TEXT_MUTED);
        statusLabel.setText("Guardando…");

        new SwingWorker<Void,Void>() {
            @Override protected Void doInBackground() throws Exception {
                ApiService.get().updateConfiguracion(dias, max, notifs); return null;
            }
            @Override protected void done() {
                try {
                    get();
                    statusLabel.setForeground(UIUtils.SUCCESS);
                    statusLabel.setText("✔  Configuración guardada exitosamente.");
                } catch (Exception e) {
                    statusLabel.setForeground(UIUtils.DANGER);
                    statusLabel.setText("✘  Error: " + e.getMessage());
                }
            }
        }.execute();
    }
}
