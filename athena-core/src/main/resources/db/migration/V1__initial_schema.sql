-- V1__initial_schema.sql
-- Athena Data Layer - Initial Schema
-- Creates 5 core tables: users, organizations, agencies, opportunities, contacts

-- Enable UUID extension
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- Users table (authentication/authorization)
CREATE TABLE users (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    email VARCHAR(255) NOT NULL UNIQUE,
    username VARCHAR(100) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    first_name VARCHAR(100),
    last_name VARCHAR(100),
    is_active BOOLEAN NOT NULL DEFAULT true,
    is_admin BOOLEAN NOT NULL DEFAULT false,
    last_login_at TIMESTAMP WITH TIME ZONE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_username ON users(username);
CREATE INDEX idx_users_is_active ON users(is_active);

-- Organizations table (contractor organizations)
CREATE TABLE organizations (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    name VARCHAR(500) NOT NULL,
    uei VARCHAR(12) UNIQUE,
    cage_code VARCHAR(5),
    duns VARCHAR(9),
    sam_url TEXT,
    primary_naics VARCHAR(6),
    business_type VARCHAR(100),
    is_small_business BOOLEAN DEFAULT false,
    is_woman_owned BOOLEAN DEFAULT false,
    is_veteran_owned BOOLEAN DEFAULT false,
    is_8a_certified BOOLEAN DEFAULT false,
    street_address VARCHAR(500),
    city VARCHAR(100),
    state_code VARCHAR(2),
    zip_code VARCHAR(10),
    country_code VARCHAR(2) DEFAULT 'US',
    website_url TEXT,
    phone VARCHAR(20),
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_organizations_uei ON organizations(uei);
CREATE INDEX idx_organizations_cage_code ON organizations(cage_code);
CREATE INDEX idx_organizations_name ON organizations(name);
CREATE INDEX idx_organizations_primary_naics ON organizations(primary_naics);

-- Agencies table (government agencies)
CREATE TABLE agencies (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    name VARCHAR(500) NOT NULL,
    abbreviation VARCHAR(50),
    parent_agency_id UUID REFERENCES agencies(id) ON DELETE SET NULL,
    department VARCHAR(200),
    tier VARCHAR(50),
    is_active BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_agencies_name ON agencies(name);
CREATE INDEX idx_agencies_abbreviation ON agencies(abbreviation);
CREATE INDEX idx_agencies_parent ON agencies(parent_agency_id);

-- Opportunities table (SAM.gov contract opportunities)
CREATE TABLE opportunities (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    notice_id VARCHAR(255) NOT NULL UNIQUE,
    title TEXT NOT NULL,
    solicitation_number VARCHAR(255),
    agency_id UUID REFERENCES agencies(id) ON DELETE SET NULL,
    office_name VARCHAR(500),
    notice_type VARCHAR(50) NOT NULL,
    base_type VARCHAR(50),
    archive_type VARCHAR(50),
    archive_date DATE,
    naics_code VARCHAR(6),
    classification_code VARCHAR(10),
    set_aside VARCHAR(100),
    posted_date DATE,
    response_deadline TIMESTAMP WITH TIME ZONE,
    description TEXT,
    additional_info_link TEXT,
    ui_link TEXT,
    point_of_contact VARCHAR(255),
    place_of_performance_city VARCHAR(100),
    place_of_performance_state VARCHAR(2),
    place_of_performance_zip VARCHAR(10),
    place_of_performance_country VARCHAR(2) DEFAULT 'USA',
    is_active BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_opportunities_notice_id ON opportunities(notice_id);
CREATE INDEX idx_opportunities_agency_id ON opportunities(agency_id);
CREATE INDEX idx_opportunities_naics_code ON opportunities(naics_code);
CREATE INDEX idx_opportunities_notice_type ON opportunities(notice_type);
CREATE INDEX idx_opportunities_posted_date ON opportunities(posted_date);
CREATE INDEX idx_opportunities_response_deadline ON opportunities(response_deadline);
CREATE INDEX idx_opportunities_is_active ON opportunities(is_active);

-- Contacts table (points of contact)
CREATE TABLE contacts (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    first_name VARCHAR(100),
    last_name VARCHAR(100),
    full_name VARCHAR(255),
    email VARCHAR(255),
    phone VARCHAR(20),
    title VARCHAR(200),
    organization_id UUID REFERENCES organizations(id) ON DELETE SET NULL,
    agency_id UUID REFERENCES agencies(id) ON DELETE SET NULL,
    opportunity_id UUID REFERENCES opportunities(id) ON DELETE CASCADE,
    contact_type VARCHAR(50),
    is_primary BOOLEAN DEFAULT false,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_contacts_email ON contacts(email);
CREATE INDEX idx_contacts_organization_id ON contacts(organization_id);
CREATE INDEX idx_contacts_agency_id ON contacts(agency_id);
CREATE INDEX idx_contacts_opportunity_id ON contacts(opportunity_id);
CREATE INDEX idx_contacts_is_primary ON contacts(is_primary);

-- Trigger function for updated_at timestamps
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';

-- Apply updated_at triggers to all tables
CREATE TRIGGER update_users_updated_at BEFORE UPDATE ON users
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_organizations_updated_at BEFORE UPDATE ON organizations
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_agencies_updated_at BEFORE UPDATE ON agencies
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_opportunities_updated_at BEFORE UPDATE ON opportunities
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_contacts_updated_at BEFORE UPDATE ON contacts
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
