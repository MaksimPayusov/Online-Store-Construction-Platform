-- Create baskets table
CREATE TABLE baskets (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL UNIQUE,
    updated_at TIMESTAMP
);

-- Create unique index on user_id for faster lookups
CREATE UNIQUE INDEX idx_user_id ON baskets(user_id);

-- Create basket_items table
CREATE TABLE basket_items (
    id UUID PRIMARY KEY,
    basket_id UUID NOT NULL,
    product_id UUID NOT NULL,
    shop_id UUID NOT NULL,
    quantity INTEGER NOT NULL CHECK (quantity >= 1),
    price_at_add DECIMAL(19, 2) NOT NULL,
    FOREIGN KEY (basket_id) REFERENCES baskets(id) ON DELETE CASCADE
);

-- Create index on basket_id for faster item lookups
CREATE INDEX idx_basket_items_basket_id ON basket_items(basket_id);
