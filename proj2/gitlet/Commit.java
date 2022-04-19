package gitlet;

// TODO: any imports you need here
import static gitlet.Utils.*;
import java.io.File;
import java.io.Serializable;
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
    public static final File Commit_DIR = Utils.join(CWD,".gitlet/object");
    /** The message of this Commit. */
    private String message;

    /* TODO: fill in the rest of this class. */
    /**
     * the timestamp
     */

    private String sha1;
    private Date timestamp;
    private String parentIndex;
    private LinkedList<String> bobIndex;

    public Commit(){
        message = "initial commit";
        timestamp = new Date(0);
        sha1 =  Utils.sha1(Utils.serialize(this));
    }

    public Commit(String message,String parentIndex){
        this.message = message;
        timestamp = new Date();
        sha1 =  Utils.sha1(Utils.serialize(this));
        this.parentIndex = parentIndex;
    }
    /**
     *
     * @return the sha1 of this.commit
     */
    public String sha1(){
        return this.sha1;
    }

    /**
     * to save this commit
     */
    public void Save(){
        writeObject(myUtils.makeFile(Commit_DIR,sha1),this);
    }

    public void addStaging(TreeMap<String,String> Staging){
        for (Map.Entry<String, String> i : Staging.entrySet()) {
            if (!Staging.containsValue(i.getValue())) {
                bobIndex.addFirst(i.getValue());
//                this.blobMapToFileName.put(entry.getKey(), entry.getValue());
            }
        }
    }

}
