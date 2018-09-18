import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.Scanner;

public class Start {
    /*Requires that you move the java files of this program to your home directory in order for the StartUp.sh on the server side to run
    *Any command called in the entire project will require privileges to run, if you do not the program will not run, as it will not have the privileges
    * this program is indeed for machines with low storage space, as well as providing a fast  alternative to SFTP, as there is no packet verification with SCP
    * */
    public static void main(String[] args) {
        boolean cond = true;
        while (cond) {
            String path = "";
            try {
                Scanner s = new Scanner(System.in);
                System.out.println("What machine is this? Server or Client");
                String userIN = s.nextLine().toUpperCase();
                if (userIN.equals("SERVER")) {

                    Process SetUpNewFolders = Runtime.getRuntime().exec("mkdir EasyFileSharing"); //Sets up the Dir for later uses
                    SetUpNewFolders.waitFor();
                    Process getSCriptLocation = Runtime.getRuntime().exec("find . -name StartUp.sh");
                    BufferedReader getStatement = new BufferedReader(new InputStreamReader(getSCriptLocation.getInputStream()));
                    String location = getStatement.readLine();
                    Process move = Runtime.getRuntime().exec("cd ..; cd ..;  mv "+location+" /etc/init"); //moves the script to a location were it will run on the server during start up
                    Process CreateQuery = Runtime.getRuntime().exec("cd ..; cd ..; chmod +x ./etc/init/StartUp.sh");//makes the start up script an executable
                    cond = false;

                } else if (userIN.equals("CLIENT")) {
                    Process createShortcut = Runtime.getRuntime().exec("alias StartFT =\'java FTRunner\'; javac FTRunner"); //changes javac * to a single word
                    createShortcut.waitFor();
                    Process createDir = Runtime.getRuntime().exec("mkdir EasyFileSharing/");
                    createDir.waitFor();
                    cond = false;
                } else System.out.println("NONE VALID INPUT ONLY USE CLIENT OR SERVER AS INPUT");
                ;

            } catch (Exception e) {
                e.printStackTrace();
                System.out.print("Make sure that are the needed files are configed on your machine");
            }
        }
    }
}
