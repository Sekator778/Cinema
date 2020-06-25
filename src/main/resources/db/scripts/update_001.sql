CREATE TABLE accounts (account_id serial primary key, name varchar(200) not null , phone int not null);

INSERT INTO accounts values (-1, 'empty', 0);

CREATE TABLE halls (hall_id int not null , row int not null, place int not null, account_id int, primary key (hall_id, row, place), foreign key (account_id) REFERENCES accounts (account_id));

INSERT INTO halls VALUES (1, 1, 1, -1);
INSERT INTO halls VALUES (1, 1, 2, -1);
INSERT INTO halls VALUES (1, 1, 3, -1);
INSERT INTO halls VALUES (1, 2, 1, -1);
INSERT INTO halls VALUES (1, 2, 2, -1);
INSERT INTO halls VALUES (1, 2, 3, -1);
INSERT INTO halls VALUES (1, 3, 1, -1);
INSERT INTO halls VALUES (1, 3, 2, -1);
INSERT INTO halls VALUES (1, 3, 3, -1);
