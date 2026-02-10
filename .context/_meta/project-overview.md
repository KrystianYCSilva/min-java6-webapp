---
description: Visao geral do projeto Censo Superior 2025, com objetivo, escopo funcional, fluxos criticos e restricoes operacionais.
tier: T2
triggers:
  - overview
  - escopo
  - objetivo
  - fluxo
  - modulo
  - hibernate
last_updated: 2026-02-10
---
# Visao Geral do Projeto

## Objetivo

Aplicacao web legada para cadastro, consulta, importacao e exportacao de dados do Censo Superior 2025 com foco em compatibilidade com stack Java 6 + Servlet/JSP.

## Escopo funcional atual

1. Autenticacao por login/senha com sessao HTTP.
2. Modulo `Aluno` (Registro 41): CRUD, listagem paginada, importacao/exportacao TXT pipe.
3. Modulo `Curso` (Registro 21): CRUD, listagem paginada, importacao/exportacao TXT pipe.
4. Modulo `CursoAluno` (Registro 42): vinculo de aluno em curso e dados complementares (telas separadas de lista e formulario).
5. Modulo `Docente` (Registro 31): CRUD, importacao/exportacao TXT pipe.
6. Modulo `IES` (Registro 11): CRUD, importacao/exportacao TXT pipe.
7. Exportacao de registros individuais e em lote.

## Restricoes

1. Codigo de producao deve permanecer compativel com Java 6.
2. Camadas devem manter isolamento (`web`, `service`, `dao`, `model`, `util`).
3. Persistencia com Hibernate 4.2 nativo (`Session`/`Transaction`), sem JPA nesta versao.
4. Banco principal de desenvolvimento/teste: H2 embarcado.

## Fluxos criticos

1. Login em `LoginServlet` e autorizacao via `AuthFilter`.
2. Persistencia de entidades principais (`aluno`, `curso`, `curso_aluno`, `docente`, `ies`).
3. Validacao de consistencia UF/codigo de municipio via tabela de apoio `municipio`.
4. Mapeamento de campos complementares de layout (`layout_campo` e tabelas `_layout_valor`).
5. Importacao e exportacao em formato TXT separado por `|`.

## Artefatos centrais

1. Backend: `src/main/java/br/gov/inep/censo`.
2. Frontend JSP: `src/main/webapp`.
3. Esquema e seeds: `src/main/resources/db`.
4. Testes: `src/test/java`.
5. Arquitetura: `docs/ARCHITECTURE.md`.
