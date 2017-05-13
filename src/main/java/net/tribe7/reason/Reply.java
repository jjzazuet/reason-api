package net.tribe7.reason;

import java.util.ArrayList;
import java.util.Collection;

/**
 * A reply is a simple wrapper object which represents an answer
 * to a request (the semantics of which are not modeled inside this class)
 * which has side effects (i.e. creating a file, writing content to a database, etc.).
 *
 * Therefore, the use of this class is only recommended for operations where side effects
 * are expected and need to be tracked.
 *
 * A reply provides:
 *
 * <ul>
 *    <li>Some sort of response data, as the type {@code T}, outlined below.</li>
 *    <li>A status which indicates if the request failed or succeeded.</li>
 *    <li>Optional error data in the form of a {@code Throwable}, if an error occurred.</li>
 *    <li>An optional message which provides more information about this reply.</li>
 * </ul>
 *
 * The type {@code T} is provided as a placeholder for the kind of answer that
 * the caller expects to receive from the operation that created this reply.
 *
 * A payload, if you will. For example, if a service class provides the following method:
 *
 * {@code public Reply<File> copy(File source, File dest) throws IOException}
 *
 * The resulting payload of the reply, retrieved by calling {@code getData} could be:
 *
 * <ul>
 *    <li>
 *       The same file reference, echoed back in case the caller is not interested in
 *       knowing more details about the target copied file {@code dest}.
 *    </li>
 *    <li>
 *       The {@code dest} reference to the copied file, in case the caller needs more
 *       information about what happened to the destination file.
 *    </li>
 *    <li>
 *       A different kind of {@code File} reference. For example, the top level directory
 *       where the destination file was copied to.
 *    </li>
 * </ul>
 *
 * While it could be argued that this is a simplistic scenario, the real advantage of a
 * reply lies within the the provided payload type {@code T}, which gives code
 * authors freedom to choose the kind of data that would be suitable for a response, plus the
 * contextual diagnostic data associated with the reply itself.
 *
 * In addition, a reply can include a collection of warning messages to signal implementation
 * details that a consumer should keep in mind. These could be, for example, API method
 * signature deprecation messages for a certain component library.
 *
 * @param <T> the target type expected from a successful reply, if any.
 * @author Jesus Zazueta
 * @since 1.0
 */
public class Reply<T> {

   public enum Status { OK, BAD, UNKNOWN }

   public static final String MESSAGE_DEFAULT = "no.additional.information";
   public static final String MESSAGE_INVALID_RESPONSE_DATA = "response.data.must.not.be.null";

   private T data;
   private Status status;
   private Throwable error;
   private String message;
   private Collection<String> warnings = new ArrayList<>();

   /** @return the reply's data, if any. */
   public T getData() { return data; }
   private void setData(T data) {
      if (data == null) {
         bad(new IllegalArgumentException(MESSAGE_INVALID_RESPONSE_DATA));
      } else {
         this.data = data;
         this.status = Status.OK;
      }
   }

   /** @return the response status, if any. */
   public Status getStatus() {
      return status == null ? Status.UNKNOWN : status;
   }

   /** @return the error data, if any. */
   public Throwable getError() { return error; }
   private void setError(Throwable error) {
      this.error = error;
      this.status = Status.BAD;
      if (error != null) { setMessage(error.getMessage()); }
      else { message = MESSAGE_DEFAULT; }
   }

   /** @return the error message, if any. */
   public String getMessage() { return message == null ? MESSAGE_DEFAULT : message; }
   private void setMessage(String message) {
      if (message != null && message.trim().length() > 0) { this.message = message; }
      else { this.message = MESSAGE_DEFAULT; }
   }

   /**
    * Signal a successful command.
    * @param data the command result's payload.
    * @return this reply.
    */
   public Reply<T> ok(T data) {
      setData(data);
      return this;
   }

   /**
    * Signal an error in a command.
    * @param t the root cause of the error.
    * @return this reply.
    */
   public Reply<T> bad(Throwable t) {
      setError(t);
      return this;
   }

   /**
    * Signal an error in a command, with an additional explanation.
    * @param t the root cause of the error.
    * @param errorMessage an optional explanation of the error. May be <code>null</code>.
    * @return this reply.
    */
   public Reply<T> bad(Throwable t, String errorMessage) {
      setError(t);
      this.message = errorMessage;
      return this;
   }

   /**
    * Signal an execution warning in a command, with additional information.
    * @param message the casue of the warning, if any.
    * @return this reply.
    */
   public Reply<T> warning(String message) {
      warnings.add(message != null ? message :
            "This reply signaled a warning without provided cause. please verify your code."
      );
      return this;
   }

   /**
    * Convenience status retrieval method.
    * @return {@code true} if the command was successful, {@code false} otherwise.
    */
   public boolean isOk() { return this.status == Status.OK; }

   /**
    * Convenience status retrieval method.
    * @return {@code true} if the command failed, {@code false} otherwise.
    */
   public boolean isBad() { return this.status == Status.BAD; }

   /**
    * Convenience status retrieval method.
    * @return {@code true} if the command includes warning messages, {@code false} otherwise.
    */
   public boolean isWarning() { return !this.warnings.isEmpty(); }

   @Override
   public String toString() {
      return String.format(
            "%s[stat: %s, msg: %s, err: %s]",
            this.getClass().getSimpleName(),
            this.status, this.message, this.error
      );
   }
}
