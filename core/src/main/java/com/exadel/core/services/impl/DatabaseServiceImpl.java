package com.exadel.core.services.impl;

import com.day.commons.datasource.poolservice.DataSourceNotFoundException;
import com.day.commons.datasource.poolservice.DataSourcePool;
import com.exadel.core.services.DatabaseService;
import lombok.extern.slf4j.Slf4j;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

@Slf4j
@Component(service = DatabaseService.class, immediate = true)
public class DatabaseServiceImpl implements DatabaseService {

    @Reference
    DataSourcePool source;

    /**
     * Get MySQL connection from Data source pool
     * @param dataSourceName Name of requesting data source
     * @return return SQL connection
     */
    public Connection getConnection(String dataSourceName) throws DataSourceNotFoundException, SQLException {
        DataSource dataSource = (DataSource) source.getDataSource(dataSourceName);
        final Connection connection = dataSource.getConnection();
        return connection;
    }
}
