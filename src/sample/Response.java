package sample;

import com.mysql.cj.protocol.Resultset;

import java.io.IOException;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Calendar;
public class Response {
    protected Connection connection;
    protected String value;
    protected OutputStream out;
    public Statement second_statement;
    public Resultset second_result;
    public Resultset cResult;
    public Statement statement;
    public String final_result;
    public Calendar cl = Calendar.getInstance();

    public Response(String value,Connection con,OutputStream outputStream,Statement statement){
        this.value  = value;
        this.connection = con;
        this.out= outputStream;
        this.statement = statement;

    }

    public void update(String value) throws SQLException {
        String updatesql = String.format("update test.value_table set flag = 1 where value = '%s'", value);
        second_statement.executeUpdate(updatesql);
    }


    public void connect_db(){
        try{
            second_statement = connection.createStatement();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void add_stx_etx(){
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


    public String date(){
        SimpleDateFormat sdf = new SimpleDateFormat("HHmmss");
        return sdf.format(cl.getTime());

    }


}

