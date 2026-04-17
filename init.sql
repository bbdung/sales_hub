CREATE TABLE IF NOT EXISTS sources
(
    id          SERIAL PRIMARY KEY,
    code        varchar(255),
    name        VARCHAR(100) NOT NULL UNIQUE,
    base_url    VARCHAR(500),
    source_type varchar(255),
    source_url  varchar(255),
    is_active   BOOLEAN      NOT NULL DEFAULT TRUE,
    config_json JSONB,
    created_at  TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    created_by  BIGINT,
    update_by   BIGINT
    );

-- Skills
CREATE TABLE IF NOT EXISTS skills
(
    id          SERIAL PRIMARY KEY,
    name        VARCHAR(200) NOT NULL UNIQUE,
    description varchar(200),
    created_at  TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    created_by  BIGINT,
    update_by   BIGINT
    );

-- Projects
CREATE TABLE IF NOT EXISTS projects
(
    id              SERIAL PRIMARY KEY,
    external_id     VARCHAR(500),
    source_id       INTEGER REFERENCES sources (id),
    title           VARCHAR(1000) NOT NULL,
    description     TEXT,
    url             VARCHAR(2000),
    budget_min      NUMERIC(12, 2),
    budget_max      NUMERIC(12, 2),
    budget_currency VARCHAR(10)   NOT NULL DEFAULT 'USD',
    budget_type     VARCHAR(50),
    timeline        VARCHAR(300),
    posted_at       TIMESTAMPTZ,
    deadline        TIMESTAMPTZ,
    status          VARCHAR(50)   NOT NULL DEFAULT 'new',
    score           NUMERIC(5, 2),
    score_breakdown JSONB,
    raw_data        JSONB,
    ai_summary      TEXT,
    ai_tags         JSONB,
    is_duplicate    BOOLEAN       NOT NULL DEFAULT FALSE,
    duplicate_of_id INTEGER REFERENCES projects (id),
    fingerprint     VARCHAR(500),
    notes           TEXT,
    created_at      TIMESTAMPTZ   NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ   NOT NULL DEFAULT NOW(),
    created_by      BIGINT,
    update_by       BIGINT
    );

CREATE INDEX IF NOT EXISTS idx_projects_source ON projects (source_id);
CREATE INDEX IF NOT EXISTS idx_projects_status ON projects (status);
CREATE INDEX IF NOT EXISTS idx_projects_score ON projects (score DESC);
CREATE INDEX IF NOT EXISTS idx_projects_posted ON projects (posted_at DESC);
CREATE INDEX IF NOT EXISTS idx_projects_fingerprint ON projects (fingerprint);
CREATE INDEX IF NOT EXISTS idx_projects_created ON projects (created_at DESC);

-- Scrape Runs
CREATE TABLE IF NOT EXISTS agent_logs
(
    id             SERIAL PRIMARY KEY,
    source_id      INTEGER     NOT NULL REFERENCES sources (id),
    started_at     TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    finished_at    TIMESTAMPTZ,
    status         VARCHAR(50) NOT NULL DEFAULT 'running',
    projects_found INTEGER     NOT NULL DEFAULT 0,
    projects_new   INTEGER     NOT NULL DEFAULT 0,
    error_message  TEXT,
    config_used    JSONB,
    created_at     TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at     TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by     BIGINT,
    update_by      BIGINT

    );
