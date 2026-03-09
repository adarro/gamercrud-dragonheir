package rest;

import email.Emails;
import io.quarkiverse.renarde.router.Router;
import io.quarkiverse.renarde.security.ControllerWithUser;
import io.quarkiverse.renarde.security.RenardeSecurity;
import io.quarkiverse.renarde.util.StringUtils;
import io.quarkus.elytron.security.common.BcryptUtil;
import io.quarkus.qute.CheckedTemplate;
import io.quarkus.qute.TemplateInstance;
import io.quarkus.security.Authenticated;
import io.quarkus.security.webauthn.WebAuthnCredentialRecord;
import io.quarkus.security.webauthn.WebAuthnLoginResponse;
import io.quarkus.security.webauthn.WebAuthnRegisterResponse;
import io.quarkus.security.webauthn.WebAuthnSecurity;
import io.smallrye.common.annotation.Blocking;
import io.vertx.ext.web.RoutingContext;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.ws.rs.BeanParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.core.NewCookie;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.ResponseBuilder;
import model.User;
import model.UserStatus;
import model.WebAuthnCredential;
import org.jboss.logging.Logger;
import org.jboss.resteasy.reactive.RestForm;
import org.jboss.resteasy.reactive.RestQuery;

import java.util.UUID;

@Blocking
public class Login extends ControllerWithUser<User> {

  private static final Logger logger = Logger.getLogger(Login.class);
  @Inject
  RenardeSecurity security;

  @Inject
  WebAuthnSecurity webAuthnSecurity;

  @CheckedTemplate
  static class Templates {
    public static native TemplateInstance login();

    public static native TemplateInstance register(String email);

    public static native TemplateInstance confirm(User newUser);

    public static native TemplateInstance confirmWebAuth(User newUser);

    public static native TemplateInstance logoutFirst();

    public static native TemplateInstance welcome();
  }

  /**
   * Login page
   */
  public TemplateInstance login() {
    return Templates.login();
  }

  /**
   * Welcome page at the end of registration
   */
  @Authenticated
  public TemplateInstance welcome() {
    return Templates.welcome();
  }

  /**
   * Manual login form
   */
  @POST
  public Response manualLogin(@NotBlank @RestForm String userName,
                              @RestForm String password,
                              @BeanParam WebAuthnLoginResponse webAuthnResponse,
                              RoutingContext ctx) {
    if (webAuthnResponse.isSet()) {
      validation.required("webAuthnId", webAuthnResponse.webAuthnId);
      validation.required("webAuthnRawId", webAuthnResponse.webAuthnRawId);
      validation.required("webAuthnResponseClientDataJSON", webAuthnResponse.webAuthnResponseClientDataJSON);
      validation.required("webAuthnResponseAuthenticatorData", webAuthnResponse.webAuthnResponseAuthenticatorData);
      validation.required("webAuthnResponseSignature", webAuthnResponse.webAuthnResponseSignature);
      // UserHandle not required
      validation.required("webAuthnType", webAuthnResponse.webAuthnType);
    } else {
      validation.required("password", password);
    }
    if (validationFailed()) {
      login();
    }
    User user = User.findRegisteredByUserName(userName);
    if (user == null) {
      validation.addError("userName", "Invalid username/password");
      prepareForErrorRedirect();
      login();
    }
    if (!webAuthnResponse.isSet()) {
      if (!BcryptUtil.matches(password, user.password)) {
        validation.addError("userName", "Invalid username/password");
        prepareForErrorRedirect();
        login();
      }
    } else {
      // This is sync anyway
      var authenticator = this.webAuthnSecurity.login(webAuthnResponse, ctx)
        .await().indefinitely();
      // bump the auth counter
      user.webAuthnCredential.counter = authenticator.getCounter();
    }
    NewCookie cookie = security.makeUserCookie(user);
    return Response.seeOther(Router.getURI(Application::index)).cookie(cookie).build();
  }

  /**
   * Manual registration form, sends confirmation email
   */
  @POST
  public TemplateInstance register(@RestForm @NotBlank @Email String email) {
    if (validationFailed())
      login();
    User newUser = User.findUnconfirmedByEmail(email);
    if (newUser == null) {
      newUser = new User();
      newUser.email = email;
      newUser.status = UserStatus.CONFIRMATION_REQUIRED;
      newUser.confirmationCode = UUID.randomUUID().toString();
      newUser.persist();
    }
    // send the confirmation code again
    Emails.confirm(newUser);
    return Templates.register(email);
  }


  /**
   * Confirmation form, once email is verified, to add user info
   */
  public TemplateInstance confirm(@RestQuery String confirmationCode) {
    checkLogoutFirst();
    User newUser = checkConfirmationCode(confirmationCode);
    return Templates.confirm(newUser);
  }

  public TemplateInstance confirmWebAuth(@RestQuery String confirmationCode) {
    checkLogoutFirst();
    User newUser = checkConfirmationCode(confirmationCode);
    return Templates.confirmWebAuth(newUser);
  }

  private void checkLogoutFirst() {
    if (getUser() != null) {
      logoutFirst();
    }
  }

  /**
   * Link to logout page
   */
  public TemplateInstance logoutFirst() {
    return Templates.logoutFirst();
  }

  private User checkConfirmationCode(String confirmationCode) {
    // can't use error reporting as those are query parameters and not form fields
    if (StringUtils.isEmpty(confirmationCode)) {
      flash("message", "Missing confirmation code");
      flash("messageType", "error");
      redirect(Application.class).index();
    }
    User user = User.findForContirmation(confirmationCode);
    if (user == null) {
      flash("message", "Invalid confirmation code");
      flash("messageType", "error");
      redirect(Application.class).index();
    }
    return user;
  }

  @POST
  @Transactional
  public Response complete(@RestQuery String confirmationCode,
                           @RestForm @NotBlank String userName,
                           @RestForm String password,
                           @RestForm String password2,
                           @BeanParam WebAuthnRegisterResponse webAuthnResponse,
                           @RestForm @NotBlank String firstName,
                           @RestForm @NotBlank String lastName,
                           RoutingContext ctx) {
    logger.debug(String.format("Completing registration for %s ", userName));
    checkLogoutFirst();
    User user = checkConfirmationCode(confirmationCode);


    if (validationFailed()) {
      logger.warn("Validation failed confirming user");
      confirmWebAuth(confirmationCode);
    }
    logger.debug("passed initial validation");
    logger.debug(String.format("is webAuth? %b", webAuthnResponse.isSet()));
    // is it OIDC?
    if (!user.isOidc()) {
      if (!webAuthnResponse.isSet()) {
        validation.required("password", password);
        validation.required("password2", password2);
        validation.equals("password", password, password2);
      } else {
        validation.required("webAuthnId", webAuthnResponse.webAuthnId);
        validation.required("webAuthnRawId", webAuthnResponse.webAuthnRawId);
        validation.required("webAuthnResponseAttestationObject", webAuthnResponse.webAuthnResponseAttestationObject);
        validation.required("webAuthnResponseClientDataJSON", webAuthnResponse.webAuthnResponseClientDataJSON);
        validation.required("webAuthnType", webAuthnResponse.webAuthnType);
      }
    }

    if (User.findRegisteredByUserName(userName) != null)
      validation.addError("userName", "User name already taken");
    if (validationFailed())
      confirmWebAuth(confirmationCode);

    if (!user.isOidc()) {
      if (!webAuthnResponse.isSet()) {
        user.password = BcryptUtil.bcryptHash(password);
      } else {
        // this is sync
        var authenticator = webAuthnSecurity.register(userName, webAuthnResponse, ctx).await().indefinitely();
        WebAuthnCredential creds = new WebAuthnCredential(authenticator, user);
        creds.persist();
      }
    }
    user.username = userName;
    user.firstName = firstName;
    user.lastName = lastName;
    user.confirmationCode = null;
    user.status = UserStatus.REGISTERED;

    ResponseBuilder responseBuilder = Response.seeOther(Router.getURI(Login::welcome));
    if (!user.isOidc()) {
      NewCookie cookie = security.makeUserCookie(user);
      responseBuilder.cookie(cookie);
    }
    return responseBuilder.build();
  }
}
