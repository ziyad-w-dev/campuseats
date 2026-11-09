CREATE TABLE vendor (
  id   BIGINT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(100) NOT NULL
);

CREATE TABLE menu_item (
  id        BIGINT AUTO_INCREMENT PRIMARY KEY,
  name      VARCHAR(100)   NOT NULL,
  price     DECIMAL(10, 2) NOT NULL,
  sold_out  BOOLEAN        NOT NULL DEFAULT FALSE,
  vendor_id BIGINT         NOT NULL REFERENCES vendor (id)
);

CREATE TABLE orders (
  id BIGINT AUTO_INCREMENT PRIMARY KEY
);

CREATE TABLE order_item (
  id           BIGINT AUTO_INCREMENT PRIMARY KEY,
  quantity     INT    NOT NULL,
  order_id     BIGINT NOT NULL REFERENCES orders (id),
  menu_item_id BIGINT NOT NULL REFERENCES menu_item (id),
  CONSTRAINT uq_order_menu_item UNIQUE (order_id, menu_item_id)
);