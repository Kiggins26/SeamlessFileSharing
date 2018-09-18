import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.Buffer;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;

public class FileTransfer extends TimerTask {
    public String info;
    public String headpath;
    String file = "path.to/file.txt";
    public FileTransfer(String MachineTag,String path){
       info = MachineTag;
       headpath = path;
    }
    private void MD5Hashcompare(File md5holder){ //this sends the clients machine's hashes of its files to the server over tcp, it is then compared on the server machine.
        try {
            Scanner md5reader = new Scanner(md5holder);
            Socket md5sender = new Socket("IPaddress",80); //   use 80 a basic port for a holder value
            while(md5reader.hasNext()){
                DataOutputStream outToServer = new DataOutputStream(md5sender.getOutputStream());
                outToServer.writeBytes(md5reader.nextLine());
            }
            md5reader.close();
        }
        catch(IOException e){
            e.printStackTrace();
        }
    }
    private void MD5HashOnLocalMachine(String[] filesThatNeedFirstVer){ //use the basic md5sum to get the unique MD5hash of a file which will be later used to validate its integrity
        try {
            File listofMD5 = new File(file);
            PrintWriter MD5adder = new PrintWriter(file);
            for(int t = 0; t<filesThatNeedFirstVer.length; t++){
                Process filechecker = Runtime.getRuntime().exec("md5sum EasyFileSharing/" + filesThatNeedFirstVer[t]);
                filechecker.waitFor();
                BufferedReader fileinfo = new BufferedReader((new InputStreamReader(filechecker.getInputStream())));
                String hashholder  = "";
                while((hashholder = fileinfo.readLine()) != null){
                    MD5adder.println(hashholder + "/n");
                }
            }
            MD5Hashcompare(listofMD5);

        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
    public void fileWipe(){ //SUDO NEEDS TO GRANTED TO "RM" this is used to remove anything in the EasyFileSharing folder, saving space on the client machine
        try{
            StringBuffer FilesinFolder = new StringBuffer();
            Process listFiles = Runtime.getRuntime().exec("ls "+ headpath);
            BufferedReader fileReader = new BufferedReader(new InputStreamReader(listFiles.getInputStream()));
            String FileHolder = "";
            while((FileHolder = fileReader.readLine()) != null){
                FilesinFolder.append(FileHolder);
            }
            String[] info = FilesinFolder.toString().split(" ");
            for(int i=0;i<info.length;i++){
                Process remove = Runtime.getRuntime().exec("rm " + "EasyFileSharing/" + info[i]);
                remove.waitFor();
            }

        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
    public boolean ClientConf(){ //this is used to get the result of the comparison from the server
        try{
            ServerSocket ClientSock = new ServerSocket(80);
            Socket confsock = ClientSock.accept();
            InputStreamReader conf = new InputStreamReader(confsock.getInputStream());
            int dec = conf.read();
            ClientSock.close();
            confsock.close();
            if(dec == 1){
                return true;
            }
            else{
                return false;
            }
        }
        catch(IOException e){
            e.printStackTrace();
            return false;
        }
    }
    @Override
    public void run(){
        try{
            //headpath refers to the user head on the server machine
            File MD5s = new File(file);
            StringBuffer FilesinFolder = new StringBuffer();
            Process listFiles = Runtime.getRuntime().exec("ls "+ headpath);
            BufferedReader fileReader = new BufferedReader(new InputStreamReader(listFiles.getInputStream()));
            String FileHolder = "";
            while((FileHolder = fileReader.readLine()) != null){
               FilesinFolder.append(FileHolder);
            }
            String[] info = FilesinFolder.toString().split(" ");
            MD5HashOnLocalMachine(info);
            Process fileSend = Runtime.getRuntime().exec("scp -r ./EasyFileSharing/" + headpath + ":./EasyFileSharing/"); //scp -r /local/directory/ username@to_host:/remote/directory/
            fileSend.waitFor();
            MD5Hashcompare(MD5s);
            if(ClientConf()){
                fileWipe();
            }
            else{
                System.out.println("an error has occurred please check the validity of your server's files. /n they might be compromised ");
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
}
