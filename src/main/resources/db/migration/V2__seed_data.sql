INSERT INTO vendor (name) VALUES ('Shawarma House'), ('Juice Corner');

INSERT INTO menu_item (name, price, sold_out, vendor_id) VALUES
  ('Chicken Shawarma', 12.00, FALSE, 1),
  ('Falafel Wrap',      8.00, TRUE,  1),
  ('Fresh Juice',       6.00, FALSE, 2);

INSERT INTO orders (id) VALUES (1);