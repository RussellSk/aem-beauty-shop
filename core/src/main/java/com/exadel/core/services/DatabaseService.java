package com.exadel.core.services;

import com.day.commons.datasource.poolservice.DataSourceNotFoundException;

import java.sql.Connection;
import java.sql.SQLException;

public interface DatabaseService {
    Connection getConnection(String dataSourceName) throws DataSourceNotFoundException, SQLException;
}
