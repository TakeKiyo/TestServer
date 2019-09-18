package sample;


import java.io.OutputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Out63class extends Response {
    public Out63class(String value, Connection connection, OutputStream outputStream, Statement statement) throws SQLException {
        super(value, connection, outputStream,statement);
    }
    public String preConnection(){
        String bc_data = value.substring(34, 48);
        String second_sql = String.format("select * from test.ID_13 where bc_data = '%s';", bc_data);
        return second_sql;
    }
    public void postConnection(ResultSet second_result) throws SQLException, InterruptedException  {
        String response_code = "";
        while (second_result.next()) {
            result_cnt += 1;
            response_code = second_result.getString("response_code");
        }
        if (result_cnt == 0){
            response_code = "00000000";
        }
        final_result = String.format("%04d", Controller.seq_num_send) + "13" + value.substring(6, 8) + date() + value.substring(14, 28) + value.substring(30, 34) + response_code;final_result = String.format("%04d", Controller.seq_num_send) + "13" + value.substring(6, 8) + date() + value.substring(14, 28) + value.substring(30, 34) + response_code;
    }


}