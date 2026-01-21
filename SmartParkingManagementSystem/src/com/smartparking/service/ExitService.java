package com.smartparking.service;

import com.smartparking.util.DbUtil;

import java.sql.*;
import java.time.Duration;
import java.time.LocalDateTime;

public class ExitService {

    public static class ExitResult {
        private final boolean success;
        private final String message;
        private final long minutes;
        private final double amount;

        public ExitResult(boolean success, String message, long minutes, double amount) {
            this.success = success;
            this.message = message;
            this.minutes = minutes;
            this.amount = amount;
        }

        public boolean isSuccess() { return success; }
        public String getMessage() { return message; }
        public long getMinutes() { return minutes; }
        public double getAmount() { return amount; }
    }

    public ExitResult exitVehicle(int ticketId) throws SQLException {

        try (Connection conn = DbUtil.getConnection()) {
            conn.setAutoCommit(false);

            int slotId;
            LocalDateTime entryTime;

            // 1️⃣ Fetch active ticket
            String fetchSql = """
                SELECT slot_id, entry_time
                FROM ticket
                WHERE ticket_id = ? AND status = 'ACTIVE'
                """;

            try (PreparedStatement ps = conn.prepareStatement(fetchSql)) {
                ps.setInt(1, ticketId);
                ResultSet rs = ps.executeQuery();

                if (!rs.next()) {
                    conn.rollback();
                    return new ExitResult(false, "Active ticket not found", 0, 0);
                }

                slotId = rs.getInt("slot_id");
                entryTime = rs.getTimestamp("entry_time").toLocalDateTime();
            }

            // 2️⃣ Calculate duration
            LocalDateTime exitTime = LocalDateTime.now();
            long minutes = Duration.between(entryTime, exitTime).toMinutes();
            if (minutes <= 0) minutes = 1;

            // 3️⃣ Calculate fee
            double amount = calculateFee(minutes);

            // 4️⃣ Update ticket
            String updateTicket = """
                UPDATE ticket
                SET exit_time = ?, amount_paid = ?, status = 'CLOSED'
                WHERE ticket_id = ?
                """;

            try (PreparedStatement ps = conn.prepareStatement(updateTicket)) {
                ps.setTimestamp(1, Timestamp.valueOf(exitTime));
                ps.setDouble(2, amount);
                ps.setInt(3, ticketId);
                ps.executeUpdate();
            }

            // 5️⃣ Free slot
            String updateSlot = """
                UPDATE parking_slot
                SET status = 'EMPTY'
                WHERE slot_id = ?
                """;

            try (PreparedStatement ps = conn.prepareStatement(updateSlot)) {
                ps.setInt(1, slotId);
                ps.executeUpdate();
            }

            conn.commit();

            return new ExitResult(true, "Vehicle exited successfully", minutes, amount);
        }
    }

    private double calculateFee(long minutes) {
        double hours = Math.ceil(minutes / 60.0);
        return Math.max(20, hours * 20);
    }
}
