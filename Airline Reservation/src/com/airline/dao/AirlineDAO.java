package com.airline.dao;

import com.airline.models.Airline;
import java.util.List;

public interface AirlineDAO {
    Airline create(Airline airline) throws Exception;
    Airline findById(int id) throws Exception;
    List<Airline> findAll() throws Exception;
    boolean update(Airline airline) throws Exception;
    boolean delete(int id) throws Exception;
}
