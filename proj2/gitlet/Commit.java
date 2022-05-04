package gitlet;

// TODO: any imports you need here
import static gitlet.Utils.*;
import java.io.File;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/** Represents a gitlet commit object.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 *  @author TODO
 */
public class Commit implements Serializable {
    /**
     * TODO: add instance variables here.
     *
     * List all instance variables of the Commit class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided one example for `message`.
     *
     */
    public static final File CWD = new File(System.getProperty("user.dir"));
    public static final File COMMIT_DIR = Utils.join(CWD,".gitlet/object");
    public static final File GITLET_DIR = join(CWD, ".gitlet");
    public static final File BOB_DIR = join(GITLET_DIR, "bob");
    public static final File GIT_SHORT_ID_DIR = join(CWD,".gitlet/short");
    /** The message of this Commit. */
    private String message;

    /* TODO: fill in the rest of this class. */
    /**
     * the timestamp
     */

    private String sha1;
    private Date date;
    private String parentIndex;
    private String secondparentIndex;
    private TreeMap<String,String> bobIndex;// store evey file's sha1

    public Commit(){
        message = "initial commit";
        date = new Date(0);
        sha1 =  Utils.sha1(Utils.serialize(this));
        bobIndex = new TreeMap<String,String>();
        this.parentIndex = null;
    }

    public Commit(String message,String parentIndex,String secondparentIndex){
        this.message = message;
        date = new Date();
        sha1 =  Utils.sha1(Utils.serialize(this));
        this.parentIndex = parentIndex;
        bobIndex = new TreeMap<String,String>();
        this.secondparentIndex = secondparentIndex;
    }


    /**
     * to save this commit
     */
    public void Save(){
        writeObject(join(COMMIT_DIR,sha1),this);
    }
    public void shortSave(){
        writeObject(join(GIT_SHORT_ID_DIR,sha1.substring(0,6)),this);
    }

    /**
     *  commit refer to the bobfile of staging
     * @param Staging store the added files
     */
    public void addStaging(TreeMap<String,String> Staging){
        for (Map.Entry<String, String> i : Staging.entrySet()) {
            if (!bobIndex.containsKey(i.getKey())) {
                bobIndex.put(i.getKey(),i.getValue());
//                this.blobMapToFileName.put(entry.getKey(), entry.getValue());
            }
            if (this.bobIndex.containsKey(i.getKey())&&
                    (!this.bobIndex.containsValue(i.getValue()))){
                bobIndex.remove(i.getKey());
                bobIndex.put(i.getKey(),i.getValue());
            }
        }
    }



    public void addPrviousCommit(TreeMap<String,String> preBObIndex){
        for (Map.Entry<String, String> i : preBObIndex.entrySet()){
            if (!this.bobIndex.containsKey(i.getKey())){
                bobIndex.put(i.getKey(),i.getValue());
            }
        }
    }

    public void deleteStageRemoveFile(TreeMap<String,String>StageRemove){
        for (Map.Entry<String,String> i : StageRemove.entrySet()){
            bobIndex.remove(i.getKey());
        }
    }

    public boolean printfCommit(){
        if (message.equals("initial commit")){
            System.out.println("===");
            System.out.println("commit "+sha1);
            System.out.println("Date: " +getTimestamp());
            System.out.println(message);
            System.out.println();
            return false;
        }
        System.out.println("===");
        System.out.println("commit "+sha1);
        System.out.println("Date: " +getTimestamp());
        System.out.println(message);
        System.out.println();
        return true;
    }

    public void checkout(String fileName){
        String bobSha1 = null;
        byte[] fileContent;
        boolean sign = false;
        for (Map.Entry<String, String> i : this.bobIndex.entrySet()) {
            if (i.getKey().equals(fileName)) {
                bobSha1 = i.getValue();
                sign = true;
                break;
            }
        }
        if (sign) {
            fileContent = readContents(join(BOB_DIR, bobSha1));
            writeContents(join(CWD, fileName), fileContent);
            return;
        }
        System.out.println("File does not exist in that commit.");
        System.exit(0);
    }

    public String getParentIndex(){
        return this.parentIndex;
    }
    public String getSecondParentIndex(){
        return this.secondparentIndex;
    }

    public String getMessage(){return this.message;}

    public TreeMap<String,String>  getBobIndex(){
        return this.bobIndex;
    }

    public String getTimestamp() {
        // Thu Jan 1 00:00:00 1970 +0000
        DateFormat dateFormat = new SimpleDateFormat("EEE MMM d HH:mm:ss yyyy Z", Locale.ENGLISH);
        return dateFormat.format(date);
    }
    /**
     *
     * @return the sha1 of this.commit
     */
    public String getsha1(){
        return this.sha1;
    }
}
