package gitlet;

import java.io.File;
import static gitlet.myUtils.*;
import static gitlet.Utils.*;

public class Blob {
    /**
     * C W D
     */
    public static final File CWD = new File(System.getProperty("user.dir"));
    /** The .gitlet directory. */
    public static final File GITLET_DIR = join(CWD,".gitlet");
    /** store the bob,tree,commit directory*/
    public static final File OBJECT_DIR = join(GITLET_DIR,"object");
    /**
     * the direction of Bob
     */
    public static final File BOB_DIR = join(GITLET_DIR,"bob");
//    public static final File BOB_DIR = join(CWD,".gitlet/object");

    private String fileName;
    private String sha1;
    public Blob(String fN){
        fileName = fN;
        sha1 = sha1(readContentsAsString(join(CWD,fileName)));
    }


    /**
     * read added file to bob
     */

    public void SaveForAdd(){
        File readFile = join(CWD,this.fileName);
        Repository.Staging(fileName,sha1);
        if(!readFile.exists()){
            System.out.println("File does not exist.");
            System.exit(0);
        }
        byte[] content = readContents(readFile);
        writeContents(join(BOB_DIR,sha1),content);
    }
}
