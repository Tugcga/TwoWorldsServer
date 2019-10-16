package Game.DataClasses;

import com.smartfoxserver.v2.entities.data.ISFSArray;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.entities.data.SFSObject;
import com.smartfoxserver.v2.util.JSONUtil;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.apache.commons.io.FileUtils;

public class ChatMessagesStore 
{
    static int messagesCount = 10;
    static String filePrefix;
    static boolean writeName = true;
    static boolean writeIP = false;
    static boolean writeTime = false;
    static String dataTemplate = "yyyy-MM-dd_HH-mm-ss";
    public static int saveInterval = 1000;  // for using in the task, in seconds
    
    public static void Init(String fileName) throws IOException
    {
        String configDataStr = JSONUtil.stripComments(FileUtils.readFileToString(new File(fileName)));
        ISFSObject configObject = SFSObject.newFromJsonData(configDataStr);
        ISFSObject data = configObject.getSFSArray("chatStore").getSFSObject(0);
        messagesCount = data.getInt("messageCount");
        filePrefix = data.getUtfString("filePrefix");
        writeName = data.getInt("writeName") == 1;
        writeIP = data.getInt("writeIP") == 1;
        writeTime = data.getInt("writeTime") == 1;
        saveInterval = data.getInt("saveInterval");
        dataTemplate = data.getUtfString("dataTemplate");
    }
    
    public static String GetTimeDate()
    {
        SimpleDateFormat formatter = new SimpleDateFormat(dataTemplate);
        Date date = new Date(System.currentTimeMillis());
        return formatter.format(date);
    }
    
    public static void AddMessage(String name, String ip, String message) throws IOException
    {
        GlobalGameData.chatMessages.add(
                (writeName ? "[" + name + "]" : "") +
                (writeIP ? "[" + ip + "]" : "") + 
                (writeTime ? "[" + GetTimeDate() + "]" : "") + 
                "[" + message + "]");
        
        if(GlobalGameData.chatMessages.size() >= messagesCount)
        {
            SaveMessages();
        }
    }
    
    public static void SaveMessages() throws IOException
    {
        if(GlobalGameData.chatMessages.size() > 0)
        {
            FileWriter fileWriter = new FileWriter(filePrefix + GetTimeDate() + ".txt");
            PrintWriter printWriter = new PrintWriter(fileWriter);
            for(String m : GlobalGameData.chatMessages)
            {
                printWriter.print(m + "\n");
            }
            printWriter.close();

            GlobalGameData.chatMessages.clear();
        }
    }
    
}
