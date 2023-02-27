package services.classes;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FakeGetters {

    private int value;

    public int get42() {
        return 42;
    }

    public boolean isThisFake() {
        return true;
    }
}
