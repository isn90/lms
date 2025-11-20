-- Create Library database
CREATE DATABASE LibraryDB;
GO

USE LibraryDB;
GO

-- Create Books table
CREATE TABLE Books (
    id NVARCHAR(50) PRIMARY KEY,
    title NVARCHAR(255) NOT NULL,
    author NVARCHAR(255) NOT NULL,
    isbn NVARCHAR(50) NOT NULL,
    genre NVARCHAR(100) NOT NULL,
    quantity INT NOT NULL DEFAULT 0,
    price DECIMAL(10, 2) NOT NULL,
    published_date DATE NOT NULL,
    created_date DATETIME2 DEFAULT GETDATE(),
    updated_date DATETIME2 DEFAULT GETDATE()
);
GO

-- Create index for better performance
CREATE INDEX IX_Books_Author ON Books(author);
CREATE INDEX IX_Books_Genre ON Books(genre);
CREATE INDEX IX_Books_ISBN ON Books(isbn);
GO

-- Insert sample data
INSERT INTO Books (id, title, author, isbn, genre, quantity, price, published_date) VALUES
('1', 'The Great Gatsby', 'F. Scott Fitzgerald', '978-0-7432-7356-5', 'Fiction', 10, 12.99, '1925-04-10'),
('2', 'To Kill a Mockingbird', 'Harper Lee', '978-0-06-112008-4', 'Fiction', 8, 14.99, '1960-07-11'),
('3', '1984', 'George Orwell', '978-0-452-28423-4', 'Dystopian', 12, 10.99, '1949-06-08'),
('4', 'Pride and Prejudice', 'Jane Austen', '978-0-14-143951-8', 'Romance', 15, 9.99, '1813-01-28'),
('5', 'The Catcher in the Rye', 'J.D. Salinger', '978-0-316-76948-0', 'Fiction', 6, 11.99, '1951-07-16');
GO

-- Create Cart table for future use
CREATE TABLE Cart (
    id INT IDENTITY(1,1) PRIMARY KEY,
    book_id NVARCHAR(50) NOT NULL,
    title NVARCHAR(255) NOT NULL,
    quantity INT NOT NULL,
    price DECIMAL(10, 2) NOT NULL,
    created_date DATETIME2 DEFAULT GETDATE(),
    FOREIGN KEY (book_id) REFERENCES Books(id)
);
GO