---
description: Visao geral do projeto Censo Superior 2025 com escopo funcional atual, fluxos criticos e restricoes operacionais.
tier: T2
triggers:
  - overview
  - escopo
  - objetivo
  - fluxo
  - modulo
  - zk
  - jpa
last_updated: 2026-02-10
---
# Visao Geral do Projeto

## Objetivo

Aplicacao web para cadastro, consulta, importacao e exportacao de dados do Censo Superior 2025, mantendo compatibilidade com stack Java 6.

## Escopo funcional atual

1. Autenticacao por login/senha com sessao HTTP.
2. Modulo `Aluno` (Registro 41): CRUD, listagem paginada, importacao/exportacao TXT pipe.
3. Modulo `Curso` (Registro 21): CRUD, listagem paginada, importacao/exportacao TXT pipe.
4. Modulo `CursoAluno` (Registro 42): vinculo de aluno em curso e dados complementares.
5. Modulo `Docente` (Registro 31): CRUD, importacao/exportacao TXT pipe.
6. Modulo `IES` (Registro 11): CRUD, importacao/exportacao TXT pipe.
7. Exportacao de registros individuais e em lote.

## Restricoes

1. Codigo de producao deve permanecer compativel com Java 6.
2. Camadas devem manter isolamento (`web/zk`, `service`, `dao`, `model`, `util`).
3. Persistencia com Hibernate 4.2 via JPA (`javax.persistence`) nesta versao.
4. Banco principal de desenvolvimento/teste: H2 embarcado.

## Fluxos criticos

1. Login em `LoginComposer` e autorizacao via `AuthFilter`.
2. Navegacao no shell `menu.zul` com `view` (conteudo principal) e `sub` (modal).
3. Persistencia de entidades principais (`aluno`, `curso`, `curso_aluno`, `docente`, `ies`).
4. Validacao de consistencia UF/codigo de municipio via tabela de apoio `municipio`.
5. Mapeamento de campos complementares de layout (`layout_campo` e tabelas `_layout_valor`).
6. Importacao e exportacao em formato TXT separado por `|`.

## Artefatos centrais

1. Backend: `src/main/java/br/gov/inep/censo`.
2. Frontend ZK: `src/main/webapp/*.zul` e `src/main/webapp/app/*.zul`.
3. Shell e estilo: `src/main/webapp/app/menu.zul` e `src/main/webapp/css/app-shell.css`.
4. Esquema e seeds: `src/main/resources/db`.
5. Testes: `src/test/java`.
6. Arquitetura: `docs/ARCHITECTURE.md`.
