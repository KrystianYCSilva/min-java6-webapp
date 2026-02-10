---
description: Registro de decisoes arquiteturais e tecnicas do projeto, com racional e impacto para manutencao futura.
tier: T2
triggers:
  - adr
  - decisao
  - tradeoff
  - historico
  - arquitetura
  - hibernate
last_updated: 2026-02-10
---
# Decisoes Chave

| ID | Decisao | Status | Racional | Impacto |
| --- | --- | --- | --- | --- |
| ADR-001 | Arquitetura em camadas (`web/service/dao/model/util`) | Aceita | Facilita manutencao em legado e separa responsabilidades | Evita acoplamento entre HTTP, regra e SQL |
| ADR-002 | Hibernate nativo em DAO (Session/Transaction) com SQL nativo pontual | Aceita | Reduzir boilerplate JDBC e manter controle fino nas tabelas auxiliares | Mudancas de schema exigem alinhar mapeamentos ORM e SQL auxiliar |
| ADR-003 | H2 embarcado para ambiente local e testes | Aceita | Rapidez de setup e execucao de suite automatizada | Aproxima testes de persistencia sem dependencia externa |
| ADR-004 | Campos de layout modelados com metadados (`layout_campo` + `_layout_valor`) | Aceita | Cobrir variacao de leiautes sem inflar schema principal | Import/export permanece extensivel por configuracao |
| ADR-005 | Autenticacao por sessao HTTP + `AuthFilter` | Aceita | Simplicidade operacional no stack servlet legado | Requer cuidado com protecao de rotas `/app/*` |
| ADR-006 | Gate de cobertura JaCoCo em `dao/service/util` com minimo 80% | Aceita | Protege camadas com maior risco de regressao | Mudancas nessas camadas devem incluir testes |
| ADR-007 | E2E Selenium mantido com `@Ignore` por padrao | Aceita | Evita fragilidade em ambiente sem navegador/driver | Execucao E2E depende de setup explicito |
| ADR-008 | Inclusao dos modulos `Docente` (31) e `IES` (11) no mesmo padrao de Aluno/Curso | Aceita | Manter consistencia operacional de CRUD, importacao e exportacao TXT pipe | Menor custo de manutencao entre modulos |
| ADR-009 | Validacao de municipio por tabela de apoio pre-carregada (`municipio`) | Aceita | Evitar inconsistencias entre UF e codigo de municipio em importacao/cadastro | Exige seed dedicado e cobertura de testes para a tabela |
| ADR-010 | Separacao de `CursoAluno` em telas `list` e `form` | Aceita | Melhorar legibilidade e manutencao de JSP | Reduz complexidade visual do modulo 42 |
| ADR-011 | Builder Pattern para entidades com formulario extenso | Aceita | Reduzir setter-sprawl e padronizar construcao de objetos no web layer | Melhora legibilidade e reduz risco de omissao de campos |
| ADR-012 | Web layer sem dependencia direta de DAO para dados de tela | Aceita | Preservar isolamento de camadas (`web -> service -> dao`) | Facilita testes e evolucao de regras |
| ADR-013 | CSRF com Synchronizer Token Pattern (`CsrfFilter` + token em sessao) | Aceita | Bloquear submissoes forjadas em operacoes `POST` de `/app/*` | Exige `_csrf` em todo formulario `POST` do escopo protegido |
| ADR-014 | Escape de saida JSP centralizado em `ViewUtils.e(...)` | Aceita | Mitigar XSS em renderizacao de dados dinamicos | Padroniza renderizacao segura e reduz risco de regressao |
| ADR-015 | Hash de senha PBKDF2 com migracao transparente de SHA-256 legado | Aceita | Elevar seguranca sem quebrar base existente de usuarios | Login bem-sucedido em hash legado dispara rehash para PBKDF2 |
| ADR-016 | Command Pattern no web layer para despacho de `acao` (`AbstractActionServlet`) | Aceita | Reduzir encadeamento de `if/else` e facilitar extensao de operacoes por modulo | Mantem comportamento e melhora legibilidade/manutenibilidade dos servlets |
| ADR-017 | Hibernate 4.2 consolidado como ORM nativo por XML (`*.hbm.xml`) | Aceita | Estabilizar persistencia em Java 6 sem depender de JPA nesta versao | DAOs passam a usar `AbstractHibernateDao` e `SessionFactory` |
| ADR-018 | JPA adiado para versao 1.2.0 | Aceita | Reduzir risco de mudanca dupla (ORM + API JPA) na mesma entrega | Baseline 1.1.x permanece Hibernate nativo sem `EntityManager` |

## Quando atualizar este arquivo

1. Introducao de nova regra estrutural.
2. Troca de tecnologia de persistencia, build ou runtime.
3. Mudanca de estrategia de testes e gates.
4. Mudanca de formato de importacao/exportacao.
5. Inclusao de novo modulo de registro no menu principal.
