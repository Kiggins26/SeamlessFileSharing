import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class ServerSide {
private String ClientIP = "";
    public String[] ClientMD5collector(){//collects information need to verify the MD5hashes of the file on clients machine, but  need to parse byte to ascii
        try{
            ServerSocket sock = new ServerSocket(80); //using 80 as a holder for the port, the port number is not that important as long as it can support TCP
            Socket compsock = sock.accept();
            InetAddress clientAddress = compsock.getInetAddress();
            ClientIP = clientAddress.getHostAddress();
            BufferedReader info = new BufferedReader(new InputStreamReader(compsock.getInputStream()));
            String total = "";
            while((total = info.readLine())!=null) {
                total = total + info.readLine();
            }
            String[] ClentMD5info = total.split(""); //add key value to split the string at;
            Arrays.sort(ClentMD5info);
            return  ClentMD5info;
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
    public String[] ServerMD5(File ServerFiles){ //collect the md5hasehes on the server machine
        try {
            String MD5adder = "";
            Scanner filereader = new Scanner(ServerFiles);
            while(filereader.hasNextLine()){
                Process MD5 = Runtime.getRuntime().exec("md5sum EasyFileSharing/"+filereader.nextLine());
                MD5.waitFor();
                String hashholder  = "";
                BufferedReader fileinfo = new BufferedReader((new InputStreamReader(MD5.getInputStream())));
                while((hashholder = fileinfo.readLine()) != null){
                    MD5adder = MD5adder+hashholder ;
                }
                MD5adder = MD5adder+"#";
            }
            return  MD5adder.split("#");
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
    public void sendConf(){ //compares two arrays of string that are the md5 hashes, then sends the corresponding boolean vaLue to the client
        byte ConfStatment = 0;
        if(Arrays.equals(ServerMD5(new File("FilesStoredonServer.txt")),ClientMD5collector())){
            ConfStatment = 1;
        }
        try{
            Socket confsender = new Socket(ClientIP,80); //   use 80 a basic port for a holder value
            DataOutputStream outToServer = new DataOutputStream(confsender.getOutputStream());
            outToServer.write(ConfStatment);
            confsender.close();
        }
        catch(IOException e){
            e.printStackTrace();
        }
    }
    public void FilesStoredOntheServer(){ //updates the files that are being stored on the the server for easier calling later in the process.
        try{
            Process listfilesOnMachines = Runtime.getRuntime().exec("ls EasyFileSharing/");
            BufferedReader filesInFolder = new BufferedReader(new InputStreamReader(listfilesOnMachines.getInputStream()));
            String holder = "";
            StringBuffer WriterHolder = new StringBuffer();
            while(( holder = filesInFolder.readLine()) != null){
                WriterHolder.append(holder);
            }
            String LSStore = WriterHolder.toString();
            Scanner FileReader = new Scanner(new File("FilesOnServer.txt"));
            Scanner LSReader = new Scanner(LSStore);
            ArrayList<String> LSGroup = new ArrayList<>();
            while(LSReader.hasNext()){
                LSGroup.add(LSReader.next());
            }
            ArrayList<String> FileGroup = new ArrayList<>();
            while(FileReader.hasNext()){
                FileGroup.add(FileReader.next());
            }
            if(FileGroup.size() != LSGroup.size()){
                if(FileGroup.size() > LSGroup.size()){
                    //Remove the files that are not on the file store(the server)
                }
                if (FileGroup.size()<LSGroup.size()){
                    PrintWriter Fileadder = new PrintWriter(new File("FilesOnServer.txt"));
                    for(int i = FileGroup.size()-1; i<LSGroup.size();i++){
                        Fileadder.println(LSGroup.get(i));
                    }
                }
            }
            
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
    public static void main(String[] args) throws IOException{  // looks for changes in the file directory, if there is a change in directory size the comparison process start
        ServerSide s = new ServerSide();
        int intFiles = 0;
        while(true){
            Process numberOfFiles = Runtime.getRuntime().exec(" ls " + "./EasyFileSharing" + " | wc -l");
            BufferedReader reader = new BufferedReader(new InputStreamReader(numberOfFiles.getInputStream()));
            int newFiles = Integer.parseInt(reader.readLine());
            if (intFiles != newFiles) {
                intFiles = newFiles;
                s.FilesStoredOntheServer();
                s.sendConf();
            }
        }
    }

}