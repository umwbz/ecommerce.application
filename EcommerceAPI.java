package org.example;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import org.json.JSONArray;
import org.json.JSONObject;
import com.sun.net.httpserver.HttpContext;
import java.sql.Connection;
import java.sql.SQLException;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.sql.*;


public class EcommerceAPI{
        private static final String API_KEY = "sukma"; //
        private static final int PORT = 8019;

        public static void main(String[] args) throws Exception {
            try {
                // Kode utama aplikasi
                HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);
                HttpContext context = server.createContext("/");
                context.setHandler(EcommerceAPI::handleRequest);
                server.start();
                System.out.println("Server started on port " + PORT);
            } catch (Exception e) {
                e.printStackTrace(System.err);
            }
        }


        private static void handleRequest(HttpExchange exchange) throws IOException {
            String requestMethod = exchange.getRequestMethod();
            String requestURI = exchange.getRequestURI().getPath();

            // Memeriksa otorisasi dengan API key
            String apiKey = exchange.getRequestHeaders().getFirst("API-Key");
            if (apiKey == null || !apiKey.equals(API_KEY)) {
                sendResponse(exchange, 401, "Unauthorized");
                return;
            }

            if (requestMethod.equals("GET")) {
                if (requestURI.equals("/users")) {
                    // Handle GET /users
                    try {
                        JSONArray usersArray = getUsersFromDatabase();
                        sendResponse(exchange, 200, usersArray.toString());
                    } catch (SQLException e) {
                        System.err.println("Error getting users from database: " + e.getMessage());
                        sendResponse(exchange, 500, "Internal Server Error");
                    }
                } else if (requestURI.matches("/users/\\d+")) {
                    // Handle GET /users/{id}
                    try {
                        int userId = extractUserId(requestURI);
                        JSONObject userObject = getUserFromDatabase(userId);
                        if (userObject != null) {
                            sendResponse(exchange, 200, userObject.toString());
                        } else {
                            sendResponse(exchange, 404, "User not found");
                        }
                    } catch (NumberFormatException e) {
                        sendResponse(exchange, 400, "Invalid user ID");
                    } catch (SQLException e) {
                        sendResponse(exchange, 500, "Internal Server Error");
                    }
                } else if (requestURI.matches("/users/\\d+/products")) {
                    // Handle GET /users/{id}/products
                    try {
                        int userId = extractUserId(requestURI);
                        JSONArray productsArray = getUserProductsFromDatabase(userId);
                        sendResponse(exchange, 200, productsArray.toString());
                    } catch (NumberFormatException e) {
                        sendResponse(exchange, 400, "Invalid user ID");
                    } catch (SQLException e) {
                        sendResponse(exchange, 500, "Internal Server Error");
                    }
                } else if (requestURI.matches("/users/\\d+/orders")) {
                    // Handle GET /users/{id}/orders
                    try {
                        int userId = extractUserId(requestURI);
                        JSONArray ordersArray = getUserOrdersFromDatabase(userId);
                        sendResponse(exchange, 200, ordersArray.toString());
                    } catch (NumberFormatException e) {
                        sendResponse(exchange, 400, "Invalid user ID");
                    } catch (SQLException e) {
                        sendResponse(exchange, 500, "Internal Server Error");
                    }
                } else if (requestURI.matches("/users/\\d+/reviews")) {
                    // Handle GET /users/{id}/reviews
                    try {
                        int userId = extractUserId(requestURI);
                        JSONArray reviewsArray = getUserReviewsFromDatabase(userId);
                        sendResponse(exchange, 200, reviewsArray.toString());
                    } catch (NumberFormatException e) {
                        sendResponse(exchange, 400, "Invalid user ID");
                    } catch (SQLException e) {
                        sendResponse(exchange, 500, "Internal Server Error");
                    }
                } else if (requestURI.matches("/orders/\\d+")) {
                    // Handle GET /orders/{id}
                    try {
                        int orderId = extractOrderId(requestURI);
                        JSONObject orderObject = getOrderFromDatabase(orderId);
                        if (orderObject != null) {
                            sendResponse(exchange, 200, orderObject.toString());
                        } else {
                            sendResponse(exchange, 404, "Order not found");
                        }
                    } catch (NumberFormatException e) {
                        sendResponse(exchange, 400, "Invalid order ID");
                    } catch (SQLException e) {
                        sendResponse(exchange, 500, "Internal Server Error");
                    }
                } else if (requestURI.equals("/products")) {
                    // Handle GET /products
                    try {
                        JSONArray productsArray = getAllProductsFromDatabase();
                        sendResponse(exchange, 200, productsArray.toString());
                    } catch (SQLException e) {
                        sendResponse(exchange, 500, "Internal Server Error");
                    }
                } else if (requestURI.matches("/products/\\d+")) {
                    // Handle GET /products/{id}
                    try {
                        int productId = extractProductId(requestURI);
                        JSONObject productObject = getProductFromDatabase(productId);
                        if (productObject != null) {
                            sendResponse(exchange, 200, productObject.toString());
                        } else {
                            sendResponse(exchange, 404, "Product not found");
                        }
                    } catch (NumberFormatException e) {
                        sendResponse(exchange, 400, "Invalid product ID");
                    } catch (SQLException e) {
                        sendResponse(exchange, 500, "Internal Server Error");
                    }
                } else {
                    sendResponse(exchange, 404, "Not Found");
                }
            } else {
                sendResponse(exchange, 405, "Method Not Allowed");
            }
        }

        private static JSONArray getUsersFromDatabase() throws SQLException {
            JSONArray usersArray = new JSONArray();
            try (Connection connection = DBConnect.getConnection();
                 Statement statement = connection.createStatement();
                 ResultSet resultSet = statement.executeQuery("SELECT * FROM users")) {
                while (resultSet.next()) {
                    JSONObject userObject = new JSONObject();
                    userObject.put("id", resultSet.getInt("id"));
                    userObject.put("name", resultSet.getString("first_name"));
                    userObject.put("email", resultSet.getString("email"));
                    userObject.put("phoneNumber", resultSet.getString("phone_number"));
                    usersArray.put(userObject);
                }
            }
            return usersArray;
        }

        private static JSONObject getUserFromDatabase(int userId) throws SQLException {
            try (Connection connection = DBConnect.getConnection();
                 PreparedStatement statement = connection.prepareStatement("SELECT * FROM users WHERE id = ?")) {
                statement.setInt(1, userId);
                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        JSONObject userObject = new JSONObject();
                        userObject.put("id", resultSet.getInt("id"));
                        userObject.put("name", resultSet.getString("first_name"));
                        userObject.put("email", resultSet.getString("email"));
                        userObject.put("phoneNumber", resultSet.getString("phone_number"));
                        // Menambahkan informasi lain yang relevan
                        return userObject;
                    }
                }
            }
            return null;
        }

        private static JSONArray getUserProductsFromDatabase(int userId) throws SQLException {
            JSONArray productsArray = new JSONArray();
            try (Connection connection = DBConnect.getConnection();
                 PreparedStatement statement = connection.prepareStatement("SELECT * FROM products WHERE user_id = ?")) {
                statement.setInt(1, userId);
                try (ResultSet resultSet = statement.executeQuery()) {
                    while (resultSet.next()) {
                        JSONObject productObject = new JSONObject();
                        productObject.put("id", resultSet.getInt("id"));
                        productObject.put("name", resultSet.getString("first_name"));
                        // Menambahkan informasi lain yang relevan
                        productsArray.put(productObject);
                    }
                }
            }
            return productsArray;
        }

        private static JSONArray getUserOrdersFromDatabase(int userId) throws SQLException {
            JSONArray ordersArray = new JSONArray();
            try (Connection connection = DBConnect.getConnection();
                 PreparedStatement statement = connection.prepareStatement("SELECT * FROM orders WHERE user_id = ?")) {
                statement.setInt(1, userId);
                try (ResultSet resultSet = statement.executeQuery()) {
                    while (resultSet.next()) {
                        JSONObject orderObject = new JSONObject();
                        orderObject.put("id", resultSet.getInt("id"));
                        orderObject.put("status", resultSet.getString("status"));
                        // Menambahkan informasi lain yang relevan
                        ordersArray.put(orderObject);
                    }
                }
            }
            return ordersArray;
        }

        private static JSONArray getUserReviewsFromDatabase(int userId) throws SQLException {
            JSONArray reviewsArray = new JSONArray();
            try (Connection connection = DBConnect.getConnection();
                 PreparedStatement statement = connection.prepareStatement("SELECT * FROM reviews WHERE user_id = ?")) {
                statement.setInt(1, userId);
                try (ResultSet resultSet = statement.executeQuery()) {
                    while (resultSet.next()) {
                        JSONObject reviewObject = new JSONObject();
                        reviewObject.put("id", resultSet.getInt("id"));
                        reviewObject.put("rating", resultSet.getInt("rating"));
                        // Menambahkan informasi lain yang relevan
                        reviewsArray.put(reviewObject);
                    }
                }
            }
            return reviewsArray;
        }

        private static JSONObject getOrderFromDatabase(int orderId) throws SQLException {
            try (Connection connection = DBConnect.getConnection();
                 PreparedStatement statement = connection.prepareStatement("SELECT * FROM orders WHERE id = ?")) {
                statement.setInt(1, orderId);
                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        JSONObject orderObject = new JSONObject();
                        orderObject.put("id", resultSet.getInt("id"));
                        orderObject.put("status", resultSet.getString("status"));
                        // Menambahkan informasi lain yang relevan
                        return orderObject;
                    }
                }
            }
            return null;
        }

        private static JSONArray getAllProductsFromDatabase() throws SQLException {
            JSONArray productsArray = new JSONArray();
            try (Connection connection = DBConnect.getConnection();
                 Statement statement = connection.createStatement();
                 ResultSet resultSet = statement.executeQuery("SELECT * FROM products")) {
                while (resultSet.next()) {
                    JSONObject productObject = new JSONObject();
                    productObject.put("id", resultSet.getInt("id"));
                    productObject.put("name", resultSet.getString("name"));
                    // Menambahkan informasi lain yang relevan
                    productsArray.put(productObject);
                }
            }
            return productsArray;
        }

        private static JSONObject getProductFromDatabase(int productId) throws SQLException {
            try (Connection connection = DBConnect.getConnection();
                 PreparedStatement statement = connection.prepareStatement("SELECT * FROM products WHERE id = ?")) {
                statement.setInt(1, productId);
                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        JSONObject productObject = new JSONObject();
                        productObject.put("id", resultSet.getInt("id"));
                        productObject.put("name", resultSet.getString("name"));
                        // Menambahkan informasi lain yang relevan
                        return productObject;
                    }
                }
            }
            return null;
        }

        private static int extractUserId(String requestURI) {
            String[] parts = requestURI.split("/");
            return Integer.parseInt(parts[2]);
        }

        private static int extractOrderId(String requestURI) {
            String[] parts = requestURI.split("/");
            return Integer.parseInt(parts[2]);
        }

        private static int extractProductId(String requestURI) {
            String[] parts = requestURI.split("/");
            return Integer.parseInt(parts[2]);
        }

        private static void sendResponse(HttpExchange exchange, int statusCode, String response) throws IOException {
            exchange.sendResponseHeaders(statusCode, response.getBytes().length);
            OutputStream outputStream = exchange.getResponseBody();
            outputStream.write(response.getBytes());
            outputStream.close();
        }

}
