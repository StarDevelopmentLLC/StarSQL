package com.stardevllc.starsql.api.annotations;

import com.stardevllc.starsql.api.interfaces.ObjectCodec;

import java.lang.annotation.*;

/**
 * This annotation allows you to tell the library a {@link ObjectCodec} for a field/column <br>
 * The MySQL type when using this feature is VARCHAR with a default length of 1000. Other types use String<br>
 * If you want to override this behavior, use the {@link Type} annotation. You can only specify a Varchar argument here. This is only to allow customization of the length
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Codec {
    Class<? extends ObjectCodec<?>> value();
}
