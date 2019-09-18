package sample;


import java.io.OutputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Out54class extends Response {
    public Out54class(String value, Connection connection, OutputStream outputStream, Statement statement) throws SQLException {
        super(value, connection, outputStream,statement);
    }

    public String preConnection(){
        String bc_data = value.substring(48, 62);
        String second_sql = String.format("select * from test.ID_04 where bc_data = '%s';", bc_data);
        return second_sql;
    }
    public void postConnection(ResultSet second_result) throws SQLException, InterruptedException  {
        String judgment_flag = "";
        String response_code = "";
        String control_info = "";
        while (second_result.next()) {
            result_cnt += 1;
            judgment_flag = second_result.getString("judgment_flag");
            response_code = second_result.getString("response_code");
            control_info = second_result.getString("control_info");
        }
        if (result_cnt == 0){
            judgment_flag = " ";
            response_code = "99999999";
            control_info = "                              ";
        }
        final_result = String.format("%04d", Controller.seq_num_send) + "04" + value.substring(6, 8) + date() + value.substring(14, 28) + value.substring(34, 48) + value.substring(28, 30) + "    " + judgment_flag +  response_code + control_info;
    }

}
