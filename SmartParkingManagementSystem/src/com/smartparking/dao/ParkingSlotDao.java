package com.smartparking.dao;

import java.sql.*;
import com.smartparking.util.DbUtil;
import java.util.ArrayList;
import java.util.List;

import com.smartparking.model.DashboardRow;
import com.smartparking.model.ParkingSlot;
import com.smartparking.util.DbUtil;




public class ParkingSlotDao {

    public List<ParkingSlot> findAll() throws SQLException {
        String sql = "SELECT slot_id, slot_code, floor, status FROM parking_slot ORDER BY floor, slot_code";
        List<ParkingSlot> list = new ArrayList<>();

        try (Connection conn = DbUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                ParkingSlot slot = new ParkingSlot(
                    rs.getInt("slot_id"),
                    rs.getString("slot_code"),
                    rs.getInt("floor"),
                    rs.getString("status")
                );
                list.add(slot);
            }
        }
        return list;
    }
    
    public ParkingSlot findFirstEmptySlot(Connection conn) throws SQLException {
        String sql = "SELECT slot_id, slot_code, floor, status FROM parking_slot WHERE status = 'EMPTY' ORDER BY floor, slot_code LIMIT 1";
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return new ParkingSlot(
                    rs.getInt("slot_id"),
                    rs.getString("slot_code"),
                    rs.getInt("floor"),
                    rs.getString("status")
                );
            }
        }
        return null;
    }
    
    
    public void updateStatus(int slotId, String status, Connection conn) throws SQLException {
        String sql = "UPDATE parking_slot SET status = ?, last_updated = CURRENT_TIMESTAMP WHERE slot_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, slotId);
            ps.executeUpdate();
        }
    }
    
    

    public List<DashboardRow> fetchDashboardData() throws SQLException {

        String sql = """
            SELECT ps.slot_code,
                   ps.floor,
                   ps.status,
                   t.ticket_id
            FROM parking_slot ps
            LEFT JOIN ticket t
              ON ps.slot_id = t.slot_id
             AND t.status = 'ACTIVE'
            ORDER BY ps.floor, ps.slot_code
            """;

        List<DashboardRow> list = new ArrayList<>();

        try (Connection conn = DbUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(new DashboardRow(
                        rs.getString("slot_code"),
                        rs.getInt("floor"),
                        rs.getString("status"),
                        (Integer) rs.getObject("ticket_id")
                ));
            }
        }

        // IMPORTANT: never return null
        return list;
    }



    
}
