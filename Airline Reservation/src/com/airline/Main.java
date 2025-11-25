package com.airline;

import com.airline.dao.impl.AirlineDAOImpl;
import com.airline.dao.impl.FlightDAOImpl;
import com.airline.dao.impl.PassengerDAOImpl;
import com.airline.dao.impl.ReservationDAOImpl;
import com.airline.models.*;
import com.airline.utils.DateTimeUtil;
import com.airline.utils.InputUtil;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

public class Main {
    private static final AirlineDAOImpl airlineDAO = new AirlineDAOImpl();
    private static final FlightDAOImpl flightDAO = new FlightDAOImpl();
    private static final PassengerDAOImpl passengerDAO = new PassengerDAOImpl();
    private static final ReservationDAOImpl reservationDAO = new ReservationDAOImpl();

    public static void main(String[] args) {
        while (true) {
            System.out.println("\n==== Airline Reservation System ====");
            System.out.println("1. Airlines (CRUD)");
            System.out.println("2. Flights (CRUD)");
            System.out.println("3. Passengers (CRUD)");
            System.out.println("4. Reservations (Create/View/Delete)");
            System.out.println("0. Exit");
            int choice = InputUtil.nextInt("Choose: ");
            try {
                switch (choice) {
                    case 1 -> airlineMenu();
                    case 2 -> flightMenu();
                    case 3 -> passengerMenu();
                    case 4 -> reservationMenu();
                    case 0 -> {
                        System.out.println("Goodbye!");
                        return;
                    }
                    default -> System.out.println("Invalid choice.");
                }
            } catch (Exception e) {
                System.err.println("Error: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private static void airlineMenu() throws Exception {
        System.out.println("\n-- Airlines --");
        System.out.println("1. Add Airline");
        System.out.println("2. List Airlines");
        System.out.println("3. Update Airline");
        System.out.println("4. Delete Airline");
        System.out.println("0. Back");
        int c = InputUtil.nextInt("Choose: ");
        switch (c) {
            case 1 -> {
                String name = InputUtil.nextLine("Name: ");
                String code = InputUtil.nextLine("Code: ");
                Airline a = new Airline(name, code);
                airlineDAO.create(a);
                System.out.println("Created: " + a);
            }
            case 2 -> {
                List<Airline> list = airlineDAO.findAll();
                list.forEach(System.out::println);
            }
            case 3 -> {
                int id = InputUtil.nextInt("Airline ID to update: ");
                Airline a = airlineDAO.findById(id);
                if (a == null) { System.out.println("Not found"); return; }
                a.setName(InputUtil.nextLine("New name ("+a.getName()+"): "));
                a.setCode(InputUtil.nextLine("New code ("+a.getCode()+"): "));
                if (airlineDAO.update(a)) System.out.println("Updated.");
            }
            case 4 -> {
                int id = InputUtil.nextInt("Airline ID to delete: ");
                if (airlineDAO.delete(id)) System.out.println("Deleted.");
                else System.out.println("Nothing deleted.");
            }
            case 0 -> {}
            default -> System.out.println("Invalid");
        }
    }

    private static void flightMenu() throws Exception {
        System.out.println("\n-- Flights --");
        System.out.println("1. Add Flight");
        System.out.println("2. List Flights");
        System.out.println("3. Update Flight");
        System.out.println("4. Delete Flight");
        System.out.println("5. Find by route");
        System.out.println("0. Back");
        int c = InputUtil.nextInt("Choose: ");
        switch (c) {
            case 1 -> {
                int airlineId = InputUtil.nextInt("Airline ID: ");
                String no = InputUtil.nextLine("Flight No: ");
                String src = InputUtil.nextLine("Source: ");
                String dst = InputUtil.nextLine("Destination: ");
                String dt = InputUtil.nextLine("Departure (yyyy-MM-dd HH:mm): ");
                LocalDateTime ldt = DateTimeUtil.parse(dt);
                int cap = InputUtil.nextInt("Capacity: ");
                double price = InputUtil.nextDouble("Price: ");
                Flight f = new Flight();
                f.setAirlineId(airlineId);
                f.setFlightNo(no);
                f.setSource(src);
                f.setDestination(dst);
                f.setDepartureTime(DateTimeUtil.toTimestamp(ldt));
                f.setCapacity(cap);
                f.setAvailableSeats(cap);
                f.setPrice(price);
                flightDAO.create(f);
                System.out.println("Created: " + f);
            }
            case 2 -> {
                List<Flight> list = flightDAO.findAll();
                list.forEach(System.out::println);
            }
            case 3 -> {
                int id = InputUtil.nextInt("Flight ID to update: ");
                Flight f = flightDAO.findById(id);
                if (f == null) { System.out.println("Not found"); return; }
                f.setFlightNo(InputUtil.nextLine("Flight No ("+f.getFlightNo()+"): "));
                f.setSource(InputUtil.nextLine("Source ("+f.getSource()+"): "));
                f.setDestination(InputUtil.nextLine("Destination ("+f.getDestination()+"): "));
                String dt = InputUtil.nextLine("Departure (yyyy-MM-dd HH:mm): ");
                f.setDepartureTime(DateTimeUtil.toTimestamp(DateTimeUtil.parse(dt)));
                int cap = InputUtil.nextInt("Capacity: ");
                f.setCapacity(cap);
                f.setAvailableSeats(InputUtil.nextInt("Available seats: "));
                f.setPrice(InputUtil.nextDouble("Price: "));
                if (flightDAO.update(f)) System.out.println("Updated.");
            }
            case 4 -> {
                int id = InputUtil.nextInt("Flight ID to delete: ");
                if (flightDAO.delete(id)) System.out.println("Deleted.");
            }
            case 5 -> {
                String src = InputUtil.nextLine("Source: ");
                String dst = InputUtil.nextLine("Destination: ");
                List<Flight> list = flightDAO.findByRoute(src, dst);
                list.forEach(System.out::println);
            }
            case 0 -> {}
            default -> System.out.println("Invalid");
        }
    }

    private static void passengerMenu() throws Exception {
        System.out.println("\n-- Passengers --");
        System.out.println("1. Add Passenger");
        System.out.println("2. List Passengers");
        System.out.println("3. Update Passenger");
        System.out.println("4. Delete Passenger");
        System.out.println("0. Back");
        int c = InputUtil.nextInt("Choose: ");
        switch (c) {
            case 1 -> {
                String fn = InputUtil.nextLine("First name: ");
                String ln = InputUtil.nextLine("Last name: ");
                String email = InputUtil.nextLine("Email: ");
                String phone = InputUtil.nextLine("Phone: ");
                Passenger p = new Passenger();
                p.setFirstName(fn);
                p.setLastName(ln);
                p.setEmail(email);
                p.setPhone(phone);
                passengerDAO.create(p);
                System.out.println("Created: " + p);
            }
            case 2 -> {
                List<Passenger> list = passengerDAO.findAll();
                list.forEach(System.out::println);
            }
            case 3 -> {
                int id = InputUtil.nextInt("Passenger ID: ");
                Passenger p = passengerDAO.findById(id);
                if (p == null) { System.out.println("Not found"); return; }
                p.setFirstName(InputUtil.nextLine("First name ("+p.getFirstName()+"): "));
                p.setLastName(InputUtil.nextLine("Last name ("+p.getLastName()+"): "));
                p.setEmail(InputUtil.nextLine("Email ("+p.getEmail()+"): "));
                p.setPhone(InputUtil.nextLine("Phone ("+p.getPhone()+"): "));
                if (passengerDAO.update(p)) System.out.println("Updated.");
            }
            case 4 -> {
                int id = InputUtil.nextInt("Passenger ID to delete: ");
                if (passengerDAO.delete(id)) System.out.println("Deleted.");
            }
            case 0 -> {}
            default -> System.out.println("Invalid");
        }
    }

    private static void reservationMenu() throws Exception {
        System.out.println("\n-- Reservations --");
        System.out.println("1. Create Reservation");
        System.out.println("2. View Reservation by ID");
        System.out.println("3. Cancel Reservation");
        System.out.println("0. Back");
        int c = InputUtil.nextInt("Choose: ");
        switch (c) {
            case 1 -> {
                int flightId = InputUtil.nextInt("Flight ID: ");
                int passengerId = InputUtil.nextInt("Passenger ID: ");
                int seats = InputUtil.nextInt("Seats to book: ");
                Reservation r = new Reservation();
                r.setFlightId(flightId);
                r.setPassengerId(passengerId);
                r.setSeatsBooked(seats);
                Reservation created = reservationDAO.create(r);
                System.out.println("Created reservation: " + created);
            }
            case 2 -> {
                int id = InputUtil.nextInt("Reservation ID: ");
                Reservation r = reservationDAO.findById(id);
                if (r != null) System.out.println(r);
                else System.out.println("Not found.");
            }
            case 3 -> {
                int id = InputUtil.nextInt("Reservation ID to cancel: ");
                if (reservationDAO.delete(id)) System.out.println("Cancelled & refunded seats.");
                else System.out.println("Cannot cancel.");
            }
            case 0 -> {}
            default -> System.out.println("Invalid");
        }
    }
}
