package encoding.encoders;

import encoding.BinaryHelper;

public class SimpleEncoder implements EncodingType{
    @Override
    public String pixelChanged(String text) {
        if(text.length() == 0) return "";

        StringBuilder out = new StringBuilder();
        char start = get01Char(text, 0);
        int count = 1;
        for (int i = 1; i < text.length(); i++) {
            if(get01Char(text, i) == start && count < 8) {
                count++;
            }
            else {
                out.append(getEncodedSequence(start, count)).append(" ");
                start = get01Char(text, i);
                count = 1;
            }
        }
        out.append(getEncodedSequence(start,count));

        return out.toString();
    }

    private String getEncodedSequence(char start, int count) {
        return start + BinaryHelper.decToBin(count - 1).substring(0,3);
    }

    private char get01Char(String text, int index) {
        if (index >= text.length()) throw new IllegalStateException();

        if(text.charAt(index) == '0') return '0';
        else return '1';
    }
    private char get01Char(char a) {
        if(a == '0') return '0';
        else return '1';
    }

    private String printCharNTimes(char a, int count) {
        return String.valueOf(a).repeat(Math.max(0, count));
    }

    @Override
    public String textChanged(String text) {
        StringBuilder output = new StringBuilder();
        StringBuilder pac4 = new StringBuilder(); //Temporary Storage
        for (int i = 0; i < text.length(); i++) {
            char current = text.charAt(i);
            if(current == ' ') continue;
            current = get01Char(current);

            pac4.append(current);
            if(pac4.length() >= 4) {
                int count = BinaryHelper.binToDec(pac4.substring(1)) + 1;
                String seq = printCharNTimes(pac4.charAt(0), count);
                output.append(seq);
                pac4 = new StringBuilder();
            }
        }

        return output.toString();
    }
}
