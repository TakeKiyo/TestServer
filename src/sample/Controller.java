package sample;

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
    public List<String> bcc_byte = new ArrayList<String>();
    public String[] bcc_string = {"0","1","2","3","4","5","6","7","8","9","A","B","C","D","E","F"};
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
    }

    public void Finish(ActionEvent event) {
        System.exit(0);
    }
    public String make_bcc(String data){
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
                int recvSize;
                int recvMsgSize; // 受信メッセージサイズ
                while(this.isActive) {
                    System.out.println("whileすたーと");
                    byte[] bytenum = new byte[21]; // 受信バッファ
                    //データ受信
                    int totalBytesRcvd = 0;
                    in.read(bytenum,0,21);
                    bytenum = Arrays.copyOfRange(bytenum,1,21);
                    String first = new String(bytenum);
                    int id = Integer.parseInt(first.substring(4,6));
                    int rest_byte = 1;
                    if (id== 52){
                        rest_byte = 41;
                    }
                    byte[] receiveBuf = new byte[rest_byte];
                    totalBytesRcvd = 0;
                    while(true){
                        recvMsgSize = in.read(receiveBuf);
                        totalBytesRcvd += recvMsgSize;
                        if (totalBytesRcvd == rest_byte){
                            break;
                        }
                    }
                    byte[] receiveBCC = Arrays.copyOfRange(receiveBuf,38,40);
                    receiveBuf = Arrays.copyOfRange(receiveBuf,0,38);
                    String second = new String(receiveBuf);
                    String str = first + second;
                    String receivedBCC = new String(receiveBCC);
                    System.out.println("受け取ったbcc:"+receivedBCC);
                    System.out.println("calculatd bcc:"+ make_bcc(str));
                    System.out.println("got:"+str);
                    String txt = Output.getText();
                    Output.setText(txt + "\n" + str);
                    try {
                        Class.forName("com.mysql.cj.jdbc.Driver");
                        Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
                        Statement statement = connection.createStatement();
                        String sql = String.format("INSERT INTO value_table VALUES ('%s',0);",str);
                        statement.executeUpdate(sql);
                        statement.close();
                        connection.close();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
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
        public void destroyThread(){
            this.isActive = false;
        }
        public String second_sql = "";
        public String bc_data = "";
        public void run(){
            while (this.isActive){
                try{
                        Class.forName("com.mysql.cj.jdbc.Driver");
                        Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
                        Statement statement = connection.createStatement();
                        String sql = "select * from test.value_table where flag = 0";
                        ResultSet cResult = statement.executeQuery(sql);
                        while(cResult.next()){
                            String value = cResult.getString("value");
                            System.out.println(value);
                            int id = Integer.parseInt(value.substring(4,6));
                            String final_result;
                            if (id == 52){
                                bc_data = value.substring(28,42);
                                System.out.println(bc_data);
                                second_sql = String.format("select * from test.ID_02 where bc_data = '%s';",bc_data);
                                System.out.println(second_sql);
                                Statement second_statement = connection.createStatement();
                                ResultSet second_result = second_statement.executeQuery(second_sql);
                                while(second_result.next()){
                                    String response_code = second_result.getString("response_code");
                                    System.out.println(response_code);
                                    Calendar cl = Calendar.getInstance();
                                    SimpleDateFormat sdf = new SimpleDateFormat("HHmmss");
                                    final_result = value.substring(0,4)+"02"+value.substring(6,8)+sdf.format(cl.getTime()) + value.substring(14,28) + response_code;
                                    //add STX,ETX
                                    byte[] first = new byte[1];
                                    first[0] = 0x02;
                                    String fi_st = new String(first);
                                    byte[] last = new byte[1];
                                    first[0] = 0x03;
                                    String la_st = new String(last);

                                    final_result = fi_st + final_result;
                                    final_result = final_result + "11" + la_st; //11はBCC

                                    System.out.println(final_result);
                                    byte[] data = final_result.getBytes();
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
                                                destroyThread();
                                                second_result.close();
                                                second_statement.close();
                                                cResult.close();
                                                statement.close();
                                                connection.close();
                                            }
                                        }
                                    }
//                                    out.write(data);
                                    String updatesql = String.format("update test.value_table set flag = 1 where value = '%s'",value);
                                    second_statement.executeUpdate(updatesql);
                                }
                                second_result.close();
                                second_statement.close();
                            }
                        }
                        cResult.close();
                        statement.close();
                        connection.close();
                        Thread.sleep(10000);
                }catch (SQLException | ClassNotFoundException | InterruptedException e) {
                }

            }


        }
    }

}


