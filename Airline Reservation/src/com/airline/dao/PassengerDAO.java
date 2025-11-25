package com.airline.dao;

import com.airline.models.Passenger;
import java.util.List;

public interface PassengerDAO {
    Passenger create(Passenger passenger) throws Exception;
    Passenger findById(int id) throws Exception;
    List<Passenger> findAll() throws Exception;
    boolean update(Passenger passenger) throws Exception;
    boolean delete(int id) throws Exception;

    // Additional convenient lookup
    Passenger findByEmail(String email) throws Exception;
}
