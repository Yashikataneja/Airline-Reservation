package com.airline.models;

import java.sql.Timestamp;

public class Reservation {
    private int id;
    private int flightId;
    private int passengerId;
    private int seatsBooked;
    private double totalPrice;
    private Timestamp bookingTime;

    // getters/setters and toString
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getFlightId() { return flightId; }
    public void setFlightId(int flightId) { this.flightId = flightId; }
    public int getPassengerId() { return passengerId; }
    public void setPassengerId(int passengerId) { this.passengerId = passengerId; }
    public int getSeatsBooked() { return seatsBooked; }
    public void setSeatsBooked(int seatsBooked) { this.seatsBooked = seatsBooked; }
    public double getTotalPrice() { return totalPrice; }
    public void setTotalPrice(double totalPrice) { this.totalPrice = totalPrice; }
    public java.sql.Timestamp getBookingTime() { return bookingTime; }
    public void setBookingTime(java.sql.Timestamp bookingTime) { this.bookingTime = bookingTime; }

    @Override
    public String toString() {
        return String.format("Reservation{id=%d, flightId=%d, passengerId=%d, seats=%d, total=%.2f, time=%s}",
                id, flightId, passengerId, seatsBooked, totalPrice, bookingTime);
    }
}
