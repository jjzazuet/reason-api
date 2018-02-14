package unit;

import j8spec.junit.J8SpecRunner;
import net.tribe7.reason.Check;
import org.junit.runner.RunWith;
import static j8spec.J8Spec.*;
import static org.junit.Assert.*;

@RunWith(J8SpecRunner.class)
public class AssertSpec { static {
  it("Parses default root cause constants.", () -> {
    assertTrue(Check.valueOf(Check.GENERAL_ERROR.toString()).equals(Check.GENERAL_ERROR));
    assertTrue(Check.valueOf(Check.MISSING_DATA.toString()).equals(Check.MISSING_DATA));
    assertTrue(Check.valueOf(Check.CONDITION_NOT_SATISFIED.toString()).equals(Check.CONDITION_NOT_SATISFIED));
  });
  it("Encodes an enum constant as a string", () ->
      assertFalse(Check.err(MyErrors.OOPS_I_FLOPPED).contains("_"))
  );
  it("Encodes a null enum constant as a default constant.", () ->
      assertTrue(Check.err(null).equals(Check.err(Check.GENERAL_ERROR)))
  );
  it("Checks that an argument is effectively not null.", () ->
      Check.notNull(new Integer []{})
  );
  it("Checks that an argument is effectively not null, with a custom error constant.", () ->
      Check.notNull(new Integer []{}, MyErrors.OOPS_I_FLOPPED)
  );
  it("Fails that an argument is not null, with no root cause provided.",
      c -> c.expected(IllegalStateException.class), () -> Check.notNull(null));
  it("Fails that an argument is not null, with a default root cause as a message.", () -> {
    try { Check.notNull(null); }
    catch (Exception e) {
      assertTrue(e instanceof IllegalStateException);
      assertNotNull(e.getMessage());
      assertTrue(e.getMessage().equals(Check.err(Check.MISSING_DATA)));
    }
  });
  it("Fails that an argument is not null, with a constant root cause provided.", () -> {
    try { Check.notNull(null, MyErrors.OOPS_I_FLOPPED); }
    catch (Exception e) {
      assertTrue(e instanceof IllegalStateException);
      assertTrue(e.getMessage().equals(Check.err(MyErrors.OOPS_I_FLOPPED)));
    }
  });
  it("Fails that an argument is not null, with a default root cause constant if invoked incorrectly.", () -> {
    try { Check.notNull(null, (Enum) null); }
    catch (Exception e) {
      assertTrue(e instanceof IllegalStateException);
      assertTrue(e.getMessage().equals(Check.err(Check.MISSING_DATA)));
    }
  });
  it("Fails that an argument is not null, with a custom root cause message.", () -> {
    String msg = "Oops, we flopped";
    try { Check.notNull(null, msg); }
    catch (Exception e) {
      assertTrue(e instanceof IllegalStateException);
      assertTrue(e.getMessage().equals(msg));
    }
  });
  it("Fails that an argument is not null, with a default root cause message if invoked incorrectly.", () -> {
    try { Check.notNull(null, (String) null); }
    catch (Exception e) {
      assertTrue(e instanceof IllegalStateException);
      assertTrue(e.getMessage().equals(Check.err(Check.MISSING_DATA)));
    }
  });
  it("Fails that an argument is not null, with a default root cause message if invoked with an empty message.", () -> {
    try { Check.notNull(null, ""); }
    catch (Exception e) {
      assertTrue(e instanceof IllegalStateException);
      assertTrue(e.getMessage().equals(Check.err(Check.MISSING_DATA)));
    }
  });

  it("Checks that an argument is effectively true.", () -> Check.isTrue(true));
  it("Checks that an argument is effectively true, with a custom error constant.", () ->
    Check.isTrue(true, MyErrors.OOPS_I_FLOPPED)
  );
  it("Fails a false argument.", c -> c.expected(IllegalStateException.class), () -> Check.isTrue(false));
  it("Fails a false argument, with a missing root cause constant.", () -> {
    try { Check.isTrue(false, (Enum) null); }
    catch (Exception e) {
      assertTrue(e instanceof IllegalStateException);
      assertNotNull(e.getMessage());
      assertTrue(e.getMessage().equals(Check.err(Check.CONDITION_NOT_SATISFIED)));
    }
  });
  it("Fails a false argument, with a missing root cause message.", () -> {
    try { Check.isTrue(false, (String) null); }
    catch (Exception e) {
      assertTrue(e instanceof IllegalStateException);
      assertNotNull(e.getMessage());
      assertTrue(e.getMessage().equals(Check.err(Check.CONDITION_NOT_SATISFIED)));
    }
  });
}}

enum MyErrors { OOPS_I_FLOPPED }
