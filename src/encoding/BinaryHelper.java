package encoding;

public class BinaryHelper {
    public static String decToBin(int dec) {
        StringBuilder bin = new StringBuilder();
        int bit;
        while (dec > 0) {
            bit = dec % 2;
            dec /= 2;
            bin.insert(0, bit);
        }
        while(bin.length() < 3) bin.insert(0,0);

        return bin.toString();
    }

    public static int binToDec(String bin) {
        int dec = 0;
        for (int i = bin.length() - 1 ; i >= 0; i--) {
            if(bin.charAt(i) == '0') continue;
            dec += Math.pow(2, bin.length() - i - 1);
        }
        return dec;
    }
}
