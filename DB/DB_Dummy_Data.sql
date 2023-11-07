-- Inserimento di un utente di esempio
INSERT INTO Utenti (nome, cognome, email, password)
VALUES ('Mario', 'Rossi', 'mario@email.com', 'password123');

-- Inserimento di un altro utente di esempio
INSERT INTO Utenti (nome, cognome, email, password)
VALUES ('Giulia', 'Verdi', 'giulia@email.com', 'securepassword');


-- Inserimento di un post di esempio associato all'utente con id_utente = 1
INSERT INTO Posts (data, titolo, contenuto, utente_id)
VALUES (SYSDATE, 'Viaggio a Parigi', 'Il mio viaggio a Parigi è stato fantastico!', 1);

-- Inserimento di un post di esempio associato all'utente con id_utente = 2
INSERT INTO Posts (data, titolo, contenuto, utente_id)
VALUES (SYSDATE, 'Escursione in montagna', 'Ho appena scalato una montagna alta 4000 metri!', 2);

-- Inserimento di una categoria di esempio
INSERT INTO Categorie (nome_categoria)
VALUES ('Natura');

-- Inserimento di un'altra categoria di esempio
INSERT INTO Categorie (nome_categoria)
VALUES ('Città');

-- Inserimento di un'associazione di post favorito per l'utente con id_utente = 1
INSERT INTO Favoriti (utente_id, post_id)
VALUES (1, 2);

-- Inserimento di un'altra associazione di post favorito per l'utente con id_utente = 2
INSERT INTO Favoriti (utente_id, post_id)
VALUES (2, 1);

-- Inserimento di un'associazione di tag per il post con id_post = 1 e categoria_id = 1
INSERT INTO Tags (post_id, categoria_id)
VALUES (1, 1);

-- Inserimento di un'altra associazione di tag per il post con id_post = 2 e categoria_id = 2
INSERT INTO Tags (post_id, categoria_id)
VALUES (2, 2);

-- Inserimento di un'immagine di esempio associata al post con id_post = 1
INSERT INTO Immagini (url, post_id)
VALUES ('http://www.example.com/image1.jpg', 1);

-- Inserimento di un'altra immagine di esempio associata al post con id_post = 2
INSERT INTO Immagini (url, post_id)
VALUES ('http://www.example.com/image2.jpg', 2);
