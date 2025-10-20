import javax.swing.*;
import java.sql.*;
import java.util.*;

public class DBHelper {
    private final String url = "jdbc:sqlite:travelagency.db";

    public void init() {
        try (Connection c = DBConnection.getConnection()) {
            try (Statement st = c.createStatement()) {
                st.execute("PRAGMA foreign_keys = ON;");
                st.execute("DROP TABLE IF EXISTS destinations;");
                st.execute("DROP TABLE IF EXISTS hotels;");
                st.execute("CREATE TABLE IF NOT EXISTS destinations (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, country TEXT, description TEXT, image_path TEXT);");
                st.execute("CREATE TABLE IF NOT EXISTS hotels (id INTEGER PRIMARY KEY AUTOINCREMENT, destination_id INTEGER, name TEXT, price_per_night REAL, rating REAL, image_path TEXT, FOREIGN KEY(destination_id) REFERENCES destinations(id));");
                st.execute("CREATE TABLE IF NOT EXISTS users (id INTEGER PRIMARY KEY AUTOINCREMENT, username TEXT UNIQUE, password TEXT);");
                st.execute("CREATE TABLE IF NOT EXISTS bookings (id INTEGER PRIMARY KEY AUTOINCREMENT, user_id INTEGER, destination_id INTEGER, hotel_id INTEGER, mode TEXT, booked_at DATETIME DEFAULT CURRENT_TIMESTAMP, FOREIGN KEY(user_id) REFERENCES users(id), FOREIGN KEY(destination_id) REFERENCES destinations(id), FOREIGN KEY(hotel_id) REFERENCES hotels(id));");
            }
            // insert sample data if destinations empty
            if (getAllDestinations().isEmpty()) {
                try (PreparedStatement ps = c.prepareStatement("INSERT INTO destinations (name,country,description,image_path) VALUES (?, ?, ?, ?);") ) {
                    insertDestination(ps, "Paris", "France", "City of lights, famous art and cuisine", "images/destinations/paris.jpg");
                    insertDestination(ps, "Goa", "India", "Beaches, nightlife and Portuguese heritage", "images/destinations/goa.jpg");
                    insertDestination(ps, "Kyoto", "Japan", "Temples, cherry blossom and culture", "images/destinations/kyoto.jpg");
                    insertDestination(ps, "New York", "USA", "The city that never sleeps", "images/destinations/newyork.jpg");
                }
                try (PreparedStatement ph = c.prepareStatement("INSERT INTO hotels (destination_id,name,price_per_night,rating,image_path) VALUES (?, ?, ?, ?, ?);") ) {
                    ph.setInt(1, 1); ph.setString(2, "Le Elegance Hotel"); ph.setDouble(3, 220.0); ph.setDouble(4, 4.6); ph.setString(5, "images/hotels/le_elegance.jpg"); ph.addBatch();
                    ph.setInt(1, 1); ph.setString(2, "CitySide Suites"); ph.setDouble(3, 165.0); ph.setDouble(4, 4.2); ph.setString(5, "images/hotels/cityside.jpg"); ph.addBatch();
                    ph.setInt(1, 2); ph.setString(2, "Beachside Resort"); ph.setDouble(3, 95.0); ph.setDouble(4, 4.1); ph.setString(5, "images/hotels/beachside.jpg"); ph.addBatch();
                    ph.setInt(1, 2); ph.setString(2, "Palm Villas"); ph.setDouble(3, 150.0); ph.setDouble(4, 4.5); ph.setString(5, "images/hotels/palmvillas.jpg"); ph.addBatch();
                    ph.setInt(1, 3); ph.setString(2, "Zen Ryokan"); ph.setDouble(3, 180.0); ph.setDouble(4, 4.7); ph.setString(5, "images/hotels/zenryokan.jpg"); ph.addBatch();
                    ph.setInt(1, 4); ph.setString(2, "Metro Grand"); ph.setDouble(3, 240.0); ph.setDouble(4, 4.4); ph.setString(5, "images/hotels/metrogrand.jpg"); ph.addBatch();
                    ph.executeBatch();
                }
            }
            // create a demo user if none
            if (getUserByUsername("demo") == null) {
                createUser("demo", "demo123");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Database initialization failed: " + ex.getMessage());
        }
    }

    private void insertDestination(PreparedStatement ps, String a, String b, String c, String img) throws SQLException {
        ps.setString(1, a); ps.setString(2, b); ps.setString(3, c); ps.setString(4, img); ps.executeUpdate();
    }

    public List<Destination> getAllDestinations() {
        List<Destination> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SELECT id,name,country,description,image_path FROM destinations ORDER BY id")) {
            while (rs.next()) {
                list.add(new Destination(rs.getInt(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getString(5)));
            }
        } catch (SQLException ex) { ex.printStackTrace(); }
        return list;
    }

    public Destination getDestinationById(int id) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT id,name,country,description,image_path FROM destinations WHERE id = ?")) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return new Destination(rs.getInt(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getString(5));
            }
        } catch (SQLException ex) { ex.printStackTrace(); }
        return null;
    }

    public List<Hotel> getHotelsByDestination(int destId) {
        List<Hotel> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT id,destination_id,name,price_per_night,rating,image_path FROM hotels WHERE destination_id = ? ORDER BY price_per_night")) {
            ps.setInt(1, destId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(new Hotel(rs.getInt(1), rs.getInt(2), rs.getString(3), rs.getDouble(4), rs.getDouble(5), rs.getString(6)));
            }
        } catch (SQLException ex) { ex.printStackTrace(); }
        return list;
    }

    public Hotel getHotelById(int id) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT id,destination_id,name,price_per_night,rating,image_path FROM hotels WHERE id = ?")) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return new Hotel(rs.getInt(1), rs.getInt(2), rs.getString(3), rs.getDouble(4), rs.getDouble(5), rs.getString(6));
            }
        } catch (SQLException ex) { ex.printStackTrace(); }
        return null;
    }

    public User getUserByUsername(String username) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT id,username,password FROM users WHERE username = ?")) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return new User(rs.getInt(1), rs.getString(2), rs.getString(3));
            }
        } catch (SQLException ex) { ex.printStackTrace(); }
        return null;
    }

    public void createUser(String username, String password) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement("INSERT INTO users (username,password) VALUES (?, ?);") ) {
            ps.setString(1, username); ps.setString(2, password); ps.executeUpdate();
        } catch (SQLException ex) { ex.printStackTrace(); }
    }

    public void createBooking(int userId, int destId, int hotelId, String mode) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement("INSERT INTO bookings (user_id,destination_id,hotel_id,mode) VALUES (?, ?, ?, ?);") ) {
            ps.setInt(1, userId); ps.setInt(2, destId); ps.setInt(3, hotelId); ps.setString(4, mode); ps.executeUpdate();
        } catch (SQLException ex) { ex.printStackTrace(); }
    }
}
