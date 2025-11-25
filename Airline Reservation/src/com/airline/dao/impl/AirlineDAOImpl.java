package com.airline.dao.impl;

import com.airline.dao.AirlineDAO;
import com.airline.models.Airline;
import com.airline.utils.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AirlineDAOImpl implements AirlineDAO {

    @Override
    public Airline create(Airline airline) throws Exception {
        String sql = "INSERT INTO airline (name, code) VALUES (?, ?)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, airline.getName());
            ps.setString(2, airline.getCode());
            int rows = ps.executeUpdate();
            if (rows == 0) throw new SQLException("Create airline failed, no rows affected.");
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    airline.setId(rs.getInt(1));
                }
            }
            return airline;
        }
    }

    @Override
    public Airline findById(int id) throws Exception {
        String sql = "SELECT id, name, code FROM airline WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Airline(rs.getInt("id"), rs.getString("name"), rs.getString("code"));
                }
            }
        }
        return null;
    }

    @Override
    public List<Airline> findAll() throws Exception {
        List<Airline> list = new ArrayList<>();
        String sql = "SELECT id, name, code FROM airline ORDER BY id";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new Airline(rs.getInt("id"), rs.getString("name"), rs.getString("code")));
            }
        }
        return list;
    }

    @Override
    public boolean update(Airline airline) throws Exception {
        String sql = "UPDATE airline SET name = ?, code = ? WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, airline.getName());
            ps.setString(2, airline.getCode());
            ps.setInt(3, airline.getId());
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public boolean delete(int id) throws Exception {
        String sql = "DELETE FROM airline WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }
}
