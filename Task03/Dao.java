package Task03;

import java.sql.SQLException;
import java.util.List;

/**
 * Generic Data Access Object (DAO) Interface
 * @param <T> The domain entity type
 * @param <K> The primary key identifier type
 * Student: Geetika T | Reg No: 192511469
 */
public interface Dao<T, K> {
    void insert(T entity) throws SQLException;
    T findById(K id) throws SQLException;
    List<T> findAll() throws SQLException;
    void update(T entity) throws SQLException;
    void delete(K id) throws SQLException;
}
