package com.bibliotec.admin.util;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

public class UIUtils {

    // ── Brand colors ──────────────────────────────────
    public static final Color PRIMARY       = new Color(26, 35, 126);   // dark navy
    public static final Color PRIMARY_LIGHT = new Color(57, 73, 171);
    public static final Color ACCENT        = new Color(61, 90, 254);
    public static final Color SUCCESS       = new Color(39, 174, 96);
    public static final Color DANGER        = new Color(231, 76, 60);
    public static final Color WARNING       = new Color(230, 126, 34);
    public static final Color BG_LIGHT      = new Color(240, 244, 248);
    public static final Color CARD_BG       = Color.WHITE;
    public static final Color TEXT_MUTED    = new Color(120, 144, 156);
    public static final Color SIDEBAR_BG    = new Color(30, 42, 58);
    public static final Color SIDEBAR_ITEM  = new Color(21, 31, 45);

    // ── Fonts ─────────────────────────────────────────
    public static final Font FONT_TITLE  = new Font("Segoe UI", Font.BOLD, 22);
    public static final Font FONT_HEADER = new Font("Segoe UI", Font.BOLD, 15);
    public static final Font FONT_BODY   = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font FONT_SMALL  = new Font("Segoe UI", Font.PLAIN, 11);
    public static final Font FONT_LABEL  = new Font("Segoe UI", Font.BOLD, 12);

    // ── Factory methods ───────────────────────────────

    public static JButton primaryButton(String text) {
        JButton b = new JButton(text);
        b.setBackground(ACCENT);
        b.setForeground(Color.WHITE);
        b.setFont(new Font("Segoe UI", Font.BOLD, 13));
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setPreferredSize(new Dimension(130, 36));
        return b;
    }

    public static JButton dangerButton(String text) {
        JButton b = primaryButton(text);
        b.setBackground(DANGER);
        return b;
    }

    public static JButton successButton(String text) {
        JButton b = primaryButton(text);
        b.setBackground(SUCCESS);
        return b;
    }

    public static JButton warningButton(String text) {
        JButton b = primaryButton(text);
        b.setBackground(WARNING);
        return b;
    }

    public static JButton ghostButton(String text) {
        JButton b = new JButton(text);
        b.setBackground(new Color(236, 240, 241));
        b.setForeground(new Color(44, 62, 80));
        b.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setPreferredSize(new Dimension(110, 34));
        return b;
    }

    public static JTextField styledField(String placeholder) {
        JTextField f = new JTextField();
        f.setFont(FONT_BODY);
        f.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(207, 216, 220), 1, true),
                BorderFactory.createEmptyBorder(6, 10, 6, 10)));
        f.setPreferredSize(new Dimension(200, 36));
        return f;
    }

    public static JPasswordField styledPasswordField() {
        JPasswordField f = new JPasswordField();
        f.setFont(FONT_BODY);
        f.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(207, 216, 220), 1, true),
                BorderFactory.createEmptyBorder(6, 10, 6, 10)));
        f.setPreferredSize(new Dimension(200, 36));
        return f;
    }

    public static JLabel fieldLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(FONT_LABEL);
        l.setForeground(new Color(96, 125, 139));
        return l;
    }

    public static JLabel titleLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(FONT_TITLE);
        l.setForeground(PRIMARY);
        return l;
    }

    public static JLabel subtitleLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(FONT_BODY);
        l.setForeground(TEXT_MUTED);
        return l;
    }

    public static JPanel cardPanel() {
        JPanel p = new JPanel();
        p.setBackground(CARD_BG);
        p.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(224, 224, 224), 1, true),
                BorderFactory.createEmptyBorder(16, 20, 16, 20)));
        return p;
    }

    public static JPanel headerPanel(String title, String subtitle) {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(Color.WHITE);
        p.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(224, 224, 224)),
                BorderFactory.createEmptyBorder(18, 24, 16, 24)));

        JPanel textBox = new JPanel();
        textBox.setOpaque(false);
        textBox.setLayout(new BoxLayout(textBox, BoxLayout.Y_AXIS));
        JLabel tl = titleLabel(title);
        JLabel sl = subtitleLabel(subtitle);
        textBox.add(tl);
        textBox.add(Box.createVerticalStrut(3));
        textBox.add(sl);

        p.add(textBox, BorderLayout.WEST);
        return p;
    }

    /** Stat card for dashboard */
    public static JPanel statCard(String label, String value, Color accent) {
        JPanel card = new JPanel(new BorderLayout(0, 6));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(224, 224, 224), 1, true),
                BorderFactory.createEmptyBorder(18, 20, 18, 20)));

        // Colored top bar
        JPanel bar = new JPanel();
        bar.setBackground(accent);
        bar.setPreferredSize(new Dimension(0, 5));

        JLabel valLabel = new JLabel(value);
        valLabel.setFont(new Font("Segoe UI", Font.BOLD, 30));
        valLabel.setForeground(accent);

        JLabel lblLabel = new JLabel(label.toUpperCase());
        lblLabel.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblLabel.setForeground(TEXT_MUTED);

        JPanel inner = new JPanel();
        inner.setOpaque(false);
        inner.setLayout(new BoxLayout(inner, BoxLayout.Y_AXIS));
        inner.add(lblLabel);
        inner.add(Box.createVerticalStrut(6));
        inner.add(valLabel);

        card.add(bar, BorderLayout.NORTH);
        card.add(inner, BorderLayout.CENTER);
        return card;
    }

    /** Style a JTable for admin use */
    public static void styleTable(JTable table) {
        table.setRowHeight(34);
        table.setFont(FONT_BODY);
        table.setSelectionBackground(new Color(232, 234, 246));
        table.setSelectionForeground(PRIMARY);
        table.setGridColor(new Color(245, 245, 245));
        table.setShowVerticalLines(false);
        table.getTableHeader().setFont(FONT_LABEL);
        table.getTableHeader().setBackground(new Color(245, 247, 250));
        table.getTableHeader().setForeground(new Color(96, 125, 139));
        table.getTableHeader().setBorder(BorderFactory.createMatteBorder(0,0,2,0, new Color(224,224,224)));
        table.setFillsViewportHeight(true);
        table.setIntercellSpacing(new Dimension(0, 0));
    }

    /** Center-align renderer */
    public static DefaultTableCellRenderer centerRenderer() {
        DefaultTableCellRenderer r = new DefaultTableCellRenderer();
        r.setHorizontalAlignment(SwingConstants.CENTER);
        return r;
    }

    public static void showError(Component parent, String msg) {
        JOptionPane.showMessageDialog(parent, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public static void showInfo(Component parent, String msg) {
        JOptionPane.showMessageDialog(parent, msg, "Información", JOptionPane.INFORMATION_MESSAGE);
    }

    public static boolean confirm(Component parent, String msg) {
        return JOptionPane.showConfirmDialog(parent, msg, "Confirmar",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION;
    }
}
