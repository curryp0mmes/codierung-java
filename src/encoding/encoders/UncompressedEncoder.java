package encoding.encoders;

public class UncompressedEncoder implements EncodingType{
    @Override
    public String pixelChanged(String text) {
        return text;
    }

    @Override
    public String textChanged(String text) {
        return text;
    }
}
