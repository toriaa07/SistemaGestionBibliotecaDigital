package com.bibliotec.admin.ui;

import com.bibliotec.admin.model.UsuarioDto;
import com.bibliotec.admin.service.ApiService;
import com.bibliotec.admin.util.UIUtils;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class NotificacionesPanel extends JPanel {

    private JComboBox<UsuarioItem> usuarioBox;
    private JComboBox<String> tipoBox;
    private JTextArea mensajeArea;
    private JLabel statusLabel;

    public NotificacionesPanel() {
        setLayout(new BorderLayout());
        setBackground(UIUtils.BG_LIGHT);
        buildUI();
        loadUsuarios();
    }

    private void buildUI() {
        JPanel header = UIUtils.headerPanel("🔔  Enviar Notificaciones",
                "Envía notificaciones a los usuarios del sistema");

        JPanel center = new JPanel(new GridBagLayout());
        center.setBackground(UIUtils.BG_LIGHT);
        center.setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));

        // Card form
        JPanel card = new JPanel();
        card.setBackground(Color.WHITE);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(224,224,224),1,true),
                BorderFactory.createEmptyBorder(28,30,28,30)));

        JLabel cardTitle = new JLabel("📨  Nueva Notificación");
        cardTitle.setFont(UIUtils.FONT_HEADER);
        cardTitle.setForeground(UIUtils.PRIMARY);
        cardTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel cardSub = new JLabel("Selecciona el usuario, el tipo y escribe el mensaje.");
        cardSub.setFont(UIUtils.FONT_SMALL);
        cardSub.setForeground(UIUtils.TEXT_MUTED);
        cardSub.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Usuario
        usuarioBox = new JComboBox<>();
        usuarioBox.setFont(UIUtils.FONT_BODY);
        usuarioBox.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));

        // Tipo
        tipoBox = new JComboBox<>(new String[]{"VENCIMIENTO","RECORDATORIO","SISTEMA"});
        tipoBox.setFont(UIUtils.FONT_BODY);
        tipoBox.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));

        // Mensaje
        mensajeArea = new JTextArea(5, 40);
        mensajeArea.setFont(UIUtils.FONT_BODY);
        mensajeArea.setLineWrap(true);
        mensajeArea.setWrapStyleWord(true);
        mensajeArea.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(207,216,220),1),
                BorderFactory.createEmptyBorder(8,10,8,10)));
        JScrollPane msScroll = new JScrollPane(mensajeArea);
        msScroll.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));
        msScroll.setAlignmentX(Component.LEFT_ALIGNMENT);

        statusLabel = new JLabel(" ");
        statusLabel.setFont(UIUtils.FONT_SMALL);
        statusLabel.setForeground(UIUtils.SUCCESS);
        statusLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Buttons
        JPanel btns = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        btns.setOpaque(false);
        btns.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton send = UIUtils.primaryButton("📨  Enviar Notificación");
        send.setPreferredSize(new Dimension(200, 40));
        send.addActionListener(e -> enviar());

        JButton clear = UIUtils.ghostButton("Limpiar");
        clear.addActionListener(e -> {
            mensajeArea.setText("");
            statusLabel.setText(" ");
        });

        // Plantillas rápidas
        JPanel templates = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        templates.setOpaque(false);
        templates.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel tplLabel = new JLabel("Plantillas rápidas:");
        tplLabel.setFont(UIUtils.FONT_LABEL);
        tplLabel.setForeground(UIUtils.TEXT_MUTED);
        templates.add(tplLabel);

        String[][] plantillas = {
            {"📅 Vencimiento próximo", "Tu préstamo está próximo a vencer. Por favor devuelve el libro antes de la fecha límite para evitar sanciones."},
            {"⚠ Préstamo vencido",     "Tu préstamo ha VENCIDO. Por favor acércate a la biblioteca para regularizar tu situación lo antes posible."},
            {"✅ Libro disponible",     "Un libro de tu interés ya está disponible en el catálogo. ¡Ingresa al sistema para solicitarlo!"},
            {"ℹ Mantenimiento",        "El sistema estará en mantenimiento programado. Disculpa los inconvenientes."},
        };
        for (String[] tpl : plantillas) {
            JButton tb = UIUtils.ghostButton(tpl[0]);
            tb.setPreferredSize(new Dimension(190, 30));
            tb.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            tb.addActionListener(e -> mensajeArea.setText(tpl[1]));
            templates.add(tb);
        }

        btns.add(send); btns.add(clear);

        card.add(cardTitle);
        card.add(Box.createVerticalStrut(4));
        card.add(cardSub);
        card.add(Box.createVerticalStrut(22));
        card.add(UIUtils.fieldLabel("Destinatario")); card.add(Box.createVerticalStrut(5));
        card.add(usuarioBox);
        card.add(Box.createVerticalStrut(14));
        card.add(UIUtils.fieldLabel("Tipo de notificación")); card.add(Box.createVerticalStrut(5));
        card.add(tipoBox);
        card.add(Box.createVerticalStrut(14));
        card.add(UIUtils.fieldLabel("Mensaje")); card.add(Box.createVerticalStrut(5));
        card.add(msScroll);
        card.add(Box.createVerticalStrut(12));
        card.add(UIUtils.fieldLabel("Plantillas rápidas:")); card.add(Box.createVerticalStrut(5));
        card.add(templates);
        card.add(Box.createVerticalStrut(18));
        card.add(btns);
        card.add(Box.createVerticalStrut(8));
        card.add(statusLabel);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1; gbc.weighty = 1;
        center.add(card, gbc);

        add(header, BorderLayout.NORTH);
        add(center, BorderLayout.CENTER);
    }

    private void loadUsuarios() {
        new SwingWorker<List<UsuarioDto>,Void>() {
            @Override protected List<UsuarioDto> doInBackground() throws Exception {
                return ApiService.get().getUsuarios(null, null, "ACTIVO");
            }
            @Override protected void done() {
                try {
                    usuarioBox.removeAllItems();
                    for (UsuarioDto u : get())
                        usuarioBox.addItem(new UsuarioItem(u));
                } catch (Exception ignored) {}
            }
        }.execute();
    }

    private void enviar() {
        UsuarioItem sel = (UsuarioItem) usuarioBox.getSelectedItem();
        String tipo = (String) tipoBox.getSelectedItem();
        String msg  = mensajeArea.getText().trim();

        if (sel == null) { UIUtils.showError(this, "Selecciona un destinatario."); return; }
        if (msg.isEmpty()) { UIUtils.showError(this, "El mensaje no puede estar vacío."); return; }

        statusLabel.setForeground(UIUtils.TEXT_MUTED);
        statusLabel.setText("Enviando…");

        new SwingWorker<Void,Void>() {
            @Override protected Void doInBackground() throws Exception {
                ApiService.get().createNotificacion(sel.id, tipo, msg); return null;
            }
            @Override protected void done() {
                try {
                    get();
                    statusLabel.setForeground(UIUtils.SUCCESS);
                    statusLabel.setText("✔  Notificación enviada a " + sel.nombre + " exitosamente.");
                    mensajeArea.setText("");
                } catch (Exception e) {
                    statusLabel.setForeground(UIUtils.DANGER);
                    statusLabel.setText("✘  Error: " + e.getMessage());
                }
            }
        }.execute();
    }

    record UsuarioItem(int id, String nombre, String correo) {
        UsuarioItem(UsuarioDto u) { this(u.getIdUsuario(), u.getNombre(), u.getCorreo()); }
        @Override public String toString() { return nombre + "  (" + correo + ")"; }
    }
}
