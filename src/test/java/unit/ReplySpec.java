package unit;

import j8spec.junit.J8SpecRunner;
import net.tribe7.reason.Reply;
import org.junit.runner.RunWith;

import static j8spec.J8Spec.*;
import static org.junit.Assert.*;
import static net.tribe7.reason.Reply.Status.*;

@RunWith(J8SpecRunner.class)
public class ReplySpec {{
  context("A reply", () -> {
    it("enumerates status values.", () -> {
      for (Reply.Status status : values()) { System.out.println(status); }
    });
    it("can parse a status value.", () -> assertTrue(valueOf("OK") == OK));
    it("has unknown status by default", () -> assertTrue(new Reply().getStatus() == UNKNOWN));
    it("holds an error message with no additional information by default.", () ->
      assertTrue(new Reply().getMessage().equals(Reply.MESSAGE_DEFAULT))
    );
    it("holds no error data.", () -> assertNull(new Reply().getError()));
    it("default status is UNKNOWN, thus not in a failed or successful state.", () -> {
      Reply r = new Reply();
      assertFalse(r.isOk());
      assertFalse(r.isBad());
    });
    it("always indicates if it is successful.", () -> {
      Reply<Long> r = new Reply<Long>().ok(0L);
      assertTrue(r.isOk());
      assertEquals(r.getStatus(), OK);
    });
    it("always indicates if it is bad.", () -> {
      Reply r = new Reply().bad(new IllegalStateException());
      assertTrue(r.isBad());
      assertEquals(r.getStatus(), BAD);
    });
    it("always has an error message even if no error cause was provided.", () -> {
      Reply r = new Reply().bad(null);
      assertTrue(r.isBad());
      assertEquals(r.getStatus(), BAD);
      assertEquals(r.getMessage(), Reply.MESSAGE_DEFAULT);
    });
    it("always has an OK status if a payload was assigned, and holds no additional error information.", () -> {
      Reply<Long> r = new Reply<Long>().ok(0L);
      assertEquals(r.getMessage(), Reply.MESSAGE_DEFAULT);
      assertNull(r.getError());
      assertTrue(r.isOk());
      assertFalse(r.isBad());
      assertEquals(r.getData(), new Long(0L));
    });
    it("always has an ERROR status, plus an error message if it fails with a single exception.", () -> {
      Reply r = new Reply().bad(new IllegalStateException("A processing error occurred."));
      assertTrue(r.isBad());
      assertNotNull(r.getError());
      assertNotNull(r.getMessage());
      assertNotEquals(r.getMessage(), Reply.MESSAGE_DEFAULT);
    });
    it("always has an ERROR status, plus an error message, even if the command fails without specifying a cause.", () -> {
      Reply r = new Reply().bad(new IllegalStateException());
      assertTrue(r.isBad());
      assertNotNull(r.getError());
      assertNotNull(r.getMessage());
      assertEquals(r.getMessage(), Reply.MESSAGE_DEFAULT);
    });
    it("has an ERROR status, plus a non-default error message if the caller specifies one.", () -> {
      String myCustomMsg = "Something really bad happened.";
      Reply r = new Reply().bad(new IllegalStateException(myCustomMsg));
      assertNotNull(r.getError());
      assertTrue(r.isBad());
      assertEquals(r.getMessage(), myCustomMsg);
    });
    it("guarantees that a failed command will always have a readable text message.", () -> {
      String oops = "";
      Reply r = new Reply().bad(new IllegalStateException(oops));
      assertNotNull(r.getMessage());
      assertTrue(r.getMessage().length() > 0);
    });
    it("fails automatically if it is indicated to succeed with an invalid response payload.", () -> {
      Reply<String> r = new Reply<String>().ok(null);
      assertNotNull(r.getError());
      assertTrue(r.isBad());
      assertEquals(r.getMessage(), Reply.MESSAGE_INVALID_RESPONSE_DATA);
    });
    it("can include warnings if it succeeds.", () -> {
      Reply<Integer> r = new Reply<Integer>().ok(12345);
      r.warning("This service method call will be deprecated in the next version, so stop using it.");
      assertTrue(r.isOk());
      assertTrue(r.isWarning());
      assertFalse(r.getWarnings().isEmpty());
    });
    it("can include warnings if it fails.", () -> {
      Reply<Integer> r = new Reply<Integer>().bad(new IllegalStateException());
      r.warning("This service method call will be deprecated in the next version, so stop using it.");
      assertTrue(r.isBad());
      assertTrue(r.isWarning());
      assertFalse(r.getWarnings().isEmpty());
    });
    it("includes a default warning message when a warning is signaled without a cause.", () -> {
      Reply r = new Reply().bad(new IllegalStateException("oops"));
      r.warning(null);
      assertTrue(r.isBad());
      assertTrue(r.isWarning());
      assertFalse(r.getWarnings().isEmpty());
    });
    it("has no warnings and does not indicate so if no warnings were issued at all.", () -> {
      Reply<Long> r = new Reply<Long>().ok(12345L);
      assertFalse(r.isWarning());
      assertTrue(r.getWarnings().isEmpty());
    });
    it("can be logged to the console.", () -> {
      Reply<Integer> r = new Reply<Integer>().ok(123);
      String rs = r.toString();
      assertNotNull(rs);
      assertTrue(rs.length() > 0);
    });
  });
}}
