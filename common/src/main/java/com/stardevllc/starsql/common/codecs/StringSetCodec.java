package com.stardevllc.starsql.common.codecs;

import com.stardevllc.starsql.api.interfaces.ObjectCodec;

import java.util.*;

public class StringSetCodec implements ObjectCodec<Set<String>> {
    @Override
    public String encode(Object object) {
        Set<String> stringSet = (Set<String>) object;
        if (stringSet.isEmpty()) {
            return null;
        }
        return join(stringSet);
    }
    
    @Override
    public Set<String> decode(String encoded) {
        if (encoded == null) {
            return new HashSet<>();
        }
        return new HashSet<>(Arrays.asList(encoded.split(",")));
    }
    
    //TODO Move to StarLib eventually
    public static String join(Collection<?> collection) {
        Iterator<?> iterator = collection.iterator();
        if (!iterator.hasNext()) {
            return "";
        } else {
            Object first = iterator.next();
            if (first == null) {
                return "";
            }
            if (!iterator.hasNext()) {
                return first.toString();
            } else {
                StringBuilder buf = new StringBuilder();
                buf.append(first);

                while (iterator.hasNext()) {
                    buf.append(",");

                    Object obj = iterator.next();
                    if (obj != null) {
                        buf.append(obj);
                    }
                }

                return buf.toString();
            }
        }
    }
}
