package com.stardevllc.starsql.codecs;

import com.stardevllc.helper.StringHelper;
import com.stardevllc.starsql.interfaces.ObjectCodec;

import java.util.*;

public class StringSetCodec implements ObjectCodec<Set<String>> {
    @Override
    public String encode(Object object) {
        Set<String> stringSet = (Set<String>) object;
        if (stringSet.isEmpty()) {
            return null;
        }
        return StringHelper.join(stringSet, ",");
    }
    
    @Override
    public Set<String> decode(String encoded) {
        if (encoded == null) {
            return new HashSet<>();
        }
        return new HashSet<>(Arrays.asList(encoded.split(",")));
    }
}