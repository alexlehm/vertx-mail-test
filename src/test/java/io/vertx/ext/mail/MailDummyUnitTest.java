package io.vertx.ext.mail;

import io.vertx.core.Vertx;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.impl.LoggerFactory;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/*
 * smtp client test using vertx unit
 */
/**
 * @author <a href="http://oss.lehmann.cx/">Alexander Lehmann</a>
 *
 */
@RunWith(VertxUnitRunner.class)
public class MailDummyUnitTest {

  private static final Logger log = LoggerFactory.getLogger(MailDummyUnitTest.class);

  Vertx vertx = Vertx.vertx();

  @Test
  public void testMail(TestContext context) {
    log.info("starting");

    Async async = context.async();

    MailConfig mailConfig = new MailConfig("localhost", 1587);

    MailService mailService = MailService.create(vertx, mailConfig);

    MailMessage email = new MailMessage()
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
        context.fail(result.cause().toString());
      }
    });
  }

  @Before
  public void before(TestContext context) {
    log.info("starting smtp server");
    Async async = context.async();
    smtpServer = new TestSmtpServer(vertx, v -> async.complete());
  }

  @After
  public void after(TestContext context) {
    log.info("stopping smtp server");
    smtpServer.stop();
  }

  private TestSmtpServer smtpServer;
}
