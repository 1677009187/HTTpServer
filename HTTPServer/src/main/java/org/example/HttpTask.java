package org.example;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;

public class HttpTask implements Runnable{
    private Socket socket;
    public HttpTask(Socket socket)
    {
        this.socket=socket;
    }

    @Override
    public void run() {
        if(socket==null){
            throw new IllegalArgumentException("socket can't be null.");
        }
        try{
            InputStream in=socket.getInputStream();
            HttpMessageParser.Request request=HttpMessageParser.parse2request(in);
            OutputStream out=socket.getOutputStream();
            PrintWriter writer=new PrintWriter(out);
            try {
                String responseString=HttpMessageParser.buildResponse(request,null);
                writer.println(responseString);
            }catch (Exception e){
                String responseString=HttpMessageParser.buildResponse(request,e.toString());
                writer.println(responseString);
            }
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try{
                socket.close();
            }catch (IOException e){
                e.printStackTrace();
            }
        }
    }
}
