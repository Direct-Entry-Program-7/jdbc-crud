DROP DATABASE IF EXISTS dep7;

CREATE DATABASE dep7;

USE dep7;

CREATE TABLE customer(
    id VARCHAR(4) PRIMARY KEY,
    nic VARCHAR(10) NOT NULL,
    name VARCHAR(50) NOT NULL,
    address VARCHAR(350) UNIQUE KEY
);

# CREATE TABLE customer(
#     id VARCHAR(4),
#     nic VARCHAR(10),
#     name VARCHAR(50) NOT NULL,
#     address VARCHAR(350),
#     CONSTRAINT pk_customer PRIMARY KEY (id),
#     CONSTRAINT UNIQUE KEY (address)
# );

INSERT INTO customer VALUES ('C001', '123456789V', 'Pethum', 'Galle');
INSERT INTO customer VALUES ('C002', '456789123V', 'Aruni', 'Matara');

ALTER TABLE customer DROP CONSTRAINT address;
ALTER TABLE customer ADD CONSTRAINT uk_nic UNIQUE KEY (nic);
# ALTER TABLE customer DROP CONSTRAINT pk_customer;
# ALTER TABLE customer DROP PRIMARY KEY;      /* MYSQL Dialect */
# ALTER TABLE customer ADD CONSTRAINT pk_customer PRIMARY KEY (id);
#
# ALTER TABLE customer DROP COLUMN address;
# ALTER TABLE customer ADD COLUMN address VARCHAR(450) NOT NULL;
ALTER TABLE customer MODIFY COLUMN address VARCHAR(450) NOT NULL;
# ALTER TABLE customer RENAME COLUMN name TO customer_name;
#
# RENAME TABLE customer TO ijse_customer;

/* Dialect */

# INSERT INTO customer VALUES ('C003', '789456123', 'Chandima', 'Matara');
