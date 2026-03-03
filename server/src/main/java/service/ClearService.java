package service;

import dataaccess.DataAccessException;
import dataaccess.DataAccess;

public class ClearService {
    private DataAccess db;

    public ClearService(DataAccess db) {
        this.db = db;
    }

    public void clear() throws ServiceException {
        try {
            db.clear();
        } catch (DataAccessException exception) {
            throw new ServiceException(500, "Error: " + exception.getMessage());
        }
    }
}
