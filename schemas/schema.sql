CREATE TABLE IF NOT EXISTS "Awards" (
    AwardID INTEGER PRIMARY KEY,
    FilmID INTEGER,
    Category VARCHAR(255),
    FilmName VARCHAR(255),
    Winner VARCHAR(255),
    FOREIGN KEY (FilmID) REFERENCES Movies(movie_id)
);

CREATE TABLE IF NOT EXISTS "Persons" (
	person_id INTEGER PRIMARY KEY AUTOINCREMENT,
	status VARCHAR(255) NOT NULL,
	name VARCHAR(255) NOT NULL,
    birthday DATE NOT NULL,
    gender VARCHAR(255) CHECK( gender IN ('male','female','other'))
);
CREATE TABLE IF NOT EXISTS "Movies_Persons"(
    movie_id INTEGER NOT NULL,
    person_name INTEGER NOT NULL,
    dob DATE NOT NULL,
    FOREIGN KEY (movie_id) REFERENCES "Movies"(movie_id),
    FOREIGN KEY (person_id) REFERENCES "Persons"(person_id),
    FOREIGN KEY (dob) REFERENCES "Persons"(birthday),
    PRIMARY KEY (movie_id, person_id, dob)
);
CREATE TABLE IF NOT EXISTS "Movies" (
    movie_id INTEGER PRIMARY KEY AUTOINCREMENT,
    title TEXT NOT NULL,
    release_year INTEGER NOT NULL CHECK (release_year >= 1928 AND release_year <= 2024),
    running_time INTEGER NOT NULL,
    genre_name TEXT NOT NULL,
    rating REAL NOT NULL CHECK (rating >= 0 AND rating <= 10)
);
