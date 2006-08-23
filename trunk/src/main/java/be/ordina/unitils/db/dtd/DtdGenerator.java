package be.ordina.unitils.db.dtd;

import javax.sql.DataSource;
import java.util.Properties;

/**
 * @author Filip Neven
 */
public interface DtdGenerator {

    void init(Properties properties, DataSource dataSource);

    void generateDtd();

}
