package com.airline;

public class TestDriver {
    public static void main(String[] args) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("Driver class loaded OK");
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }
}

