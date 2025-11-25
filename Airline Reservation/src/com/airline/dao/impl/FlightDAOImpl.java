package com.airline.dao.impl;

import com.airline.dao.FlightDAO;
import com.airline.models.Flight;
import com.airline.utils.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FlightDAOImpl implements FlightDAO {

    @Override
    public Flight create(Flight f) throws Exception {
        String sql = "INSERT INTO flight (airline_id, flight_no, source, destination, departure_time, capacity, available_seats, price) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, f.getAirlineId());
            ps.setString(2, f.getFlightNo());
            ps.setString(3, f.getSource());
            ps.setString(4, f.getDestination());
            ps.setTimestamp(5, f.getDepartureTime());
            ps.setInt(6, f.getCapacity());
            ps.setInt(7, f.getAvailableSeats());
            ps.setDouble(8, f.getPrice());

            int rows = ps.executeUpdate();
            if (rows == 0) throw new SQLException("Creating flight failed, no rows affected.");

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    f.setId(rs.getInt(1));
                }
            }
            return f;
        }
    }

    @Override
    public Flight findById(int id) throws Exception {
        String sql = "SELECT * FROM flight WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRowToFlight(rs);
                }
            }
        }
        return null;
    }

    @Override
    public List<Flight> findAll() throws Exception {
        List<Flight> list = new ArrayList<>();
        String sql = "SELECT * FROM flight ORDER BY id";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(mapRowToFlight(rs));
            }
        }
        return list;
    }

    @Override
    public boolean update(Flight f) throws Exception {
        String sql = "UPDATE flight SET airline_id=?, flight_no=?, source=?, destination=?, departure_time=?, capacity=?, available_seats=?, price=? WHERE id=?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, f.getAirlineId());
            ps.setString(2, f.getFlightNo());
            ps.setString(3, f.getSource());
            ps.setString(4, f.getDestination());
            ps.setTimestamp(5, f.getDepartureTime());
            ps.setInt(6, f.getCapacity());
            ps.setInt(7, f.getAvailableSeats());
            ps.setDouble(8, f.getPrice());
            ps.setInt(9, f.getId());

            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public boolean delete(int id) throws Exception {
        String sql = "DELETE FROM flight WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public List<Flight> findByRoute(String source, String destination) throws Exception {
        List<Flight> list = new ArrayList<>();
        String sql = "SELECT * FROM flight WHERE source LIKE ? AND destination LIKE ? ORDER BY departure_time";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            // use % for partial matching if desired, caller can pass "Delhi" or "%Del%"
            ps.setString(1, source);
            ps.setString(2, destination);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRowToFlight(rs));
                }
            }
        }
        return list;
    }

    @Override
    public List<Flight> findByAirlineId(int airlineId) throws Exception {
        List<Flight> list = new ArrayList<>();
        String sql = "SELECT * FROM flight WHERE airline_id = ? ORDER BY departure_time";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, airlineId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRowToFlight(rs));
                }
            }
        }
        return list;
    }

    /**
     * Utility: map current ResultSet row to Flight object
     */
    private Flight mapRowToFlight(ResultSet rs) throws SQLException {
        Flight f = new Flight();
        f.setId(rs.getInt("id"));
        f.setAirlineId(rs.getInt("airline_id"));
        f.setFlightNo(rs.getString("flight_no"));
        f.setSource(rs.getString("source"));
        f.setDestination(rs.getString("destination"));
        f.setDepartureTime(rs.getTimestamp("departure_time"));
        f.setCapacity(rs.getInt("capacity"));
        f.setAvailableSeats(rs.getInt("available_seats"));
        f.setPrice(rs.getDouble("price"));
        return f;
    }

    /**
     * Optional helper used in reservation logic: change available seats atomically (works with an open Connection)
     * delta can be negative (decrease) or positive (increase). Returns true if update happened.
     */
    public boolean changeAvailableSeats(Connection conn, int flightId, int delta) throws Exception {
        String sql = "UPDATE flight SET available_seats = available_seats + ? WHERE id = ? AND available_seats + ? >= 0";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, delta);
            ps.setInt(2, flightId);
            ps.setInt(3, delta);
            return ps.executeUpdate() > 0;
        }
    }
}
