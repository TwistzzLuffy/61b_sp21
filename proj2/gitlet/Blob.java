package gitlet;

import java.io.File;
import java.sql.Savepoint;
import java.util.HashMap;

import static gitlet.myUtils.*;
import static gitlet.Utils.*;

public class Blob {
    /**
     * C W D
     */
    public static final File CWD = new File(System.getProperty("user.dir"));
    /**
     * the direction of Bob
     */
    public static final File bob_DIR = join(CWD,".gitlet/object");

    /**
     * Stage fi
     */

    public static final HashMap Staging = new HashMap();

    private String fileName;
    private String sha1;
    public Blob(String fN){
        fileName = fN;
        sha1 = readContentsAsString(join(CWD,fileName));
    }

    /**
     * read added file to bob
     */

    public void Save(){
        File readFile = join(CWD,this.fileName);
        Staging(fileName);
        if (findFile(readFile)){
            System.out.println("File does not exist.");
            System.exit(0);
        }
        byte[] content = readContents(readFile);
        writeContents(makeFile(bob_DIR,sha1),content);
    }

    /**
     * staging added file
     * @param file added file
     */
    public void Staging(String file){
        File stagFile = join(bob_DIR,"staging");
        Staging.put(this.fileName,sha1);
        writeObject(stagFile,Staging);
    }
}
