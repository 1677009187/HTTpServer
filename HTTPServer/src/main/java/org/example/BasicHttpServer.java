package org.example;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.*;

public class BasicHttpServer {
    //单线程执行器，用来启动http服务器
    private static ExecutorService bootStrapExecutor= Executors.newSingleThreadExecutor();
    //线程池，用来处理客户端http请求
    private static ExecutorService taskExecutor;
    //监听端口号
    private static int port=8999;
    public static void startHttpServer(){
        int nThreads=Runtime.getRuntime().availableProcessors();
        taskExecutor=new ThreadPoolExecutor(nThreads,nThreads,0L,TimeUnit.MILLISECONDS,new LinkedBlockingDeque<>(100),new ThreadPoolExecutor.DiscardPolicy());
        while(true){
            try {
                ServerSocket serverSocket=new ServerSocket(port);
                bootStrapExecutor.submit(new ServerThread(serverSocket));
                break;
            }catch (Exception e){
                try {
                    TimeUnit.SECONDS.sleep(10);
                }catch (InterruptedException ie){
                    Thread.currentThread().interrupt();
                }
            }
        }
        bootStrapExecutor.shutdown();
    }
    public static class ServerThread implements Runnable{
        private ServerSocket serverSocket;
        public ServerThread(ServerSocket serverSocket){
            this.serverSocket=serverSocket;
        }

        @Override
        public void run() {
            while(true){
                try {
                    Socket socket=serverSocket.accept();
                    HttpTask httpTask=new HttpTask(socket);
                    taskExecutor.submit(httpTask);
                } catch (IOException e) {
                    e.printStackTrace();
                    try {
                        TimeUnit.SECONDS.sleep(1);
                    }catch (InterruptedException ie){
                        Thread.currentThread().interrupt();
                    }
                }
            }
        }
    }
}
