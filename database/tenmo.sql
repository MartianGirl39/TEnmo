ROLLBACK;

BEGIN TRANSACTION;

DROP TABLE IF EXISTS group_expense_contribution, group_expense, friend, friend_group_member, friend_group,transfer, account, tenmo_user, transfer_type, transfer_status;
DROP SEQUENCE IF EXISTS seq_user_id, seq_account_id, seq_transfer_id;


CREATE TABLE transfer_type (
	transfer_type_id serial NOT NULL,
	transfer_type_desc varchar(10) NOT NULL,
	CONSTRAINT PK_transfer_type PRIMARY KEY (transfer_type_id)
);

CREATE TABLE transfer_status (
	transfer_status_id serial NOT NULL,
	transfer_status_desc varchar(10) NOT NULL,
	CONSTRAINT PK_transfer_status PRIMARY KEY (transfer_status_id)
);

CREATE SEQUENCE seq_user_id
  INCREMENT BY 1
  START WITH 1001
  NO MAXVALUE;

CREATE TABLE tenmo_user (
	user_id int NOT NULL DEFAULT nextval('seq_user_id'),
	username varchar(50) UNIQUE NOT NULL,
	first_name varchar(50) NOT NULL,
	last_name varchar(50) DEFAULT '',
	password_hash varchar(200) NOT NULL,
	role varchar(20),
	CONSTRAINT PK_tenmo_user PRIMARY KEY (user_id),
	CONSTRAINT UQ_username UNIQUE (username)
);

CREATE SEQUENCE seq_account_id
  INCREMENT BY 1
  START WITH 2001
  NO MAXVALUE;

CREATE TABLE account (
	account_id int NOT NULL DEFAULT nextval('seq_account_id'),
	user_id int NOT NULL,
	balance decimal(13, 2) NOT NULL,
	CONSTRAINT PK_account PRIMARY KEY (account_id),
	CONSTRAINT FK_account_tenmo_user FOREIGN KEY (user_id) REFERENCES tenmo_user (user_id)
);

CREATE SEQUENCE seq_transfer_id
  INCREMENT BY 1
  START WITH 3001
  NO MAXVALUE;

CREATE TABLE transfer (
	transfer_id int NOT NULL DEFAULT nextval('seq_transfer_id'),
	transfer_type_id int NOT NULL,
	transfer_status_id int NOT NULL,
	account_from int NOT NULL,
	account_to int NOT NULL,
	amount decimal(13, 2) NOT NULL,
	message varchar NOT NULL DEFAULT '',
	CONSTRAINT PK_transfer PRIMARY KEY (transfer_id),
	CONSTRAINT FK_transfer_account_from FOREIGN KEY (account_from) REFERENCES account (account_id),
	CONSTRAINT FK_transfer_account_to FOREIGN KEY (account_to) REFERENCES account (account_id),
	CONSTRAINT FK_transfer_transfer_status FOREIGN KEY (transfer_status_id) REFERENCES transfer_status (transfer_status_id),
	CONSTRAINT FK_transfer_transfer_type FOREIGN KEY (transfer_type_id) REFERENCES transfer_type (transfer_type_id),
	CONSTRAINT CK_transfer_not_same_account CHECK (account_from <> account_to),
	CONSTRAINT CK_transfer_amount_gt_0 CHECK (amount > 0)
);

CREATE TABLE friend (
	user_1 int NOT NULL UNIQUE,
	user_2 int NOT NULL UNIQUE,
	CONSTRAINT PK_friend PRIMARY KEY (user_1, user_2),
	CONSTRAINT FK_friend_user1 FOREIGN KEY (user_1) REFERENCES account (account_id),
	CONSTRAINT FK_friend_user2 FOREIGN KEY (user_2) REFERENCES account (account_id)
);

CREATE TABLE friend_group (
	group_id serial NOT NULL,
	group_name varchar(50) NOT NULL,
	creator_id int NOT NULL,
	CONSTRAINT PK_groups PRIMARY KEY (group_id),
	CONSTRAINT FK_creator FOREIGN KEY (creator_id) REFERENCES account (account_id)
);

CREATE TABLE friend_group_member (
	group_id int NOT NULL,
	member_id int NOT NULL,
	member_role varchar(50) NOT NULL,
	CONSTRAINT PK_group_member PRIMARY KEY (group_id, member_id),
	CONSTRAINT FK_group FOREIGN KEY (group_id) REFERENCES friend_group (group_id),
	CONSTRAINT FK_group_member FOREIGN KEY (member_id) REFERENCES account (account_id)
);

CREATE TABLE group_expense (
	expense_id serial NOT NULL,
	group_id int NOT NULL,
	total_needed decimal NOT NULL,
	total_given decimal NOT NULL,
	due_date date,
	transfer_status_id int NOT NULL,
	repeating boolean DEFAULT 'false',
	CONSTRAINT PK_expnese PRIMARY KEY (expense_id),
	CONSTRAINT FK_expense_group FOREIGN KEY (group_id) REFERENCES friend_group (group_id),
	CONSTRAINT FK_expense_status FOREIGN KEY (transfer_status_id) REFERENCES transfer_status(transfer_status_id)
);

CREATE TABLE group_expense_contribution (
	expense_id int NOT NULL,
	account_id int NOT NULL,
	amount decimal NOT NULL,
	CONSTRAINT PK_expense_contribution PRIMARY KEY (expense_id, account_id),
	CONSTRAINT FK_expense_id_contrib FOREIGN KEY (expense_id) REFERENCES group_expense (expense_id),
	CONSTRAINT FK_contribution_account FOREIGN KEY (account_id) REFERENCES account (account_id)
);

INSERT INTO transfer_status (transfer_status_desc) VALUES ('Pending');
INSERT INTO transfer_status (transfer_status_desc) VALUES ('Approved');
INSERT INTO transfer_status (transfer_status_desc) VALUES ('Rejected');
INSERT INTO transfer_status (transfer_status_desc) VALUES ('Canceled');
INSERT INTO transfer_status (transfer_status_desc) VALUES ('Failed');

INSERT INTO transfer_type (transfer_type_desc) VALUES ('Request');
INSERT INTO transfer_type (transfer_type_desc) VALUES ('Send');

COMMIT;