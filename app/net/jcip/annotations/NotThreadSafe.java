package net.jcip.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * The presence of this annotation indicates that the author believes the class is not thread-safe.
 * The absence of this annotation does not indicate that the class is thread-safe, instead this annotation is for
 * cases where a naïve assumption could be easily made that the class is thread-safe. In general, it is a bad plan
 * to assume a class is thread safe without good reason.
 */
@Documented
@Target(TYPE)
@Retention(RUNTIME)
public @interface NotThreadSafe {
}

