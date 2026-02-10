---
description: Workflow de build, empacotamento e deploy local no Tomcat para o webapp, incluindo validacoes de smoke test.
tier: T2
triggers:
  - deploy
  - tomcat
  - package
  - war
  - smoke test
last_updated: 2026-02-10
---
# Workflow de Deploy

## Pre-requisitos

1. JDK compativel com o build do projeto.
2. Maven instalado.
3. Tomcat 6 ou 7 disponivel para deploy local.

## Passo a passo local

### 1. Executar testes

```bash
mvn '-Dmaven.repo.local=.m2/repository' '-Dmaven.compiler.source=1.7' '-Dmaven.compiler.target=1.7' test
```

### 2. Gerar pacote WAR

```bash
mvn clean package
```

### 3. Publicar no container

1. Copiar `target/censo-superior-2025.war` para `TOMCAT_HOME/webapps/`.
2. Reiniciar Tomcat (ou aguardar auto-deploy).

### 4. Smoke test

1. Abrir `http://localhost:8080/censo-superior-2025/login.zul`.
2. Login com `admin / admin123`.
3. Validar redirecionamento para `app/menu.zul?view=dashboard`.
4. Navegar no menu lateral para Aluno, Curso, CursoAluno, Docente e IES.
5. Abrir ao menos um cadastro em sub-window (`sub=...`) e salvar.
6. Testar importacao/exportacao TXT pipe em pelo menos um modulo.

## Sinais de problema comuns

1. Erro de compilacao por versao de JDK.
2. Falha de autenticacao por seed ausente.
3. Pagina de erro por incompatibilidade de schema/seed.
4. Configuracao JDBC invalida para `EntityManagerFactory`.
5. Uso de pool embutido do Hibernate em ambiente de producao sem ajuste de datasource.

## Checklist antes de entregar

1. Suite de testes passou.
2. WAR foi gerado sem erro.
3. Login e navegacao basica no shell funcionam.
4. Nao ha alteracao manual em artefatos gerados.
