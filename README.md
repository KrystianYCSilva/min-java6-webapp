# Censo Superior 2025 - WebApp Java 6

Prototipo funcional com:
- Java 6 (JDK 1.6)
- Servlet 2.5 / JSP
- Hibernate ORM 4.2 (compativel com Java 6)
- Hibernate nativo nos DAOs (`Session` + `Transaction` + HQL/SQL nativo)
- Mapeamento ORM por XML (`*.hbm.xml`), sem anotacoes JPA
- Tomcat 6/7
- Maven 3.2.5 (compatibilidade de projeto)
- H2 embarcado

## Arquitetura (Padrao Ouro para legado)

### Camadas
- `web` (Servlets): recebe request/response e delega para servicos.
- `service`: validacoes e regras de negocio.
- `dao`: persistencia Hibernate nativa sobre `SessionFactory`.
- `model`: entidades de dominio.
- `util`: utilitarios de seguranca/validacao/mapeamento de request.

### Principios SOLID aplicados
- `S` (Single Responsibility): cada camada com responsabilidade unica.
- `O` (Open/Closed): campos multivalorados modelados por catalogo (`dominio_opcao`) sem adicionar novas colunas.
- `L` (Liskov): modelos e servicos sem herancas fragilizadas.
- `I` (Interface Segregation): servicos e utilitarios pequenos e focados.
- `D` (Dependency Inversion): servlets dependem de servicos, nao de SQL direto.

### Padroes de projeto utilizados
- DAO Pattern (`AlunoDAO`, `CursoDAO`, `CursoAlunoDAO`, `DocenteDAO`, `IesDAO`, etc.).
- Service Layer (`AlunoService`, `CursoService`, `CursoAlunoService`, `DocenteService`, `IesService`, `AuthService`).
- Template transacional Hibernate (`AbstractHibernateDao`) para padronizar `Session`/`Transaction`.
- ORM mapping por XML (`hibernate.cfg.xml` + `*.hbm.xml`) para compatibilidade Java 6 sem JPA.
- Bridge de persistencia via `HibernateConnectionProvider` (boot e ciclo de vida de `SessionFactory`).

## Modelagem de banco

### Tabelas principais
- `usuario`
- `aluno` (Registro 41)
- `curso` (Registro 21)
- `curso_aluno` (Registro 42)
- `docente` (Registro 31)
- `ies` (Registro 11 - laboratorio)
- `municipio` (tabela de apoio para validacao de UF/codigo)

### Tabelas auxiliares 1..N (normalizacao)
- `dominio_opcao`
- `aluno_opcao`
- `curso_opcao`
- `curso_aluno_opcao`

Categorias carregadas em `seed.sql`:
- `ALUNO_TIPO_DEFICIENCIA`
- `CURSO_RECURSO_TECNOLOGIA_ASSISTIVA`
- `CURSO_ALUNO_TIPO_FINANCIAMENTO`
- `CURSO_ALUNO_APOIO_SOCIAL`
- `CURSO_ALUNO_ATIVIDADE_EXTRACURRICULAR`
- `CURSO_ALUNO_RESERVA_VAGA`

### Cobertura de todos os campos de leiaute
- `layout_campo` guarda os metadados dos CSVs (Registros 11, 21, 31, 41 e 42).
- `aluno_layout_valor`, `curso_layout_valor`, `curso_aluno_layout_valor`, `docente_layout_valor`, `ies_layout_valor` guardam valores complementares.
- `seed_layout.sql` contem os layouts base de aluno/curso/curso-aluno.
- `seed_layout_ies_docente.sql` contem os layouts de docente e IES.
- `seed_municipio.sql` pre-carrega a tabela de apoio de municipios.

## Fluxo funcional
- Login via `LoginServlet` com hash PBKDF2 e compatibilidade com SHA-256 legado.
- `AuthFilter` protege `/app/*`.
- Menu com acesso aos modulos:
  - Aluno
  - Curso
  - CursoAluno (Registro 42)
  - Docente (Registro 31)
  - IES (Registro 11)
- Listagens paginadas dos modulos com acoes:
  - `Alterar`
  - `Mostrar`
  - `Excluir`
  - `Exportar TXT` por item
- Operacoes em lote de Aluno, Curso, Docente e IES:
  - importacao de TXT separado por `|`
  - exportacao total em TXT separado por `|`
- `CursoAluno` organizado em telas separadas de lista e formulario (`curso-aluno-list.jsp` e `curso-aluno-form.jsp`).

## Build e execucao

### Build
```bash
mvn clean package
```

Observacao: em JDKs modernos, `source/target 1.6` pode exigir JDK 6/7 para empacotamento completo.

### Credencial inicial
- Login: `admin`
- Senha: `admin123`

## Testes

Execucao recomendada no workspace:
```bash
mvn -Dmaven.repo.local=.m2/repository -Dmaven.compiler.source=1.7 -Dmaven.compiler.target=1.7 test
```

Cobertura:
- JaCoCo com gate minimo de `80%` (linha) para `dao`, `service` e `util`.
- Resultado atual da suite: acima do gate, com DAOs rodando sobre Hibernate nativo (`Session`/`Transaction`).

Consulte `docs/TEST-PLAN.md` para detalhes da piramide de testes e roteiro E2E.
Consulte `docs/ARCHITECTURE.md` para visao arquitetural detalhada do sistema.
Consulte `docs/HIBERNATE-MIGRATION.md` para historico da migracao de persistencia.
Consulte `docs/HIBERNATE-NATIVE-WHAT-CHANGED.md` para o baseline atual (sem JPA; JPA previsto para `1.2.0`).
