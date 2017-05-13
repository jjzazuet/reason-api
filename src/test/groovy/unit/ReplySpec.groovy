package unit

import net.tribe7.reason.Reply
import spock.lang.Specification
import static net.tribe7.reason.Reply.Status.*

class ReplySpec extends Specification {

   void "Status values for a Reply can be enumerated."() {
      values().each { s -> println s }
      expect: true
   }

   void "A status value for a Reply can be parsed."() {
      def stat = valueOf('OK')
      expect: stat && stat == OK
   }

   void "By default, a Reply's status is unknown."() {
      def cr = new Reply()
      expect: cr.status == UNKNOWN
   }

   void "By default, a Reply's error message holds no additional information."() {
      def cr = new Reply()
      expect: cr.message == Reply.MESSAGE_DEFAULT
   }

   void "By default, a Reply holds no error data."() {
      def cr = new Reply()
      expect: cr.error == null
   }

   void "By default, a Reply status is UNKNOWN, thus it is not in a failed or successful state."() {
      def cr = new Reply()
      expect: !cr.ok && !cr.bad
   }

   void "A successful Reply always indicates so."() {
      def cr = new Reply().ok(0L)
      expect: cr.ok && cr.status == OK
   }

   void "A bad Reply always indicates so."() {
      def cr = new Reply().bad(new IllegalStateException())
      expect: cr.bad && cr.status == BAD
   }

   void "A bad Reply has a message, even if no error cause was provided."() {
      def cr = new Reply().bad(null)
      expect: cr.bad && cr.status == BAD && cr.message == Reply.MESSAGE_DEFAULT
   }

   void "A Reply always has an OK status if a payload was assigned, and holds no additional error information."() {
      def cr = new Reply<Long>().ok(0L)
      expect: cr.message == Reply.MESSAGE_DEFAULT &&
            cr.error == null && cr.ok && !cr.bad && cr.data == 0L
   }

   void "A Reply always has an ERROR status, plus an error message if it fails with a single exception."() {
      def cr = new Reply().bad(new IllegalStateException("A processing error occurred."))
      expect: cr.bad && cr.error && cr.message != null &&
            cr.message != Reply.MESSAGE_DEFAULT
   }

   void "A Reply always has an ERROR status, plus an error message, even if the command fails without specifying a cause."() {
      def cr = new Reply().bad(new IllegalStateException())
      expect: cr.bad && cr.error && cr.message != null &&
            cr.message == Reply.MESSAGE_DEFAULT
   }

   void "A Reply has an ERROR status, plus a non-default error message if the caller specifies one."() {
      def myCustomMsg = 'Something really bad happened.'
      def cr = new Reply().bad(new IllegalStateException(), myCustomMsg)
      expect: cr.error && cr.bad && cr.message != null && cr.message == myCustomMsg
   }

   void "A Reply guarantees that a failed command will always have a readable text message."() {
      def oops = ''
      def cr = new Reply().bad(new IllegalStateException(oops))
      expect: cr.message != null && cr.message.length() > 0
   }

   void "A Reply fails automatically if it is indicated to succeed with an invalid response payload."() {
      def oops = null
      def cr = new Reply().ok(oops)
      expect: cr.error && cr.bad && cr.message == Reply.MESSAGE_INVALID_RESPONSE_DATA
   }

   void "A successful Reply can include warnings."() {
      def cr = new Reply().ok(12345)
      cr.warning("This service method call will be deprecated, so stop using it.")
      expect: cr.ok && cr.warning
   }

   void "A bad Reply can include warnings."() {
      def cr = new Reply().bad(new IllegalStateException())
      cr.warning("This service method call is now deprecated. Stop using it.")
      expect: cr.bad && cr.warning
   }

   void "A Reply must include a default warning message when a warning is signaled without a cause."() {
      def cr = new Reply().bad(new IllegalStateException('oops'))
      cr.warning(null)
      expect: cr.bad && cr.warning
   }

   void "A Reply that recorded no warnings must not contain any warnings."() {
      def cr = new Reply().ok(123456)
      expect: !cr.warning
   }

   void "A Reply can be logged to console."() {
      def cr = new Reply().ok(123)
      def toString = cr.toString()
      expect: toString && toString.length() > 0
   }
}
