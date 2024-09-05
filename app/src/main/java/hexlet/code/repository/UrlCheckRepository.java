package hexlet.code.repository;

import hexlet.code.model.UrlCheck;

import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

public class UrlCheckRepository extends BaseRepository {
    public static void save(UrlCheck urlCheck) throws SQLException {
        var sql = "INSERT INTO url_checks (url_id, status_code, title, h1, description, created_at) "
                + "VALUES (?, ?, ?, ?, ?, ?)";

        try (var conn = dataSource.getConnection();
             var preparedStatement = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            var createdAt = LocalDateTime.now();
            preparedStatement.setLong(1, urlCheck.getUrlId());
            preparedStatement.setInt(2, urlCheck.getStatusCode());
            preparedStatement.setString(3, urlCheck.getTitle());
            preparedStatement.setString(4, urlCheck.getH1());
            preparedStatement.setString(5, urlCheck.getDescription());
            preparedStatement.setTimestamp(6, Timestamp.valueOf(createdAt));
            preparedStatement.executeUpdate();
            var generatedKeys = preparedStatement.getGeneratedKeys();
            if (generatedKeys.next()) {
                urlCheck.setId(generatedKeys.getLong(1));
                urlCheck.setCreatedAt(createdAt);
            } else {
                throw new SQLException("DB have not returned an id after saving an entity");
            }
        }
    }

    public static List<UrlCheck> getEntities(Long urlId) throws SQLException {
        var sql = "SELECT * FROM url_checks WHERE url_id = ?";
        try (var conn = dataSource.getConnection();
             var stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, urlId);
            var resultSet = stmt.executeQuery();
            var result = new ArrayList<UrlCheck>();
            while (resultSet.next()) {
                var id = resultSet.getLong("id");
                //var urlId = resultSet.getLong("urlId");
                var statusCode = resultSet.getInt("status_code");
                var title = resultSet.getString("title");
                var h1 = resultSet.getString("h1");
                var description = resultSet.getString("description");
                var createdAt = resultSet.getTimestamp("created_at").toLocalDateTime();
                var urlCheckTemp = new UrlCheck(id, urlId, statusCode, title, h1, description, createdAt);
                result.add(urlCheckTemp);
            }
            return result;
        }
    }

    public static Optional<UrlCheck> getLastCheck(Long urlId) throws SQLException {
        var sql = "SELECT * FROM url_checks WHERE url_id = ? "
                + "ORDER BY created_at DESC LIMIT 1";
        try (var conn = dataSource.getConnection();
             var stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, urlId);
            var resultSet = stmt.executeQuery();

            if (resultSet.next()) {
                var urlCheckTemp = new UrlCheck();
                urlCheckTemp.setId(resultSet.getLong("id"));
                urlCheckTemp.setStatusCode(resultSet.getInt("status_code"));
                urlCheckTemp.setTitle(resultSet.getString("title"));
                urlCheckTemp.setH1(resultSet.getString("h1"));
                urlCheckTemp.setDescription(resultSet.getString("description"));
                urlCheckTemp.setCreatedAt(resultSet.getTimestamp("created_at").toLocalDateTime());
                return Optional.of(urlCheckTemp);
            }
            return Optional.empty();
        }
    }

    public static HashMap<Long, UrlCheck> getLastChecks() throws SQLException {
        var sql = "SELECT distinct on (url_id) * FROM url_checks order by url_id, id desc";
        try (var conn = dataSource.getConnection();
             var stmt = conn.prepareStatement(sql)) {
            var resultSet = stmt.executeQuery();
            HashMap<Long, UrlCheck> result = new HashMap<>();
            while (resultSet.next()) {
                var urlCheckTemp = new UrlCheck();
                urlCheckTemp.setId(resultSet.getLong("id"));
                urlCheckTemp.setUrlId(resultSet.getLong("url_id"));
                urlCheckTemp.setStatusCode(resultSet.getInt("status_code"));
                urlCheckTemp.setTitle(resultSet.getString("title"));
                urlCheckTemp.setH1(resultSet.getString("h1"));
                urlCheckTemp.setDescription(resultSet.getString("description"));
                urlCheckTemp.setCreatedAt(resultSet.getTimestamp("created_at").toLocalDateTime());
                result.put(urlCheckTemp.getUrlId(), urlCheckTemp);
            }
            return result;
        }
    }
}
