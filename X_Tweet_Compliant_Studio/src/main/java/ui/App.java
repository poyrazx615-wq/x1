package ui;

import manage.Controller;
import report.ActionRecord;
import utils.ConfigIO;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.*;
import java.util.List;
import java.util.Properties;

public class App extends JFrame {
    private final JTextArea logArea = new JTextArea();
    private final Controller controller = new Controller(this::appendLog);
    private final JComboBox<String> profileBox = new JComboBox<>(new String[]{"default"});

    // Settings fields
    private final JTextField accessTokenField = new JTextField(); // TW_ACCESS_TOKEN
    private final JTextField hashtagsField = new JTextField("data/hashtags.txt");
    private final JTextField urlsField = new JTextField("data/urls.txt");
    private final JTextField csvPathField = new JTextField("logs/actions.csv");
    private final JTextField jsonPathField = new JTextField("logs/actions.json");

    // Compose fields
    private final JTextArea composeText = new JTextArea(5, 40);
    private final JTextField replyToField = new JTextField();
    private final JTextField quoteIdField = new JTextField();

    // Scheduler
    private final JTextField cronField = new JTextField("0 0/30 * * * ?");

    // Report table
    private final DefaultTableModel tableModel = new DefaultTableModel(new Object[]{"ts","action","ref","success","message"}, 0);

    public App() {
        super("X Tweet — Compliant Studio");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 720);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        add(buildTabs(), BorderLayout.CENTER);
        add(buildBottomBar(), BorderLayout.SOUTH);

        loadPropsToForm("configs/default.properties");
    }

    private JTabbedPane buildTabs() {
        var tabs = new JTabbedPane();
        tabs.addTab("Profil & Ayarlar", makeSettingsPanel());
        tabs.addTab("Compose / Reply / Quote", makeComposePanel());
        tabs.addTab("Planlayıcı", makeSchedulerPanel());
        tabs.addTab("Manual Assist", makeManualPanel());
        tabs.addTab("Log & Rapor", makeLogPanel());
        return tabs;
    }

    private JPanel makeSettingsPanel() {
        JPanel p = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(6,6,6,6);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1; int r=0;

        addRow(p,c,r++,"Profil", profileBox);
        JButton btnLoad = new JButton("Yükle");
        btnLoad.addActionListener(e-> loadSelectedProfile());
        JButton btnSave = new JButton("Kaydet");
        btnSave.addActionListener(e-> saveSelectedProfile());
        JPanel profBtns = new JPanel(new FlowLayout(FlowLayout.LEFT));
        profBtns.add(btnLoad); profBtns.add(btnSave);
        addRow(p,c,r++,"Profil İşlemleri", profBtns);

        addRow(p,c,r++,"Twitter Access Token", accessTokenField);
        addFileRow(p,c,r++,"Hashtag Dosyası", hashtagsField);
        addFileRow(p,c,r++,"URL Listesi (Manual)", urlsField);
        addFileRow(p,c,r++,"CSV Yol", csvPathField);
        addFileRow(p,c,r++,"JSON Yol", jsonPathField);

        return p;
    }

    private JPanel makeComposePanel() {
        JPanel p = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(6,6,6,6);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1; int r=0;

        c.gridx=0; c.gridy=r; p.add(new JLabel("Metin"), c);
        c.gridx=1; JScrollPane sp = new JScrollPane(composeText);
        p.add(sp, c); r++;

        addRow(p,c,r++,"Reply Tweet ID", replyToField);
        addRow(p,c,r++,"Quote Tweet ID", quoteIdField);

        JButton sendNow = new JButton("Hemen Gönder");
        sendNow.addActionListener(this::composeNow);
        c.gridx=1; c.gridy=r; p.add(sendNow, c);

        return p;
    }

    private JPanel makeSchedulerPanel() {
        JPanel p = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(6,6,6,6);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1; int r=0;

        addRow(p,c,r++,"Cron İfadesi", cronField);
        JButton btnPlan = new JButton("Planla (Compose)");
        btnPlan.addActionListener(this::scheduleCompose);
        c.gridx=1; c.gridy=r; p.add(btnPlan, c); r++;

        JButton btnStop = new JButton("Tüm Planları Durdur");
        btnStop.addActionListener(e -> controller.stopAllSchedules());
        c.gridx=1; c.gridy=r; p.add(btnStop, c);
        return p;
    }

    private JPanel makeManualPanel() {
        JPanel p = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(6,6,6,6);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1; int r=0;

        addFileRow(p,c,r++,"Tweet/Profil URL Listesi", urlsField);
        JTextField maxCount = new JTextField("10");
        addRow(p,c,r++,"Açılacak Adet", maxCount);

        JButton openBtn = new JButton("URL’leri Aç (Manuel İşlem)");
        openBtn.addActionListener(e -> {
            try {
                controller.openManualUrls(urlsField.getText().trim(), Integer.parseInt(maxCount.getText().trim()));
            } catch (NumberFormatException ex) {
                appendLog("❌ Geçersiz sayı.");
            }
        });
        c.gridx=1; c.gridy=r; p.add(openBtn, c);
        return p;
    }

    private JPanel makeLogPanel() {
        JPanel p = new JPanel(new BorderLayout());
        logArea.setEditable(false);
        logArea.setLineWrap(true);
        p.add(new JScrollPane(logArea), BorderLayout.CENTER);

        JTable table = new JTable(tableModel);
        p.add(new JScrollPane(table), BorderLayout.EAST);

        JPanel exportPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton exportBtn = new JButton("CSV/JSON Kaydet");
        exportBtn.addActionListener(e -> exportReports());
        exportPanel.add(exportBtn);
        p.add(exportPanel, BorderLayout.SOUTH);
        return p;
    }

    private JPanel buildBottomBar() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton quit = new JButton("Çıkış");
        quit.addActionListener(e -> System.exit(0));
        p.add(quit);
        return p;
    }

    private void addRow(JPanel p, GridBagConstraints c, int r, String label, JComponent comp) {
        c.gridx=0; c.gridy=r; p.add(new JLabel(label), c);
        c.gridx=1; p.add(comp, c);
    }
    private void addFileRow(JPanel p, GridBagConstraints c, int r, String label, JTextField field) {
        c.gridx=0; c.gridy=r; p.add(new JLabel(label), c);
        JPanel line = new JPanel(new BorderLayout(4,0));
        line.add(field, BorderLayout.CENTER);
        JButton b = new JButton("Seç");
        b.addActionListener(e -> chooseFile(field));
        line.add(b, BorderLayout.EAST);
        c.gridx=1; p.add(line, c);
    }

    private void chooseFile(JTextField target) {
        JFileChooser fc = new JFileChooser(new File("."));
        int r = fc.showOpenDialog(this);
        if (r == JFileChooser.APPROVE_OPTION) {
            target.setText(fc.getSelectedFile().getPath());
        }
    }

    private void composeNow(ActionEvent e) {
        Properties props = collectProps();
        String text = composeText.getText().trim();
        String replyTo = replyToField.getText().trim();
        String quoteId = quoteIdField.getText().trim();
        if (text.isBlank()) { appendLog("❌ Metin boş olamaz."); return; }
        controller.composeNow(props, text, replyTo, quoteId);
    }

    private void scheduleCompose(ActionEvent e) {
        Properties props = collectProps();
        String text = composeText.getText().trim();
        String replyTo = replyToField.getText().trim();
        String quoteId = quoteIdField.getText().trim();
        String cron = cronField.getText().trim();
        if (text.isBlank()) { appendLog("❌ Metin boş olamaz."); return; }
        controller.scheduleCompose(props, text, replyTo, quoteId, cron);
    }

    private void exportReports() {
        var rep = controller.getReporter();
        rep.flushCSV(csvPathField.getText().trim());
        rep.flushJSON(jsonPathField.getText().trim());
        appendLog("💾 Raporlar kaydedildi.");
    }

    private void appendLog(String msg) {
        SwingUtilities.invokeLater(() -> {
            logArea.append(msg + "\n");
            logArea.setCaretPosition(logArea.getDocument().getLength());
        });
    }

    private void loadPropsToForm(String path) {
        Properties p = ConfigIO.load(path);
        accessTokenField.setText(p.getProperty("TW_ACCESS_TOKEN",""));
        hashtagsField.setText(p.getProperty("HASHTAGS_FILE","data/hashtags.txt"));
        urlsField.setText(p.getProperty("URLS_FILE","data/urls.txt"));
        csvPathField.setText(p.getProperty("CSV_PATH","logs/actions.csv"));
        jsonPathField.setText(p.getProperty("JSON_PATH","logs/actions.json"));
    }

    private Properties collectProps() {
        Properties p = new Properties();
        p.setProperty("TW_ACCESS_TOKEN", accessTokenField.getText().trim());
        p.setProperty("HASHTAGS_FILE", hashtagsField.getText().trim());
        p.setProperty("URLS_FILE", urlsField.getText().trim());
        p.setProperty("CSV_PATH", csvPathField.getText().trim());
        p.setProperty("JSON_PATH", jsonPathField.getText().trim());
        return p;
    }

    private void loadSelectedProfile() {
        String prof = (String) profileBox.getSelectedItem();
        loadPropsToForm("configs/" + prof + ".properties");
        appendLog("📂 Profil yüklendi: " + prof);
    }

    private void saveSelectedProfile() {
        String prof = (String) profileBox.getSelectedItem();
        Properties p = collectProps();
        try {
            ConfigIO.save(p, "configs/" + prof + ".properties");
            appendLog("💾 Profil kaydedildi: " + prof);
        } catch (Exception ex) {
            appendLog("❌ Profil kaydedilemedi: " + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new App().setVisible(true));
    }
}