-- V3__add_final_entities.sql
-- Athena Data Layer - Phase 2 Batch 3 (Final Batch)
-- Creates 8 tables: saved_searches, opportunity_scores, alerts, teams, team_members,
-- competitor_intel, historical_data, sync_logs
-- Completes Phase 2 data layer migration (19/19 entities)

-- SavedSearches table (user-saved search queries)
CREATE TABLE saved_searches (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID NOT NULL,
    search_name VARCHAR(255) NOT NULL,
    search_criteria JSONB NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT true,
    last_executed TIMESTAMP WITH TIME ZONE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_saved_searches_user_id ON saved_searches(user_id);
CREATE INDEX idx_saved_searches_is_active ON saved_searches(is_active);
CREATE INDEX idx_saved_searches_last_executed ON saved_searches(last_executed);

COMMENT ON TABLE saved_searches IS 'User-saved search queries for quick re-execution';
COMMENT ON COLUMN saved_searches.search_criteria IS 'JSONB search criteria (filters, keywords, NAICS codes, etc.)';

-- OpportunityScores table (AI scoring results)
CREATE TABLE opportunity_scores (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    opportunity_id UUID NOT NULL,
    score_type VARCHAR(50) NOT NULL,
    score_value NUMERIC(5,2) NOT NULL,
    confidence NUMERIC(5,2),
    scored_at TIMESTAMP WITH TIME ZONE NOT NULL,
    metadata JSONB,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_opportunity_scores_opportunity_id ON opportunity_scores(opportunity_id);
CREATE INDEX idx_opportunity_scores_score_type ON opportunity_scores(score_type);
CREATE INDEX idx_opportunity_scores_scored_at ON opportunity_scores(scored_at);
CREATE INDEX idx_opportunity_scores_score_value ON opportunity_scores(score_value);

COMMENT ON TABLE opportunity_scores IS 'AI-generated scores for opportunities (relevance, win probability, strategic fit)';
COMMENT ON COLUMN opportunity_scores.score_type IS 'Type of score: relevance, win_probability, strategic_fit, etc.';
COMMENT ON COLUMN opportunity_scores.metadata IS 'Additional scoring metadata (model version, features used, etc.)';

-- Alerts table (user notification preferences)
CREATE TABLE alerts (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID NOT NULL,
    alert_type VARCHAR(50) NOT NULL,
    criteria JSONB NOT NULL,
    frequency VARCHAR(50) NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT true,
    last_triggered TIMESTAMP WITH TIME ZONE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_alerts_user_id ON alerts(user_id);
CREATE INDEX idx_alerts_alert_type ON alerts(alert_type);
CREATE INDEX idx_alerts_is_active ON alerts(is_active);
CREATE INDEX idx_alerts_last_triggered ON alerts(last_triggered);

COMMENT ON TABLE alerts IS 'User notification preferences and automated alert triggers';
COMMENT ON COLUMN alerts.criteria IS 'JSONB criteria for triggering alerts (keywords, NAICS, agencies, etc.)';
COMMENT ON COLUMN alerts.frequency IS 'Alert frequency: realtime, daily, weekly';

-- Teams table (contractor teaming arrangements)
CREATE TABLE teams (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    lead_organization_id UUID NOT NULL,
    opportunity_id UUID NOT NULL,
    team_name VARCHAR(255) NOT NULL,
    status VARCHAR(50) NOT NULL,
    created_by UUID NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_teams_lead_organization_id ON teams(lead_organization_id);
CREATE INDEX idx_teams_opportunity_id ON teams(opportunity_id);
CREATE INDEX idx_teams_created_by ON teams(created_by);
CREATE INDEX idx_teams_status ON teams(status);

COMMENT ON TABLE teams IS 'Contractor teams formed to pursue opportunities';
COMMENT ON COLUMN teams.status IS 'Team status: forming, active, submitted, awarded, disbanded';

-- TeamMembers table (team membership)
CREATE TABLE team_members (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    team_id UUID NOT NULL,
    organization_id UUID NOT NULL,
    role VARCHAR(100) NOT NULL,
    capabilities TEXT,
    is_prime BOOLEAN NOT NULL DEFAULT false,
    added_by UUID NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_team_members_team_id ON team_members(team_id);
CREATE INDEX idx_team_members_organization_id ON team_members(organization_id);
CREATE INDEX idx_team_members_added_by ON team_members(added_by);
CREATE INDEX idx_team_members_is_prime ON team_members(is_prime);

COMMENT ON TABLE team_members IS 'Individual team member organizations with roles and capabilities';
COMMENT ON COLUMN team_members.role IS 'Member role: prime, subcontractor, partner, consultant';
COMMENT ON COLUMN team_members.is_prime IS 'True if this member is the prime contractor';

-- CompetitorIntel table (competitive analysis)
CREATE TABLE competitor_intel (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    organization_id UUID NOT NULL,
    opportunity_id UUID NOT NULL,
    likelihood VARCHAR(50),
    strengths TEXT,
    weaknesses TEXT,
    source VARCHAR(100) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_competitor_intel_organization_id ON competitor_intel(organization_id);
CREATE INDEX idx_competitor_intel_opportunity_id ON competitor_intel(opportunity_id);
CREATE INDEX idx_competitor_intel_likelihood ON competitor_intel(likelihood);
CREATE INDEX idx_competitor_intel_source ON competitor_intel(source);

COMMENT ON TABLE competitor_intel IS 'Competitive intelligence on competitors for specific opportunities';
COMMENT ON COLUMN competitor_intel.likelihood IS 'Win likelihood: very_low, low, medium, high, very_high';
COMMENT ON COLUMN competitor_intel.source IS 'Intel source: public, internal, industry, automated';

-- HistoricalData table (historical trends and snapshots)
CREATE TABLE historical_data (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    entity_type VARCHAR(100) NOT NULL,
    entity_id UUID NOT NULL,
    data_type VARCHAR(100) NOT NULL,
    data_value JSONB NOT NULL,
    captured_at TIMESTAMP WITH TIME ZONE NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_historical_data_entity_type ON historical_data(entity_type);
CREATE INDEX idx_historical_data_entity_id ON historical_data(entity_id);
CREATE INDEX idx_historical_data_data_type ON historical_data(data_type);
CREATE INDEX idx_historical_data_captured_at ON historical_data(captured_at);

COMMENT ON TABLE historical_data IS 'Generic historical snapshots for time-series analytics';
COMMENT ON COLUMN historical_data.entity_type IS 'Entity type: opportunity, organization, award, etc.';
COMMENT ON COLUMN historical_data.data_type IS 'Data type: award_trends, score_history, status_change, etc.';
COMMENT ON COLUMN historical_data.data_value IS 'JSONB snapshot data';

-- SyncLogs table (SAM.gov sync tracking)
CREATE TABLE sync_logs (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    sync_type VARCHAR(100) NOT NULL,
    started_at TIMESTAMP WITH TIME ZONE NOT NULL,
    completed_at TIMESTAMP WITH TIME ZONE,
    status VARCHAR(50) NOT NULL,
    records_processed INTEGER DEFAULT 0,
    error_count INTEGER DEFAULT 0,
    error_log TEXT,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_sync_logs_sync_type ON sync_logs(sync_type);
CREATE INDEX idx_sync_logs_status ON sync_logs(status);
CREATE INDEX idx_sync_logs_started_at ON sync_logs(started_at);
CREATE INDEX idx_sync_logs_completed_at ON sync_logs(completed_at);

COMMENT ON TABLE sync_logs IS 'SAM.gov synchronization operation tracking';
COMMENT ON COLUMN sync_logs.sync_type IS 'Sync type: opportunities, awards, organizations, full';
COMMENT ON COLUMN sync_logs.status IS 'Sync status: running, success, failed, partial';
