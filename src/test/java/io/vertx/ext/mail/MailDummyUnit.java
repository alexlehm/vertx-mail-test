package io.vertx.ext.mail;

import io.vertx.core.logging.Logger;
import io.vertx.core.logging.impl.LoggerFactory;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.Unit;
import io.vertx.test.core.VertxTestBase;

import org.junit.Test;

/*
 * smtp client test using vertx unit poc
 */
/**
 * @author <a href="http://oss.lehmann.cx/">Alexander Lehmann</a>
 *
 */
public class MailDummyUnit extends VertxTestBase {

  private static final Logger log = LoggerFactory.getLogger(MailDummyUnit.class);

  @Test
  public void mailTest() throws InterruptedException {
    Unit.test("mail test", test -> {
      log.info("starting");

      Async async = test.async();

      MailConfig mailConfig = new MailConfig("localhost", 1587);

      MailService mailService = MailService.create(vertx, mailConfig);

      MailMessage email=new MailMessage()
      .setFrom("user@example.com")
      .setBounceAddress("sender@example.com")
      .setTo("user@example.net")
      .setSubject("Test email")
      .setText("this is a message");

      mailService.sendMail(email, result -> {
        log.info("mail finished");
        if (result.succeeded()) {
          log.info(result.result().toString());
          async.complete();
        } else {
          log.warn("got exception", result.cause());
          test.fail(result.cause().toString());
        }
      });
    })
    .before(test -> {
      smtpServer = new TestSmtpServer(vertx, v -> test.async().complete());
    })
    .after(v -> {
      smtpServer.stop();
    })
    .runner().run();
    Thread.sleep(10000);
  }

  private TestSmtpServer smtpServer;
}
