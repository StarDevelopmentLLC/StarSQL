package com.stardevllc.stardata.api.annotations;

import java.lang.annotation.*;

/**
 * This annotation allows customization of the datatype. This is to be used with the {@link Codec} annotation. <br>
 * Please be aware that if not handled correctly, things can fail to process and throw Exceptions. The project that this is taken from mainly uses it to customize varchar types with an SqlCodec
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Type {
    String value();
}
