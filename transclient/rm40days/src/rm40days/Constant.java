/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 * 
 */

///////////////////////////
//命令格式：java -jar 1 C:\workspace\test2 C:\workspace\test1
//第一个参数是天数，即大于多少天的删除
//后面的所有参数都为地址，即清理的是哪个目录
///////////////////////////

package rm40days;

/**
 *
 * @author ZhangYun
 */
public class Constant {

    public static String FILE_PATH = "/home/zhangyun/test/";
    //public static String FILE_PATH = "c://test/";
   
    //public static String FILE_PATH = "C://software/photoshopCS3/Adobe Photoshop CS3/ACE.dll";
    public static int DATE_LIMIT = 40;
}
