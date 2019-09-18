package sample;

import com.mysql.cj.protocol.Resultset;

import java.io.IOException;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Calendar;

abstract class Response {
    public  String final_result;
    protected Connection connection;
    protected String value;
    protected OutputStream out;
    public Statement second_statement;
    public ResultSet second_result;
    public Resultset cResult;
    public Statement statement;
    public int result_cnt = 0;

    public Response(String value,Connection con,OutputStream outputStream,Statement statement){
        this.value  = value;
        this.connection = con;
        this.out= outputStream;
        this.statement = statement;

    }

    public ResultSet make_connection(String second_sql){
        try{
            second_statement = connection.createStatement();
            second_result = (ResultSet) second_statement.executeQuery(second_sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return (ResultSet) second_result;
    }

    public  void add_stx_etx(){
        String bcc = Controller.make_bcc(final_result);
        byte[] first = new byte[1];
        first[0] = 0x02;
        String fi_st = new String(first);
        byte[] last = new byte[1];
        first[0] = 0x03;
        String la_st = new String(last);
        final_result = fi_st + final_result;
        final_result = final_result + bcc + la_st;
    }


    public static String date(){
        Calendar cl = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("HHmmss");
        return sdf.format(cl.getTime());
    }


    public  void send_data() throws InterruptedException, SQLException {
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

    public void update(String value) throws SQLException {
        String updatesql = String.format("update test.value_table set flag = 1 where value = '%s'", value);
        second_statement.executeUpdate(updatesql);
    }

    public void close_second_connection() throws SQLException {
        second_result.close();
        second_statement.close();

    }

    public void execute() throws SQLException, InterruptedException {
        String second_sql = preConnection();
        second_result = make_connection(second_sql);
        postConnection(second_result);
        Controller.add_seq_send();
        add_stx_etx();
        send_data();
        update(value);
        close_second_connection();

    }

    abstract String preConnection();
    abstract void postConnection(ResultSet second_result) throws SQLException, InterruptedException;





}

