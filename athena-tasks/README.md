# athena-tasks

Background task processing for Athena platform.

## Responsibilities
- Scheduled tasks (@Scheduled)
- Spring Batch jobs (complex workflows)
- Async processing
- External API collection
- Document processing

## Key Components
- Collection Tasks: SAM.gov, SBIR.gov, USAspending data collection
- Transform Tasks: ELT pipeline (raw → normalized)
- Scoring Tasks: Two-stage scoring pipeline
- Alert Tasks: Notification delivery
- Document Tasks: PDF extraction, Excel export

## Scheduling
- SAM.gov: Hourly (`@Scheduled(cron = "0 0 * * * *")`)
- SBIR: Weekly
- Transforms: Every 15 minutes
- Alerts: Every 15 minutes
