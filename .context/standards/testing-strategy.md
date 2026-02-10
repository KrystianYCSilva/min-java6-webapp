---
description: Estrategia de testes (unitario, integracao e E2E), comandos recomendados e regras de cobertura para prevenir regressao.
tier: T1
triggers:
  - teste
  - cobertura
  - jacoco
  - integracao
  - e2e
last_updated: 2026-02-10
---
# Estrategia de Testes

## Padrao de piramide

1. Base: testes unitarios (`util`, `enums`, validacoes de service).
2. Meio: integracao (`dao`, `service` com banco H2 e DBUnit).
3. Topo: E2E Selenium (ativacao explicita quando ambiente estiver pronto).

## Cobertura e gate

1. Plugin: `org.jacoco:jacoco-maven-plugin:0.8.8`.
2. Cobertura minima de linha: `80%`.
3. Escopo do gate: pacotes `dao`, `service`, `util`.

## Comandos recomendados

### Suite principal no workspace atual

```bash
mvn -Dmaven.repo.local=.m2/repository -Dmaven.compiler.source=1.7 -Dmaven.compiler.target=1.7 test
```

### E2E (quando houver container + driver)

```bash
mvn -De2e.baseUrl=http://localhost:8080/censo-superior-2025 -Dtest=CensoE2ETest test
```

## Regras de cobertura por tipo de mudanca

1. Mudanca em `util`: atualizar teste unitario correspondente.
2. Mudanca em regra de `service`: adicionar/ajustar teste de service.
3. Mudanca em SQL/DAO: atualizar teste de integracao DAO.
4. Mudanca em autenticacao/filtro: atualizar teste de `AuthFilter`.
5. Mudanca de fluxo ponta a ponta: considerar ajuste de E2E.
6. Mudanca em registros `11` ou `31`: incluir cobertura de import/export TXT pipe e validacoes de municipio/UF.

## Politica de estabilidade

1. Evitar flakiness: E2E fica desacoplado da suite padrao.
2. Sem dependencia de estado manual de banco: usar `TestDatabaseSupport`.
3. Falha no gate de cobertura bloqueia entrega.
