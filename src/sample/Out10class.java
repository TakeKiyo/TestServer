package sample;

import java.io.OutputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Out10class extends Response{
    private Statement id10_statement;
    private ResultSet id10_result;
    public Statement id10_second_statement;
    public Out10class(String value,Connection connection, OutputStream outputStream, Statement statement){
        super(value,connection,outputStream,statement);
    }

    public String preConnection(){
        try{
            id10_statement = connection.createStatement();
            String id10_sql = "select * from test.ID_10 where read_flag = 1 limit 30;";
            id10_result = id10_statement.executeQuery(id10_sql);
            id10_second_statement = connection.createStatement();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "";
    }

    public void close_connecition() throws SQLException {
        id10_second_statement.close();
        id10_statement.close();
    }
    public void update_10(String station_num) throws SQLException {
        String updatesql = String.format("update test.ID_10 set read_flag = 4 where station_num = '%s'",station_num);
        id10_second_statement.executeUpdate(updatesql);
    }

    public void postConnection(ResultSet id10_result) throws SQLException, InterruptedException {
        int result_cnt = 0;
        String first = "";
        String second = "";
        while(id10_result.next()){
            result_cnt += 1;
            String station_num = id10_result.getString("station_num");
            String retrieval_limit_num = id10_result.getString("retrieval_limit_num");
            second += station_num;
            second += String.format("%02d",Integer.parseInt(retrieval_limit_num));
            update_10(station_num);
        }
        if (result_cnt > 0){
            first = String.format("%04d", Controller.seq_num_send) + "1000" + Response.date() + "000000" + String.format("%02d", result_cnt) + "01";
            final_result = first + second;
            System.out.println(final_result);
            Controller.add_seq_send();
            add_stx_etx();
            send_data();
        }
        close_connecition();

    }

    public void execute() throws SQLException, InterruptedException {
        preConnection();
        postConnection(id10_result);
    }


}
