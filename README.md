# Visor y Editor de Procesos Empresariales LULO

## Descripción
Este proyecto consiste en el desarrollo de un **visor y editor de procesos empresariales**. Su objetivo es permitir la **visualización y edición** de procesos asociados a una empresa, garantizando la **separación de información entre organizaciones** 

Cada empresa contará con sus **propios usuarios**, y los procesos creados o gestionados serán **exclusivos** de la empresa del usuario autenticado.

## Objetivos del proyecto
- Permitir a los usuarios **consultar, crear, modificar y organizar procesos**.
- Garantizar separación de datos por empresa.
- Aplicar conceptos de **arquitectura web**, **separación de responsabilidades**, **seguridad básica** y **buenas prácticas** de desarrollo.
- Desarrollar el sistema de forma **incremental**, con entregas evaluables.

## Alcance
### Incluye
- Gestión de **empresas** (organizaciones).
- Gestión de **usuarios** asociados a una empresa.
- Gestión de **procesos** (visualización, creación, edición, organización).
- API REST para exponer funcionalidades del backend.
- Interfaz web para consumir la API y operar sobre procesos.
- Integración y despliegue de la solución completa.

## Entregas del semestre
### Entrega 1 — Backend
- Modelo de datos.
- Lógica de negocio.
- Servicios REST para empresas, usuarios y procesos.
- Validaciones, manejo de errores y seguridad básica.

### Entrega 2 — Frontend
- Interfaz web que consuma la API.
- Visualización y edición de procesos.
- Flujo de autenticación y navegación base.

### Entrega 3 — Integración final
- Integración end-to-end (frontend + backend).
- Revisión de seguridad básica.
- Calidad de código y pruebas.
- Despliegue y demostración funcional.

## Arquitectura (propuesta)
- **Frontend:** Aplicación web SPA que consume servicios REST.
- **Backend:** API REST con separación por capas (controladores, servicios, repositorios).
- **Persistencia:** Base de datos relacional con separación lógica por empresa (por ejemplo, `company_id` como clave de partición lógica).
- **Autenticación/Autorización:** JWT o sesión (según el stack acordado), con control de acceso por empresa.

## Reglas clave del sistema
- Un usuario pertenece a **una empresa**.
- Un usuario autenticado solo puede ver y modificar:
  - Sus datos (según permisos).
  - Procesos de su empresa.
- No existe visibilidad cruzada entre empresas.
- El sistema solo gestiona **edición/visualización**, no ejecución.

## Funcionalidades esperadas
- Autenticación de usuarios.
- CRUD de procesos (crear, consultar, actualizar, eliminar).
- Organización de procesos (por ejemplo: carpetas, etiquetas, estados, o versiones; según definición del equipo).
- Visualización del proceso (diagrama o estructura, según el formato definido).
