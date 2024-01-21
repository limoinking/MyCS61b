package bstmap;

import static org.junit.Assert.*;
import org.junit.Test;
public class mytest
{
    @Test
    public void sanityGenericsTest() {
        BSTMap<String, String> a = new BSTMap<String, String>();

        a.put("b","2");
        a.put("a","1");
        a.put("c","3");
        for(String T : a)
        {
            System.out.println(T);
        }
        for(String T : a)
        {
            System.out.println(T);
        }

    }
}
