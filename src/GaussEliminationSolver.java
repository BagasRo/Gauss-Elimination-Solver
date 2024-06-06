import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GaussEliminationSolver {

    public static final int SIZE = 10;
    private static float[][] a = new float[SIZE][SIZE];
    private static float[] x = new float[SIZE];
    private static int n;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(GaussEliminationSolver::createAndShowGUI);
    }

    private static void createAndShowGUI() {
        JFrame frame = new JFrame("Pemecah Eliminasi Gauss");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(750, 550);
        frame.setLayout(new BorderLayout(10, 10));

        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.setBackground(new Color(173, 216, 230));

        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 10));
        inputPanel.setBackground(new Color(173, 216, 230));
        JLabel label = new JLabel("Masukkan jumlah variabel yang tidak diketahui: ");
        JTextField textField = new JTextField(5);
        textField.setToolTipText("Jumlah variabel dalam sistem persamaan");
        inputPanel.add(label);
        inputPanel.add(textField);

        // Add help button to the top-right corner
        JButton helpButton = new JButton("Panduan");
        inputPanel.add(Box.createHorizontalGlue());
        inputPanel.add(helpButton);

        panel.add(inputPanel, BorderLayout.NORTH);

        JTextArea matrixInput = new JTextArea(10, 50);
        matrixInput.setBorder(BorderFactory.createTitledBorder("Masukkan koefisien Matriks Augmented:"));
        matrixInput.setFont(new Font("Monospaced", Font.PLAIN, 12));
        matrixInput.setLineWrap(true);
        matrixInput.setWrapStyleWord(true);
        matrixInput.setBackground(new Color(224, 255, 255));
        panel.add(new JScrollPane(matrixInput), BorderLayout.CENTER);

        JTextArea resultArea = new JTextArea(10, 50);
        resultArea.setBorder(BorderFactory.createTitledBorder("Solusi:"));
        resultArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        resultArea.setEditable(false);
        resultArea.setBackground(new Color(224, 255, 255));
        panel.add(new JScrollPane(resultArea), BorderLayout.SOUTH);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        buttonPanel.setBackground(new Color(173, 216, 230));
        JButton solveButton = new JButton("Selesaikan");
        JButton clearButton = new JButton("Bersihkan");
        JButton exitButton = new JButton("Keluar");
        buttonPanel.add(solveButton);
        buttonPanel.add(clearButton);
        buttonPanel.add(exitButton);

        solveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    n = Integer.parseInt(textField.getText().trim());

                    if (n <= 0 || n > SIZE) {
                        JOptionPane.showMessageDialog(frame, "Jumlah variabel tidak valid. Silakan masukkan angka antara 1 dan " + SIZE + ".", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    String[] lines = matrixInput.getText().trim().split("\n");
                    if (lines.length != n) {
                        JOptionPane.showMessageDialog(frame, "Matriks input tidak valid. Silakan masukkan tepat " + n + " baris.", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    for (int i = 0; i < n; i++) {
                        String[] values = lines[i].trim().split("\\s+");
                        if (values.length != n + 1) {
                            JOptionPane.showMessageDialog(frame, "Matriks input tidak valid. Setiap baris harus memiliki tepat " + (n + 1) + " nilai.", "Error", JOptionPane.ERROR_MESSAGE);
                            return;
                        }
                        for (int j = 0; j <= n; j++) {
                            a[i][j] = Float.parseFloat(values[j]);
                        }
                    }

                    gaussElimination(a, n, x);

                    StringBuilder result = new StringBuilder("Solusi:\n");
                    for (int i = 0; i < n; i++) {
                        if (x[i] == -0.0f) {
                            x[i] = 0.0f;
                        }
                        result.append(String.format("x[%d] = %.3f%n", i + 1, x[i]));
                    }
                    resultArea.setText(result.toString());

                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(frame, "Input tidak valid. Silakan masukkan angka yang valid.", "Error", JOptionPane.ERROR_MESSAGE);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(frame, "Terjadi kesalahan: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        clearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                textField.setText("");
                matrixInput.setText("");
                resultArea.setText("");
            }
        });

        exitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.dispose();
            }
        });

        helpButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(frame,
                        "Panduan Penggunaan:\n" +
                        "1. Masukkan jumlah variabel pada bagian atas.\n" +
                        "2. Masukkan koefisien matriks augmented. Setiap baris mewakili satu persamaan.\n" +
                        "3. Pastikan setiap baris memiliki n+1 nilai (n koefisien variabel + 1 konstanta).\n" +
                        "Contoh untuk 3 variabel:\n" +
                        "2 3 1 1\n" +
                        "4 1 2 2\n" +
                        "3 2 3 3\n" +
                        "\n"+
                        "4. Klik tombol 'Selesaikan' untuk mendapatkan solusi.\n" +
                        "5. Klik tombol 'Bersihkan' untuk menghapus input dan hasil.\n" +
                        "6. Klik tombol 'Keluar' untuk menutup aplikasi.",
                        "Panduan Penggunaan", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        frame.add(panel, BorderLayout.CENTER);
        frame.add(buttonPanel, BorderLayout.SOUTH);

        frame.setVisible(true);
    }

    public static void gaussElimination(float[][] a, int n, float[] x) {
        for (int i = 0; i < n - 1; i++) {
            if (Math.abs(a[i][i]) < 1e-9) {
                JOptionPane.showMessageDialog(null, "Kesalahan Matematika: Pembagian dengan nol!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            for (int j = i + 1; j < n; j++) {
                float ratio = a[j][i] / a[i][i];
                for (int k = 0; k <= n; k++) {
                    a[j][k] -= ratio * a[i][k];
                }
            }
        }

        x[n - 1] = a[n - 1][n] / a[n - 1][n - 1];

        for (int i = n - 2; i >= 0; i--) {
            x[i] = a[i][n];
            for (int j = i + 1; j < n; j++) {
                x[i] -= a[i][j] * x[j];
            }
            if (Math.abs(a[i][i]) < 1e-9) {
                x[i] = 0;
            } else {
                x[i] /= a[i][i];
            }
        }
    }
}
