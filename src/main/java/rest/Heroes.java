package rest;

import io.quarkiverse.renarde.Controller;
import io.quarkus.qute.CheckedTemplate;
import io.quarkus.qute.TemplateInstance;
import io.truthencode.games.dragonheir.model.Hero;

import java.util.List;

public class Heroes extends Controller {

    @CheckedTemplate
    public static class Templates {
        /**
         * This specifies that the Todos/index.html template does not take any parameter
         */
        public static native TemplateInstance index();

        /**
         * This specifies that the Todos/todos.html template takes a todos parameter of type List&lt;Todo&gt;
         */
        public static native TemplateInstance heroes(List<Hero> heroes);
    }

    public TemplateInstance index() {
        // renders the Todos/index.html template
        return Heroes.Templates.index();
    }

    public TemplateInstance heroes() {
        return Heroes.Templates.heroes(Hero.listAll());
    }
}
