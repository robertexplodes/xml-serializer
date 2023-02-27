package services.classes;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.*;

@AllArgsConstructor
@Data
public class Collections {

    private List<String> list;
    private Set<Part> set;
    private Map<Integer, Strings> map;
}
