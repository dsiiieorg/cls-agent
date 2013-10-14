/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author root
 */
public class Test {

    public static void main(String[] args) {
        String str = "'qqq',111,'aaaaff,ffgggg',234";
        String[] strs = str.split(",");
        StringBuffer bs = new StringBuffer();
        List<String> list = new ArrayList<String>();
        for (int i = 0; i < strs.length; i++) {
            if (strs[i].startsWith("'") && !strs[i].endsWith("'")) {
                for (int j = i + 1; j < strs.length; j++) {
                    strs[i] += "," + strs[j];
                    bs.append(j + ",");
                    if (strs[j].endsWith("'")) {
                        break;
                    }
                }
            }
            list.add(strs[i]);
        }
   //     System.out.println("bs:"+bs.toString());
        if (bs.toString().length() !=0) {
            System.out.println(bs.toString().substring(0, bs.toString().length() - 1));
            System.out.println(list.toString());
            String[] s = bs.toString().split(",");
            System.out.println("s.leng:" + s.length);
            for (int i = s.length - 1; i >= 0; i--) {
                System.out.println(s[i]);
                list.remove(Integer.parseInt(s[i]));
            }
            String[] newStr = list.toArray(new String[1]);
            System.out.println("newStr.leng:" + newStr.length);
            for (int i = 0; i < newStr.length; i++) {
                System.out.println(newStr[i]);
            }
        }

    }
}
