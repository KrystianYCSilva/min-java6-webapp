---
description: Inventario tecnico com linguagem, framework, dependencias, plugins Maven, comandos de build/teste e restricoes de runtime.
tier: T2
triggers:
  - stack
  - java
  - maven
  - dependencia
  - versao
last_updated: 2026-02-10
---
# Stack Tecnica

## Runtime e linguagem

| Item | Valor |
| --- | --- |
| Linguagem principal | Java 6 (`source/target 1.6`) |
| Web | Servlet 2.5 + JSP 2.1 |
| Container alvo | Tomcat 6/7 |
| Banco | H2 (`com.h2database:h2:1.3.176`) |
| Empacotamento | WAR (`censo-superior-2025.war`) |

## Dependencias principais (pom.xml)

### Producao

1. `javax.servlet:servlet-api:2.5` (provided)
2. `javax.servlet.jsp:jsp-api:2.1` (provided)
3. `com.h2database:h2:1.3.176` (runtime)

### Teste

1. `junit:junit:4.11`
2. `org.dbunit:dbunit:2.5.4`
3. `org.mockito:mockito-all:1.10.19`
4. `org.seleniumhq.selenium:selenium-java:2.53.1`

## Plugins Maven relevantes

1. `maven-compiler-plugin:2.5.1`
2. `maven-war-plugin:2.6`
3. `maven-surefire-plugin:2.19.1`
4. `jacoco-maven-plugin:0.8.8`

## Comandos usuais

### Build

```bash
mvn clean package
```

### Testes recomendados no workspace atual

```bash
mvn -Dmaven.repo.local=.m2/repository -Dmaven.compiler.source=1.7 -Dmaven.compiler.target=1.7 test
```

## Gate de qualidade

`JaCoCo` exige cobertura minima de linha `0.80` para os pacotes:

1. `br/gov/inep/censo/dao/*`
2. `br/gov/inep/censo/service/*`
3. `br/gov/inep/censo/util/*`
