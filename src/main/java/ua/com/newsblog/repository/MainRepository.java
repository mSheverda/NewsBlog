package ua.com.newsblog.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.com.newsblog.model.Model;

/**
 * Created by Max on 11/27/2016.
 */
public interface MainRepository<T extends Model, E extends Number> extends JpaRepository<T, E> {
   // List<T> search(String searchTerm);
}
