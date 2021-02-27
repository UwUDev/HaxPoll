package me.uwu.haxpoll.proxy;

import org.apache.http.HttpHost;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;

import java.net.InetAddress;

public class CheckThread implements Runnable{
    private String ip;
    private int port;

    public CheckThread(String ip, int port){
        this.ip = ip;
        this.port = port;
    }

    public void run() {
        String separator = "\t\t";
        if(ip.length() < 12)
            separator = "\t\t\t";
        try {
            InetAddress addr=InetAddress.getByName(this.ip);
            if(addr.isReachable(Checker.TIMEOUT)){
                testSocket(this.ip, this.port);
            } else {
                Checker.COUNT++;
                System.out.println("Ip: " + this.ip + separator + "Port: " + this.port + "\t\tDead   \t\tN°" + Checker.COUNT);
            }
        } catch (Exception ignored){
            Checker.COUNT++;
            System.out.println("Ip: " + this.ip + separator + "Port: " + this.port + "\t\tDead   \t\tN°" + Checker.COUNT);
        }
    }

    public void testSocket(String ip, int port) {
        String separator = "\t\t";
        if(ip.length() < 12)
            separator = "\t\t\t";
        try {
            HttpClient client = new DefaultHttpClient();
            HttpGet get = new HttpGet("http://1.1.1.1/");
            HttpHost proxy = new HttpHost(ip, port);
            client.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);
            client.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, Checker.TIMEOUT);
            client.execute(get);
            /*System.out.println(response.getStatusLine());
            System.out.println(response.toString());
            System.out.println(ip + ":" + port + " @@ working");*/
            Checker.COUNT++;
            System.out.println("Ip: " + ip + separator + "Port: " + port + "\t\tWorking\t\tN°" + Checker.COUNT);
            Checker.working.add(ip + ":" + port);
        } catch (Exception ignored) {
            Checker.COUNT++;
            System.out.println("Ip: " + ip + separator + "Port: " + port + "\t\tDead   \t\tN°" + Checker.COUNT);
        }
    }
}
