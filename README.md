# API Rest de Inventario de Productos

Microservicio de inventario para un sitio web de ecommerce.

---

## Tecnologías Usadas

- Java
- Maven
- Spring Boot
- PostgreSQL
- Docker
- Postman

---

## Requisitos Técnicos

Para ejecutar la aplicación es necesario tener instalado en tu máquina local:

- Docker para la ejecución de todo el microservicio.
- Postman para utilizar y verificar las rutas de la API.

---

## Descripción

Esta API basada en un enfoque de microservicios fue creada con el objetivo de administrar los productos de un inventario de una tienda e-commerce de artículos de electrónica y robótica.

---

## Pasos para ejecutar

1. **Clonar el repositorio** del proyecto en tu máquina local:
   ```bash
   git clone https://github.com/MarcoAntonioRG/API-Inventario.git
   cd API-Inventario

2. Ejecutar micro servicio de inventario y base de datos con:

        docker-compose -f docker-compose-inventory.yml up

3. (Opcional) Ejecutar broker de mensajería de RabbitMQ para notificación de bajo stock con:

        docker-compose -f docker-compose-rabbitmq.yml up

