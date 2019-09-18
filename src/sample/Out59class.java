package sample;


import java.io.OutputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Out59class extends Response {
    public Out59class(String value, Connection connection, OutputStream outputStream, Statement statement) throws SQLException {
        super(value, connection, outputStream,statement);
    }
    public String preConnection(){
        String retrieval_request_num = value.substring(20, 32);
        String second_sql = String.format("select * from test.ID_09 where retrieval_request_num = '%s';", retrieval_request_num);
        return second_sql;
    }
    public void update_09(String value) throws SQLException {
        Statement id09_statement;
        id09_statement = connection.createStatement();
        String updatesql = String.format("update test.ID_09 set read_flag = 4 where retrieval_request_num = '%s'", value);
        id09_statement.executeUpdate(updatesql);
        id09_statement.close();
    }
    public void postConnection(ResultSet second_result) throws SQLException, InterruptedException  {
        while (second_result.next()) {
            result_cnt += 1;
            String retrieval_request_num = second_result.getString("retrieval_request_num");
            String read_flag = second_result.getString("read_flag");
            if (read_flag.equals("1")){
                update_09(retrieval_request_num);
            }
        }
        if (result_cnt == 0){
        }
        final_result = String.format("%04d", Controller.seq_num_send) + "09" + value.substring(6, 8) + date() + value.substring(14, 32);
    }

}