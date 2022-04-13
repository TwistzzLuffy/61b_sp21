package gitlet;

import java.io.File;
import java.io.IOException;

import static gitlet.Utils.*;

// TODO: any imports you need here

/** Represents a gitlet repository.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 *  @author TODO
 */
public class Repository {
    /**
     * TODO: add instance variables here.
     *
     * List all instance variables of the Repository class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided two examples for you.
     */


    /** The current working directory. */
    public static final File CWD = new File(System.getProperty("user.dir"));
    /** The .gitlet directory. */
    public static final File GITLET_DIR = join(CWD, ".gitlet");

    /** store the bob,tree,commit directory*/
    public static final File Object_DIR = join(CWD,".gitlet/object");
    /* TODO: fill in the rest of this class. */
    /** create .gitlet*/
    public static void setupPersistence(){
        GITLET_DIR.mkdir();
        Object_DIR.mkdir();
    }

    /**
     * init
     */
    public static void gitInit(){
        /*if(GITLET_DIR.exists()){
            System.out.println("A Gitlet version-control system already exists in the current directory.");
            System.exit(0);
        }*/
        Commit commit_0 = new Commit();
    }

    /**
     * add
     */
    public static void gitAdd(String fileName) throws IOException {
        try {
            gitAdd(fileName);
        }
        catch(IOException e) {
            e.printStackTrace();
        }
        File stagFile = Utils.join(CWD,fileName);
        Blob blob0 = new Blob(fileName);
        blob0.txtContent = readContentsAsString(stagFile);
        String fileIndex = sha1(blob0);
        File bobFile = new File(Object_DIR+"fileIndex");
        if (bobFile.exists()){
            System.out.println("File does not exist.");
            System.exit(0);
        }
        bobFile.createNewFile();
        blob0.insertFileIndex(fileIndex);
        writeContents(bobFile,blob0);

    }
}
