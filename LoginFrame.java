package com.movie.ui;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import com.movie.bus.CustomerBUS;
import com.movie.bus.AdminBUS; // Thêm import
import com.movie.util.PasswordEncrypter;
import com.movie.network.SocketClient;

public class LoginFrame extends JFrame {
    private final CustomerBUS customerBUS = new CustomerBUS();
    private final AdminBUS adminBUS = new AdminBUS(); // Thêm AdminBUS
    private boolean isAdminLogin;

    public LoginFrame() {
        initUI();
    }

    private void initUI() {
        setTitle("Đăng nhập hệ thống");
        setSize(400, 500);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(20, 20, 40));

        JLabel backgroundLabel = new JLabel();
        ImageIcon backgroundIcon = new ImageIcon(getClass().getResource("/images/dangnhap.jpg"));
        if (backgroundIcon.getImage() != null) {
            backgroundLabel.setIcon(new ImageIcon(backgroundIcon.getImage().getScaledInstance(200, 500, Image.SCALE_SMOOTH)));
        } else {
            backgroundLabel.setText("Hình nền không tải được");
        }
        backgroundLabel.setPreferredSize(new Dimension(200, 500));
        mainPanel.add(backgroundLabel, BorderLayout.WEST);

        JPanel loginPanel = new JPanel(new GridBagLayout());
        loginPanel.setBackground(new Color(30, 30, 50));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel titleLabel = new JLabel("ĐĂNG NHẬP HỆ THỐNG");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        gbc.gridx = 0;
        gbc.gridy = 0;
        loginPanel.add(titleLabel, gbc);

        JRadioButton userRadio = new JRadioButton("Người dùng");
        JRadioButton adminRadio = new JRadioButton("Quản trị viên");
        userRadio.setForeground(Color.LIGHT_GRAY);
        adminRadio.setForeground(Color.LIGHT_GRAY);
        userRadio.setBackground(new Color(30, 30, 50));
        adminRadio.setBackground(new Color(30, 30, 50));
        ButtonGroup group = new ButtonGroup();
        group.add(userRadio);
        group.add(adminRadio);
        userRadio.setSelected(true);
        JPanel radioPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        radioPanel.setBackground(new Color(30, 30, 50));
        radioPanel.add(userRadio);
        radioPanel.add(adminRadio);
        gbc.gridy = 1;
        loginPanel.add(radioPanel, gbc);

        JTextField usernameField = new JTextField(20);
        usernameField.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.GRAY), "Tên đăng nhập"));
        usernameField.setBackground(Color.WHITE);
        gbc.gridy = 2;
        loginPanel.add(usernameField, gbc);

        JPasswordField passwordField = new JPasswordField(20);
        passwordField.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.GRAY), "Mật khẩu"));
        passwordField.setBackground(Color.WHITE);
        gbc.gridy = 3;
        loginPanel.add(passwordField, gbc);

        JButton loginButton = new JButton("Đăng nhập");
        loginButton.setBackground(new Color(75, 0, 130));
        loginButton.setForeground(Color.WHITE);
        loginButton.setFont(new Font("Arial", Font.BOLD, 16));
        loginButton.addActionListener(e -> {
            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword()).trim();
            isAdminLogin = adminRadio.isSelected();
            if (authenticate(username, password)) {
                SocketClient client = new SocketClient("localhost", 5000);
                client.start();
                int attempts = 20;
                while (attempts > 0 && !client.isConnected()) {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException ie) {
                        ie.printStackTrace();
                    }
                    attempts--;
                }
                if (client.isConnected()) {
                    client.sendMessage("Login: " + username);
                } else {
                    JOptionPane.showMessageDialog(null, "Không thể kết nối đến server!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                if (isAdminLogin) {
                    new AdminFrame().setVisible(true);
                } else {
                    int customerId = customerBUS.getCustomerIdByUsername(username);
                    if (customerId != -1) {
                        new UserFrame(customerId).setVisible(true);
                    } else {
                        JOptionPane.showMessageDialog(null, "Không thể lấy CustomerID!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                }
                dispose();
            } else {
                JOptionPane.showMessageDialog(null, "Đăng nhập thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });
        gbc.gridy = 4;
        loginPanel.add(loginButton, gbc);

        JButton registerButton = new JButton("Đăng ký người dùng");
        registerButton.setBackground(new Color(0, 120, 215));
        registerButton.setForeground(Color.WHITE);
        registerButton.setFont(new Font("Arial", Font.BOLD, 16));
        registerButton.addActionListener(e -> {
            new RegisterFrame(this).setVisible(true);
        });
        gbc.gridy = 5;
        loginPanel.add(registerButton, gbc);

        mainPanel.add(loginPanel, BorderLayout.CENTER);
        add(mainPanel);
        setVisible(true);
    }

    private boolean authenticate(String username, String password) {
        if (isAdminLogin) {
            return adminBUS.validateAdmin(username, password);
        } else {
            return customerBUS.validateUserPlain(username, password); // Lỗi ở đây
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
    }
}