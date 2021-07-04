DROP TABLE IF EXISTS name;

CREATE TABLE name (id int, "first" varchar(255), middle varchar(255), "last" varchar(255), UNIQUE (id));

INSERT INTO name(id, "first", middle, "last") VALUES (1, 'Eric', 'Donald', 'Murphy');
INSERT INTO name(id, "first", middle, "last") VALUES (2, 'Rosa', 'Louise McCauley', 'Parks');
INSERT INTO name(id, "first", middle, "last") VALUES (3, 'Mohandas', 'Karamchand', 'Gandhi');
INSERT INTO name(id, "first", middle, "last") VALUES (4, 'John', 'Fitzgerald', 'Kennedy');
INSERT INTO name(id, "first", middle, "last") VALUES (5, 'William', 'Jefferson', 'Clinton');