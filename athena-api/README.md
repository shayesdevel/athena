# athena-api

REST API module for Athena platform.

## Responsibilities
- Spring MVC Controllers (14 endpoints)
- Request/Response DTOs
- Spring Security configuration
- JWT authentication
- OpenAPI/Swagger documentation
- API versioning

## Dependencies
- `athena-core`: Domain models, repositories, services
- `athena-common`: Shared utilities

## Key Components
- Controllers: OpportunitiesController, AuthController, WorkspacesController, etc.
- Security: JwtTokenProvider, SecurityConfig, AuthenticationFilter
- DTOs: Request/Response models with Bean Validation
- Exception Handlers: Global exception handling

## Endpoints
See Springdoc OpenAPI UI: `/swagger-ui.html` when running
