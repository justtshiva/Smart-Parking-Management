package com.smartparking.dao;

import com.smartparking.util.DbUtil;

import java.sql.*;

public class VehicleDao {

    public int findByNumber(String vehicleNumber) throws SQLException {
        String sql = "SELECT vehicle_id FROM vehicle WHERE vehicle_number = ?";
        try (Connection conn = DbUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, vehicleNumber);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt("vehicle_id");
            }
        }
        return -1;
    }

    public int createVehicle(String vehicleNumber, String vehicleType, int ownerId) throws SQLException {
        String sql = "INSERT INTO vehicle (vehicle_number, vehicle_type, owner_id) VALUES (?, ?, ?)";
        try (Connection conn = DbUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, vehicleNumber);
            ps.setString(2, vehicleType);
            ps.setInt(3, ownerId);
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) return rs.getInt(1);
            }
        }
        throw new SQLException("Failed to create vehicle");
    }
}
