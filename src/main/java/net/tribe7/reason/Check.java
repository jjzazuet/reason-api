package net.tribe7.reason;

/**
 * Basic assertions, with optional enum support for constant root cause definitions.
 *
 * @author Jesus Zazueta
 * @since 2.0.1
 */
public enum Check {

  /**
   * A general processing error, with no further (or unknown) cause.
   */
  GENERAL_ERROR,

  /**
   * An error triggered due to an invalid state, either caused by a routine's
   * internal processing, or invalid input parameters.
   */
  CONDITION_NOT_SATISFIED,

  /**
   * An error triggered due to missing arguments into the input routine.
   */
  MISSING_DATA;

  /**
   * Encode a string representation for an error constant.
   *
   * @param e the error constant. May be {@code null},
   *          which defaults to {@link #GENERAL_ERROR}.
   * @return the error constant in lowercase format, with underscores {@code '_'}
   * replaced by dots {@code '.'}.
   */
  public static String err(Enum<?> e) {
    if (e == null) {
      e = GENERAL_ERROR;
    }
    return e.toString().toLowerCase().replace("_", ".");
  }

  /**
   * Non-null argument check.
   *
   * @param test the argument to check. Must not be {@code null}.
   * @param <T>  the type of the argument to check.
   * @return the argument itself.
   * @throws IllegalStateException if {@code test} is {@code null},
   *                               with {@link #MISSING_DATA} as the root cause.
   */
  public static <T> T notNull(T test) {
    return notNull(test, err(MISSING_DATA));
  }

  /**
   * Non-null argument check, with support for a root cause constant.
   *
   * @param test      the argument to check. Must not be {@code null}.
   * @param errorEnum the root cause error constant.
   *                  May be {@code null}, which defaults to {@link #MISSING_DATA}
   * @param <T>       the type of the argument to check.
   * @return the argument itself.
   * @throws IllegalStateException if {@code test} is {@code null}, with {@code errorEnum} as the root cause,
   *                               or {@link #MISSING_DATA} if {@code errorEnum} is {@code null}.
   */
  public static <T> T notNull(T test, Enum<?> errorEnum) {
    return notNull(test, err(errorEnum == null ? MISSING_DATA : errorEnum));
  }

  /**
   * Non-null argument check, with support for a free-form string root cause constant.
   *
   * @param test    the argument to check. Must not be {@code null}.
   * @param message the free-form root cause message.
   *                May be {@code null}, which defaults to {@link #MISSING_DATA}.
   * @param <T>     the type of the argument to check.
   * @return the argument itself.
   * @throws IllegalStateException if {@code test} is {@code null}, with {@code message} as the root cause,
   *                               or {@link #MISSING_DATA} if {@code message} is {@code null}.
   */
  public static <T> T notNull(T test, String message) {
    if (test == null) {
      if (message != null && message.trim().length() > 0) {
        throw new IllegalStateException(message);
      }
      throw new IllegalStateException(err(MISSING_DATA));
    }
    return test;
  }

  /**
   * Basic truth check.
   *
   * @param condition the condition to test.
   * @throws IllegalStateException if {@code condition} is {@code false},
   *                               with {@link #CONDITION_NOT_SATISFIED}
   *                               as the root cause.
   */
  public static void isTrue(boolean condition) {
    isTrue(condition, CONDITION_NOT_SATISFIED);
  }

  /**
   * Basic truth check, with error constant support.
   *
   * @param condition the condition to test.
   * @param whenFalse a root cause error constant.
   *                  May be {@code null}, which defaults to {@link #CONDITION_NOT_SATISFIED}.
   * @throws IllegalStateException if {@code condition} is {@code false}, with {@code whenFalse}
   *                               as the root cause message, or {@link #CONDITION_NOT_SATISFIED} if {@code whenFalse} is {@code null}.
   */
  public static void isTrue(boolean condition, Enum<?> whenFalse) {
    isTrue(condition, err(whenFalse == null ? CONDITION_NOT_SATISFIED : whenFalse));
  }

  /**
   * Basic truth check, with support for a free-form string root cause constant.
   *
   * @param condition the condition to test.
   * @param message   a free-form string root cause constant.
   *                  May be {@code null}, which defaults to {@link #CONDITION_NOT_SATISFIED}.
   * @throws IllegalStateException if {@code condition} is {@code false}, with {@code message}
   *                               as the root cause message, or {@link #CONDITION_NOT_SATISFIED}
   *                               if {@code message} is {@code null}.
   */
  public static void isTrue(boolean condition, String message) {
    if (!condition) {
      throw new IllegalStateException(
          message == null ? err(CONDITION_NOT_SATISFIED) : message
      );
    }
  }
}
