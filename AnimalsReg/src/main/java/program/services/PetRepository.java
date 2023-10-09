package program.services;

import program.model.Creator;
import program.model.Pet;
import program.model.PetCreator;
import program.model.PetType;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;


public class PetRepository implements IRepository<Pet> {
    private final Creator petCreator;
    private ResultSet resultSet;
    private String request;

    public PetRepository() {
        this.petCreator = new PetCreator();
    }

    @Override
    public List<Pet> getAll() {
        List<Pet> farm = new ArrayList<>();
        Pet pet;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            try (Connection dbConnection = getConnection()) {
                Statement sqlSt = dbConnection.createStatement();
                request = "SELECT type_id, id, animal_name, dateofbirth FROM pet_list ORDER BY id";
                resultSet = sqlSt.executeQuery(request);
                while (resultSet.next()) {

                    PetType type = PetType.getType(resultSet.getInt(1));
                    int id = resultSet.getInt(2);
                    String name = resultSet.getString(3);
                    LocalDate birthday = resultSet.getDate(4).toLocalDate();

                    pet = petCreator.createPet(type, name, birthday);
                    pet.setPetId(id);
                    farm.add(pet);
                }
                return farm;
            }
        } catch (ClassNotFoundException | IOException | SQLException ex) {
            Logger.getLogger(PetRepository.class.getName()).log(Level.SEVERE, null, ex);
            throw new RuntimeException(ex.getMessage());
        }
    }

    @Override
    public Pet getById(int petId) {
        Pet pet = null;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            try (Connection dbConnection = getConnection()) {
                request = "SELECT type_id, id, animal_name, dateofbirth FROM pet_list WHERE id = ?";
                PreparedStatement prepSt = dbConnection.prepareStatement(request);
                prepSt.setInt(1, petId);
                resultSet = prepSt.executeQuery();

                if (resultSet.next()) {

                    PetType type = PetType.getType(resultSet.getInt(1));
                    int id = resultSet.getInt(2);
                    String name = resultSet.getString(3);
                    LocalDate birthday = resultSet.getDate(4).toLocalDate();

                    pet = petCreator.createPet(type, name, birthday);
                    pet.setPetId(id);
                }
                return pet;
            }
        } catch (ClassNotFoundException | IOException | SQLException ex) {
            Logger.getLogger(PetRepository.class.getName()).log(Level.SEVERE, null, ex);
            throw new RuntimeException(ex.getMessage());
        }
    }

    @Override
    public int create(Pet pet) {
        int rows;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            try (Connection dbConnection = getConnection()) {

                request = "INSERT INTO pet_list (animal_name, dateofbirth, type_id) SELECT ?, ?, " +
                        "(SELECT id FROM animal_types WHERE type_name = ?)";
                PreparedStatement prepSt = dbConnection.prepareStatement(request);
                prepSt.setString(1, pet.getName());
                prepSt.setDate(2, Date.valueOf(pet.getBirthdayDate()));
                prepSt.setString(3, pet.getClass().getSimpleName());

                rows = prepSt.executeUpdate();
                return rows;
            }
        } catch (ClassNotFoundException | IOException | SQLException ex) {
            Logger.getLogger(PetRepository.class.getName()).log(Level.SEVERE, null, ex);
            throw new RuntimeException(ex.getMessage());
        }
    }

    public void train(int id, String command) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            try (Connection dbConnection = getConnection()) {
                request = "INSERT INTO pet_commands (pet_id, command_id) SELECT ?, " +
                        "(SELECT id FROM commands WHERE command_name = ?)";
                PreparedStatement prepSt = dbConnection.prepareStatement(request);
                prepSt.setInt(1, id);
                prepSt.setString(2, command);
                prepSt.executeUpdate();
            }
        } catch (ClassNotFoundException | IOException | SQLException ex) {
            Logger.getLogger(PetRepository.class.getName()).log(Level.SEVERE, null, ex);
            throw new RuntimeException(ex.getMessage());
        }
    }

    public List<String> getCommandsById(int petId, int commands_type) {
        List<String> commands = new ArrayList<>();
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            try (Connection dbConnection = getConnection()) {
                if (commands_type == 1) {
                    request = "SELECT command_name FROM pet_commands pc JOIN commands c " +
                            "ON pc.command_id = c.id WHERE pc.pet_id = ?";
                } else {
                    request = "SELECT command_name FROM commands c JOIN spec_commands sc " +
                            "ON c.id = sc.command_id WHERE sc.type_id = (SELECT type_id FROM pet_list WHERE id = ?)";
                }
                PreparedStatement prepSt = dbConnection.prepareStatement(request);
                prepSt.setInt(1, petId);
                resultSet = prepSt.executeQuery();
                while (resultSet.next()) {
                    commands.add(resultSet.getString(1));
                }
                return commands;
            }
        } catch (ClassNotFoundException | IOException | SQLException ex) {
            Logger.getLogger(PetRepository.class.getName()).log(Level.SEVERE, null, ex);
            throw new RuntimeException(ex.getMessage());
        }
    }

    @Override
    public int update(Pet pet) {
        int rows;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            try (Connection dbConnection = getConnection()) {
                request = "UPDATE pet_list SET animal_name = ?, dateofbirth = ? WHERE id = ?";
                PreparedStatement prepSt = dbConnection.prepareStatement(request);

                prepSt.setString(1, pet.getName());
                prepSt.setDate(2, Date.valueOf(pet.getBirthdayDate()));
                prepSt.setInt(3, pet.getPetId());

                rows = prepSt.executeUpdate();
                return rows;
            }
        } catch (ClassNotFoundException | IOException | SQLException ex) {
            Logger.getLogger(PetRepository.class.getName()).log(Level.SEVERE, null, ex);
            throw new RuntimeException(ex.getMessage());
        }
    }

    @Override
    public void delete(int id) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            try (Connection dbConnection = getConnection()) {
                request = "DELETE FROM pet_list WHERE id = ?";
                PreparedStatement prepSt = dbConnection.prepareStatement(request);
                prepSt.setInt(1, id);
                prepSt.executeUpdate();
            }
        } catch (ClassNotFoundException | IOException | SQLException ex) {
            Logger.getLogger(PetRepository.class.getName()).log(Level.SEVERE, null, ex);
            throw new RuntimeException(ex.getMessage());
        }
    }

    public static Connection getConnection() throws SQLException, IOException {
        Properties props = new Properties();
        try (FileInputStream fis = new FileInputStream("src/Resources/db.properties")) {
            props.load(fis);
            String url = props.getProperty("url");
            String username = props.getProperty("username");
            String password = props.getProperty("password");
            return DriverManager.getConnection(url, username, password);
        }
    }
}
