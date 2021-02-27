package me.uwu.haxpoll.proxy;

import me.uwu.haxpoll.HaxPoll;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class Checker {

    public static int TIMEOUT = 10000;
    public static int COUNT = 0;

    public static List<String> working = new LinkedList<>();

    public static void run (String proxies, HaxPoll t) throws IOException {
        TIMEOUT = 10000;

        List<String> lines = new ArrayList<>(Arrays.asList(proxies.split("\n")));

        List<Thread> lizrt = new ArrayList<>();

        for (String l : lines){
            String[] oof = l.split(":");
            Runnable r = new CheckThread(oof[0], Integer.parseInt(oof[1]));
            Thread th = new Thread(r);
            th.start();
            lizrt.add(th);
        }

        Thread waitingThread = new Thread(() -> {
            long lastMS = System.currentTimeMillis();
            int timeout = 0;
            int threshold = 0;
            while(true){
                long curr = System.currentTimeMillis();
                if(curr - lastMS > 20 * 1000){
                    lastMS = curr;
                    int last = timeout;
                    timeout = Checker.COUNT;
                    if(last == timeout){
                        if(threshold++ > 2){
                            lizrt.forEach(Thread::interrupt);
                            break;
                        }
                    }
                    System.out.println(Checker.COUNT + " euh " + lines.size());
                }
                if(Checker.COUNT == lines.size() - 1){
                    break;
                }
            }
            t.start();
        });
        waitingThread.start();
    }
}
