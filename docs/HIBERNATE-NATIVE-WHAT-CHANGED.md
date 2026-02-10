# Hibernate Nativo: O Que Mudou (Historico v1.1.0)

Este documento registra a fase anterior da migracao, quando a persistencia foi padronizada em Hibernate nativo com mapeamento XML (`*.hbm.xml`).

## 1. Contexto da fase historica

Antes da fase atual com anotacoes JPA:

1. DAOs em JDBC manual (`Connection`, `PreparedStatement`, `ResultSet`).
2. Hibernate usado apenas como ponte para abrir conexao.

Na fase v1.1.0:

1. DAOs migraram para `Session`/`Transaction` com template comum (`AbstractHibernateDao`).
2. Entidades passaram a ser mapeadas por XML (`hibernate.cfg.xml` + `*.hbm.xml`).
3. SQL nativo ficou restrito a tabelas auxiliares de relacao e layout.

## 2. Situacao atual (apos esta fase)

O baseline vigente deixou de usar `*.hbm.xml` e passou para anotacoes `javax.persistence` nas entidades.

Referencias atualizadas:

1. `docs/HIBERNATE-MIGRATION.md`
2. `README.md`

## 3. Validacao historica da fase v1.1.0

Comando executado na epoca:

```bash
mvn '-Dmaven.repo.local=.m2/repository' '-Dmaven.compiler.source=1.7' '-Dmaven.compiler.target=1.7' test
```

Status da fase:

1. `BUILD SUCCESS`
2. `38` testes executados, `0` falhas, `0` erros.
