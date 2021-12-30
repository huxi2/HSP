package Talk.utils;

import java.util.Scanner;

public class Utility {

    /**
     * @param charLength 读取前几位
     * @return
     */
    public static String readString(int charLength) {
        Scanner sc = new Scanner(System.in);
        String input = sc.nextLine();
        charLength = Math.min(input.length(), charLength);
        String res = input.substring(0, charLength);
        return res;
    }

}
