CREATE TABLE IF NOT EXISTS adres (
  id_uzytkownika int(10) NOT NULL,
  adres varchar(50) NOT NULL,
  region varchar(50) NOT NULL,
  kodPocztowy varchar(6) NOT NULL,
  KEY id_uzytkownika (id_uzytkownika)
);

CREATE TABLE IF NOT EXISTS dane_uzytkownika (
  id_uzytkownika int(10) NOT NULL,
  imie varchar(30) NOT NULL,
  nazwisko varchar(40) NOT NULL,
  email varchar(50) NOT NULL,
  KEY id_uzytkownika (id_uzytkownika)
);

CREATE TABLE IF NOT EXISTS lista_znajomych (
  id_uzytkownika int(10) NOT NULL,
  id_znajomego int(10) NOT NULL,
  KEY id_uzytkownika (id_uzytkownika),
  KEY id_znajomego (id_znajomego)
);

CREATE TABLE IF NOT EXISTS projekt (
  id_projektu int(10) NOT NULL AUTO_INCREMENT,
  nazwa varchar(30) NOT NULL,
  opis varchar(250) NOT NULL,
  data_utworzenia date NOT NULL,
  PRIMARY KEY (id_projektu)
);

CREATE TABLE IF NOT EXISTS uzytkownicy (
  id_uzytkownika int(10) NOT NULL AUTO_INCREMENT,
  login varchar(30) NOT NULL,
  haslo varchar(50) NOT NULL,
  PRIMARY KEY (id_uzytkownika)
);

CREATE TABLE IF NOT EXISTS uzytkownik_projekt (
  id_uzytkownika int(10) NOT NULL,
  id_projektu int(10) NOT NULL,
  KEY id_uzytkownika (id_uzytkownika),
  KEY id_projektu (id_projektu)
);


ALTER TABLE adres
  ADD CONSTRAINT adres_ibfk_1 FOREIGN KEY IF NOT EXISTS(id_uzytkownika) REFERENCES uzytkownicy (id_uzytkownika);

ALTER TABLE dane_uzytkownika
  ADD CONSTRAINT dane_uzytkownika_ibfk_1 FOREIGN KEY IF NOT EXISTS(id_uzytkownika) REFERENCES uzytkownicy (id_uzytkownika);

ALTER TABLE lista_znajomych
  ADD CONSTRAINT lista_znajomych_ibfk_1 FOREIGN KEY IF NOT EXISTS(id_uzytkownika) REFERENCES uzytkownicy (id_uzytkownika),
  ADD CONSTRAINT lista_znajomych_ibfk_2 FOREIGN KEY IF NOT EXISTS(id_znajomego) REFERENCES uzytkownicy (id_uzytkownika);

ALTER TABLE uzytkownik_projekt
  ADD CONSTRAINT uzytkownik_projekt_ibfk_1 FOREIGN KEY IF NOT EXISTS(id_uzytkownika) REFERENCES uzytkownicy (id_uzytkownika),
  ADD CONSTRAINT uzytkownik_projekt_ibfk_2 FOREIGN KEY IF NOT EXISTS(id_projektu) REFERENCES projekt (id_projektu);
COMMIT;
