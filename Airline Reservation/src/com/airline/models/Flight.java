package com.airline.models;

import java.sql.Timestamp;

public class Flight {
    private int id;
    private int airlineId;
    private String flightNo;
    private String source;
    private String destination;
    private Timestamp departureTime;
    private int capacity;
    private int availableSeats;
    private double price;

    public Flight() {}

    // getters/setters...
    // generate toString
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getAirlineId() { return airlineId; }
    public void setAirlineId(int airlineId) { this.airlineId = airlineId; }
    public String getFlightNo() { return flightNo; }
    public void setFlightNo(String flightNo) { this.flightNo = flightNo; }
    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }
    public String getDestination() { return destination; }
    public void setDestination(String destination) { this.destination = destination; }
    public Timestamp getDepartureTime() { return departureTime; }
    public void setDepartureTime(Timestamp departureTime) { this.departureTime = departureTime; }
    public int getCapacity() { return capacity; }
    public void setCapacity(int capacity) { this.capacity = capacity; }
    public int getAvailableSeats() { return availableSeats; }
    public void setAvailableSeats(int availableSeats) { this.availableSeats = availableSeats; }
    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    @Override
    public String toString() {
        return String.format("Flight{id=%d, flightNo=%s, src=%s, dst=%s, dep=%s, avail=%d, price=%.2f}",
                id, flightNo, source, destination, departureTime, availableSeats, price);
    }
}
