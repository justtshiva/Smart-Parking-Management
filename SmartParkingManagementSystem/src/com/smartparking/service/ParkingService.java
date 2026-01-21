package com.smartparking.service;

import com.smartparking.dao.*;
import com.smartparking.model.ParkingSlot;
import com.smartparking.util.DbUtil;

import java.sql.Connection;
import java.sql.SQLException;

public class ParkingService {

    private final ParkingSlotDao slotDao = new ParkingSlotDao();
    private final OwnerDao ownerDao = new OwnerDao();
    private final VehicleDao vehicleDao = new VehicleDao();
    private final TicketDao ticketDao = new TicketDao();

    public static class ParkResult {
        private final boolean success;
        private final String message;
        private final int ticketId;
        private final String slotCode;

        public ParkResult(boolean success, String message, int ticketId, String slotCode) {
            this.success = success; this.message = message; this.ticketId = ticketId; this.slotCode = slotCode;
        }
        public boolean isSuccess() { return success; }
        public String getMessage() { return message; }
        public int getTicketId() { return ticketId; }
        public String getSlotCode() { return slotCode; }
    }

    public ParkResult parkVehicle(String ownerName, String phone, String vehicleNumber, String vehicleType) throws SQLException {
        // Use single DB connection to manage transaction
        try (Connection conn = DbUtil.getConnection()) {
            try {
                conn.setAutoCommit(false);

                // 1. find first empty slot (we'll call a method that uses this same connection)
                ParkingSlot empty = slotDao.findFirstEmptySlot(conn);
                if (empty == null) {
                    conn.rollback();
                    return new ParkResult(false, "No empty slots", -1, null);
                }

                // 2. create or find owner (we need ownerDao to accept connection or do separate; simpler: do with separate connection)
                // For simplicity, ownerDao/vehicleDao use their own connections — that's acceptable here. Alternatively, refactor to pass conn.
                int ownerId = ownerDao.findOrCreateOwner(ownerName, phone);

                // 3. find or create vehicle
                int vehicleId = vehicleDao.findByNumber(vehicleNumber);
                if (vehicleId == -1) {
                    vehicleId = vehicleDao.createVehicle(vehicleNumber, vehicleType, ownerId);
                }

                // 4. create ticket using current transaction conn
                int ticketId = ticketDao.createTicket(empty.getSlotId(), vehicleId, conn);

                // 5. update slot to OCCUPIED using this connection
                slotDao.updateStatus(empty.getSlotId(), "OCCUPIED", conn);

                conn.commit();
                return new ParkResult(true, "OK", ticketId, empty.getSlotCode());
            } catch (Exception ex) {
                conn.rollback();
                throw ex;
            } finally {
                conn.setAutoCommit(true);
            }
        }
    }
}
