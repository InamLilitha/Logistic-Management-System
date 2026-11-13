package za.ac.cput.logisticmanagementsystem.config;

import jakarta.annotation.PostConstruct;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

/**
 * Migrates invoice databases created before the total_amount column rename.
 */
@Component
public class InvoiceSchemaMigration {

    private final JdbcTemplate jdbcTemplate;

    public InvoiceSchemaMigration(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @PostConstruct
    void migrate() {
        if (!tableExists()) {
            return;
        }

        boolean hasLegacyColumn = columnExists("total");
        boolean hasCurrentColumn = columnExists("total_amount");

        if (hasLegacyColumn && !hasCurrentColumn) {
            jdbcTemplate.execute(
                    "ALTER TABLE invoice CHANGE COLUMN total total_amount DOUBLE NOT NULL");
            return;
        }

        if (hasLegacyColumn) {
            jdbcTemplate.update(
                    "UPDATE invoice SET total_amount = total WHERE total_amount IS NULL");
            jdbcTemplate.execute(
                    "ALTER TABLE invoice MODIFY COLUMN total DOUBLE NULL");
        }
    }

    private boolean tableExists() {
        Integer tableCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM information_schema.tables " +
                        "WHERE table_schema = DATABASE() AND table_name = 'invoice'",
                Integer.class);
        return tableCount != null && tableCount > 0;
    }

    private boolean columnExists(String columnName) {
        Integer columnCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM information_schema.columns " +
                        "WHERE table_schema = DATABASE() AND table_name = 'invoice' " +
                        "AND column_name = ?",
                Integer.class,
                        columnName);
        return columnCount != null && columnCount > 0;
    }
}
