-- Create yookassa_payments table
CREATE TABLE yookassa_payments (
    id UUID PRIMARY KEY,
    payment_id VARCHAR(100) NOT NULL UNIQUE,
    amount DECIMAL(12, 2) NOT NULL,
    currency VARCHAR(3) NOT NULL,
    description TEXT,
    status VARCHAR(50) NOT NULL,
    confirmation_token VARCHAR(500),
    paid BOOLEAN DEFAULT FALSE,
    test BOOLEAN DEFAULT FALSE,
    refundable BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL,
    captured_at TIMESTAMP,
    order_id UUID,
    metadata JSONB,
    CONSTRAINT fk_payment_order FOREIGN KEY (order_id) REFERENCES orders(id)
);

-- Create indexes
CREATE INDEX idx_yookassa_payment_id ON yookassa_payments(payment_id);
CREATE INDEX idx_yookassa_status ON yookassa_payments(status);
CREATE INDEX idx_yookassa_created_at ON yookassa_payments(created_at);
CREATE INDEX idx_yookassa_order_id ON yookassa_payments(order_id);
