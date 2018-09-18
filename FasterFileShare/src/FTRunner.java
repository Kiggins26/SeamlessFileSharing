import java.util.Timer;
import java.util.TimerTask;

public class FTRunner {
    public static void main(String[] args){ //this runs a the thread that was defined in the Filetransfer class
        TimerTask filertask = new FileTransfer("holder", "holder");
        Timer timer = new Timer();
        timer.schedule(filertask, 10000,18000000);
    }
}
