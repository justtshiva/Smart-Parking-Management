package com.smartparking.model;

public class ParkingSlot {

    private int slotId;
    private String slotCode;
    private int floor;
    private String status;

    public ParkingSlot() {}

    public ParkingSlot(int slotId, String slotCode, int floor, String status) {
        this.slotId = slotId;
        this.slotCode = slotCode;
        this.floor = floor;
        this.status = status;
    }

    public int getSlotId() {
        return slotId;
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

    public void setSlotId(int slotId) {
        this.slotId = slotId;
    }

    public void setSlotCode(String slotCode) {
        this.slotCode = slotCode;
    }

    public void setFloor(int floor) {
        this.floor = floor;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return slotId + " | " + slotCode + " | Floor: " + floor + " | " + status;
    }
}
