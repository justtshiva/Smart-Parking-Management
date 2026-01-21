package com.smartparking.ui;

import java.util.List;

import com.smartparking.dao.ParkingSlotDao;
import com.smartparking.model.ParkingSlot;

public class TestSlots {
    public static void main(String[] args) {
        try {
            ParkingSlotDao dao = new ParkingSlotDao();
            List<ParkingSlot> slots = dao.findAll();

            for (ParkingSlot s : slots) {
                System.out.println(s);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
