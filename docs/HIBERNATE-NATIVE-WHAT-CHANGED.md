# Hibernate Nativo: O Que Mudou

Este documento resume as mudancas aplicadas para elevar a persistencia do projeto ao padrao Hibernate nativo (sem JPA).

## 1. Antes e depois

Antes:

1. DAOs em JDBC manual (`Connection`, `PreparedStatement`, `ResultSet`).
2. Hibernate usado apenas como ponte para abrir conexao.
3. Dependencia `hibernate-entitymanager` presente.

Depois:

1. DAOs executam em `Session`/`Transaction` com template comum (`AbstractHibernateDao`).
2. Entidades mapeadas por XML (`*.hbm.xml`) com `SessionFactory` configurada por `hibernate.cfg.xml`.
3. `hibernate-entitymanager` removido.
4. SQL nativo restrito a tabelas auxiliares de relacao e layout.

## 2. Arquivos-chave adicionados

1. `src/main/resources/hibernate.cfg.xml`
2. `src/main/resources/br/gov/inep/censo/model/Aluno.hbm.xml`
3. `src/main/resources/br/gov/inep/censo/model/Curso.hbm.xml`
4. `src/main/resources/br/gov/inep/censo/model/CursoAluno.hbm.xml`
5. `src/main/resources/br/gov/inep/censo/model/Docente.hbm.xml`
6. `src/main/resources/br/gov/inep/censo/model/Ies.hbm.xml`
7. `src/main/resources/br/gov/inep/censo/model/Municipio.hbm.xml`
8. `src/main/resources/br/gov/inep/censo/model/OpcaoDominio.hbm.xml`
9. `src/main/resources/br/gov/inep/censo/model/LayoutCampo.hbm.xml`
10. `src/main/resources/br/gov/inep/censo/model/Usuario.hbm.xml`
11. `src/main/java/br/gov/inep/censo/dao/AbstractHibernateDao.java`

## 3. Arquivos alterados (alto impacto)

1. `pom.xml` (remove `hibernate-entitymanager`)
2. `src/main/java/br/gov/inep/censo/config/HibernateConnectionProvider.java`
3. `src/main/java/br/gov/inep/censo/config/ConnectionFactory.java`
4. DAOs em `src/main/java/br/gov/inep/censo/dao/*` (migrados para Hibernate nativo)
5. `src/main/java/br/gov/inep/censo/model/CursoAluno.java` (relacionamento ORM com `Aluno`/`Curso`)
6. `src/main/resources/db/schema.sql` (cascades + indices)

## 4. Impacto em comportamento

1. Contratos de `service` e `web` foram preservados.
2. Testes existentes de DAO/service continuam verdes.
3. Exclusoes agora contam com reforco de integridade via FK com `ON DELETE CASCADE`.
4. Estrutura atual e compativel com H2 e preparada para PostgreSQL/MySQL/DB2.

## 5. O que ficou para a versao 1.2.0

1. Introducao de JPA (`EntityManager`, anotacoes JPA, estrategia de migracao anotada).
2. Revisao de estrategia de fetch/lazy para padroes JPA.

## 6. Validacao

Comando executado:

```bash
mvn '-Dmaven.repo.local=.m2/repository' '-Dmaven.compiler.source=1.7' '-Dmaven.compiler.target=1.7' test
```

Status:

1. `BUILD SUCCESS`
2. `38` testes executados, `0` falhas, `0` erros.
