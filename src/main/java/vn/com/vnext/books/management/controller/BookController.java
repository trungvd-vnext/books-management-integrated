package vn.com.vnext.books.management.controller;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import vn.com.vnext.books.management.model.Book;
import vn.com.vnext.books.management.repository.BookRepository;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

@CrossOrigin(origins = "http://localhost:8081")
@RestController
@RequestMapping("/api")
public class BookController {
    @Autowired(required = true)
    BookRepository bookRepository;

    private Sort.Direction getSortDirection(String direction) {
        if (direction.equals("asc")) {
            return Sort.Direction.ASC;
        } else if (direction.equals("desc")) {
            return Sort.Direction.DESC;
        }

        return Sort.Direction.ASC;
    }

    // Forwards all routes to FrontEnd except: '/', '/index.html', '/api', '/api/**'
    // Required because of 'mode: history' usage in frontend routing, see README for further details
    @RequestMapping(value = "{_:^(?!index\\.html|api).$}")
    public String redirectApi() {
        return "forward:/";
    }

    @GetMapping("/sortedbooks")
    public ResponseEntity<List<Book>> getAllBooks(@RequestParam(defaultValue = "id,desc") String[] sort) {

        try {
            List<Sort.Order> orders = new ArrayList<Sort.Order>();

            if (sort[0].contains(",")) {
                // will sort more than 2 fields
                // sortOrder="field, direction"
                for (String sortOrder : sort) {
                    String[] _sort = sortOrder.split(",");
                    orders.add(new Sort.Order(getSortDirection(_sort[1]), _sort[0]));
                }
            } else {
                // sort=[field, direction]
                orders.add(new Sort.Order(getSortDirection(sort[1]), sort[0]));
            }

            List<Book> books = bookRepository.findAll(Sort.by(orders));

            if (books.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }

            return new ResponseEntity<>(books, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/books")
    public ResponseEntity<Map<String, Object>> getAllBooksPage(
            @RequestParam(required = false) String title,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "3") int size,
            @RequestParam(defaultValue = "id,desc") String[] sort) {

        try {
            List<Sort.Order> orders = new ArrayList<Sort.Order>();

            if (sort[0].contains(",")) {
                // will sort more than 2 fields
                // sortOrder="field, direction"
                for (String sortOrder : sort) {
                    String[] _sort = sortOrder.split(",");
                    orders.add(new Sort.Order(getSortDirection(_sort[1]), _sort[0]));
                }
            } else {
                // sort=[field, direction]
                orders.add(new Sort.Order(getSortDirection(sort[1]), sort[0]));
            }

            List<Book> books = new ArrayList<Book>();
            Pageable pagingSort = PageRequest.of(page, size, Sort.by(orders));
            Page<Book> pageTuts;
            if (title == null)
                pageTuts = bookRepository.findAll(pagingSort);
            else
                pageTuts = bookRepository.findByTitleContaining(title, pagingSort);

            books = pageTuts.getContent();

            Map<String, Object> response = new HashMap<>();
            response.put("books", books);
            response.put("currentPage", pageTuts.getNumber());
            response.put("totalItems", pageTuts.getTotalElements());
            response.put("totalPages", pageTuts.getTotalPages());

            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/books/published")
    public ResponseEntity<Map<String, Object>> findByPublished(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "3") int size) {

        try {
            List<Book> books = new ArrayList<Book>();
            Pageable paging = PageRequest.of(page, size);

            Page<Book> pageTuts = bookRepository.findByPublished(true, paging);
            books = pageTuts.getContent();

            Map<String, Object> response = new HashMap<>();
            response.put("books", books);
            response.put("currentPage", pageTuts.getNumber());
            response.put("totalItems", pageTuts.getTotalElements());
            response.put("totalPages", pageTuts.getTotalPages());

            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/books/{id}")
    public ResponseEntity<Book> getBookById(@PathVariable("id") long id) {
        Optional<Book> bookData = bookRepository.findById(id);

        if (bookData.isPresent()) {
            return new ResponseEntity<>(bookData.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/books")
    public ResponseEntity<Book> createBook(@RequestBody ObjectNode json) {
        try {
            Book book = new Book();
            book.setTitle(json.get("title").textValue());
            book.setAuthor(json.get("author").textValue());
            book.setDescription(json.get("description").textValue());
            String base64 = json.get("data").textValue();
            System.out.println(base64);
            byte[] decodedBytes = Base64.getMimeDecoder().decode(base64.split(",")[1]);
            book.setData(decodedBytes);
            Book _book = bookRepository.save(book);
            //Book _book = bookRepository.save(new Book(book.getTitle(), book.getAuthor(), book.getDescription(), false, book.getData()));
            return new ResponseEntity<>(null, HttpStatus.CREATED);
        } catch (Exception e) {
                return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/books/{id}")
    public ResponseEntity<Book> updateBook(@PathVariable("id") long id, @RequestBody Book book) {
        Optional<Book> bookData = bookRepository.findById(id);

        if (bookData.isPresent()) {
            Book _book = bookData.get();
            _book.setTitle(book.getTitle());
            _book.setDescription(book.getDescription());
            _book.setPublished(book.isPublished());
            _book.setData(book.getData());
            return new ResponseEntity<>(bookRepository.save(_book), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/books/{id}")
    public ResponseEntity<HttpStatus> deleteBook(@PathVariable("id") long id) {
        try {
            bookRepository.deleteById(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/books")
    public ResponseEntity<HttpStatus> deleteAllBooks() {
        try {
            bookRepository.deleteAll();
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
