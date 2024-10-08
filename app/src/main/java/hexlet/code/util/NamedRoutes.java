package hexlet.code.util;

public class NamedRoutes {
    public static String urlsPath() {
        return "/urls/";
    }
    public static String urlPath(String id) {
        return urlsPath() + id;
    }
    public static String urlPath(Long id) {
        return urlsPath() + id;
    }
    public static String mainPath() {
        return "/";
    }
    public static String runCheckPath(String urlId) {
        return urlsPath() + urlId + "/checks";
    }
    public static String runCheckPath(Long urlId) {
        return urlsPath() + urlId + "/checks";
    }
}
