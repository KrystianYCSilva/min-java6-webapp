# Migracao Hibernate (Fase 1 -> Fase 2)

## 1. Objetivo

Consolidar o Hibernate como camada de persistencia do projeto Java 6 sem usar JPA nesta versao.

## 2. Evolucao por fase

### Fase 1 (concluida)

1. Inclusao de Hibernate 4.2 (`hibernate-core`) no build.
2. Introducao de `HibernateConnectionProvider`.
3. DAOs ainda orientados a JDBC/SQL manual para reduzir risco inicial.

### Fase 2 (estado atual)

1. DAOs migrados para Hibernate nativo (`Session` + `Transaction`).
2. Introducao de `AbstractHibernateDao` para padrao unico de transacao/erro.
3. Mapeamento ORM por XML:
   - `src/main/resources/hibernate.cfg.xml`
   - `src/main/resources/br/gov/inep/censo/model/*.hbm.xml`
4. Remocao de `hibernate-entitymanager` (sem JPA nesta versao).
5. Relacionamentos do banco reforcados com `ON DELETE CASCADE` e indices.

## 3. Baseline tecnico atual

### Dependencias

1. `org.hibernate:hibernate-core:4.2.21.Final`
2. Sem `hibernate-entitymanager`.

### Persistencia

1. CRUD principal em HQL/operacoes de entidade.
2. SQL nativo mantido apenas para tabelas auxiliares de alto volume (`*_opcao`, `*_layout_valor`).
3. Mapeamento de relacionamento `CursoAluno -> Aluno/Curso` por `many-to-one`.

### Configuracao

1. Dialect e driver resolvidos por URL JDBC (`H2`, `PostgreSQL`, `MySQL`, `DB2`).
2. `SessionFactory` invalidada quando `ConnectionFactory.configure(...)` altera credenciais/URL.
3. `SessionFactory` encerrada no `DatabaseBootstrapListener.contextDestroyed`.

## 4. Banco e integridade referencial

Alteracoes relevantes em `schema.sql`:

1. `ON DELETE CASCADE` em FKs de tabelas filhas:
   - `curso_aluno`
   - `aluno_opcao`, `curso_opcao`, `curso_aluno_opcao`
   - `*_layout_valor`
2. Novos indices para busca/paginacao e validacao:
   - nome de entidades principais
   - chaves de relacionamento em `curso_aluno`
   - `municipio(codigo_uf, nome)`

## 5. Nao objetivos desta versao

1. Nao usar JPA/`EntityManager`.
2. Nao usar anotacoes JPA nas entidades.

## 6. Roadmap

1. `v1.2.0`: avaliar camada JPA (anotacoes e/ou `EntityManager`) sobre o baseline Hibernate atual.

## 7. Validacao executada

Comando:

```bash
mvn '-Dmaven.repo.local=.m2/repository' '-Dmaven.compiler.source=1.7' '-Dmaven.compiler.target=1.7' test
```

Resultado:

1. `BUILD SUCCESS`
2. `Tests run: 38, Failures: 0, Errors: 0, Skipped: 1`
3. JaCoCo: `All coverage checks have been met`
