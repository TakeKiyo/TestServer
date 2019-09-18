package sample;


import java.io.OutputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Out55class extends Response {
    public Out55class(String value, Connection connection, OutputStream outputStream, Statement statement) throws SQLException {
        super(value, connection, outputStream,statement);
    }

    public String preConnection(){
        String pr_num = value.substring(20, 34);
        String second_sql = String.format("select * from test.ID_05 where pr_num = '%s';", pr_num);
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
        final_result = String.format("%04d", Controller.seq_num_send) + "05" + value.substring(6, 8) + date() + value.substring(14, 34) + value.substring(37, 39) +"    " + judgment_flag +  response_code + control_info;
    }

}
