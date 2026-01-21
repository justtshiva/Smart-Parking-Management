package com.smartparking.dao;

import com.smartparking.util.DbUtil;

import java.sql.*;

public class TicketDao {

    public int createTicket(int slotId, int vehicleId, Connection conn) throws SQLException {
        // expects caller-managed connection/transaction
        String sql = "INSERT INTO ticket (slot_id, vehicle_id, entry_time, status) VALUES (?, ?, NOW(), 'ACTIVE')";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, slotId);
            ps.setInt(2, vehicleId);
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) return rs.getInt(1);
            }
        }
        throw new SQLException("Failed to create ticket");
    }
}
