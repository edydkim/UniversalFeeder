package org.ufm.common.db;

import org.ufm.enums.ExConfPropEnum;
import org.ufm.enums.DataSourcePropEnum;
import org.ufm.util.Cryptor;
import com.jolbox.bonecp.BoneCP;
import com.jolbox.bonecp.BoneCPConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;

@ThreadSafe
public class ConnectionPool {
    private static final Logger logger = LoggerFactory.getLogger(ConnectionPool.class);

    // non-static for unknown allocated memory size
    private BoneCP boneCP;

    // Singleton
    private ConnectionPool() {
        try {
            Class.forName(DataSourcePropEnum.DRIVER.value()).newInstance();

            setup();
        } catch (Exception e) {
            logger.error("DB Initialization is failed..", e);
        }
    }

    private static class ConnectionPoolHolder {
        private static final ConnectionPool instance = new ConnectionPool();
    }

    public static ConnectionPool getInstance() {
        return ConnectionPoolHolder.instance;
    }

    private void setup() throws Exception {
        // setup the connection pool
        BoneCPConfig config = new BoneCPConfig();
        config.setJdbcUrl(DataSourcePropEnum.URL.value());
        config.setUsername(DataSourcePropEnum.USER.value());
        config.setPassword(Cryptor.decrypt(DataSourcePropEnum.PASSWORD.value()));
        config.setMinConnectionsPerPartition(Integer.parseInt(ExConfPropEnum.THREAD_MAX_SIZE.value()));
        config.setMaxConnectionsPerPartition(Integer.parseInt(ExConfPropEnum.THREAD_MAX_SIZE.value()) * 2);
        config.setPartitionCount(1);
        config.setCloseConnectionWatchTimeout(10, TimeUnit.SECONDS);
        config.setConnectionTimeout(10, TimeUnit.SECONDS);
        config.setIdleMaxAge(60, TimeUnit.MINUTES);
        // <- config.setLazyInit(true);

        // setup the connection pool
        boneCP = new BoneCP(config);
    }

    public Connection getConnection() throws SQLException {
        return this.boneCP.getConnection();
    }
}
