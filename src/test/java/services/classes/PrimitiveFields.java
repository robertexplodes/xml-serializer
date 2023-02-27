package services.classes;

public class PrimitiveFields {

    private final char someChar;
    private final long someLong;
    private final boolean someBoolean;

    public PrimitiveFields(char someChar, long someLong, boolean someBoolean) {
        this.someChar = someChar;
        this.someLong = someLong;
        this.someBoolean = someBoolean;
    }

    public char getSomeChar() {
        return someChar;
    }

    public long getSomeLong() {
        return someLong;
    }

    public boolean isSomeBoolean() {
        return someBoolean;
    }
}
