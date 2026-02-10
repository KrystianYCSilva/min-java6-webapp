---
description: Conceitos de dominio do Censo Superior (registros 21, 41 e 42), entidades principais, relacoes e termos usados no codigo.
tier: T3
triggers:
  - dominio
  - aluno
  - curso
  - docente
  - ies
  - municipio
  - registro 41
  - registro 42
last_updated: 2026-02-10
---
# Conceitos de Dominio

## Registros principais

1. Registro 11: dados de `IES` (laboratorio).
2. Registro 21: dados de `Curso`.
3. Registro 31: dados de `Docente`.
4. Registro 41: dados de `Aluno`.
5. Registro 42: vinculacao `CursoAluno` (aluno em curso).

## Entidades centrais

| Entidade | Papel |
| --- | --- |
| `usuario` | Autenticacao de acesso ao sistema |
| `aluno` | Cadastro do discente (dados base + complementares) |
| `curso` | Cadastro do curso (dados base + complementares) |
| `curso_aluno` | Relacao entre aluno e curso |
| `docente` | Cadastro de docente com validacoes de CPF e municipio/UF |
| `ies` | Cadastro de laboratorio no contexto da IES |
| `municipio` | Tabela de apoio para validar consistencia de codigo e UF |
| `dominio_opcao` | Catalogo de opcoes multivalor por categoria |
| `layout_campo` | Metadados oficiais de campos de leiaute |

## Estrategia de campos complementares

1. Campos variaveis do leiaute ficam em tabelas de valor por modulo.
2. Metadados de campos vivem em `layout_campo`.
3. Tabelas `_layout_valor` por modulo: `aluno`, `curso`, `curso_aluno`, `docente`, `ies`.
4. Isso permite evolucao de leiaute sem proliferar colunas fixas.

## Operacoes de negocio recorrentes

1. Importacao TXT pipe (`|`) por modulo.
2. Exportacao individual e exportacao total em TXT pipe.
3. Listagem paginada para consulta operacional.
4. Validacoes de regra para periodo, semestre e dominios enumerados.
5. Validacao de municipio por UF nos modulos `Docente` e `IES`.

## Termos importantes para busca semantica

1. `layout_campo`
2. `extra_*`
3. `dominio_opcao`
4. `registro 11`
5. `registro 21`
6. `registro 31`
7. `registro 41`
8. `registro 42`
9. `municipio`
