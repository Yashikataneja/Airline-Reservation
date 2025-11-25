package com.airline.models;

public class Airline {
    private int id;
    private String name;
    private String code;

    public Airline() {}
    public Airline(int id, String name, String code) {
        this.id = id; this.name = name; this.code = code;
    }
    public Airline(String name, String code) { this.name = name; this.code = code; }

    // getters/setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    @Override
    public String toString() {
        return String.format("Airline{id=%d, name='%s', code='%s'}", id, name, code);
    }
}
