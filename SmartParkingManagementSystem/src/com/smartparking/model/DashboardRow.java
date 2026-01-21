package com.smartparking.model;

public class DashboardRow {

    private String slotCode;
    private int floor;
    private String status;
    private Integer ticketId; // nullable

    public DashboardRow(String slotCode, int floor, String status, Integer ticketId) {
        this.slotCode = slotCode;
        this.floor = floor;
        this.status = status;
        this.ticketId = ticketId;
    }

    public String getSlotCode() {
        return slotCode;
    }

    public int getFloor() {
        return floor;
    }

    public String getStatus() {
        return status;
    }

    public Integer getTicketId() {
        return ticketId;
    }
}
