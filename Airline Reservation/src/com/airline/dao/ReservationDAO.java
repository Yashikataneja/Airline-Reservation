package com.airline.dao;

import com.airline.models.Reservation;
import java.util.List;

public interface ReservationDAO {
    // create reservation (should be implemented using a transaction that
    // checks & updates flight.available_seats)
    Reservation create(Reservation reservation) throws Exception;

    Reservation findById(int id) throws Exception;
    List<Reservation> findAll() throws Exception;
    boolean delete(int id) throws Exception; // cancel reservation (should refund seats)
    boolean update(Reservation reservation) throws Exception; // optional, e.g. change seats

    // Helpful lookups
    List<Reservation> findByPassengerId(int passengerId) throws Exception;
    List<Reservation> findByFlightId(int flightId) throws Exception;
}
