package com.smartparking.ui;

import java.sql.Connection;
import com.smartparking.util.DbUtil;

public class Simple {
    public static void main(String[] args) {
        try (Connection conn = DbUtil.getConnection()) {
            System.out.println("Connection Successful!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
