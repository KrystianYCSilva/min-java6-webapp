# Migracao Hibernate (Fase 1 -> Fase 3)

## 1. Objetivo

Consolidar o Hibernate como camada de persistencia do projeto Java 6 com mapeamento por anotacoes JPA `javax.persistence`, preservando os contratos legados.

## 2. Evolucao por fase

### Fase 1 (concluida)

1. Inclusao de Hibernate 4.2 (`hibernate-core`) no build.
2. Introducao de `HibernateConnectionProvider`.
3. DAOs ainda orientados a JDBC/SQL manual para reduzir risco inicial.

### Fase 2 (concluida)

1. DAOs migrados para Hibernate nativo (`Session` + `Transaction`).
2. Introducao de `AbstractHibernateDao` para padrao unico de transacao/erro.
3. Mapeamento ORM por XML (`hibernate.cfg.xml` + `*.hbm.xml`).
4. Relacionamentos do banco reforcados com `ON DELETE CASCADE` e indices.

### Fase 3 (estado atual)

1. Entidades migradas para anotacoes JPA `javax.persistence` (`@Entity`, `@Table`, `@Column`, `@ManyToOne`, `@JoinColumn`, `@Transient`).
2. `hibernate.cfg.xml` passou a usar `mapping class="..."`.
3. Arquivos legados `*.hbm.xml` foram removidos.
4. DAOs continuam em Hibernate nativo (`Session`/`Transaction`) para minimizar risco de regressao.

## 3. Baseline tecnico atual

### Dependencias

1. `org.hibernate:hibernate-core:4.2.21.Final`
2. API JPA (`javax.persistence`) provida pelo stack do Hibernate 4.2.

### Persistencia

1. CRUD principal em HQL/operacoes de entidade.
2. SQL nativo mantido apenas para tabelas auxiliares de alto volume (`*_opcao`, `*_layout_valor`).
3. Relacionamento `CursoAluno -> Aluno/Curso` mapeado por `@ManyToOne(fetch = LAZY)`.

### Configuracao

1. Dialect e driver resolvidos por URL JDBC (`H2`, `PostgreSQL`, `MySQL`, `DB2`).
2. `SessionFactory` invalidada quando `ConnectionFactory.configure(...)` altera credenciais/URL.
3. `SessionFactory` encerrada no `DatabaseBootstrapListener.contextDestroyed`.

## 4. Banco e integridade referencial

Alteracoes relevantes em `schema.sql` mantidas:

1. `ON DELETE CASCADE` em FKs de tabelas filhas.
2. Indices para busca/paginacao e validacao (inclusive `municipio(codigo_uf, nome)`).

## 5. Nao objetivos desta versao

1. Nao migrar DAOs para `EntityManager`.
2. Nao alterar contratos das camadas `service` e `web`.

## 6. Proximos passos sugeridos

1. Avaliar migracao gradual para `EntityManager` somente se houver ganho funcional/operacional claro.
2. Revisar estrategia de fetch e write patterns para cargas maiores em producao.

## 7. Validacao executada

Comando:

```bash
mvn '-Dmaven.repo.local=.m2/repository' '-Dmaven.compiler.source=1.7' '-Dmaven.compiler.target=1.7' test
```

Resultado:

1. `BUILD SUCCESS`
2. `Tests run: 38, Failures: 0, Errors: 0, Skipped: 1`
3. JaCoCo: `All coverage checks have been met`
