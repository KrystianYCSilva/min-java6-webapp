---
description: Padroes de qualidade de codigo para Java 6 legado, com foco em legibilidade, seguranca, manutencao e consistencia entre camadas.
tier: T1
triggers:
  - qualidade
  - padrao
  - java6
  - clean code
  - manutencao
last_updated: 2026-02-10
---
# Padroes de Qualidade de Codigo

## Principios gerais

1. Priorizar clareza e previsibilidade sobre abstrações complexas.
2. Escrever metodos curtos com responsabilidade unica.
3. Nomes de classes e metodos devem refletir dominio ou acao real.
4. Evitar duplicacao entre servicos e DAOs.

## Regras para Java legado

1. Sem lambda, stream, try-with-resources ou APIs apos Java 6 em producao.
2. Fechar recursos JDBC de forma explicita (usar infraestrutura existente em `AbstractJdbcDao`).
3. Evitar `catch` vazio; sempre registrar contexto do erro e propagar de forma controlada.
4. Em DAO, obter conexao apenas por `HibernateConnectionProvider`.

## Regras de seguranca e validacao

1. Entrada HTTP deve ser validada antes de persistir.
2. Senhas devem usar utilitario central (`PasswordUtil`) e nao hash ad-hoc.
3. Consultas SQL devem ser parametrizadas (nunca concatenar entrada do usuario).
4. Nao registrar dados sensiveis em logs ou mensagens de erro.

## Regras de design por camada

1. `web`: parse de parametros, validacao basica e roteamento de resposta.
2. `service`: regras de negocio, orquestracao e invariantes de dominio.
3. `dao`: SQL, mapeamento e transacao local sobre conexao gerenciada por Hibernate.
4. `util`: funcoes reutilizaveis sem estado de request.

## Definicao de pronto para alteracao de codigo

1. O comportamento alterado tem teste cobrindo caso feliz e caso de erro.
2. O fluxo continua aderente a arquitetura em camadas.
3. Nao ha alteracao colateral em `target/` ou `.m2/`.
