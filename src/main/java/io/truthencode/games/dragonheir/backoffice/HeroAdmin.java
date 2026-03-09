package io.truthencode.games.dragonheir.backoffice;

import io.quarkiverse.renarde.backoffice.BackofficeController;
import io.quarkus.security.Authenticated;

import io.truthencode.games.dragonheir.model.Hero;

@Authenticated
public class HeroAdmin extends BackofficeController<Hero> {
  
}
