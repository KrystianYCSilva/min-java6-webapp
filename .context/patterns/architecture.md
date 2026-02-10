---
description: Blueprint da arquitetura do sistema com camadas, fluxo de request, responsabilidades e padroes aplicados no legado.
tier: T1
triggers:
  - blueprint
  - arquitetura
  - fluxo
  - servlet
  - jdbc
  - hibernate
last_updated: 2026-02-10
---
# Blueprint de Arquitetura

## Estilo geral

Aplicacao monolitica web em arquitetura em camadas, orientada a casos de uso de cadastro e importacao/exportacao para Censo Superior.

Modulos ativos:

1. Aluno (Registro 41)
2. Curso (Registro 21)
3. CursoAluno (Registro 42)
4. Docente (Registro 31)
5. IES (Registro 11)

## Fluxo principal de request

```text
HTTP Request
  -> AuthFilter (quando rota /app/*)
  -> CsrfFilter (apenas POST em /app/*)
  -> Servlet (web, despacho por Command)
  -> Service (regra de negocio)
  -> DAO (Hibernate nativo)
  -> AbstractHibernateDao / HibernateConnectionProvider (SessionFactory)
  -> H2 / tabelas
  -> Service
  -> Servlet
HTTP Response (JSP com output escaping via ViewUtils.e)
```

## Padroes adotados

1. DAO Pattern para isolamento de persistencia.
2. Service Layer para invariantes de negocio.
3. Template transacional de persistencia via Hibernate (`AbstractHibernateDao` + `HibernateConnectionProvider`).
4. Filter para controle de autenticacao de sessao.
5. Builder Pattern para montagem de entidades com muitos campos vindos de formulario.
6. Synchronizer Token Pattern para protecao CSRF em operacoes mutaveis.
7. Output Encoding centralizado em `ViewUtils.e(...)` para mitigar XSS.
8. Password hardening com PBKDF2 e migracao progressiva de hashes legados.
9. Command Pattern no web layer para despacho de `acao` com mapa de comandos.

## Fronteiras de responsabilidade

1. `web`: entrada/saida HTTP e navegacao.
2. `service`: validacao, consistencia e orquestracao.
3. `dao`: persistencia Hibernate, query e mapeamento ORM.
4. `model`: representacao de entidades de dominio.
5. `util`: hash, validacao e mapeamentos auxiliares.

## Pontos de atencao arquitetural

1. Fluxos de import/export devem manter consistencia entre modelo e metadados de layout.
2. Mudancas de schema impactam mapeamentos ORM, DAO, service e testes em cascata.
3. Dependencias devem ser unidirecionais para evitar acoplamento ciclico.
4. Validacao de UF/municipio em `DocenteService` e `IesService` depende da tabela `municipio`.
5. Pool interno do Hibernate e aceitavel para desenvolvimento, mas nao para producao.
6. `CursoAluno` segue padrao de telas separadas de listagem e formulario para legibilidade.
