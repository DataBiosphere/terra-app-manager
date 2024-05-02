package bio.terra.appmanager.dao;

import bio.terra.appmanager.model.ChartVersion;
import io.opentelemetry.instrumentation.annotations.WithSpan;
import jakarta.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class ChartVersionDao {

  private static final RowMapper<ChartVersion> CHART_VERSION_ROW_MAPPER =
      (rs, rowNum) ->
          new ChartVersion(
              rs.getString("chart_name"),
              rs.getString("chart_version"),
              rs.getString("app_version"),
              rs.getDate("created_at"),
              rs.getDate("deleted_at"));

  private final NamedParameterJdbcTemplate jdbcTemplate;

  public ChartVersionDao(NamedParameterJdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  private void deleteActiveVersions(List<String> chartVersionNames, Date inactiveDate) {
    if (chartVersionNames.isEmpty()) {
      return; // nothing to invalidate
    }

    var query =
        "UPDATE chart_versions"
            + " SET deleted_at = :deletedAt"
            + " WHERE chart_name in (:chartNames)"
            + " AND deleted_at is null";

    var namedParameters =
        new MapSqlParameterSource()
            .addValue("deletedAt", inactiveDate)
            .addValue("chartNames", chartVersionNames);

    jdbcTemplate.update(query, namedParameters);
  }

  /**
   * @param version {@link ChartVersion} to add to the repository
   */
  @WithSpan
  public void upsert(ChartVersion version) {
    // make sure the activeDate and inactiveDate(s) are the same date/time
    Date currentDate = new Date();
    deleteActiveVersions(List.of(version.chartName()), currentDate);

    var query =
        "INSERT INTO chart_versions (chart_name, chart_version, app_version, created_at)"
            + " VALUES (:chartName, :chartVersion, :appVersion, :createdAt)";

    var namedParameters =
        new MapSqlParameterSource()
            .addValue("chartName", version.chartName())
            .addValue("chartVersion", version.chartVersion())
            .addValue("appVersion", version.appVersion())
            .addValue("createdAt", currentDate);

    jdbcTemplate.update(query, namedParameters);
  }

  /**
   * @return list of ACTIVE {@link ChartVersion}s
   */
  public List<ChartVersion> get() {
    return get(false);
  }

  /**
   * @param includeAll {@code true} if we should return all versions, including inactive {@link
   *     ChartVersion}s.
   * @return list of {@link ChartVersion}s based on the parameters provided
   */
  public List<ChartVersion> get(boolean includeAll) {
    return get(List.of(), includeAll);
  }

  /**
   * @param chartNames non-null list of chartNames to filter the return results.
   * @return list of ACTIVE {@link ChartVersion}s based on the parameters provided
   */
  public List<ChartVersion> get(List<String> chartNames) {
    return get(chartNames, false);
  }

  /**
   * @param chartNames non-null list of chartNames to filter the return results.
   * @param includeAll {@code true} if we should return all versions, including inactive {@link
   *     ChartVersion}s.
   * @return list of {@link ChartVersion}s based on the parameters provided
   */
  @WithSpan
  public List<ChartVersion> get(@NotNull List<String> chartNames, boolean includeAll) {
    var query =
        "SELECT chart_name, chart_version, app_version, created_at, deleted_at"
            + " FROM chart_versions";

    var namedParameters = new MapSqlParameterSource();
    var conditions = new ArrayList<String>();

    if (!chartNames.isEmpty()) {
      conditions.add(" chart_name in (:chartNames)");
      namedParameters.addValue("chartNames", chartNames);
    }

    if (!includeAll) {
      conditions.add(" deleted_at is null");
    }

    if (!conditions.isEmpty()) {
      query += " WHERE" + String.join(" AND", conditions);
    }

    return jdbcTemplate.queryForStream(query, namedParameters, CHART_VERSION_ROW_MAPPER).toList();
  }

  /**
   * Soft-delete all {@link ChartVersion}s for the {@code chartNames} provided
   *
   * @param chartNames list of chart names to delete.
   */
  @WithSpan
  public void delete(List<String> chartNames) {
    deleteActiveVersions(chartNames, new Date());
  }
}
