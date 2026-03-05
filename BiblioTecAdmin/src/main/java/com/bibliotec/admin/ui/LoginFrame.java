package com.bibliotec.admin.ui;

import com.bibliotec.admin.Main;
import com.bibliotec.admin.service.ApiService;
import com.bibliotec.admin.util.SessionManager;
import com.bibliotec.admin.util.UIUtils;

import javax.swing.*;
import java.awt.*;

public class LoginFrame extends JFrame {

    private JTextField correoField;
    private JPasswordField passField;
    private JLabel errorLabel;
    private JButton loginBtn;

    public LoginFrame() {
        setTitle("BiblioTec Admin — Iniciar Sesión");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(860, 540);
        setLocationRelativeTo(null);
        setResizable(false);
        buildUI();
    }

    private void buildUI() {
        JPanel root = new JPanel(new BorderLayout());

        // ── Left panel ──────────────────────────────
        JPanel left = new JPanel();
        left.setBackground(UIUtils.PRIMARY);
        left.setPreferredSize(new Dimension(360, 0));
        left.setLayout(new GridBagLayout());

        JPanel brand = new JPanel();
        brand.setOpaque(false);
        brand.setLayout(new BoxLayout(brand, BoxLayout.Y_AXIS));

        JLabel icon = new JLabel("📚");
        icon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 60));
        icon.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel title = new JLabel("BiblioTec");
        title.setFont(new Font("Segoe UI", Font.BOLD, 34));
        title.setForeground(Color.WHITE);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel sub = new JLabel("<html><center>Panel de Administración<br>Biblioteca Digital</center></html>");
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        sub.setForeground(new Color(255, 255, 255, 180));
        sub.setAlignmentX(Component.CENTER_ALIGNMENT);

        JSeparator sep = new JSeparator();
        sep.setForeground(new Color(255, 255, 255, 60));
        sep.setMaximumSize(new Dimension(200, 1));
        sep.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel school = new JLabel("Colegio Padre Arrupe");
        school.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        school.setForeground(new Color(255, 255, 255, 120));
        school.setAlignmentX(Component.CENTER_ALIGNMENT);

        brand.add(icon);
        brand.add(Box.createVerticalStrut(10));
        brand.add(title);
        brand.add(Box.createVerticalStrut(8));
        brand.add(sub);
        brand.add(Box.createVerticalStrut(18));
        brand.add(sep);
        brand.add(Box.createVerticalStrut(14));
        brand.add(school);
        left.add(brand);

        // ── Right panel ─────────────────────────────
        JPanel right = new JPanel();
        right.setBackground(Color.WHITE);
        right.setLayout(new GridBagLayout());

        JPanel form = new JPanel();
        form.setOpaque(false);
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setMaximumSize(new Dimension(340, 400));

        JLabel formTitle = new JLabel("Iniciar Sesión");
        formTitle.setFont(new Font("Segoe UI", Font.BOLD, 26));
        formTitle.setForeground(UIUtils.PRIMARY);
        formTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel formSub = new JLabel("Ingresa tus credenciales de administrador");
        formSub.setFont(UIUtils.FONT_SMALL);
        formSub.setForeground(UIUtils.TEXT_MUTED);
        formSub.setAlignmentX(Component.LEFT_ALIGNMENT);

        correoField = UIUtils.styledField("correo");
        correoField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));

        passField = UIUtils.styledPasswordField();
        passField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));

        errorLabel = new JLabel(" ");
        errorLabel.setFont(UIUtils.FONT_SMALL);
        errorLabel.setForeground(UIUtils.DANGER);
        errorLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        loginBtn = UIUtils.primaryButton("Ingresar");
        loginBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        loginBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        loginBtn.addActionListener(e -> doLogin());

        passField.addActionListener(e -> doLogin());

        form.add(formTitle);
        form.add(Box.createVerticalStrut(4));
        form.add(formSub);
        form.add(Box.createVerticalStrut(28));
        form.add(UIUtils.fieldLabel("Correo electrónico"));
        form.add(Box.createVerticalStrut(5));
        form.add(correoField);
        form.add(Box.createVerticalStrut(16));
        form.add(UIUtils.fieldLabel("Contraseña"));
        form.add(Box.createVerticalStrut(5));
        form.add(passField);
        form.add(Box.createVerticalStrut(6));
        form.add(errorLabel);
        form.add(Box.createVerticalStrut(16));
        form.add(loginBtn);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(0, 50, 0, 50);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        right.add(form, gbc);

        root.add(left, BorderLayout.WEST);
        root.add(right, BorderLayout.CENTER);
        setContentPane(root);
    }

    private void doLogin() {
        String correo = correoField.getText().trim();
        String pass   = new String(passField.getPassword());

        if (correo.isEmpty() || pass.isEmpty()) {
            errorLabel.setText("Por favor ingresa correo y contraseña.");
            return;
        }

        loginBtn.setEnabled(false);
        loginBtn.setText("Ingresando…");
        errorLabel.setText(" ");

        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            String errMsg = null;
            @Override
            protected Void doInBackground() {
                try {
                    var resp = ApiService.get().login(correo, pass);
                    if (!"ADMIN".equals(resp.rol())) {
                        errMsg = "Acceso denegado. Solo administradores pueden ingresar.";
                        return null;
                    }
                    SessionManager.getInstance().setSession(resp.token(), resp.nombre(), resp.correo(), resp.rol());
                } catch (Exception ex) {
                    errMsg = "Credenciales incorrectas. Verifica e intenta de nuevo.";
                }
                return null;
            }
            @Override
            protected void done() {
                if (errMsg != null) {
                    errorLabel.setText(errMsg);
                    loginBtn.setEnabled(true);
                    loginBtn.setText("Ingresar");
                } else {
                    dispose();
                    Main.showMain();
                }
            }
        };
        worker.execute();
    }
}
