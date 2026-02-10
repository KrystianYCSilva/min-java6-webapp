---
description: Regras arquiteturais obrigatorias do projeto (T0/T1), incluindo dependencias entre camadas e politicas de mudanca estrutural.
tier: T1
triggers:
  - regra
  - arquitetura
  - camadas
  - dependencia
  - sql
  - jpa
  - zk
last_updated: 2026-02-10
---
# Regras Arquiteturais

## Regras obrigatorias (MUST)

1. `web/zk` deve apenas orquestrar interacao de tela e delegar para `service`.
2. Regra de negocio deve ficar em `service`.
3. Persistencia deve ficar apenas em classes `dao`.
4. SQL nativo em `dao` deve usar bind seguro de parametros.
5. Classes `model` nao devem depender de `web` nem de `dao`.
6. Novos fluxos autenticados devem passar por `AuthFilter`.
7. Mudancas de schema devem manter compatibilidade com seeds e testes.
8. DAOs devem usar `AbstractJpaDao` (sem `DriverManager` direto).

## Direcao de dependencias permitida

1. `web/zk -> service`
2. `service -> dao | model | util`
3. `dao -> model | config | domain`
4. `util` deve ser reutilizavel e sem dependencia de camada web

## Regras de mudanca de banco

1. Alterou tabela? atualizar `schema.sql`.
2. Alterou dominio base? atualizar `seed.sql`.
3. Alterou metadados de layout de aluno/curso/curso-aluno? atualizar `seed_layout.sql`.
4. Alterou metadados de layout de docente/ies? atualizar `seed_layout_ies_docente.sql`.
5. Alterou referencia de municipio/UF? atualizar `seed_municipio.sql`.
6. Alterou persistencia? atualizar testes de `dao` e `service`.

## Regras de compatibilidade

1. Codigo de producao deve seguir Java 6.
2. Evitar recursos de Java 7+ no pacote `src/main/java`.
3. O `web.xml` legado e obrigatorio (`failOnMissingWebXml=true`).
4. Versao Hibernate deve permanecer na linha compativel com Java 6 (`4.2.x`).
