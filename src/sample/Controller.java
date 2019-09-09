package sample;

import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;


import java.net.*;
import java.io.*;
import java.sql.*;
import java.util.Objects;


public class Controller {
    public Button start;
    public Label resultLabel;
    public TextArea Output;
    public Statement statement = null;
    public Connection connection = null;
    public void Clicked(ActionEvent event) {
        String txt ="接続されました";
        resultLabel.setText(txt);
        StartThread StartThread = new StartThread();
        StartThread.start();
    }

    public void Finish(ActionEvent event) {
        System.exit(0);
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
        public int BUFSIZE = 32;

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
                    byte[] bytenum = new byte[1]; // 受信バッファ
                    //データ受信
                    int totalBytesRcvd = 0;
                    in.read(bytenum,0,1);
                    String aa = new String(bytenum);
                    int num = Integer.parseInt(aa);
                    System.out.println(num);
                    byte[] receiveBuf = new byte[num];
                    totalBytesRcvd = 0;
                    while(true){
                        recvMsgSize = in.read(receiveBuf);
                        totalBytesRcvd += recvMsgSize;
                        if (totalBytesRcvd == num){
                            break;
                        }
                    }
                    String str = new String(receiveBuf);
                    System.out.println(str);
                    String txt = Output.getText();
                    Output.setText(txt + "\n" + str);
                    try {
                        Class.forName("com.mysql.cj.jdbc.Driver");
                        Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
                        Statement statement = connection.createStatement();
                        String sql = String.format("INSERT INTO tabletest VALUES (%d,'%s',0);",num,str);
                        int result = statement.executeUpdate(sql);
                        System.out.println("結果１：" + result);
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
        int nummm;
        public void run(){
            byte[] senddata = "234".getBytes();
            while (this.isActive){
                try{
                        Class.forName("com.mysql.cj.jdbc.Driver");
                        Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
                        Statement statement = connection.createStatement();
                        String sql = "select * from test.tabletest where flag = 0";
                        ResultSet cResult = statement.executeQuery(sql);
                        while(cResult.next()){
                            int result_id = cResult.getInt("ID");
                            String result_val = cResult.getString("VALUE");
                            String combined = String.valueOf(result_id) + result_val;
                            byte[] data = combined.getBytes();
                            out.write(data);
                            String updatesql = String.format("update test.tabletest set flag = 1 where ID=  %d and VALUE = '%s'",result_id,result_val);
                            statement.executeUpdate(updatesql);
                        }
                        cResult.close();
                        statement.close();
                        connection.close();

                        Thread.sleep(10000);
                }catch (Exception e) {
                }

            }


        }
    }

}


