CREATE TABLE customers (
    customer_id UUID NOT NULL DEFAULT gen_random_uuid() PRIMARY KEY,
    first_name VARCHAR(36) NOT NULL,
    last_name VARCHAR(36),
    email VARCHAR(100) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);