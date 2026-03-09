package util;

import java.util.Date;

import io.quarkus.elytron.security.common.BcryptUtil;
import jakarta.transaction.Transactional;
import model.Todo;
import model.User;
import model.UserStatus;

public class Startup {
  @io.quarkus.runtime.Startup
  @Transactional
  public void onStartup() {
    System.err.println("Adding user admin");
    User adm = new User();
    adm.email = "admin@example.com";
    adm.firstName = "Michael";
    adm.lastName = "Hawk";
    adm.username = "MiHawk";
    adm.password = BcryptUtil.bcryptHash("1q2w3e4r");
    adm.status = UserStatus.REGISTERED;
    adm.isAdmin = true;
    adm.persist();

    Todo todo = new Todo();
    todo.owner = adm;
    todo.task = "Buy cheese";
    todo.done = true;
    todo.doneDate = new Date();
    todo.persist();

    todo = new Todo();
    todo.owner = adm;
    todo.task = "Eat cheese";
    todo.persist();

    todo = new Todo();
    todo.owner = adm;
    todo.task = "Remove me before production";
    todo.persist();

    System.err.println("Adding unconfirmed user");
    User unconfirmedUser = new User();
    unconfirmedUser.email = "newGuy@example.com";
    unconfirmedUser.confirmationCode = "384d007b-45bc-472a-9b7a-2fd1e160cf72";
    unconfirmedUser.status = UserStatus.CONFIRMATION_REQUIRED;
    unconfirmedUser.persist();

  }
}
