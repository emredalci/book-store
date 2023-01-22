package com.store.book.libraryservice.service;

import com.store.book.libraryservice.client.BookServiceClient;
import com.store.book.libraryservice.dto.AddBookRequest;
import com.store.book.libraryservice.dto.LibraryDto;
import com.store.book.libraryservice.exception.LibraryNotFoundException;
import com.store.book.libraryservice.model.Library;
import com.store.book.libraryservice.repository.LibraryRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class LibraryService {

    private final LibraryRepository libraryRepository;
    private final BookServiceClient bookServiceClient;

    public LibraryService(LibraryRepository libraryRepository, BookServiceClient bookServiceClient) {
        this.libraryRepository = libraryRepository;
        this.bookServiceClient = bookServiceClient;
    }

    public LibraryDto getAllBooksInLibraryById(String id){
        Library library = libraryRepository.findById(id)
                .orElseThrow(() -> new LibraryNotFoundException("Library could not found by id " + id));

        return new LibraryDto(library.getId(),
                library.getUserBook().stream()
                .map(bookServiceClient::getBookById)
                .map(ResponseEntity::getBody)
                .toList());

    }

    public LibraryDto createLibrary(){
        Library newLibrary = libraryRepository.save(new Library());
        return new LibraryDto(newLibrary.getId());
    }

    public void addBookToLibrary(AddBookRequest request){
        String bookId = bookServiceClient.getBookByIsbn(request.getIsbn()).getBody().getBookId();

        Library library = libraryRepository.findById(request.getId())
                .orElseThrow(() -> new LibraryNotFoundException("Library could not found by id " + request.getId()));

        library.getUserBook().add(bookId);

        libraryRepository.save(library);
    }
}
