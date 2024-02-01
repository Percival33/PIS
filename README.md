# PIS

<!-- TOC -->
* [PIS](#pis)
  * [Project Overview](#project-overview)
    * [Note](#note)
  * [Team Members](#team-members)
  * [Project Requirements](#project-requirements)
  * [Structure](#structure)
<!-- TOC -->

## Project Overview
This repository contains a microservices-based Parcel Tracking System, initially developed as a college assignment by a team of four. 
It allows clients to add and track shipments using unique tracking numbers. The system integrates with various carriers for precise shipment location tracking. 
Key features include local database storage of tracking history and automatic updates for registered shipments. 
The system also supports searching across all shipment attributes and offers functionality for printing tracking histories, with files stored in a dedicated resource.

### Note
At the time of college submission, the project was in its [initial form](https://github.com/Percival33/PIS/tree/e904b722c8ec0cf4c6c1359c2bb9e564471e1912). 
Post-submission, it has been further developed to include Kubernetes support, along with the addition of monitoring tools Grafana and Prometheus. 

## Team Members
- [Dawid Kaszyński](https://github.com/dawidkasz)
- [Mikołaj Szawerda](https://github.com/MikolajSzawerda)
- [Jakub Jażdzyk](https://github.com/kubajaz)
- [Marcin Jarczewski](https://github.com/percival33)

## Project Requirements

**CI/CD:** Jenkins  
**Artifact Repository:** Nexus

**Databases:**
- MongoDB
- Elasticsearch

**Backend Technologies:**
- Java 17
- Gradle/Maven
- Spring Boot 3.1
- Hibernate
- OpenFeign
- Resilience4j
- Apache Kafka
- MinIO
- Hashicorp Vault
- JUnit
- Docker + Docker Compose

**Frontend Technologies:**
- React

**Microservices:**
- Service Discovery
- API Gateway
- Config Server

## Structure

The project is structured as a monorepo, including the following services:

- `gateway`: API gateway for microservices.
- `frontend`: React-based user interface.
- `parcel-finder`: Service for package searching with Elasticsearch and Kafka.
- `parcel-tracker`: Continuous package tracking service.
- `report-generator`: PDF report generation for package tracking history.
- `service-discovery`: Eureka discovery server.
- `config-server`: Management of external configurations for microservices.

**Additional Directories:**
- `devops`: Configuration for NGINX reverse proxy, Jenkins setup, and automated build and deployment scripts.