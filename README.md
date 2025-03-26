# API Rest de Inventario de Productos

Esta API basada en un enfoque de microservicios fue creada con el objetivo de administrar los productos de un inventario de una tienda e-commerce de artículos de electrónica y robótica.

# Como ejecutar

1. Ejecutar micro servicio de inventario y base de datos con:

        docker-compose -f docker-compose-inventory.yml up

2. Ejecutar broker de mensajería de RabbitMQ para notificación de bajo stock con:

        docker-compose -f docker-compose-rabbitmq.yml up

