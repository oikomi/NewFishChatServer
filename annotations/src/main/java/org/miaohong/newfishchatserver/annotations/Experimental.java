package org.miaohong.newfishchatserver.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

/**
 * Annotation to mark classes for experimental use.
 *
 * <p>Classes with this annotation are neither battle-tested nor stable, and may be changed or removed
 * in future versions.
 *
 * <p>This annotation also excludes classes with evolving interfaces / signatures
 * annotated with {@link Public} and {@link PublicEvolving}.
 */
@Documented
@Target(ElementType.TYPE)
@Public
public @interface Experimental {
}
