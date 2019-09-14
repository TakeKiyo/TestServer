package sample;


import java.io.IOException;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Out52class extends Response {
    public Out52class(String value, Connection connection, OutputStream outputStream, Statement statement) throws SQLException {
        super(value, connection, outputStream,statement);
    }
    public ResultSet second_result;
    public void close_second_connection() throws SQLException {
        second_result.close();
        second_statement.close();

    }

    public void send_data() throws InterruptedException, SQLException {
        byte[] data= final_result.getBytes();
        try{
            out.write(data);
        }catch(IOException e){
            Thread.sleep(10000);
            try{
                out.write(data);
            }catch(IOException e2){
                Thread.sleep(10000);
                try{
                    out.write(data);
                }catch(IOException e3){
                    close_second_connection();
                    Controller.OutThread.closeResultset((ResultSet) cResult);
                    statement.close();
                    connection.close();
                }
            }
        }

    }

    public void execute_query() throws SQLException, InterruptedException {
        String bc_data = value.substring(28, 42);
        String second_sql = String.format("select * from test.ID_02 where bc_data = '%s';", bc_data);
        System.out.println(second_sql);
        connect_db();
        second_result = second_statement.executeQuery(second_sql);
        int result_cnt = 0;
        while (second_result.next()) {
            result_cnt += 1;
            String response_code = second_result.getString("response_code");
            final_result = String.format("%04d", Controller.seq_num_send) + "02" + value.substring(6, 8) + date() + value.substring(14, 28) + response_code;
            Controller.add_seq_send();
            //add STX,ETX
            add_stx_etx();
        }
        if (result_cnt == 0){
            String response_code = "00000000";
            final_result = String.format("%04d", Controller.seq_num_send) + "02" + value.substring(6, 8) + date() + value.substring(14, 28) + response_code;
            Controller.add_seq_send();
            add_stx_etx();
        }
        send_data();
        update(value);
        close_second_connection();

    }


}
