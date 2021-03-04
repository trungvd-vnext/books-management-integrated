package vn.com.vnext.books.management.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.com.vnext.books.management.model.Book;

import java.util.List;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
    Page<Book> findByPublished(boolean published, Pageable pageable);

    Page<Book> findByTitleContaining(String title, Pageable pageable);

    List<Book> findByTitleContaining(String title, Sort sort);
}
