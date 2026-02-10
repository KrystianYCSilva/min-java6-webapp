# Migracao Hibernate (Fase 1 -> Fase 4)

## 1. Objetivo

Consolidar o Hibernate como provedor JPA em Java 6, com entidades anotadas e DAOs padronizados em `EntityManager`.

## 2. Evolucao por fase

### Fase 1 (concluida)

1. Inclusao de Hibernate 4.2 (`hibernate-core`) no build.
2. Introducao de `HibernateConnectionProvider`.
3. DAOs ainda orientados a JDBC/SQL manual para reduzir risco inicial.

### Fase 2 (concluida)

1. DAOs migrados para Hibernate nativo (`Session` + `Transaction`).
2. Introducao de template transacional comum na camada DAO.
3. Mapeamento ORM por XML (`hibernate.cfg.xml` + `*.hbm.xml`).
4. Relacionamentos do banco reforcados com `ON DELETE CASCADE` e indices.

### Fase 3 (concluida)

1. Entidades migradas para anotacoes JPA `javax.persistence` (`@Entity`, `@Table`, `@Column`, `@ManyToOne`, `@JoinColumn`, `@Transient`).
2. Arquivos legados `*.hbm.xml` removidos.

### Fase 4 (estado atual)

1. DAOs migrados para JPA (`EntityManager` + `EntityTransaction`) com template `AbstractJpaDao`.
2. Bootstrap JPA padrao com `META-INF/persistence.xml` (`RESOURCE_LOCAL`).
3. `HibernateConnectionProvider` passou a gerenciar `EntityManagerFactory`.
4. Dependencia `hibernate-entitymanager` adicionada para suporte JPA completo no Hibernate 4.2.
5. `hibernate.cfg.xml` removido para evitar configuracao duplicada.

## 3. Baseline tecnico atual

### Dependencias

1. `org.hibernate:hibernate-core:4.2.21.Final`
2. `org.hibernate:hibernate-entitymanager:4.2.21.Final`

### Persistencia

1. CRUD principal em JPQL e operacoes de entidade.
2. SQL nativo mantido apenas para tabelas auxiliares de alto volume (`*_opcao`, `*_layout_valor`).
3. Relacionamento `CursoAluno -> Aluno/Curso` mapeado por `@ManyToOne(fetch = LAZY)`.

### Configuracao

1. Dialect e driver resolvidos por URL JDBC (`H2`, `PostgreSQL`, `MySQL`, `DB2`).
2. `EntityManagerFactory` invalidada quando `ConnectionFactory.configure(...)` altera credenciais/URL.
3. `EntityManagerFactory` encerrada no `DatabaseBootstrapListener.contextDestroyed`.

## 4. Banco e integridade referencial

Alteracoes relevantes em `schema.sql` mantidas:

1. `ON DELETE CASCADE` em FKs de tabelas filhas.
2. Indices para busca/paginacao e validacao (inclusive `municipio(codigo_uf, nome)`).

## 5. Nao objetivos desta versao

1. Nao migrar para stack mais moderna que Java 6 / Servlet 2.5.
2. Nao alterar contratos das camadas `service` e `web`.

## 6. Validacao executada

Comando:

```bash
mvn '-Dmaven.repo.local=.m2/repository' '-Dmaven.compiler.source=1.7' '-Dmaven.compiler.target=1.7' test
```

Resultado:

1. `BUILD SUCCESS`
2. `Tests run: 38, Failures: 0, Errors: 0, Skipped: 1`
3. JaCoCo: `All coverage checks have been met`
