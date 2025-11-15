-- V2__add_reference_and_transactional_entities.sql
-- Athena Data Layer - Phase 2 Batch 2
-- Creates 6 additional tables: notice_types, set_asides, naics, contract_vehicles, attachments, awards
-- Reference data tables for opportunity classification and transactional tables for attachments and awards

-- Notice Types table (reference data for SAM.gov notice types)
CREATE TABLE notice_types (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    code VARCHAR(50) NOT NULL UNIQUE,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    is_active BOOLEAN NOT NULL DEFAULT true,
    category VARCHAR(50),
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_notice_types_code ON notice_types(code);
CREATE INDEX idx_notice_types_is_active ON notice_types(is_active);

-- Set-Aside types table (reference data for contract set-aside programs)
CREATE TABLE set_asides (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    code VARCHAR(50) NOT NULL UNIQUE,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    is_active BOOLEAN NOT NULL DEFAULT true,
    eligibility_criteria VARCHAR(100),
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_set_asides_code ON set_asides(code);
CREATE INDEX idx_set_asides_is_active ON set_asides(is_active);

-- NAICS codes table (North American Industry Classification System)
CREATE TABLE naics (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    code VARCHAR(6) NOT NULL UNIQUE,
    title VARCHAR(500) NOT NULL,
    description TEXT,
    parent_code VARCHAR(6),
    level INTEGER NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT true,
    year_version VARCHAR(50),
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_naics_code ON naics(code);
CREATE INDEX idx_naics_parent_code ON naics(parent_code);
CREATE INDEX idx_naics_is_active ON naics(is_active);

-- Contract Vehicles table (reference data for acquisition vehicles)
CREATE TABLE contract_vehicles (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    code VARCHAR(50) NOT NULL UNIQUE,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    is_active BOOLEAN NOT NULL DEFAULT true,
    category VARCHAR(50),
    managing_agency VARCHAR(255),
    url TEXT,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_contract_vehicles_code ON contract_vehicles(code);
CREATE INDEX idx_contract_vehicles_is_active ON contract_vehicles(is_active);
CREATE INDEX idx_contract_vehicles_category ON contract_vehicles(category);

-- Attachments table (document attachments for opportunities)
CREATE TABLE attachments (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    opportunity_id UUID NOT NULL REFERENCES opportunities(id) ON DELETE CASCADE,
    file_name VARCHAR(500) NOT NULL,
    file_url TEXT NOT NULL,
    type VARCHAR(50),
    mime_type VARCHAR(100),
    file_size BIGINT,
    description TEXT,
    sam_attachment_id VARCHAR(50),
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_attachments_opportunity_id ON attachments(opportunity_id);
CREATE INDEX idx_attachments_type ON attachments(type);
CREATE INDEX idx_attachments_created_at ON attachments(created_at);

-- Awards table (contract award data)
CREATE TABLE awards (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    opportunity_id UUID REFERENCES opportunities(id) ON DELETE SET NULL,
    contract_number VARCHAR(255) NOT NULL UNIQUE,
    title VARCHAR(500),
    organization_id UUID REFERENCES organizations(id) ON DELETE SET NULL,
    awardee_name VARCHAR(500),
    awardee_uei VARCHAR(12),
    awardee_duns VARCHAR(9),
    award_date DATE,
    award_amount DECIMAL(15, 2),
    currency VARCHAR(3) DEFAULT 'USD',
    start_date DATE,
    end_date DATE,
    agency_id UUID REFERENCES agencies(id) ON DELETE SET NULL,
    awarding_office VARCHAR(500),
    award_type VARCHAR(100),
    naics_code VARCHAR(6),
    set_aside VARCHAR(100),
    description TEXT,
    is_active BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_awards_opportunity_id ON awards(opportunity_id);
CREATE INDEX idx_awards_organization_id ON awards(organization_id);
CREATE INDEX idx_awards_contract_number ON awards(contract_number);
CREATE INDEX idx_awards_award_date ON awards(award_date);
CREATE INDEX idx_awards_is_active ON awards(is_active);

-- Apply updated_at triggers to all new tables
CREATE TRIGGER update_notice_types_updated_at BEFORE UPDATE ON notice_types
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_set_asides_updated_at BEFORE UPDATE ON set_asides
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_naics_updated_at BEFORE UPDATE ON naics
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_contract_vehicles_updated_at BEFORE UPDATE ON contract_vehicles
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_attachments_updated_at BEFORE UPDATE ON attachments
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_awards_updated_at BEFORE UPDATE ON awards
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
