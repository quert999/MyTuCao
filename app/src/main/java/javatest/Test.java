package javatest;

import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import mainfrags.UtilsKt;

/**
 * Created by Administrator on 2017/6/15.
 */

public class Test {
    public static void main(String[] args) {

        String s = "";
        synchronized (s) {
            try {
                s.wait(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            s.notify();
        }
    }


    public class WorkThread extends Thread{
        @Override
        public void run() {
            super.run();
            work();


            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            notify();
        }

        private void work(){
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}