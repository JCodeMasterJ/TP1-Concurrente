                # Sistema de Procesamiento de Jobs en Cluster Distribuido

Este proyecto consiste en el desarrollo de un sistema concurrente programado en **Java** que simula el ciclo de vida de trabajos computacionales (*jobs*) dentro de un cluster de alto rendimiento. El objetivo principal es gestionar la asignación de recursos, validación y ejecución de tareas de forma asíncrona y eficiente.

## Escenario del Sistema

El sistema administra un cluster compuesto por **200 nodos de cómputo**. Cada nodo es un recurso crítico que puede estar en uno de tres estados: `Libre`, `Ocupado` o `Fuera de Servicio`. Además, cada nodo mantiene un histórico de ejecuciones realizadas.

### Arquitectura de Procesamiento

El flujo de trabajo se divide en cuatro etapas principales ejecutadas de forma concurrente por distintos grupos de hilos:

1.  **Ingreso de Jobs (Scheduler):** 3 hilos encargados de asignar jobs a nodos aleatorios disponibles.
2.  **Validación (Pre-Execution Check):** 2 hilos que verifican la configuración técnica de los jobs antes de su ejecución (tasa de éxito del 85%).
3.  **Ejecución (Worker Execution):** 3 hilos que simulan el procesamiento efectivo del job (tasa de éxito del 90%).
4.  **Verificación Final (Auditor):** 2 hilos que realizan el post-procesamiento y control de calidad de los resultados (tasa de éxito del 95%).

## Requerimientos Técnicos y de Concurrencia

* **Sincronización:** Cada nodo y cada job deben ser accedidos por un solo hilo a la vez para evitar condiciones de carrera (*race conditions*).
* **Gestión de Colecciones:** Implementación de estructuras para el manejo de colas de jobs (En espera, En ejecución, Finalizados, Fallidos y Validados).
* **Monitoreo (Logging):** Un proceso de auditoría debe registrar estadísticas en un archivo cada 200 milisegundos, reportando el estado global del sistema.
* **Simulación Temporal:** Cada etapa posee demoras configurables para modelar un entorno de carga realista.

## Estructura del Proyecto

El sistema está diseñado bajo el paradigma de orientación a objetos, separando la lógica de los actores (hilos), las entidades de datos (Nodos y Jobs) y el monitor de recursos compartidos.

---
*Este proyecto es parte del Trabajo Práctico N° 1 de la asignatura Programación Concurrente - FCEFYN - UNC.*