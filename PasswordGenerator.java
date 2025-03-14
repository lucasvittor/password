import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HexFormat;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.swing.*;

public class PasswordGenerator extends JFrame {
    private JSpinner lengthSpinner;
    private JSpinner numPasswordsSpinner;
    private JCheckBox specialCharsCheckbox;
    private JCheckBox numbersCheckbox;
    private JCheckBox excludeSimilarCheckbox;
    private JPasswordField passwordField; // Usar JPasswordField para senhas
    private JTextArea historyArea;
    private JProgressBar strengthBar;
    private JButton toggleVisibilityButton;
    private JButton generateButton;
    private JButton saveButton; // Botão para salvar senhas
    private ArrayList<String> passwordHistory;
    private SecretKey secretKey;

    public PasswordGenerator() {
        // Setup JFrame
        setTitle("Gerador de Senhas Seguras");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL; // Corrigido para GridBagConstraints.HORIZONTAL

        // Initialize password history
        passwordHistory = new ArrayList<>();

        // Password Length
        gbc.gridx = 0;
        gbc.gridy = 0;
        add(new JLabel("Comprimento da Senha:"), gbc);
        lengthSpinner = new JSpinner(new SpinnerNumberModel(12, 8, 20, 1));
        gbc.gridx = 1;
        add(lengthSpinner, gbc);

        // Number of Passwords
        gbc.gridx = 0;
        gbc.gridy = 1;
        add(new JLabel("Número de Senhas:"), gbc);
        numPasswordsSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 100, 1));
        gbc.gridx = 1;
        add(numPasswordsSpinner, gbc);

        // Include Special Characters
        gbc.gridx = 0;
        gbc.gridy = 2;
        specialCharsCheckbox = new JCheckBox("Incluir Caracteres Especiais");
        add(specialCharsCheckbox, gbc);

        // Include Numbers
        gbc.gridx = 0;
        gbc.gridy = 3;
        numbersCheckbox = new JCheckBox("Incluir Números");
        add(numbersCheckbox, gbc);

        // Exclude Similar Characters
        gbc.gridx = 0;
        gbc.gridy = 4;
        excludeSimilarCheckbox = new JCheckBox("Excluir Caracteres Semelhantes");
        add(excludeSimilarCheckbox, gbc);

        // Toggle Password Visibility
        gbc.gridx = 0;
        gbc.gridy = 5;
        toggleVisibilityButton = new JButton("Mostrar Senha");
        add(toggleVisibilityButton, gbc);

        // Generate Password Button
        gbc.gridx = 0;
        gbc.gridy = 6;
        generateButton = new JButton("Gerar Senhas");
        add(generateButton, gbc);

        // Generated Passwords Area
        gbc.gridx = 0;
        gbc.gridy = 7;
        add(new JLabel("Senhas Geradas:"), gbc);
        passwordField = new JPasswordField(30); // Usar JPasswordField
        passwordField.setEditable(false);
        add(passwordField, gbc);

        // Strength Bar
        gbc.gridx = 0;
        gbc.gridy = 8;
        strengthBar = new JProgressBar(0, 100);
        strengthBar.setStringPainted(true);
        add(strengthBar, gbc);

        // Save Button
        gbc.gridx = 0;
        gbc.gridy = 10;
        saveButton = new JButton("Salvar Senhas");
        add(saveButton, gbc);

        // Button Actions
        generateButton.addActionListener(new GeneratePasswordAction());
        toggleVisibilityButton.addActionListener(new ToggleVisibilityAction());
        saveButton.addActionListener(new SavePasswordAction());

        setSize(700, 600);
        setVisible(true);
        setLocationRelativeTo(null);
        generateSecretKey(); 
    }

    private void generateSecretKey() {
        try {
            KeyGenerator keyGen = KeyGenerator.getInstance("AES");
            keyGen.init(128);
            secretKey = keyGen.generateKey();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class GeneratePasswordAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            int length = (int) lengthSpinner.getValue();
            int numPasswords = (int) numPasswordsSpinner.getValue();
            boolean useSpecialChars = specialCharsCheckbox.isSelected();
            boolean useNumbers = numbersCheckbox.isSelected();
            boolean excludeSimilar = excludeSimilarCheckbox.isSelected();

            if (length < 8) {
                JOptionPane.showMessageDialog(null, "A senha deve ter pelo menos 8 caracteres.", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }

            StringBuilder allPasswords = new StringBuilder();
            for (int i = 0; i < numPasswords; i++) {
                String password = generatePassword(length, useSpecialChars, useNumbers, excludeSimilar);
                allPasswords.append(password).append("\n");
                passwordHistory.add(password);
            }
            passwordField.setText(allPasswords.toString().trim());

            // Calculate strength of the last generated password
            String lastPassword = allPasswords.toString().split("\n")[numPasswords - 1];
            int strength = calculatePasswordStrength(lastPassword);
            strengthBar.setValue(strength);
            strengthBar.setString(getStrengthLabel(strength));

            updateStrengthBarColor(strength);
        }
    }

    private class ToggleVisibilityAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (toggleVisibilityButton.getText().equals("Mostrar Senha")) {
                toggleVisibilityButton.setText("Ocultar Senha");
                passwordField.setEchoChar((char) 0); 
            } else {
                toggleVisibilityButton.setText("Mostrar Senha");
                passwordField.setEchoChar('*'); 
            }
        }
    }

    private class SavePasswordAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            savePasswords();
        }
    }

    private String generatePassword(int length, boolean useSpecialChars, boolean useNumbers, boolean excludeSimilar) {
        StringBuilder characters = new StringBuilder("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz");
        if (useSpecialChars) {
            characters.append("!@#$%^&*()_-+=<>?{}[]|~");
        }
        if (useNumbers) {
            characters.append("0123456789");
        }
        if (excludeSimilar) {
            characters = new StringBuilder(characters.toString().replaceAll("[O0l1]", ""));
        }

        SecureRandom random = new SecureRandom();
        StringBuilder password = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int index = random.nextInt(characters.length());
            password.append(characters.charAt(index));
        }

        return password.toString();
    }

    private int calculatePasswordStrength(String password) {
        int strength = 0;
        if (password.length() >= 8) strength += 25;
        if (password.matches(".*[0-9].*")) strength += 25;
        if (password.matches(".*[a-z].*")) strength += 25;
        if (password.matches(".*[A-Z].*")) strength += 25;
        if (password.matches(".*[@#$%^&+=].*")) strength += 25;

        return strength;
    }

    private String getStrengthLabel(int strength) {
        if (strength < 50) return "Fraca";
        if (strength < 75) return "Média";
        return "Forte";
    }

    private void updateStrengthBarColor(int strength) {
        if (strength < 50) {
            strengthBar.setForeground(Color.RED);
        } else if (strength < 75) {
            strengthBar.setForeground(Color.ORANGE);
        } else {
            strengthBar.setForeground(Color.GREEN);
        }
    }

    // Encrypt and save passwords
    private void savePasswords() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("senhas_encriptadas.txt", true))) {
            for (String password : passwordHistory) {
                String encryptedPassword = encryptPassword(password);
                writer.write(encryptedPassword);
                writer.newLine();
            }
            JOptionPane.showMessageDialog(null, "Senhas salvas com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(null, "Erro ao salvar senhas: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private String encryptPassword(String password) {
        try {
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            byte[] encrypted = cipher.doFinal(password.getBytes());
            return HexFormat.of().formatHex(encrypted); 
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(PasswordGenerator::new);
    }
}
