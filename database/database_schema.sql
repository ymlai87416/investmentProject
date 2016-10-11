CREATE DATABASE securities_master;
USE securities_master;

CREATE USER sec_user@localhost IDENTIFIED BY 'testing123';
GRANT ALL PRIVILEGES ON securities_master.* TO sec_user@localhost;
FLUSH PRIVILEGES;

CREATE TABLE exchange (
	id bigint NOT NULL AUTO_INCREMENT,
	version bigint NOT NULL,
	abbrev varchar(32) NOT NULL,
	name varchar(255) NOT NULL,
	city varchar(255) NULL,
	country varchar(255) NULL,
	currency varchar(64) NULL,
	timezone_offset time NULL,
	created_date datetime NOT NULL,
	last_updated_date datetime NOT NULL,
	PRIMARY KEY (id)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;


CREATE TABLE data_vendor (
	id bigint NOT NULL AUTO_INCREMENT,
	version bigint NOT NULL,
	name varchar(64) NOT NULL,
	website_url varchar(255) NULL,
	support_email varchar(255) NULL,
	created_date datetime NOT NULL,
	last_updated_date datetime NOT NULL,
	PRIMARY KEY (id)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;


CREATE TABLE symbol (
	id bigint NOT NULL AUTO_INCREMENT,
	version bigint NOT NULL,
	exchange_id bigint NULL,
	ticker varchar(32) NOT NULL,
	instrument varchar(64) NOT NULL,
	name varchar(255) NULL,
	sector varchar(255) NULL,
	lot	int NULL,
	currency varchar(32) NULL,
	created_date datetime NOT NULL,
	last_updated_date datetime NOT NULL,
	PRIMARY KEY (id),
	KEY index_exchange_id (exchange_id)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

CREATE TABLE daily_price (
	id bigint NOT NULL AUTO_INCREMENT,
	version bigint NOT NULL,
	data_vendor_id bigint NOT NULL,
	symbol_id bigint NOT NULL,
	price_date datetime NOT NULL,
	created_date datetime NOT NULL,
	last_updated_date datetime NOT NULL,
	open_price decimal(19,4) NULL,
	high_price decimal(19,4) NULL,
	low_price decimal(19,4) NULL,
	close_price decimal(19,4) NULL,
	adj_close_price decimal(19,4) NULL,
	volume bigint NULL,
	PRIMARY KEY (id),
	KEY index_data_vendor_id (data_vendor_id),
	KEY index_symbol_id (symbol_id)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

CREATE TABLE time_series(
	id bigint NOT NULL AUTO_INCREMENT,
	version bigint NOT NULL,
	series_name varchar(255) NOT NULL,
	category varchar(255) NOT NULL,
	created_date datetime NOT NULL,
	last_updated_date datetime NOT NULL,
	PRIMARY KEY (id)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

CREATE TABLE time_point(
	id bigint NOT NULL AUTO_INCREMENT,
	version bigint NOT NULL,
	series_id bigint NOT NULL,
	time_point_date bigint NOT NULL,
	`value` decimal(19,4) NULL,
	created_date datetime NOT NULL,
	last_updated_date datetime NOT NULL,
	PRIMARY KEY (id),
	KEY index_series_id (series_id)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

