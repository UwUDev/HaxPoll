package me.uwu.haxpoll;

import javafx.application.Platform;

import java.util.LinkedList;
import java.util.List;

public abstract class HaxPoll extends Thread{

    public String url;
    public boolean cancel = false;
    public int target;
    public String boxSlot;
    public boolean secure;

    public List<Thread> threads = new LinkedList<>();

    @Override
    public void run() {
        int batchSize = 12;
        int count = 0;

        loop:
        while (!cancel && target > count) {
            int next = Math.min(target - count, batchSize);

            for (int i = 1; i <= next; i++) {
                Thread voteThread = new VoteThread(this, count++, secure);
                voteThread.start();
                threads.add(voteThread);
                if (cancel) break loop;
            }

            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if(count % 1000 == 0){
                System.out.println("Cleaned");
                System.gc();
            }
        }

        if (cancel) System.out.println("Prematurely stopped process.");
        else{
            System.out.println("Finished to vote " + target + " times :)");
        }

        Platform.runLater(this::finishCallback);
    }

    public void retry(int id){
        Thread t = new VoteThread(this, id, secure);
        t.start();
        threads.add(t);
    }

    public abstract void finishCallback();
    public abstract void updateStats(boolean error);
}
