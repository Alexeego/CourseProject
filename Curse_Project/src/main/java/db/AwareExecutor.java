package db;


import dao.UserDAO;
import exceptions.GenericDAOException;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import user.User;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collector;

/**
 * Created by Alexey on 12.11.2016.
 */
public class AwareExecutor {

    private static final Logger LOG = LoggerFactory.getLogger(AwareExecutor.class);

    /*
    *   Configuration Hibernate and SessionFactory
     */

    private static SessionFactory sessionFactory;

    public static void configurationHibernate() {
        //loads configuration and mappings
        Configuration configuration = new Configuration().configure();
        StandardServiceRegistryBuilder registry = new StandardServiceRegistryBuilder();
        registry.applySettings(configuration.getProperties());
        ServiceRegistry serviceRegistry = registry.build();

        // builds a session factory from the service registry
        sessionFactory = configuration.buildSessionFactory(serviceRegistry);
    }


    /*
    *   Manage DataBase
     */

    private static final String DB_URL = "jdbc:mysql://";
    // Read system properties from file
    public static Properties getEnvironmentProperties() throws IOException {
        Properties properties = new Properties();
        properties.loadFromXML(AwareExecutor.class.getClassLoader().getResourceAsStream("environment.xml"));
        return properties;
    }
    // Read sql script from file
    private static List<String> readDBCreateScript(String dbName) throws IOException {
        LinkedList<String> listScript = new LinkedList<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                AwareExecutor.class.getClassLoader().getResourceAsStream("db/db-create-script.sql"), Charset.forName("UTF-8")))) {
            while (reader.ready())
                listScript.add(reader.readLine());
        }

        StringBuilder builder = new StringBuilder();
        return listScript.stream()
                .collect(Collector.of(LinkedList<String>::new,
                        (l, s) -> {
                            s = s.replaceAll("%DATABASENAME%", dbName);
                            if (s.contains(";")) {
                                Arrays.stream(s.split(";")).forEachOrdered(line -> {
                                    builder.append(line).append(";");
                                    l.add(builder.toString().trim());
                                    builder.delete(0, builder.length());
                                });
                            } else builder.append(s).append(' ');
                        },
                        (l1, l2) -> {
                            l1.addAll(l2);
                            return l1;
                        }));
    }

    // Create DataBase
    private static void createDB() throws GenericDAOException {
        try {
            Properties properties = getEnvironmentProperties();
            String host = properties.getProperty("db_host");
            int port = Integer.parseInt(properties.getProperty("db_port"));
            String user = properties.getProperty("db_user");
            String password = properties.getProperty("db_password");
            String useSSL = properties.getProperty("db_useSSL");
            String sqlUrl = DB_URL + host + ":" + port + "/?useSSL=" + useSSL;
            String dbName = properties.getProperty("db_name");

            DriverManager.registerDriver(new com.mysql.jdbc.Driver());

            List<String> listScript = readDBCreateScript(dbName);

            try (Connection connection = DriverManager.getConnection(sqlUrl, user, password);
                 Statement statement = connection.createStatement()) {
                statement.executeUpdate(listScript.get(0));
                LOG.info("DataBase '" + dbName + "' created");
            }
        } catch (SQLException | IOException e) {
            throw new GenericDAOException(e);
        }

    }


    // Filling DataBase
    private static void fillingDB() {
        try {
            UserDAO userDAO = new UserDAO();

            User systemAdmin = new User("Alexey", "alexeego", true);
            if (!userDAO.findByField(systemAdmin.getName()).isPresent())
                userDAO.insert(systemAdmin);

            User user = new User("a", "a", true);
            if (!userDAO.findByField(user.getName()).isPresent())
                userDAO.insert(user);

            user = new User("ale", "a");
            user.setAccess((byte) 1);
            if (!userDAO.findByField(user.getName()).isPresent())
                userDAO.insert(user);

            user = new User("w", "a");
            if (!userDAO.findByField(user.getName()).isPresent())
                userDAO.insert(user);

            user = new User("q", "a", false);
            if (!userDAO.findByField(user.getName()).isPresent())
                userDAO.insert(user);

        } catch (Exception ignored) {
        }
    }
    /*
    *   Method for primary initialization DataBase:
    *   - Create DB if not exists
    *   - Configuration Hibernate
    *   - Filling primary data
     */

    public static void initializationDataBase() throws GenericDAOException {
        createDB();
        configurationHibernate();
        fillingDB();
    }


    /*
    *   Method for executing task from DAO
     */

    public <T> T submit(ConnectionTask<T> task) {
        Session session = sessionFactory.openSession();
        try {
            session.beginTransaction();
            return task.execute(session);
        } catch (HibernateException e) {
            if (session.getTransaction() != null) session.getTransaction().rollback();
            throw e;
        } finally {
            try {
                session.getTransaction().commit();
                session.close();
            } catch (Exception ignored) {
            }
        }
    }

}
