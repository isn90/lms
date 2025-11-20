package com.cloudx.azure.library_management_system.repository;

import com.cloudx.azure.library_management_system.entity.Books;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookRepository extends JpaRepository<Books, String> {
    Optional<Books> findByIsbn(String isbn);
    List<Books> findByAuthor(String author);
    List<Books> findByGenre(String genre);
    List<Books> findByTitleContaining(String title);
}
