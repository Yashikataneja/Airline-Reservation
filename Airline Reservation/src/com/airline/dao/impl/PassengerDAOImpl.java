package com.airline.dao.impl;

import com.airline.dao.PassengerDAO;
import com.airline.models.Passenger;
import com.airline.utils.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PassengerDAOImpl implements PassengerDAO {

    @Override
    public Passenger create(Passenger p) throws Exception {
        String sql = "INSERT INTO passenger (first_name, last_name, email, phone) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, p.getFirstName());
            ps.setString(2, p.getLastName());
            ps.setString(3, p.getEmail());
            ps.setString(4, p.getPhone());

            int rows = ps.executeUpdate();
            if (rows == 0) throw new SQLException("Create passenger failed, no rows affected.");

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    p.setId(rs.getInt(1));
                }
            }
            return p;
        }
    }

    @Override
    public Passenger findById(int id) throws Exception {
        String sql = "SELECT id, first_name, last_name, email, phone FROM passenger WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Passenger p = new Passenger();
                    p.setId(rs.getInt("id"));
                    p.setFirstName(rs.getString("first_name"));
                    p.setLastName(rs.getString("last_name"));
                    p.setEmail(rs.getString("email"));
                    p.setPhone(rs.getString("phone"));
                    return p;
                }
            }
        }
        return null;
    }

    @Override
    public List<Passenger> findAll() throws Exception {
        List<Passenger> list = new ArrayList<>();
        String sql = "SELECT id, first_name, last_name, email, phone FROM passenger ORDER BY id";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Passenger p = new Passenger();
                p.setId(rs.getInt("id"));
                p.setFirstName(rs.getString("first_name"));
                p.setLastName(rs.getString("last_name"));
                p.setEmail(rs.getString("email"));
                p.setPhone(rs.getString("phone"));
                list.add(p);
            }
        }
        return list;
    }

    @Override
    public boolean update(Passenger p) throws Exception {
        String sql = "UPDATE passenger SET first_name = ?, last_name = ?, email = ?, phone = ? WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, p.getFirstName());
            ps.setString(2, p.getLastName());
            ps.setString(3, p.getEmail());
            ps.setString(4, p.getPhone());
            ps.setInt(5, p.getId());
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public boolean delete(int id) throws Exception {
        String sql = "DELETE FROM passenger WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public Passenger findByEmail(String email) throws Exception {
        if (email == null || email.isEmpty()) return null;
        String sql = "SELECT id, first_name, last_name, email, phone FROM passenger WHERE email = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Passenger p = new Passenger();
                    p.setId(rs.getInt("id"));
                    p.setFirstName(rs.getString("first_name"));
                    p.setLastName(rs.getString("last_name"));
                    p.setEmail(rs.getString("email"));
                    p.setPhone(rs.getString("phone"));
                    return p;
                }
            }
        }
        return null;
    }
}
