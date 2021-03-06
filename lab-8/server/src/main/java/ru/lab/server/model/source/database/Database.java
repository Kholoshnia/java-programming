package ru.lab.server.model.source.database;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.util.Supplier;
import ru.lab.server.model.domain.dto.dtos.*;
import ru.lab.server.model.source.DataSource;
import ru.lab.server.model.source.database.exceptions.DatabaseException;
import ru.lab.server.model.source.exceptions.DataSourceException;

import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ResourceBundle;

/** Database class is used to initialize tables. */
public final class Database extends DataSource {
  private static final Logger logger = LogManager.getLogger(Database.class);

  private static final String INIT_USERS_TABLE_EXCEPTION;
  private static final String INIT_WORKERS_TABLE_EXCEPTION;
  private static final String INIT_COORDINATES_TABLE_EXCEPTION;
  private static final String INIT_PERSONS_TABLE_EXCEPTION;
  private static final String INIT_LOCATIONS_TABLE_EXCEPTION;

  private static final String CREATE_IF_NOT_EXISTS_USERS_TABLE =
      "CREATE TABLE IF NOT EXISTS "
          + UserDTO.TABLE_NAME
          + " ("
          + UserDTO.ID_COLUMN
          + " SERIAL NOT NULL PRIMARY KEY, "
          + UserDTO.NAME_COLUMN
          + " VARCHAR NOT NULL CHECK(LENGTH("
          + UserDTO.NAME_COLUMN
          + ")>=2) CHECK(LENGTH("
          + UserDTO.NAME_COLUMN
          + ")<=100), "
          + UserDTO.LOGIN_COLUMN
          + " VARCHAR NOT NULL UNIQUE CHECK(LENGTH("
          + UserDTO.LOGIN_COLUMN
          + ")>=2) CHECK(LENGTH("
          + UserDTO.LOGIN_COLUMN
          + ")<=100), "
          + UserDTO.PASSWORD_COLUMN
          + " VARCHAR NOT NULL CHECK(LENGTH("
          + UserDTO.PASSWORD_COLUMN
          + ")>=8) CHECK(LENGTH("
          + UserDTO.PASSWORD_COLUMN
          + ")<=100), "
          + UserDTO.ROLE_COLUMN
          + " VARCHAR NOT NULL)";

  private static final String CREATE_IF_NOT_EXISTS_COORDINATES_TABLE =
      "CREATE TABLE IF NOT EXISTS "
          + CoordinatesDTO.TABLE_NAME
          + " ("
          + CoordinatesDTO.ID_COLUMN
          + " SERIAL NOT NULL PRIMARY KEY, "
          + CoordinatesDTO.OWNER_ID_COLUMN
          + " SERIAL NOT NULL, "
          + CoordinatesDTO.X_COLUMN
          + " DOUBLE PRECISION NOT NULL CHECK("
          + CoordinatesDTO.X_COLUMN
          + ">-433), "
          + CoordinatesDTO.Y_COLUMN
          + " DOUBLE PRECISION NOT NULL CHECK("
          + CoordinatesDTO.Y_COLUMN
          + ">-501), FOREIGN KEY ("
          + CoordinatesDTO.OWNER_ID_COLUMN
          + ") REFERENCES "
          + UserDTO.TABLE_NAME
          + "("
          + UserDTO.ID_COLUMN
          + "))";

  private static final String CREATE_IF_NOT_EXISTS_LOCATIONS_TABLE =
      "CREATE TABLE IF NOT EXISTS "
          + LocationDTO.TABLE_NAME
          + " ("
          + LocationDTO.ID_COLUMN
          + " SERIAL NOT NULL PRIMARY KEY, "
          + LocationDTO.OWNER_ID_COLUMN
          + " SERIAL NOT NULL , "
          + LocationDTO.X_COLUMN
          + " BIGINT NOT NULL, "
          + LocationDTO.Y_COLUMN
          + " BIGINT NOT NULL, "
          + LocationDTO.Z_COLUMN
          + " DOUBLE PRECISION NOT NULL, "
          + LocationDTO.NAME_COLUMN
          + " VARCHAR NULL CHECK(LENGTH("
          + LocationDTO.NAME_COLUMN
          + ")<=461), FOREIGN KEY ("
          + LocationDTO.OWNER_ID_COLUMN
          + ") REFERENCES "
          + UserDTO.TABLE_NAME
          + "("
          + UserDTO.ID_COLUMN
          + "))";

  private static final String CREATE_IF_NOT_EXISTS_PERSONS_TABLE =
      "CREATE TABLE IF NOT EXISTS "
          + PersonDTO.TABLE_NAME
          + " ("
          + PersonDTO.ID_COLUMN
          + " SERIAL NOT NULL PRIMARY KEY, "
          + PersonDTO.OWNER_ID_COLUMN
          + " SERIAL NOT NULL, "
          + PersonDTO.PASSPORT_ID_COLUMN
          + " VARCHAR NOT NULL CHECK(LENGTH("
          + PersonDTO.PASSPORT_ID_COLUMN
          + ")>=10) CHECK(LENGTH("
          + PersonDTO.PASSPORT_ID_COLUMN
          + ")<=44), "
          + PersonDTO.EYE_COLOR_COLUMN
          + " VARCHAR NULL, "
          + PersonDTO.HAIR_COLOR_COLUMN
          + " VARCHAR NOT NULL, "
          + PersonDTO.LOCATION_COLUMN
          + " SERIAL NOT NULL, FOREIGN KEY ("
          + PersonDTO.LOCATION_COLUMN
          + ") REFERENCES "
          + LocationDTO.TABLE_NAME
          + "("
          + LocationDTO.ID_COLUMN
          + "), FOREIGN KEY ("
          + PersonDTO.OWNER_ID_COLUMN
          + ") REFERENCES "
          + UserDTO.TABLE_NAME
          + "("
          + UserDTO.ID_COLUMN
          + "))";

  private static final String CREATE_IF_NOT_EXISTS_WORKERS_TABLE =
      "CREATE TABLE IF NOT EXISTS "
          + WorkerDTO.TABLE_NAME
          + " ("
          + WorkerDTO.ID_COLUMN
          + " SERIAL NOT NULL PRIMARY KEY, "
          + WorkerDTO.OWNER_ID_COLUMN
          + " SERIAL NOT NULL, "
          + WorkerDTO.KEY_COLUMN
          + " INT NOT NULL UNIQUE CHECK("
          + WorkerDTO.KEY_COLUMN
          + ">0), "
          + WorkerDTO.NAME_COLUMN
          + " VARCHAR NOT NULL CHECK(LENGTH("
          + WorkerDTO.NAME_COLUMN
          + ")>=0), "
          + WorkerDTO.COORDINATES_COLUMN
          + " SERIAL NOT NULL, "
          + WorkerDTO.CREATION_DATE_COLUMN
          + " DATE NOT NULL, "
          + WorkerDTO.SALARY_COLUMN
          + " DOUBLE PRECISION NOT NULL CHECK("
          + WorkerDTO.SALARY_COLUMN
          + ">0), "
          + WorkerDTO.START_DATE_COLUMN
          + " TIMESTAMP NOT NULL, "
          + WorkerDTO.END_DATE_COLUMN
          + " TIMESTAMP NULL, "
          + WorkerDTO.STATUS_COLUMN
          + " VARCHAR NULL, "
          + WorkerDTO.PERSON_COLUMN
          + " BIGINT NULL REFERENCES "
          + PersonDTO.TABLE_NAME
          + ", FOREIGN KEY ("
          + WorkerDTO.COORDINATES_COLUMN
          + ") REFERENCES "
          + CoordinatesDTO.TABLE_NAME
          + "("
          + CoordinatesDTO.ID_COLUMN
          + ") ON DELETE CASCADE, FOREIGN KEY ("
          + WorkerDTO.OWNER_ID_COLUMN
          + ") REFERENCES "
          + UserDTO.TABLE_NAME
          + "("
          + UserDTO.ID_COLUMN
          + "))";

  static {
    ResourceBundle resourceBundle = ResourceBundle.getBundle("internal.Database");

    INIT_USERS_TABLE_EXCEPTION = resourceBundle.getString("exceptions.initUsersTable");
    INIT_WORKERS_TABLE_EXCEPTION = resourceBundle.getString("exceptions.initWorkersTable");
    INIT_COORDINATES_TABLE_EXCEPTION = resourceBundle.getString("exceptions.initCoordinatesTable");
    INIT_PERSONS_TABLE_EXCEPTION = resourceBundle.getString("exceptions.initPersonsTable");
    INIT_LOCATIONS_TABLE_EXCEPTION = resourceBundle.getString("exceptions.initLocationsTable");
  }

  /**
   * Initializes all tables. NOTE: order is important.
   *
   * @param url database url for {@link DriverManager}
   * @param user database user
   * @param password database password
   * @throws DataSourceException - in case of data source exceptions
   * @throws DatabaseException - if initialization is incorrect
   */
  public Database(String url, String user, String password)
      throws DataSourceException, DatabaseException {
    super(url, user, password);
    initUsersTable();
    initCoordinatesTable();
    initLocationsTable();
    initPersonsTable();
    initWorkersTable();
    logger.debug(() -> "All tables were initialized.");
  }

  /**
   * Initializes users table. Creates new one if not exists.
   *
   * @throws DatabaseException - if initialization is incorrect
   */
  private void initUsersTable() throws DataSourceException {
    PreparedStatement preparedStatement =
        getPrepareStatement(CREATE_IF_NOT_EXISTS_USERS_TABLE, Statement.NO_GENERATED_KEYS);

    try {
      preparedStatement.executeUpdate();
    } catch (SQLException e) {
      logger.fatal(
          "Cannot create users table, query: {}.",
          (Supplier<?>) () -> CREATE_IF_NOT_EXISTS_USERS_TABLE,
          e);
      throw new DatabaseException(INIT_USERS_TABLE_EXCEPTION, e);
    } finally {
      closePrepareStatement(preparedStatement);
    }

    logger.debug(() -> "Users table was initialized.");
  }

  /**
   * Initializes coordinates table. Creates new one if not exists.
   *
   * @throws DatabaseException - if initialization is incorrect
   */
  private void initCoordinatesTable() throws DataSourceException {
    PreparedStatement preparedStatement =
        getPrepareStatement(CREATE_IF_NOT_EXISTS_COORDINATES_TABLE, Statement.NO_GENERATED_KEYS);

    try {
      preparedStatement.executeUpdate();
    } catch (SQLException e) {
      logger.fatal(
          "Cannot initialize coordinates table, query: {}.",
          (Supplier<?>) () -> CREATE_IF_NOT_EXISTS_COORDINATES_TABLE,
          e);
      throw new DatabaseException(INIT_COORDINATES_TABLE_EXCEPTION, e);
    } finally {
      closePrepareStatement(preparedStatement);
    }

    logger.debug(() -> "CoordinatesDTO table was initialized.");
  }

  /**
   * Initializes persons table. Creates new one if not exists.
   *
   * @throws DatabaseException - if initialization is incorrect
   */
  private void initPersonsTable() throws DataSourceException {
    PreparedStatement preparedStatement =
        getPrepareStatement(CREATE_IF_NOT_EXISTS_PERSONS_TABLE, Statement.NO_GENERATED_KEYS);

    try {
      preparedStatement.executeUpdate();
    } catch (SQLException e) {
      logger.fatal(
          "Cannot initialize persons table, query: {}.",
          (Supplier<?>) () -> CREATE_IF_NOT_EXISTS_PERSONS_TABLE,
          e);
      throw new DatabaseException(INIT_PERSONS_TABLE_EXCEPTION, e);
    } finally {
      closePrepareStatement(preparedStatement);
    }

    logger.debug(() -> "Persons table was initialized.");
  }

  /**
   * Initializes locations table. Creates new one if not exists.
   *
   * @throws DatabaseException - if initialization is incorrect
   */
  private void initLocationsTable() throws DataSourceException {
    PreparedStatement preparedStatement =
        getPrepareStatement(CREATE_IF_NOT_EXISTS_LOCATIONS_TABLE, Statement.NO_GENERATED_KEYS);

    try {
      preparedStatement.executeUpdate();
    } catch (SQLException e) {
      logger.fatal(
          "Cannot initialize locations table, query: {}.",
          (Supplier<?>) () -> CREATE_IF_NOT_EXISTS_LOCATIONS_TABLE,
          e);
      throw new DatabaseException(INIT_LOCATIONS_TABLE_EXCEPTION, e);
    } finally {
      closePrepareStatement(preparedStatement);
    }

    logger.debug(() -> "Locations table was initialized.");
  }

  /**
   * Initializes workers table. Creates new one if not exists.
   *
   * @throws DatabaseException - if initialization is incorrect
   */
  private void initWorkersTable() throws DataSourceException {
    PreparedStatement preparedStatement =
        getPrepareStatement(CREATE_IF_NOT_EXISTS_WORKERS_TABLE, Statement.NO_GENERATED_KEYS);

    try {
      preparedStatement.executeUpdate();
    } catch (SQLException e) {
      logger.fatal(
          "Cannot create workers table, query: {}.",
          (Supplier<?>) () -> CREATE_IF_NOT_EXISTS_WORKERS_TABLE,
          e);
      throw new DatabaseException(INIT_WORKERS_TABLE_EXCEPTION, e);
    } finally {
      closePrepareStatement(preparedStatement);
    }

    logger.debug(() -> "Workers table was initialized.");
  }
}
