package com.airline.dao;

import com.airline.models.Flight;
import java.util.List;

public interface FlightDAO {
    Flight create(Flight flight) throws Exception;
    Flight findById(int id) throws Exception;
    List<Flight> findAll() throws Exception;
    boolean update(Flight flight) throws Exception;
    boolean delete(int id) throws Exception;

    // Extra helpful queries for flights
    List<Flight> findByRoute(String source, String destination) throws Exception;
    List<Flight> findByAirlineId(int airlineId) throws Exception;
}
