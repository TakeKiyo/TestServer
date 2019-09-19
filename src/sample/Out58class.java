package sample;

import java.io.OutputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


public class Out58class extends Response{
    public Out58class(String value, Connection connection, OutputStream outputStream, Statement statement) throws SQLException {
        super(value, connection, outputStream,statement);
    }

    public String preConnection(){
        return "";
    }
    public void postConnection(ResultSet second_result) throws SQLException, InterruptedException  {
        update(value);
    }
    public void update(String value) throws SQLException {
        String updatesql = String.format("update test.value_table set flag = 1 where value = '%s'", value);
        second_statement.executeUpdate(updatesql);
    }

}
