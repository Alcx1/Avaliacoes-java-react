package br.com.meuapp.clienteavaliacao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Random;
import java.util.Scanner;

public class ClienteAvaliacaoApp {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Digite o número do pedido: ");
        String numeroPedido = scanner.nextLine();

        if (isPedidoValido(numeroPedido)) {
            System.out.print("Avalie seu pedido (1 a 5): ");
            int avaliacao = scanner.nextInt();

            salvarAvaliacao(numeroPedido, avaliacao);

            String cupom = gerarCupom();
            salvarCupom(numeroPedido, cupom);

            System.out.println("Obrigado pela avaliação! Seu cupom de desconto é: " + cupom);
        } else {
            System.out.println("Número de pedido inválido.");
        }

        scanner.close();
    }

    private static boolean isPedidoValido(String numeroPedido) {
        String sql = "SELECT COUNT(*) FROM pedidos WHERE numero_pedido = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, numeroPedido);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private static void salvarAvaliacao(String numeroPedido, int avaliacao) {
        String sql = "INSERT INTO avaliacoes (numero_pedido, avaliacao) VALUES (?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, numeroPedido);
            stmt.setInt(2, avaliacao);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static String gerarCupom() {
        Random random = new Random();
        int codigo = random.nextInt(999999);
        return "CUPOM" + String.format("%06d", codigo);
    }

    private static void salvarCupom(String numeroPedido, String codigoCupom) {
        String sql = "INSERT INTO cupons (numero_pedido, codigo_cupom) VALUES (?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, numeroPedido);
            stmt.setString(2, codigoCupom);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
