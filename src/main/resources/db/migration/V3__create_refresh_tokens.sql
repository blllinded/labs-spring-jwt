CREATE TABLE IF NOT EXISTS refresh_tokens (
                                              id INTEGER PRIMARY KEY AUTOINCREMENT,
                                              user_id INTEGER NOT NULL,
                                              token TEXT NOT NULL UNIQUE,
                                              expires_at TEXT NOT NULL,
                                              revoked INTEGER NOT NULL DEFAULT 0,
                                              created_at TEXT NOT NULL DEFAULT (datetime('now')),
    replaced_by_token TEXT,
    FOREIGN KEY(user_id) REFERENCES users(id) ON DELETE CASCADE
    );

CREATE INDEX IF NOT EXISTS idx_refresh_user_id ON refresh_tokens(user_id);
CREATE INDEX IF NOT EXISTS idx_refresh_token ON refresh_tokens(token);
