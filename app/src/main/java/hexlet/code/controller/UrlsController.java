package hexlet.code.controller;

import hexlet.code.dto.BasePage;
import hexlet.code.dto.UrlPage;
import hexlet.code.dto.UrlsPage;
import hexlet.code.model.Url;
import hexlet.code.model.UrlCheck;
import hexlet.code.repository.UrlCheckRepository;
import hexlet.code.repository.UrlRepository;
import hexlet.code.util.NamedRoutes;
import io.javalin.http.Context;
import io.javalin.http.NotFoundResponse;
import kong.unirest.Unirest;
import kong.unirest.UnirestException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.net.MalformedURLException;
import java.net.URI;
import java.sql.SQLException;

import static io.javalin.rendering.template.TemplateUtil.model;

public class UrlsController {
    public static void index(Context ctx) throws SQLException {
        var urls = UrlRepository.getEntities();
        var urlChecks = UrlCheckRepository.getLastChecks();
        var page = new UrlsPage(urls, urlChecks);
        consumeSessionAttribute(page, ctx);
        ctx.render("index.jte", model("page", page));
    }

    public static void show(Context ctx) throws SQLException {
        var id = ctx.pathParamAsClass("id", Long.class).get();
        var url = UrlRepository.find(id)
                .orElseThrow(() -> new NotFoundResponse("Entity with id = " + id + " not found"));
        var checks = UrlCheckRepository.getEntities(id);
        var page = new UrlPage(url, checks);
        consumeSessionAttribute(page, ctx);
        ctx.render("show.jte", model("page", page));
    }

    public static void create(Context ctx) throws SQLException {
        var urlStr = ctx.formParam("url");
        try {
            var url = URI.create(urlStr).toURL();
            //var urlResultStr = uri.getScheme() + "://" + uri.getAuthority();
            var urlResultStr = url.getProtocol() + "://" + url.getAuthority();
            if (UrlRepository.exist(urlResultStr)) {
                setSessionAttribute(ctx, "Error", "Страница уже существует!");
                ctx.redirect(NamedRoutes.mainPath());
            } else {
                UrlRepository.save(new Url(urlResultStr));
                setSessionAttribute(ctx, "Success", "Страница успешно добавлена!");
                ctx.redirect(NamedRoutes.urlsPath());
            }
        } catch (MalformedURLException | IllegalArgumentException e) {
            setSessionAttribute(ctx, "Error", "Некорректный URL!");
            ctx.redirect(NamedRoutes.mainPath());
        }
    }

    public static void buid(Context ctx) throws SQLException {
        var page = new BasePage();
        consumeSessionAttribute(page, ctx);
        ctx.render("main.jte", model("page", page));
    }

    public static void checkUrl(Context ctx) throws SQLException {
        var urlId = ctx.pathParam("id");
        var url = UrlRepository.find(Long.valueOf(urlId));
        var urlSring = url.get().getName();
        Unirest.config().reset();
        Unirest.config().connectTimeout(1000);
        try {
            var responseHttp = Unirest.get(urlSring).asString();
            String body = responseHttp.getBody();
            Document doc = Jsoup.parse(body);
            String title = doc.title();
            String h1 = doc.select("h1").text();
            String description = doc.select("meta[name=description]").attr("content");
            var urlCheck = new UrlCheck(Long.valueOf(urlId), responseHttp.getStatus(), title, h1, description);
            UrlCheckRepository.save(urlCheck);
        } catch (UnirestException e) {
            var urlCheck = new UrlCheck(Long.valueOf(urlId));
            urlCheck.setDescription(e.getMessage());
            urlCheck.setTitle("Invalid URL");
            UrlCheckRepository.save(urlCheck);
            setSessionAttribute(ctx, "Error", "Некорректный URL!");
        }
        ctx.redirect(NamedRoutes.urlPath(urlId));
    }

    private static void setSessionAttribute(Context ctx, String type, String message) {
        ctx.sessionAttribute("type", type);
        ctx.sessionAttribute("message", message);
    }

    private static void consumeSessionAttribute(BasePage page, Context ctx) {
        page.setMessage(ctx.consumeSessionAttribute("message"));
        page.setType(ctx.consumeSessionAttribute("type"));
    }
}
