package sample;

import com.mysql.cj.protocol.Resultset;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;


import java.net.*;
import java.io.*;
import java.sql.*;
import java.util.Arrays;
import java.util.Objects;
import java.util.Calendar;
import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.List;


public class Controller {
    public Button start;
    public Label resultLabel;
    public TextArea Output;
    public Statement statement = null;
    public Connection connection = null;
    public static List<String> bcc_byte = new ArrayList<String>();
    public List<String> id_list = new ArrayList<String>();
    public static String[] bcc_string = {"0","1","2","3","4","5","6","7","8","9","A","B","C","D","E","F"};
    public int[] length_list = {61,65,146,76,376,51,96,43,105,39,51,138};
    public static int seq_num_send;
    public static int seq_num_get;
    public void Clicked(ActionEvent event) {
        String txt ="接続されました";
        resultLabel.setText(txt);
        StartThread StartThread = new StartThread();
        StartThread.start();
        bcc_byte.add("0000");
        bcc_byte.add("0001");
        bcc_byte.add("0010");
        bcc_byte.add("0011");
        bcc_byte.add("0100");
        bcc_byte.add("0101");
        bcc_byte.add("0110");
        bcc_byte.add("0111");
        bcc_byte.add("1000");
        bcc_byte.add("1001");
        bcc_byte.add("1010");
        bcc_byte.add("1011");
        bcc_byte.add("1100");
        bcc_byte.add("1101");
        bcc_byte.add("1110");
        bcc_byte.add("1111");
        id_list.add("52");
        id_list.add("53");
        id_list.add("54");
        id_list.add("55");
        id_list.add("56");
        id_list.add("57");
        id_list.add("58");
        id_list.add("59");
        id_list.add("63");
        id_list.add("64");
        id_list.add("66");
        id_list.add("70");
    }


    public void Finish(ActionEvent event) {
        System.exit(0);
    }
    public static String make_bcc(String data){
        byte[] data_byte = data.getBytes();
        byte res = (byte) (data_byte[0] ^ data_byte[1]);
        for (int i=2;i<data_byte.length;i++){
            res = (byte)(res ^ data_byte[i]);
        }

        String str = Integer.toBinaryString(res);
        str = String.format("%8s", str).replace(' ', '0');
        String first = str.substring(0,4);
        String second = str.substring(4,8);
        int first_idx = bcc_byte.indexOf(first);
        int second_idx = bcc_byte.indexOf(second);
        first = bcc_string[first_idx];
        second = bcc_string[second_idx];
        return first+second;

    }

    public boolean check_id(String id){
        if (id_list.contains(id)) return true;
        else return false;
    }

    public boolean check_length(String id,int i){
        if (check_id(id)){
            int idx = id_list.indexOf(id);
            int expected_len = length_list[idx];
            if (i == expected_len) return true;
            else return false;
        }else {
            return false;
        }


    }

    public static void add_seq_send(){
        seq_num_send += 1;
        if (seq_num_send == 10000) seq_num_send = 1;
    }

    public void add_seq_get(){
        seq_num_get += 1;
        if (seq_num_get == 10000) seq_num_get = 1;
    }

    public boolean send_data(OutputStream out,byte[] data) throws InterruptedException {
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
                    return false;
                }
            }
        }
        return true;
    }

    public int deal_seq(String expected_number,String actual_number){
//        System.out.println(expected_number);
//        System.out.println(actual_number);
        if (expected_number.equals(actual_number))  {
            add_seq_get();
            System.out.println("SEQ:一致");
            return 1;
        } else if (Integer.parseInt(expected_number) - Integer.parseInt(actual_number) == 1) {
            System.out.println("SEQ:前回と一致");
            return 2;
        } else if (expected_number.equals("0001") && actual_number.equals("9999")) {
            System.out.println("SEQ:前回と一致");
            return 2;
        } else if(actual_number.equals("0000")) {
            add_seq_get();
            System.out.println("SEQ:0000");
            return 3;
        } else{
            System.out.println("SEQ:else");
            add_seq_get();
            return 4;
        }
    }


    class StartThread extends Thread {
        private Socket socket = null;
        private MyThread thread = null;
        public void run(){
            try {
                ServerSocket ServerSocket = new ServerSocket(5000);
                while(true) {
                    socket = ServerSocket.accept();
                    if (Objects.nonNull(thread)){
                        thread.destroyThread();
                    }
                    seq_num_send=0;
                    seq_num_get =0;
                    thread= new MyThread(socket);
                    thread.start();
                }
            }catch(IOException e){
                System.out.println(e);
            }

        }
    }
    class MyThread extends Thread {
        private Socket socket;
        private boolean isActive;
        public MyThread(Socket socket){
            this.socket = socket;
            this.isActive = true;
        }

        public void destroyThread(){
            this.isActive = false;
        }
        public InThread inThread = null;
        public OutThread outThread =null;
        public void run() {
            try {
                InputStream in = socket.getInputStream();
                OutputStream out = socket.getOutputStream();
                inThread = new InThread(in);
                outThread = new OutThread(out);
                inThread.start();
                outThread.start();
            }catch (IOException e) {
                System.out.println(e);
            }
            while(this.isActive) {
            }
            inThread.destroyThread();
            outThread.destroyThread();
        }

    }
    class InThread extends Thread{
        private InputStream in;
        private boolean isActive;
        public InThread(InputStream inputStream){

            this.in = inputStream;
            this.isActive = true;
        }
        public void destroyThread(){
            this.isActive = false;
        }
        static final String URL = "jdbc:mysql://localhost:3306/test";
        static final String USERNAME = "root";
        static final String PASSWORD = "";
        public void run(){
            try {
                int recvMsgSize; // 受信メッセージサイズ
                while(this.isActive) {
                    System.out.println("InThread:whileすたーと");
//                     stx,etxで判別
                    byte[] msg = new byte[1024];
                    int i =0;
                    in.read(msg,0,1);
                    if (msg[0] == 0x02) {
                        i = 1;
                        while (true) {
                            in.read(msg, i, 1);
                            if (msg[i] == 0x03) {
                                break;
                            }
                            i++;
                        }

                        byte[] receiveBCC = Arrays.copyOfRange(msg, i - 2, i);
                        msg = Arrays.copyOfRange(msg, 1, i - 2);
                        String receivedBCC = new String(receiveBCC);
                        String str = new String(msg);
                        String calculated_bcc = make_bcc(str);
                        String seq_flag = " ";
                        String bcc_flag = " ";
                        String id_flag = " ";
                        String len_flag = " ";
                        int id = Integer.parseInt(str.substring(4, 6));
                        String seq = new String(Arrays.copyOfRange(msg, 0, 4));
                        int check_seq = deal_seq(String.format("%04d", seq_num_get), seq);
                        if (!(receivedBCC.equals(calculated_bcc))) {
                            bcc_flag = "N";
                        }
                        if ((check_seq == 2) || (check_seq == 4)) {
                            seq_flag = "N";
                        }
                        if (!check_id(String.valueOf(id))){
                            id_flag = "N";
                        }
                        if (!(check_length(String.valueOf(id),i))){
                            len_flag = "N";
                        }
                        System.out.println("bcc_check:"+ String.valueOf(receivedBCC.equals(calculated_bcc)));
                        System.out.println("id_check:" + String.valueOf(check_id(String.valueOf(id))));
                        System.out.println("len_check:"+ String.valueOf(check_length(String.valueOf(id),i)));
                        System.out.println("id:" + String.valueOf(id));
                        System.out.println("got:" + str);
                        Calendar cl = Calendar.getInstance();
                        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
                        String txt = Output.getText();
                        Output.setText(txt + "\n" + sdf.format(cl.getTime()) + " " + seq_flag + " " + bcc_flag + " " + " " + id_flag+ " " + len_flag+ str);
                        try {
                            Class.forName("com.mysql.cj.jdbc.Driver");
                            Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
                            Statement statement = connection.createStatement();
                            if (check_seq != 2 && receivedBCC.equals(calculated_bcc) && check_id(String.valueOf(id)) && check_length(String.valueOf(id),i))
                            {
                                String sql = String.format("INSERT INTO value_table VALUES ('%s',0);",str);
                                statement.executeUpdate(sql);
                                statement.close();
                                connection.close();
                            }
                        } catch (SQLException e) {
                            e.printStackTrace();
                        } catch (Exception e) {
                        }
                        }
                }
            } catch(IOException e){
                System.out.println(e);
            }


        }
    }

    class OutThread extends Thread{
        private OutputStream out;
        private boolean isActive;
        static final String URL = "jdbc:mysql://localhost:3306/test";
        static final String USERNAME = "root";
        static final String PASSWORD = "";
        public OutThread(OutputStream outputStream){

            this.out = outputStream;
            this.isActive = true;
        }
        public  void destroyThread(){
            this.isActive = false;
        }
        public String second_sql = "";
        public String bc_data = "";
        public Calendar cl = Calendar.getInstance();
        public ResultSet cResult;
        public void run(){
            while (this.isActive){
                try{
                        Class.forName("com.mysql.cj.jdbc.Driver");
                        Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
                        Statement statement = connection.createStatement();
                        String sql = "select * from test.value_table where flag = 0";
                        cResult = statement.executeQuery(sql);
                        while(cResult.next()){
                            String value = cResult.getString("value");
                            System.out.println(value);
                            int id = Integer.parseInt(value.substring(4,6));
                            System.out.println("id:"+ String.valueOf(id));
                            String final_result;
                            if (id == 52){
                                Out52class out52class = new Out52class(value,connection,out,statement);
                                final_result = out52class.execute_query();
                                byte[] data= final_result.getBytes();
                                if (!(send_data(out, data))) {
                                    destroyThread();
                                    out52class.close_second_connection();
                                    cResult.close();
                                    statement.close();
                                    connection.close();
                                }
                                out52class.update(value);
                                out52class.close_second_connection();
                                }
                            }
                    cResult.close();
                    statement.close();
                    connection.close();
                        } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (SQLException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
                try{
                Thread.sleep(10000);
                }catch (InterruptedException e) {
                }

            }


        }
    }

}


