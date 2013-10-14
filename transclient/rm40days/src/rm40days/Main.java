/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package rm40days;

/**
 *
 * @author root
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        int date = 0;
        try {
            date = Integer.parseInt(args[0]);
        } catch (Exception e) {
            System.out.println("date err " + args[0]);
            return;
        }
        for (int i = 0; i < args.length; i++) {
            if (i > 0) {
                new FileTools().run(date,args[i]);
            }
        }
    }

}
