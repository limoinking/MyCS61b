package gitlet;

import java.io.File;
import java.io.Serializable;
import static gitlet.MyUtils.*;
import static gitlet.Utils.*;
import static gitlet.Repository.*;
//这个就是存储每一个文件所对应的一些情况的
public class Blob implements Serializable
{
    private String blobID;
    private String copiedFileID;
    private String copiedFileContent;
    private String copiedFileName;

    public Blob(File stagingFile)
    {
        this.blobID = sha1(serialize(this));//这个是作为blobs特有的ID
        this.copiedFileName = stagingFile.getName();//获取文件名字
        this.copiedFileID = getFileID(stagingFile);//获取文件的sha1哈希值
        //注意区分这两个哈希值不是一个东西
        this.copiedFileContent = readContentsAsString(stagingFile);
    }

    public String getBlobID()
    {
        return this.blobID;
    }

    public String getCopiedFileID()
    {
        return this.copiedFileID;
    }

    public String getCopiedFileContent()
    {
        return this.copiedFileContent;
    }

    public String getCopiedFileName()
    {
        return this.copiedFileName;
    }
}
