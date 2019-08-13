import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class LockCheckerTest {

    @Test
    public void test(){
        List<String> set = new ArrayList<>(LockChecker.validate("{xxxxxx{{{}{}{}"));
        Assert.assertEquals(set.get(0), "{xxxxxx{{}}}");

        List<String> set2 = new ArrayList<String>(LockChecker.validate("{}}{}}"));
        Assert.assertEquals(set2.get(0), "{{}}");
        Assert.assertEquals(set2.get(1), "{}{}");

        List<String> set3 = new ArrayList<>(LockChecker.validate("{}x}x}"));
        for(String s: set3){
            Assert.assertTrue(s.equals("{x}x") || s.equals("{xx}") || s.equals("{}xx"));
        }

        List<String> set4 = new ArrayList<>( LockChecker.validate("{"));
        Assert.assertTrue(set4.size() == 1 && set4.get(0).equals(""));
    }

}
