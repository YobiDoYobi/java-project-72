package hexlet.code.controller;

import hexlet.code.dto.BasePage;
import hexlet.code.dto.UrlPage;
import hexlet.code.dto.UrlsPage;
import hexlet.code.model.Url;
import hexlet.code.repository.UrlRepository;
import hexlet.code.util.NamedRoutes;
import io.javalin.http.Context;
import io.javalin.http.NotFoundResponse;

import java.net.MalformedURLException;
import java.net.URI;
import java.sql.SQLException;

import static io.javalin.rendering.template.TemplateUtil.model;

public class UrlsController {
    public static void index(Context ctx) throws SQLException {
        var urls = UrlRepository.getEntities();
        var page = new UrlsPage(urls);
        page.setFlash(ctx.consumeSessionAttribute("flash"));
        ctx.render("index.jte", model("page", page));
    }

    public static void show(Context ctx) throws SQLException {
        var id = ctx.pathParamAsClass("id", Long.class).get();
        var car = UrlRepository.find(id)
                .orElseThrow(() -> new NotFoundResponse("Entity with id = " + id + " not found"));
        var page = new UrlPage(car);
        ctx.render("show.jte", model("page", page));
    }

    public static void create(Context ctx) throws SQLException {
        var urlStr = ctx.formParam("url");
        try {
            URI.create(urlStr).toURL();
            var uri = URI.create(urlStr);
            var urlResultStr = uri.getScheme() + "://" + uri.getHost();
            if (UrlRepository.exist(urlResultStr)) {
                ctx.sessionAttribute("flash", "Страница уже существует!");
                ctx.redirect(NamedRoutes.mainPath());
            } else {
                UrlRepository.save(new Url(urlResultStr));
                ctx.sessionAttribute("flash", "Страница успешно добавлена!");
                ctx.redirect(NamedRoutes.urlsPath());
            }
        } catch (MalformedURLException | IllegalArgumentException e) {
            ctx.sessionAttribute("flash", "Некорректный URL!");
            ctx.redirect(NamedRoutes.mainPath());
        }
    }

    public static void buid(Context ctx) throws SQLException {
        var page = new BasePage();
        page.setFlash(ctx.consumeSessionAttribute("flash"));
        ctx.render("main.jte", model("page", page));
    }
}
