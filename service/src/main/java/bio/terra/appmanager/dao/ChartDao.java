package bio.terra.appmanager.dao;

import bio.terra.appmanager.model.Chart;
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
public class ChartDao {

  private static final RowMapper<Chart> CHART_ROW_MAPPER =
      (rs, rowNum) ->
          new Chart(
              rs.getString("name"),
              rs.getString("version"),
              rs.getString("app_version"),
              rs.getDate("created_at"),
              rs.getDate("deleted_at"));

  private final NamedParameterJdbcTemplate jdbcTemplate;

  public ChartDao(NamedParameterJdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  private void deleteActiveVersions(List<String> chartNames, Date inactiveDate) {
    if (chartNames.isEmpty()) {
      return; // nothing to invalidate
    }

    var query =
        "UPDATE charts"
            + " SET deleted_at = :deletedAt"
            + " WHERE name in (:chartNames)"
            + " AND deleted_at is null";

    var namedParameters =
        new MapSqlParameterSource()
            .addValue("deletedAt", inactiveDate)
            .addValue("chartNames", chartNames);

    jdbcTemplate.update(query, namedParameters);
  }

  /**
   * @param chart {@link Chart} to add to the repository
   */
  @WithSpan
  public void upsert(Chart chart) {
    // make sure the activeDate and inactiveDate(s) are the same date/time
    Date currentDate = new Date();
    deleteActiveVersions(List.of(chart.name()), currentDate);

    var query =
        "INSERT INTO charts (name, version, app_version, created_at)"
            + " VALUES (:chartName, :chartVersion, :appVersion, :createdAt)";

    var namedParameters =
        new MapSqlParameterSource()
            .addValue("chartName", chart.name())
            .addValue("chartVersion", chart.version())
            .addValue("appVersion", chart.appVersion())
            .addValue("createdAt", currentDate);

    jdbcTemplate.update(query, namedParameters);
  }

  /**
   * @return list of ACTIVE {@link Chart}s
   */
  public List<Chart> get() {
    return get(false);
  }

  /**
   * @param includeAll {@code true} if we should return all versions, including inactive {@link
   *     Chart}s.
   * @return list of {@link Chart}s based on the parameters provided
   */
  public List<Chart> get(boolean includeAll) {
    return get(List.of(), includeAll);
  }

  /**
   * @param chartNames non-null list of chartNames to filter the return results.
   * @return list of ACTIVE {@link Chart}s based on the parameters provided
   */
  public List<Chart> get(List<String> chartNames) {
    return get(chartNames, false);
  }

  /**
   * @param chartNames non-null list of chartNames to filter the return results.
   * @param includeAll {@code true} if we should return all versions, including inactive {@link
   *     Chart}s.
   * @return list of {@link Chart}s based on the parameters provided
   */
  @WithSpan
  public List<Chart> get(@NotNull List<String> chartNames, boolean includeAll) {
    var query = "SELECT name, version, app_version, created_at, deleted_at" + " FROM charts";

    var namedParameters = new MapSqlParameterSource();
    var conditions = new ArrayList<String>();

    if (!chartNames.isEmpty()) {
      conditions.add(" name in (:chartNames)");
      namedParameters.addValue("chartNames", chartNames);
    }

    if (!includeAll) {
      conditions.add(" deleted_at is null");
    }

    if (!conditions.isEmpty()) {
      query += " WHERE" + String.join(" AND", conditions);
    }

    return jdbcTemplate.queryForStream(query, namedParameters, CHART_ROW_MAPPER).toList();
  }

  /**
   * Soft-delete all {@link Chart}s for the {@code chartNames} provided
   *
   * @param chartNames list of chart names to delete.
   */
  @WithSpan
  public void delete(List<String> chartNames) {
    deleteActiveVersions(chartNames, new Date());
  }
}
