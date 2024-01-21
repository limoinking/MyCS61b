package gitlet;

import java.io.File;
import java.io.Serializable;
import java.util.List;

import static gitlet.Repository.*;
import static gitlet.Utils.*;
import static gitlet.Utils.*;
import static gitlet.Repository.*;
import static gitlet.Commit.*;

public class MyUtils {
    //存储字典和object在blobs中的
    public static void saveDirAndObjInBlobs(Serializable SerObj, File FOLDER, String ID)
    //其实这个可以算是特别供应给Blob类去存的,其他的commit类也需要就是
    {
        Commit parentCommit = getCurrentCommit();//得到当前提交的是哪一个
        List<String> parentBlobIDs = parentCommit.getBlobIDs();//得到父节点的blobs集合
        //因为父节点可能会指向多个blobs,就是在pcommit中,可能会指向多个文件情况
        if(parentBlobIDs.size() != 0)
        {
            for(String parentID : parentBlobIDs)
            {
                if(ID.equals(parentID))//如果当前的文件不是一个新的文件
                {
                    return;//说明这个已经存在，没有必要再去更改
                }
            }
        }
        //接下来就是全新文件的情况
        List<String> dirIDList = Utils.plainFilenamesIn(FOLDER);//得到文件夹中所有的数据
        String dirID = getDirID(ID);//获取头2位ID为索引
        if(!dirIDList.contains(dirID))//如果没有这样ID就要先创一个
        {
            saveDir(FOLDER,dirID);//创立这个2位ID文件夹
        }
        List<String> IDList = Utils.plainFilenamesIn(join(FOLDER,dirID));//如果有头ID,看看头ID里面的长ID有啥
        if(IDList != null && !IDList.contains(ID))//前一个是判断是否分配了空间,后一个是判断是否存在,如果存在这样ID就直接存
            //其实这句可以只要前半段,因为后半段发生的概率极低,即出现冲突的时候
        {
            saveObj(FOLDER,dirID,ID,SerObj);
        }
    }

    public static String getDirID(String ID)
    {
        return ID.substring(0,2);//截取两位作为前缀ID，方便查找

    }

    public static void saveDir(File FOLDER,String dirID)
    {
        File dir = join(FOLDER,dirID);
        dir.mkdir();//创建子文件夹
    }
    public static void saveObj(File FOLDER,String dirID,String ID,Serializable SerObj)//专门供给blobs类
    {
        File file = join(FOLDER,dirID,ID);//合成子文件路径
        writeObject(file,SerObj);//向该文件夹中写入该对象
    }
    public static void saveObj(File FOLDER, String name, Serializable SerObj) {//专门实用性很广,就是多了一个写入的步骤看着舒服
        File file = join(FOLDER, name);
        writeObject(file, SerObj);//向文件中序列化写入对象
    }

    public static void saveContent(File FOLDER, String name, String content)
    {
        File file = join(FOLDER,name);
        writeContents(file,content);
    }
    public static String getFileID(File file)
    {
        //使用文件名字 + 文件内容转为byte然后求解哈希问题
        return sha1(serialize(file.getName()),serialize(readContentsAsString(file)));//通过文件名+文件内容
        //来得到唯一的哈希值来确保文件没有发生变化
    }
    public static boolean validateDirAndFolder()
    {
        return GITLET_DIR.exists();//检验路径是否是存在的
    }
}
