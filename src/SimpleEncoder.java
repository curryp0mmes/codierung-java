public class SimpleEncoder implements EncodingType{
    @Override
    public String pixelChanged(String text) {
        return "coming soon";
    }

    @Override
    public String textChanged(String text) {
        return "00000";
    }
}
