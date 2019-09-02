package sample;

import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;

import java.net.*;
import java.io.*;

public class Controller {
    public Button start;
    public Label resultLabel;
    public TextArea Output;
    public void Clicked(ActionEvent event) {
        String result ="接続されました";
        resultLabel.setText(result);
        try {
            ServerSocket ServerSocket = new ServerSocket(5000);
            while(true) {
                Socket socket = ServerSocket.accept();
                MyThread thread= new MyThread(socket);
                thread.start();

            }
        }catch(IOException e){
            System.out.println(e);
        }

    }
    public void Finish(ActionEvent event) {
        System.exit(0);
    }

    class MyThread extends Thread {
        private Socket socket;

        public MyThread(Socket socket){
            this.socket = socket;
        }

        public void run() {
            try {
                //データ受信
                InputStream cInput = socket.getInputStream();
                InputStreamReader cInputReader = new InputStreamReader(cInput);
                BufferedReader cBreader = new BufferedReader(cInputReader);
                String sData = cBreader.readLine();
                Output.setText(sData + "\n"+ Output.getText() );
                System.out.println("受信データ:" + sData);

                //データ(受信文+送った)を送信
                OutputStream cOut = socket.getOutputStream();
                OutputStreamWriter cOutwriter = new OutputStreamWriter(cOut);
                cOutwriter.write(sData + "送った");
                cOutwriter.flush();
                socket.close();
                System.out.println(Output.getText());
            }catch(IOException e){
                System.out.println(e);
            }
        }

    }

}

