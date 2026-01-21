package com.smartparking.dao;

import com.smartparking.util.DbUtil;

import java.sql.*;

public class OwnerDao {

    public int findOrCreateOwner(String name, String phone) throws SQLException {
        // Try to find by phone first (if given)
        String selectSql = "SELECT owner_id FROM owner WHERE phone = ?";
        try (Connection conn = DbUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(selectSql)) {
            ps.setString(1, phone);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt("owner_id");
            }
        }

        // Insert new owner
        String insertSql = "INSERT INTO owner (name, phone) VALUES (?, ?)";
        try (Connection conn = DbUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, name);
            ps.setString(2, phone);
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) return rs.getInt(1);
            }
        }
        throw new SQLException("Failed to create owner");
    }
}
