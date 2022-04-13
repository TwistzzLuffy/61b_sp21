package gitlet;

public class Blob {
    private static String fileIndex;
    private String fileName;
    public String txtContent;
    public Blob(String fN){
        fileName = fN;
    }
    public static void insertFileIndex(String fI){
        fileIndex = fI;
    }
}
