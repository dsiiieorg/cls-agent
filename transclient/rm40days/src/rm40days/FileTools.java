/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rm40days;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author ZhangYun
 */
public class FileTools {

    public void run(int date, String file_path) {
        File root = new File(file_path);
        rm40days(date, root);
        /*
         * SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
         * Date begin = df.parse("2004-01-02 11:30:24"); Date end =
         * df.parse("2004-03-26 13:31:40"); long between = (end.getTime() -
         * begin.getTime()) / 1000;//除以1000是为了转换成秒 int day = between / (24 *
         * 3600); int hour = between % (24 * 3600) / 3600; int minute = between
         * % 3600 / 60; int second = between % 60;
         */
        //int i = 0;


    }

    private void rm40days(int date, File root) {
        File list[] = root.listFiles();
        if (list == null) {
            return;
        }
//        if(list.length==0){
//            deal(date,root);
//            return;
//        }
        for (int i = 0; i < list.length; i++) {
            if (list[i].isDirectory()) {

                if (list[i].getName().contains("Rm40days")) {
                    continue;
                }
                rm40days(date, list[i]);
                File tmpList[] = list[i].listFiles();
                if (tmpList.length == 0) {
                    //System.out.println("Directory name is " + list[i].getName());
                    //System.out.println("Directory time is " + getLastModifiedTime(list[i]));
                    //deal(date, list[i]);
                    //list[i].delete();//20120625 zy
                }
            } else {
                if (list[i].getName().contains("Rm40days")) {
                    continue;
                }
                deal(date, list[i]);
            }
        }
    }

    private void deal(int date, File file) {
        String begin = getLastModifiedTime(file);//得到文件最后修改的时间
        boolean rmFlag = compareDate(date, begin);//比较是不是大于40天,大于是true
        if (rmFlag) {
            file.delete();
        }
    }

    private String getLastModifiedTime(File file) {
        Long time = file.lastModified();
        Date date = new Date();
        date.setTime(time);
        String pattern = "yyyy-MM-dd";
        SimpleDateFormat formatter = new SimpleDateFormat(pattern, Locale.getDefault());
        return formatter.format(date);
    }

    private boolean compareDate(int date1, String begin) {
        Date date = new Date();
        String pattern = "yyyy-MM-dd";
        SimpleDateFormat formatter = new SimpleDateFormat(pattern, Locale.getDefault());
        String end = formatter.format(date);

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date begin_d = df.parse(begin);
            Date end_d = df.parse(end);
            long between = (end_d.getTime() - begin_d.getTime()) / 1000;
            long day = between / (24 * 3600);
            if (day > date1) {
                return true;
            } else {
                return false;
            }
        } catch (ParseException ex) {
            Logger.getLogger(FileTools.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }

    }
}
