package br.com.meuapp.clienteavaliacao;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Random;

import io.nayuki.qrcodegen.QrCode;
import io.nayuki.qrcodegen.QrCode.Ecc;

public class ClienteAvaliacaoGUI {
    private JFrame frame;
    private JTextField txtNumeroPedido;
    private StarRater starRater1, starRater2, starRater3, starRater4;
    private JLabel lblCupom;

    public ClienteAvaliacaoGUI() {
        initialize();
    }

    private void initialize() {
        frame = new JFrame();
        frame.setUndecorated(true);
        frame.setSize(500, 600);
        frame.setShape(new RoundRectangle2D.Double(0, 0, frame.getWidth(), frame.getHeight(), 50, 50)); // Arredondar cantos
        frame.setLocationRelativeTo(null);

        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(new Color(245, 245, 245)); // Light grey background
                g.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        frame.setContentPane(panel);
        panel.setLayout(new GridBagLayout());

        JLabel lblTitulo = new JLabel("Avaliação de Pedidos");
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 24));
        lblTitulo.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel lblNumeroPedido = new JLabel("Número do Pedido:");
        lblNumeroPedido.setFont(new Font("Arial", Font.PLAIN, 16));
        txtNumeroPedido = new JTextField();
        txtNumeroPedido.setColumns(10);

        JLabel lblAvaliacao1 = new JLabel("Avaliação (Geral):");
        lblAvaliacao1.setFont(new Font("Arial", Font.PLAIN, 16));
        starRater1 = new StarRater(5, 0);

        JLabel lblAvaliacao2 = new JLabel("Atendimento ao Cliente:");
        lblAvaliacao2.setFont(new Font("Arial", Font.PLAIN, 16));
        starRater2 = new StarRater(5, 0);

        JLabel lblAvaliacao3 = new JLabel("Estabelecimento:");
        lblAvaliacao3.setFont(new Font("Arial", Font.PLAIN, 16));
        starRater3 = new StarRater(5, 0);

        JLabel lblAvaliacao4 = new JLabel("Localização:");
        lblAvaliacao4.setFont(new Font("Arial", Font.PLAIN, 16));
        starRater4 = new StarRater(5, 0);

        JButton btnAvaliar = new JButton("Avaliar");
        btnAvaliar.setFont(new Font("Arial", Font.BOLD, 16));
        btnAvaliar.setBackground(new Color(0xb22222)); // Change the background color to #b22222
        btnAvaliar.setForeground(Color.WHITE);
        btnAvaliar.setBorder(new LineBorder(new Color(0xb22222), 1, true)); // Change the border color to #b22222
        btnAvaliar.setFocusPainted(false);
        btnAvaliar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String numeroPedido = txtNumeroPedido.getText();
                int avaliacao1 = starRater1.getRating();
                int avaliacao2 = starRater2.getRating();
                int avaliacao3 = starRater3.getRating();
                int avaliacao4 = starRater4.getRating();
                avaliarPedido(numeroPedido, avaliacao1, avaliacao2, avaliacao3, avaliacao4);
            }
        });

        lblCupom = new JLabel("");
        lblCupom.setFont(new Font("Arial", Font.BOLD, 14));
        lblCupom.setHorizontalAlignment(SwingConstants.CENTER);
        lblCupom.setVerticalAlignment(SwingConstants.TOP);
        lblCupom.setBorder(new EmptyBorder(10, 0, 0, 0));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.weightx = 1.0;
        panel.add(lblTitulo, gbc);

        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.LINE_END;
        panel.add(lblNumeroPedido, gbc);

        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.anchor = GridBagConstraints.LINE_START;
        panel.add(txtNumeroPedido, gbc);

        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.LINE_END;
        panel.add(lblAvaliacao1, gbc);

        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.anchor = GridBagConstraints.LINE_START;
        panel.add(starRater1, gbc);

        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.LINE_END;
        panel.add(lblAvaliacao2, gbc);

        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.anchor = GridBagConstraints.LINE_START;
        panel.add(starRater2, gbc);

        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.LINE_END;
        panel.add(lblAvaliacao3, gbc);

        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.anchor = GridBagConstraints.LINE_START;
        panel.add(starRater3, gbc);

        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.LINE_END;
        panel.add(lblAvaliacao4, gbc);

        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.anchor = GridBagConstraints.LINE_START;
        panel.add(starRater4, gbc);

        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(btnAvaliar, gbc);

        gbc.gridwidth = GridBagConstraints.REMAINDER;
        panel.add(lblCupom, gbc);
    }

    private void avaliarPedido(String numero_Pedido, int avaliacao1, int atendimento_cliente, int estabelecimento, int localizacao) {
        if (isPedidoValido(numero_Pedido)) {
            salvarAvaliacao(numero_Pedido, avaliacao1, atendimento_cliente, estabelecimento, localizacao);
            String cupom = gerarCupom();
            salvarCupom(numero_Pedido, cupom);
            lblCupom.setText("<html>Obrigado pela avaliação!<br>Seu cupom de desconto é: " + cupom + "</html>");
            exibirQRCode(cupom);
        } else {
            lblCupom.setText("Número de pedido inválido.");
        }
    }

    private boolean isPedidoValido(String numero_Pedido) {
        String sql = "SELECT COUNT(*) FROM pedidos WHERE numero_pedido = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, numero_Pedido);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private void salvarAvaliacao(String numero_Pedido, int avaliacao1, int atendimento_cliente, int estabelecimento, int localizacao) {
        String sql = "INSERT INTO avaliacoes (numero_Pedido, avaliacao1, atendimento_cliente, estabelecimento, localizacao) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, numero_Pedido);
            stmt.setInt(2, avaliacao1);
            stmt.setInt(3, atendimento_cliente);
            stmt.setInt(4, estabelecimento);
            stmt.setInt(5, localizacao);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private String gerarCupom() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder cupom = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < 8; i++) {
            cupom.append(chars.charAt(random.nextInt(chars.length())));
        }
        return cupom.toString();
    }

    private void salvarCupom(String numeroPedido, String cupom) {
        String sql = "UPDATE pedidos SET cupom = ? WHERE numero_pedido = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, cupom);
            stmt.setString(2, numeroPedido);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void exibirQRCode(String cupom) {
        try {
            String url = "http://192.168.18.6:3000/cupom/" + cupom;
            QrCode qr0 = QrCode.encodeText(url, Ecc.MEDIUM);
            BufferedImage img = toBufferedImage(qr0, 4, 10);
            ImageIcon icon = new ImageIcon(img);
            JOptionPane.showMessageDialog(frame, "", "QR Code", JOptionPane.INFORMATION_MESSAGE, icon);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private BufferedImage toBufferedImage(QrCode qr, int scale, int border) {
        int size = qr.size;
        BufferedImage img = new BufferedImage((size + border * 2) * scale, (size + border * 2) * scale, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
        g.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_SPEED);
        g.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_DISABLE);
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED);
        g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);

        g.setColor(Color.WHITE);
        g.fillRect(0, 0, img.getWidth(), img.getHeight());
        g.setColor(Color.BLACK);
        for (int y = 0; y < size; y++) {
            for (int x = 0; x < size; x++) {
                if (qr.getModule(x, y)) {
                    g.fillRect((x + border) * scale, (y + border) * scale, scale, scale);
                }
            }
        }
        g.dispose();
        return img;
    }

    public void show() {
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    ClienteAvaliacaoGUI window = new ClienteAvaliacaoGUI();
                    window.show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
