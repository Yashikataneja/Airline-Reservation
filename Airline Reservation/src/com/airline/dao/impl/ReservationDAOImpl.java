package com.airline.dao.impl;

import com.airline.dao.ReservationDAO;
import com.airline.models.Reservation;
import com.airline.utils.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReservationDAOImpl implements ReservationDAO {

    /**
     * Create a reservation using a transaction:
     *  - lock flight row (FOR UPDATE)
     *  - check available seats
     *  - insert reservation
     *  - decrement available seats
     */
    @Override
    public Reservation create(Reservation r) throws Exception {
        Connection conn = null;
        PreparedStatement psInsert = null;
        try {
            conn = DBUtil.getConnection();
            conn.setAutoCommit(false);

            // Lock flight row and get available seats & price
            String checkSql = "SELECT available_seats, price FROM flight WHERE id = ? FOR UPDATE";
            try (PreparedStatement checkPs = conn.prepareStatement(checkSql)) {
                checkPs.setInt(1, r.getFlightId());
                try (ResultSet rs = checkPs.executeQuery()) {
                    if (!rs.next()) throw new SQLException("Flight not found.");
                    int avail = rs.getInt("available_seats");
                    double price = rs.getDouble("price");
                    if (avail < r.getSeatsBooked()) {
                        throw new SQLException("Not enough seats. Available: " + avail);
                    }
                    r.setTotalPrice(price * r.getSeatsBooked());
                }
            }

            // Insert reservation
            String insertSql = "INSERT INTO reservation (flight_id, passenger_id, seats_booked, total_price) VALUES (?, ?, ?, ?)";
            psInsert = conn.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS);
            psInsert.setInt(1, r.getFlightId());
            psInsert.setInt(2, r.getPassengerId());
            psInsert.setInt(3, r.getSeatsBooked());
            psInsert.setDouble(4, r.getTotalPrice());
            int rows = psInsert.executeUpdate();
            if (rows == 0) throw new SQLException("Creating reservation failed, no rows affected.");
            try (ResultSet gk = psInsert.getGeneratedKeys()) {
                if (gk.next()) r.setId(gk.getInt(1));
            }

            // Deduct available seats
            String updateSeatsSql = "UPDATE flight SET available_seats = available_seats - ? WHERE id = ?";
            try (PreparedStatement up = conn.prepareStatement(updateSeatsSql)) {
                up.setInt(1, r.getSeatsBooked());
                up.setInt(2, r.getFlightId());
                up.executeUpdate();
            }

            conn.commit();
            return r;
        } catch (Exception ex) {
            if (conn != null) {
                try { conn.rollback(); } catch (Exception e) { /* ignore */ }
            }
            throw ex;
        } finally {
            if (psInsert != null) try { psInsert.close(); } catch (Exception e) {}
            if (conn != null) try { conn.setAutoCommit(true); conn.close(); } catch (Exception e) {}
        }
    }

    @Override
    public Reservation findById(int id) throws Exception {
        String sql = "SELECT * FROM reservation WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRowToReservation(rs);
            }
        }
        return null;
    }

    @Override
    public List<Reservation> findAll() throws Exception {
        List<Reservation> list = new ArrayList<>();
        String sql = "SELECT * FROM reservation ORDER BY booking_time DESC";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapRowToReservation(rs));
        }
        return list;
    }

    /**
     * Update a reservation. This implementation:
     *  - allows changing passenger_id and seats_booked
     *  - adjusts flight.available_seats accordingly inside a transaction
     *  - recalculates total_price using flight.price
     *
     * Returns true if update succeeded.
     */
    @Override
    public boolean update(Reservation newRes) throws Exception {
        Connection conn = null;
        try {
            conn = DBUtil.getConnection();
            conn.setAutoCommit(false);

            // Lock reservation row
            String selRes = "SELECT flight_id, seats_booked FROM reservation WHERE id = ? FOR UPDATE";
            int flightId;
            int oldSeats;
            try (PreparedStatement ps = conn.prepareStatement(selRes)) {
                ps.setInt(1, newRes.getId());
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        conn.rollback();
                        return false; // reservation not found
                    }
                    flightId = rs.getInt("flight_id");
                    oldSeats = rs.getInt("seats_booked");
                }
            }

            int seatDiff = newRes.getSeatsBooked() - oldSeats; // >0 need more seats, <0 refund

            if (seatDiff != 0) {
                // Lock flight row
                String selFlight = "SELECT available_seats FROM flight WHERE id = ? FOR UPDATE";
                int avail;
                try (PreparedStatement psf = conn.prepareStatement(selFlight)) {
                    psf.setInt(1, flightId);
                    try (ResultSet rs = psf.executeQuery()) {
                        if (!rs.next()) {
                            conn.rollback();
                            throw new SQLException("Flight not found while updating reservation.");
                        }
                        avail = rs.getInt("available_seats");
                    }
                }
                if (seatDiff > 0 && avail < seatDiff) {
                    conn.rollback();
                    throw new SQLException("Not enough seats to increase booking. Available: " + avail);
                }
                // Adjust flight.available_seats
                String updSeats = "UPDATE flight SET available_seats = available_seats - ? WHERE id = ?";
                try (PreparedStatement psu = conn.prepareStatement(updSeats)) {
                    psu.setInt(1, seatDiff);
                    psu.setInt(2, flightId);
                    psu.executeUpdate();
                }
            }

            // Recompute total price from flight price
            double price = 0.0;
            String priceSql = "SELECT price FROM flight WHERE id = ?";
            try (PreparedStatement psp = conn.prepareStatement(priceSql)) {
                psp.setInt(1, flightId);
                try (ResultSet rs = psp.executeQuery()) {
                    if (rs.next()) price = rs.getDouble("price");
                }
            }
            double newTotal = price * newRes.getSeatsBooked();

            // Update reservation row
            String updRes = "UPDATE reservation SET passenger_id = ?, seats_booked = ?, total_price = ? WHERE id = ?";
            try (PreparedStatement psu2 = conn.prepareStatement(updRes)) {
                psu2.setInt(1, newRes.getPassengerId());
                psu2.setInt(2, newRes.getSeatsBooked());
                psu2.setDouble(3, newTotal);
                psu2.setInt(4, newRes.getId());
                psu2.executeUpdate();
            }

            conn.commit();
            return true;
        } catch (Exception ex) {
            if (conn != null) try { conn.rollback(); } catch (Exception e) {}
            throw ex;
        } finally {
            if (conn != null) try { conn.setAutoCommit(true); conn.close(); } catch (Exception e) {}
        }
    }

    /**
     * Delete (cancel) reservation and refund seats to flight.available_seats.
     * Operation done inside a transaction using SELECT ... FOR UPDATE.
     */
    @Override
    public boolean delete(int id) throws Exception {
        Connection conn = null;
        try {
            conn = DBUtil.getConnection();
            conn.setAutoCommit(false);

            // Lock and fetch reservation
            String sel = "SELECT flight_id, seats_booked FROM reservation WHERE id = ? FOR UPDATE";
            int flightId;
            int seats;
            try (PreparedStatement ps = conn.prepareStatement(sel)) {
                ps.setInt(1, id);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        conn.rollback();
                        return false;
                    }
                    flightId = rs.getInt("flight_id");
                    seats = rs.getInt("seats_booked");
                }
            }

            // Delete reservation
            try (PreparedStatement psDel = conn.prepareStatement("DELETE FROM reservation WHERE id = ?")) {
                psDel.setInt(1, id);
                psDel.executeUpdate();
            }

            // Refund seats
            try (PreparedStatement psRef = conn.prepareStatement("UPDATE flight SET available_seats = available_seats + ? WHERE id = ?")) {
                psRef.setInt(1, seats);
                psRef.setInt(2, flightId);
                psRef.executeUpdate();
            }

            conn.commit();
            return true;
        } catch (Exception ex) {
            if (conn != null) try { conn.rollback(); } catch (Exception e) {}
            throw ex;
        } finally {
            if (conn != null) try { conn.setAutoCommit(true); conn.close(); } catch (Exception e) {}
        }
    }

    @Override
    public List<Reservation> findByPassengerId(int passengerId) throws Exception {
        List<Reservation> list = new ArrayList<>();
        String sql = "SELECT * FROM reservation WHERE passenger_id = ? ORDER BY booking_time DESC";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, passengerId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRowToReservation(rs));
            }
        }
        return list;
    }

    @Override
    public List<Reservation> findByFlightId(int flightId) throws Exception {
        List<Reservation> list = new ArrayList<>();
        String sql = "SELECT * FROM reservation WHERE flight_id = ? ORDER BY booking_time DESC";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, flightId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRowToReservation(rs));
            }
        }
        return list;
    }

    // Helper to map a ResultSet row to Reservation model
    private Reservation mapRowToReservation(ResultSet rs) throws SQLException {
        Reservation r = new Reservation();
        r.setId(rs.getInt("id"));
        r.setFlightId(rs.getInt("flight_id"));
        r.setPassengerId(rs.getInt("passenger_id"));
        r.setSeatsBooked(rs.getInt("seats_booked"));
        r.setTotalPrice(rs.getDouble("total_price"));
        r.setBookingTime(rs.getTimestamp("booking_time"));
        return r;
    }
}
