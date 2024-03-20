CREATE TABLE IF NOT EXISTS "Awards" (
    award_id INTEGER PRIMARY KEY AUTOINCREMENT,
    award_name TEXT NOT NULL,
    category TEXT NOT NULL
);
CREATE TABLE IF NOT EXISTS "Persons" (
	person_id INTEGER PRIMARY KEY AUTOINCREMENT,
	status VARCHAR(255) NOT NULL,
	name VARCHAR(255) NOT NULL,
    birthday DATE NOT NULL CHECK (birthday LIKE '____-__-__'),
    gender VARCHAR(255) CHECK( gender IN ('Male','Female','Other'))
);
CREATE TABLE IF NOT EXISTS "Movie_Awards"(
	movie_id INTEGER NOT NULL,
    award_id INTEGER NOT NULL,
    presenting_year INTEGER NOT NULL,
    FOREIGN KEY (movie_id) REFERENCES "Movies"(movie_id),
    FOREIGN KEY (award_id) REFERENCES "Awards"(award_id),
    PRIMARY KEY (movie_id, award_id, presenting_year)
);
CREATE TABLE IF NOT EXISTS "Person_Awards"(
    person_id INTEGER NOT NULL,
    award_id INTEGER NOT NULL,
    presenting_year INTEGER NOT NULL,
    FOREIGN KEY (person_id) REFERENCES "Persons"(person_id),
    FOREIGN KEY (award_id) REFERENCES "Awards"(award_id),
    PRIMARY KEY (person_id, award_id, presenting_year)
);
CREATE TABLE IF NOT EXISTS "Movies_Persons"(
    movie_id INTEGER NOT NULL,
    person_id INTEGER NOT NULL,
    FOREIGN KEY (movie_id) REFERENCES "Movies"(movie_id),
    FOREIGN KEY (person_id) REFERENCES "Persons"(person_id),
    PRIMARY KEY (movie_id, person_id)
);
CREATE TABLE IF NOT EXISTS "Genres" (
    genre_id INTEGER PRIMARY KEY AUTOINCREMENT,
    genre_name TEXT NOT NULL
);
CREATE TABLE IF NOT EXISTS "Movies_Genres" (
    movie_id INTEGER NOT NULL,
    genre_id INTEGER NOT NULL,
    FOREIGN KEY (movie_id) REFERENCES Movies(movie_id),
    FOREIGN KEY (genre_id) REFERENCES "Genres"(genre_id),
    PRIMARY KEY (movie_id, genre_id)
);
CREATE TABLE IF NOT EXISTS "Movies" (
    movie_id INTEGER PRIMARY KEY AUTOINCREMENT,
    title TEXT NOT NULL,
    release_year DATE NOT NULL CHECK (release_year LIKE '____'),
    running_time INTEGER NOT NULL,
    rating REAL NOT NULL CHECK (rating >= 0 AND rating <= 10)
);
