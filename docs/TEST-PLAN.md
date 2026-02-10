# Plano de Testes - Censo Superior 2025

## 1. Setup de ambiente

Dependencias de teste no `pom.xml`:
- `junit:junit:4.11`
- `org.dbunit:dbunit:2.5.4`
- `org.mockito:mockito-all:1.10.19`
- `org.seleniumhq.selenium:selenium-java:2.53.1`

Dependencias de persistencia em producao (tambem exercitadas nos testes de integracao):
- `org.hibernate:hibernate-core:4.2.21.Final`
- `org.hibernate:hibernate-entitymanager:4.2.21.Final`

Controle de cobertura:
- `org.jacoco:jacoco-maven-plugin:0.8.8`
- Gate de cobertura minima (linha): `80%`
- Escopo do gate: pacotes `dao`, `service` e `util`

Infra de apoio:
- `src/test/java/br/gov/inep/censo/support/TestDatabaseSupport.java`
  - reinicializa H2 em memoria;
  - executa `schema.sql`, `seed.sql`, `seed_layout.sql`, `seed_layout_ies_docente.sql` e `seed_municipio.sql`.
- `src/main/java/br/gov/inep/censo/config/HibernateConnectionProvider.java`
  - abre conexoes JDBC via `SessionFactory` Hibernate;
  - resolve `Dialect` por URL (H2/PostgreSQL/MySQL/DB2);
  - preserva o contrato atual dos DAOs legados.

## 2. Camada 1 - Unitarios (base da piramide)

Classes:
- `src/test/java/br/gov/inep/censo/util/ValidationUtilsTest.java`
- `src/test/java/br/gov/inep/censo/util/PasswordUtilTest.java`
- `src/test/java/br/gov/inep/censo/util/RequestFieldMapperTest.java`
- `src/test/java/br/gov/inep/censo/model/enums/EnumMappingTest.java`
- `src/test/java/br/gov/inep/censo/service/CursoAlunoServiceTest.java`

Casos cobertos:
- validacao de CPF, periodo e semestre;
- hash SHA-256;
- mapeamento de campos complementares `extra_*`;
- mapeamento de enums (`cor/raca`, `nacionalidade`, `nivel`, `formato`);
- validacao de negocio do Registro 42 (periodo/semestre obrigatorios e validos).

## 3. Camada 2 - Integracao (meio da piramide)

### 3.1 DAO + persistencia Hibernate/JDBC (hibrida)

Classes:
- `src/test/java/br/gov/inep/censo/dao/AlunoDAOTest.java`
- `src/test/java/br/gov/inep/censo/dao/CursoDAOTest.java`
- `src/test/java/br/gov/inep/censo/dao/DocenteDAOTest.java`
- `src/test/java/br/gov/inep/censo/dao/IesDAOTest.java`
- `src/test/java/br/gov/inep/censo/dao/MunicipioDAOTest.java`

Fluxos validados:
1. Inserir Aluno, Curso e vinculacao em `curso_aluno`.
2. Carregar opcoes 1..N e campos complementares por campo e por numero.
3. Atualizar, paginar, contar e excluir.
4. Persistir e consultar Docente/IES com campos complementares.
5. Validar tabela de apoio `municipio` por codigo e UF.
6. Validar contagem de linhas com DBUnit.
7. Validar DAOs com conexao provida pelo Hibernate sem regressao funcional.

### 3.2 Servicos com banco

Classes:
- `src/test/java/br/gov/inep/censo/service/AlunoServiceTest.java`
- `src/test/java/br/gov/inep/censo/service/CursoServiceTest.java`
- `src/test/java/br/gov/inep/censo/service/DocenteServiceTest.java`
- `src/test/java/br/gov/inep/censo/service/IesServiceTest.java`
- `src/test/java/br/gov/inep/censo/service/AuthServiceTest.java`
- `src/test/java/br/gov/inep/censo/service/CatalogoServiceTest.java`

Fluxos validados:
1. Importacao TXT pipe (`Registro 41` e `Registro 21`).
2. Exportacao TXT pipe (individual e total).
3. Importacao/exportacao TXT pipe para `Registro 31` (Docente) e `Registro 11` (IES).
4. Validacoes de cadastro, CPF e consistencia UF/municipio.
5. Autenticacao de usuario padrao em banco.
6. Carregamento de catalogos de opcoes e metadados de leiaute.

### 3.3 Filtro de autenticacao

Classe:
- `src/test/java/br/gov/inep/censo/web/filter/AuthFilterTest.java`

Casos:
- bloqueia acesso sem sessao;
- permite acesso com `usuarioLogado` na sessao.

## 4. Camada 3 - E2E (topo da piramide)

Classe:
- `src/test/java/br/gov/inep/censo/e2e/CensoE2ETest.java`

Fluxo E2E modelado:
1. Abre `/login`.
2. Faz login (`admin/admin123`).
3. Navega para modulo Aluno.
4. Preenche formulario.
5. Salva registro.
6. Abre `home.jsp`.
7. Verifica mensagem de sucesso.

Observacao:
- teste E2E esta com `@Ignore` por padrao para nao quebrar execucao local sem navegador/driver.

## 5. Roteiro de execucao

### 5.1 Unitarios + Integracao + cobertura
```bash
mvn test
```

No ambiente deste workspace (JDK moderno + plugin WAR legado), usar:
```bash
mvn -Dmaven.repo.local=.m2/repository -Dmaven.compiler.source=1.7 -Dmaven.compiler.target=1.7 test
```

### 5.2 E2E
Pre-requisitos:
1. Tomcat com aplicacao publicada.
2. WebDriver configurado.
3. URL base configurada.

Execucao:
```bash
mvn -De2e.baseUrl=http://localhost:8080/censo-superior-2025 -Dtest=CensoE2ETest test
```

## 6. Estrutura de testes

```text
src/test/java/
└─ br/gov/inep/censo/
   ├─ support/
   │  └─ TestDatabaseSupport.java
   ├─ util/
   │  ├─ ValidationUtilsTest.java
   │  ├─ PasswordUtilTest.java
   │  └─ RequestFieldMapperTest.java
   ├─ model/enums/
   │  └─ EnumMappingTest.java
   ├─ dao/
   │  ├─ AlunoDAOTest.java
   │  ├─ CursoDAOTest.java
   │  ├─ DocenteDAOTest.java
   │  ├─ IesDAOTest.java
   │  └─ MunicipioDAOTest.java
   ├─ service/
   │  ├─ AlunoServiceTest.java
   │  ├─ CursoServiceTest.java
   │  ├─ DocenteServiceTest.java
   │  ├─ IesServiceTest.java
   │  ├─ CursoAlunoServiceTest.java
   │  ├─ AuthServiceTest.java
   │  └─ CatalogoServiceTest.java
   ├─ web/filter/
   │  └─ AuthFilterTest.java
   └─ e2e/
      └─ CensoE2ETest.java
```
