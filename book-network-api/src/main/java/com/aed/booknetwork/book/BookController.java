package com.aed.booknetwork.book;

import com.aed.booknetwork.common.PageResponse;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("books")
@RequiredArgsConstructor
@Tag(name = "Book")
public class BookController {

    private final BookService bookService;

    @PostMapping
    public ResponseEntity<Integer> createBook(@Valid @RequestBody BookRequest request, Authentication connectedUser) {
        return ResponseEntity.ok(bookService.createBook(request, connectedUser));
    }

    @GetMapping("/{bookId}")
    public ResponseEntity<BookResponse> getBookById(@PathVariable Integer bookId) {
        return ResponseEntity.ok(bookService.getBookById(bookId));
    }

    @GetMapping
    public ResponseEntity<PageResponse<BookResponse>> getAllBooks(
            @RequestParam(name = "page", defaultValue = "0", required = false) int page,
            @RequestParam(name = "size", defaultValue = "10", required = false) int size,
            Authentication connectedUser
    ) {
        return ResponseEntity.ok(bookService.getAllBooks(page, size, connectedUser));
    }

    @GetMapping("/owner")
    public ResponseEntity<PageResponse<BookResponse>> findAllBooksByOwner(
            @RequestParam(name = "page", defaultValue = "0", required = false) int page,
            @RequestParam(name = "size", defaultValue = "10", required = false) int size,
            Authentication connectedUser
    ) {
        return ResponseEntity.ok(bookService.findAllBooksByOwner(page, size, connectedUser));
    }

    @GetMapping("/borrowed")
    public ResponseEntity<PageResponse<BorrowedBookResponse>> findAllBorrowedBooks(
            @RequestParam(name = "page", defaultValue = "0", required = false) int page,
            @RequestParam(name = "size", defaultValue = "10", required = false) int size,
            Authentication connectedUser
    ) {
        return ResponseEntity.ok(bookService.findAllBorrowedBooks(page, size, connectedUser));
    }

    @GetMapping("/returned")
    public ResponseEntity<PageResponse<BorrowedBookResponse>> findAllReturnedBooks(
            @RequestParam(name = "page", defaultValue = "0", required = false) int page,
            @RequestParam(name = "size", defaultValue = "10", required = false) int size,
            Authentication connectedUser
    ) {
        return ResponseEntity.ok(bookService.findAllReturnedBooks(page, size, connectedUser));
    }

    @PatchMapping("/shareable/{bookId}")
    public ResponseEntity<Integer> updateShareableStatus(@PathVariable Integer bookId, Authentication connectedUser) {
        return ResponseEntity.ok(bookService.updateShareableStatus(bookId, connectedUser));
    }

    @PatchMapping("/archived/{bookId}")
    public ResponseEntity<Integer> updateArchivedStatus(@PathVariable Integer bookId, Authentication connectedUser) {
        return ResponseEntity.ok(bookService.updateArchivedStatus(bookId, connectedUser));
    }

    @PostMapping("borrow/{bookId}")
    public ResponseEntity<Integer> borrowBook(@PathVariable Integer bookId, Authentication connectedUser) {
        return ResponseEntity.ok(bookService.borrowBook(bookId, connectedUser));
    }

    @PatchMapping("borrow/return/{bookId}")
    public ResponseEntity<Integer> returnBorrowBook(@PathVariable Integer bookId, Authentication connectedUser) {
        return ResponseEntity.ok(bookService.returnBorrowedBook(bookId, connectedUser));
    }

    @PatchMapping("borrow/return/approve/{bookId}")
    public ResponseEntity<Integer> approveReturnBorrowBook(@PathVariable Integer bookId, Authentication connectedUser) {
        return ResponseEntity.ok(bookService.approveReturnBorrowedBook(bookId, connectedUser));
    }

    @PostMapping(value = "/cover/{bookId}", consumes = {"multipart/form-data"})
    public ResponseEntity<?> uploadBookCoverPicture(@PathVariable Integer bookId, @Parameter() @RequestPart("file") MultipartFile file, Authentication connectedUser) {
        bookService.uploadBookCoverPicture(file, connectedUser, bookId);
        return ResponseEntity.accepted().build();
    }
}
