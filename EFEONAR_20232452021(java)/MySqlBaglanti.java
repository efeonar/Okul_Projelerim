import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;


public class MySqlBaglanti {
    private static final String URL = "jdbc:mysql://localhost:3306/ekofit";
    private static final String USER = "root";
    private static final String PASSWORD = "4545";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);



    }
}
