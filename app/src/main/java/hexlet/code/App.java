package hexlet.code;

import io.javalin.Javalin;
import io.javalin.rendering.template.JavalinJte;
import lombok.extern.slf4j.Slf4j;

public class App {

    public static Javalin getApp() {
        var app = Javalin.create(config -> {
            config.bundledPlugins.enableDevLogging();
            config.fileRenderer(new JavalinJte());
        });
        app.get("/", ctx -> ctx.result("Hello World"));
        return app;
    }

    public static void main(String[] args) {
        Javalin app = getApp();
        app.start(getPort());
    }

    private static int getPort() {
        String port = System.getenv().getOrDefault("PORT", "7071");
        return Integer.valueOf(port);
    }
    //@Slf4j
    /*public class HelloWorld {
        public static void main(String[] args) {
            var app = Javalin.create(*//*config*//*)
                    .get("/", ctx -> ctx.result("Hello World"))
                    .start(7070);
        }


    }*/
}
