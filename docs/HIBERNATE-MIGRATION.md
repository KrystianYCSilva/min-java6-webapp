# Migracao Hibernate (Fase 1)

## 1. Objetivo

Introduzir o Hibernate como primeiro framework de persistencia do projeto, mantendo compatibilidade com Java 6 e preservando o comportamento funcional atual.

## 2. Escopo desta fase

1. Adicao de `hibernate-core` e `hibernate-entitymanager` (versao `4.2.21.Final`, compativel com Java 6).
2. Introducao de `HibernateConnectionProvider` para gerenciar conexoes JDBC por `SessionFactory`.
3. Migracao dos DAOs para obter conexao via Hibernate (sem alterar contratos de `service`/`web`).
4. Manutencao dos SQLs legados nesta etapa para reduzir risco de regressao.
5. Atualizacao da arquitetura e contexto do projeto para refletir a nova base de persistencia.

## 3. O que mudou no codigo

## 3.1 Build e dependencias

- `pom.xml`
  - `org.hibernate:hibernate-core:4.2.21.Final`
  - `org.hibernate:hibernate-entitymanager:4.2.21.Final`

## 3.2 Infra de persistencia

- `src/main/java/br/gov/inep/censo/config/HibernateConnectionProvider.java`
  - cria e reaproveita `SessionFactory`;
  - resolve `Dialect` por URL:
    - H2 -> `org.hibernate.dialect.H2Dialect`
    - PostgreSQL -> `org.hibernate.dialect.PostgreSQLDialect`
    - MySQL -> `org.hibernate.dialect.MySQL5InnoDBDialect`
    - IBM DB2 -> `org.hibernate.dialect.DB2Dialect`
  - entrega `Connection` mantendo compatibilidade com DAOs legados.

- `src/main/java/br/gov/inep/censo/config/ConnectionFactory.java`
  - passa a expor URL/usuario/senha atuais para o provider Hibernate;
  - invalida a `SessionFactory` apenas quando a configuracao realmente muda.

- `src/main/java/br/gov/inep/censo/config/DatabaseBootstrapListener.java`
  - encerra recursos Hibernate em `contextDestroyed`.

## 3.3 DAOs

Todos os DAOs de `src/main/java/br/gov/inep/censo/dao` agora abrem conexao via `HibernateConnectionProvider`.

Observacao: os SQLs, joins e regras de mapeamento continuam no mesmo formato legado por seguranca da migracao.

## 4. Impacto arquitetural

Antes:
- DAO -> `ConnectionFactory` (DriverManager direto) -> Banco

Agora:
- DAO -> `HibernateConnectionProvider` -> `SessionFactory` Hibernate -> Banco

Beneficios imediatos:
1. Base pronta para evoluir para ORM gradual.
2. Estrategia de `Dialect` documentada para H2/PostgreSQL/MySQL/DB2.
3. Menor acoplamento dos DAOs ao mecanismo de abertura de conexao.

## 5. Banco de dados e relacionamentos

Nao foi necessario alterar `schema.sql` nesta fase.

Racional:
1. O schema atual ja possui relacionamentos essenciais (ex.: `curso_aluno` -> `aluno`/`curso`).
2. O objetivo principal da Fase 1 e trocar o mecanismo de acesso, nao reescrever o modelo relacional.
3. Mudancas de FK adicionais podem ser feitas em uma fase dedicada de endurecimento de integridade.

## 6. Validacao executada

Comando:

```bash
mvn -Dmaven.repo.local=.m2/repository -Dmaven.compiler.source=1.7 -Dmaven.compiler.target=1.7 test
```

Resultado:
1. `BUILD SUCCESS`
2. `Tests run: 38, Failures: 0, Errors: 0, Skipped: 1`
3. JaCoCo: `All coverage checks have been met`

## 7. Limites desta fase

1. Pool embutido do Hibernate foi mantido por simplicidade (adequado para dev/teste).
2. DAOs continuam orientados a SQL manual; ainda nao ha substituicao completa por entidades/HQL/Criteria.

## 8. Proximas fases sugeridas

1. Introduzir mapeamento de entidades JPA gradualmente por modulo (`Aluno`, `Curso`, `CursoAluno`).
2. Substituir operacoes CRUD de baixo risco por `Session`/HQL.
3. Evoluir estrategia de pool para ambiente produtivo (por ex. datasource do container).
4. Definir conjunto de testes de regressao especifico para migracao ORM (comparando SQL legado vs. ORM).
