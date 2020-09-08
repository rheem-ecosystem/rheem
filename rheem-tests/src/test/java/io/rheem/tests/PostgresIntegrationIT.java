package io.rheem.tests;

import org.junit.Ignore;



/**
 * Test the Postgres integration with Rheem.
 */
@Ignore("Requires specific PostgreSQL installation.")
public class PostgresIntegrationIT {
//TODO: validate this code correspond to the integration test if not create the one need it
//
//    private static final PostgresPlatform pg = PostgresPlatform.getInstance();
//
//    @BeforeClass
//    public static void setup() {
//
//        Statement stmt = null;
//
//        try {
//            Connection connection = pg.getConnection();
//            stmt = connection.createStatement();
//
//            String sql = "DROP TABLE IF EXISTS EMPLOYEE;";
//            stmt.executeUpdate(sql);
//
//            sql = "CREATE TABLE EMPLOYEE (ID INTEGER, SALARY DECIMAL);";
//            stmt.executeUpdate(sql);
//
//            sql = "INSERT INTO EMPLOYEE (ID, SALARY) VALUES (1, 800.5), (2, 1100),(3, 3000),(4, 5000.8);";
//            stmt.executeUpdate(sql);
//
//            stmt.close();
//
//        } catch (SQLException e) {
//            throw new RheemException(e);
//        }
//    }
//
//    @AfterClass
//    public static void tearDown() {
//        Statement stmt = null;
//
//        try {
//            Connection connection = pg.getConnection();
//            String sql = "DROP TABLE IF EXISTS EMPLOYEE;";
//            stmt = connection.createStatement();
//            stmt.executeUpdate(sql);
//            stmt.close();
//
//        } catch (SQLException e) {
//            throw new RheemException(e);
//        }
//    }

}
