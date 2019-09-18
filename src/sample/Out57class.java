package sample;


import java.io.OutputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Out57class extends Response {
    public Out57class(String value, Connection connection, OutputStream outputStream, Statement statement) throws SQLException {
        super(value, connection, outputStream,statement);
    }

    public String preConnection(){
        String bc_data = value.substring(28, 42);
        String second_sql = String.format("select * from test.ID_07 where bc_data = '%s';", bc_data);
        return second_sql;
    }
    public void postConnection(ResultSet second_result) throws SQLException, InterruptedException  {
        String aisle_distribution_key = "";
        String priority_1 = "";
        String priority_2 = "";
        String response_code = "";
        String control_info = "";
        while (second_result.next()) {
            result_cnt += 1;
            aisle_distribution_key = second_result.getString("aisle_distribution_key");
            priority_1 = second_result.getString("priority_1");
            priority_2 = second_result.getString("priority_2");
            response_code = second_result.getString("response_code");
            control_info = second_result.getString("control_info");
        }
        if (result_cnt == 0){
            aisle_distribution_key = "                    ";
            priority_1 = "   ";
            priority_2 = "   ";
            response_code = "99999999";
            control_info = "                              ";
        }
        final_result = String.format("%04d", Controller.seq_num_send) + "07" + value.substring(6, 8) + date() + value.substring(14, 42) + aisle_distribution_key + priority_1 + priority_2 +  response_code + control_info;
    }

}
