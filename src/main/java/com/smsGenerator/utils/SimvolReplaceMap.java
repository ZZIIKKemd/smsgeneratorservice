package com.smsGenerator.utils;

import com.google.common.collect.ImmutableMap;
import java.util.Map;

public class SimvolReplaceMap {
    public static final Map<Character, Character> MY_MAP = ImmutableMap.<Character, Character>builder()
            .put('А', 'A')
            .put('а', 'a')
            .put('В', 'B')
            .put('Е', 'E')
            .put('е', 'e')
            .put('К', 'K')
            .put('М', 'M')
            .put('Н', 'H')
            .put('О', 'O')
            .put('о', 'o')
            .put('Р', 'P')
            .put('р', 'p')
            .put('С', 'C')
            .put('с', 'c')
            .put('Т', 'T')
            .put('Х', 'X')
            .put('х', 'x')
            .build();
}
