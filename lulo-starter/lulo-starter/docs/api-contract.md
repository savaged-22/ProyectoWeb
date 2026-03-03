# API Contract (Borrador) — Lulo

Guía de endpoints para alinear Front/Back.

## Auth
- POST /api/auth/login
- POST /api/auth/invitations/{token}/accept

## Company / Pool (multiempresa)
- POST /api/companies
- GET  /api/companies/{id}
- PATCH /api/companies/{id}
- POST /api/companies/{id}/pool

## Users / Invitations
- POST /api/users/invitations
- GET  /api/users
- PATCH /api/users/{id}/role

## Processes
- POST /api/processes
- GET  /api/processes?status=&category=&q=&page=&size=
- GET  /api/processes/{id}
- PATCH /api/processes/{id}
- DELETE /api/processes/{id}   (soft delete)

## Diagram (activities/arcs/gateways/lanes)
- POST   /api/processes/{id}/activities
- PATCH  /api/activities/{id}
- DELETE /api/activities/{id}

- POST   /api/processes/{id}/arcs
- PATCH  /api/arcs/{id}
- DELETE /api/arcs/{id}

- POST   /api/processes/{id}/gateways
- PATCH  /api/gateways/{id}
- DELETE /api/gateways/{id}

- POST   /api/processes/{id}/lanes
- PATCH  /api/lanes/{id}
- DELETE /api/lanes/{id}

## Sharing / RBAC
- POST  /api/processes/{id}/share
- GET   /api/processes/{id}/share
- PATCH /api/processes/{id}/share

- GET   /api/rbac/roles
- POST  /api/rbac/roles
- PATCH /api/rbac/roles/{id}
- DELETE /api/rbac/roles/{id}
- GET   /api/rbac/permissions
- PATCH /api/rbac/roles/{id}/permissions
