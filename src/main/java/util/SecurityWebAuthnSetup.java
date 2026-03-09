package util;


import java.util.Collections;
import java.util.List;
import java.util.Set;

import io.quarkus.security.webauthn.WebAuthnCredentialRecord;
import io.quarkus.security.webauthn.WebAuthnUserProvider;
import io.smallrye.common.annotation.Blocking;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import model.User;
import model.WebAuthnCredential;

@Blocking
@ApplicationScoped
public class SecurityWebAuthnSetup implements WebAuthnUserProvider {

    @Transactional
    @Override
    public Uni<List<WebAuthnCredentialRecord>> findByUsername(String userId) {
        return Uni.createFrom().item(
                WebAuthnCredential.findByUsername(userId)
                        .stream()
                        .map(WebAuthnCredential::toWebAuthnCredentialRecord)
                        .toList());
    }

    @Transactional
    @Override
    public Uni<WebAuthnCredentialRecord> findByCredentialId(String credId) {
        WebAuthnCredential creds = WebAuthnCredential.findByCredentialId(credId);
        if(creds == null)
            return Uni.createFrom()
                    .failure(new RuntimeException("No such credential ID"));
        return Uni.createFrom().item(creds.toWebAuthnCredentialRecord());
    }

    @Override
    public Set<String> getRoles(String userId) {
        if(userId.equals("admin")) {
            return Set.of("user", "admin");
        }
        return Collections.singleton("user");
    }
}
