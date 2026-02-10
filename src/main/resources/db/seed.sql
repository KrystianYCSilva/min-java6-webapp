-- Usuario padrao:
-- login: admin
-- senha: admin123
-- hash PBKDF2: PBKDF2$12000$00112233445566778899aabbccddeeff$452a7c2746e82019bde89f0578b311fb8ab9a68563b62db465f8ef7a73afd19c

MERGE INTO usuario (login, senha_hash, nome, ativo)
KEY(login)
VALUES ('admin', 'PBKDF2$12000$00112233445566778899aabbccddeeff$452a7c2746e82019bde89f0578b311fb8ab9a68563b62db465f8ef7a73afd19c', 'Administrador do Censo', TRUE);

-- ============================
-- CATALOGO DE OPCOES (1..N)
-- ============================

-- ALUNO - Tipo de deficiencia (Registro 41, campos 13 a 22)
MERGE INTO dominio_opcao (categoria, codigo, nome, ativo) KEY(categoria, codigo) VALUES ('ALUNO_TIPO_DEFICIENCIA', 'CEGUEIRA', 'Tipo de deficiencia - Cegueira', TRUE);
MERGE INTO dominio_opcao (categoria, codigo, nome, ativo) KEY(categoria, codigo) VALUES ('ALUNO_TIPO_DEFICIENCIA', 'BAIXA_VISAO', 'Tipo de deficiencia - Baixa visao', TRUE);
MERGE INTO dominio_opcao (categoria, codigo, nome, ativo) KEY(categoria, codigo) VALUES ('ALUNO_TIPO_DEFICIENCIA', 'VISAO_MONOCULAR', 'Tipo de deficiencia - Visao monocular', TRUE);
MERGE INTO dominio_opcao (categoria, codigo, nome, ativo) KEY(categoria, codigo) VALUES ('ALUNO_TIPO_DEFICIENCIA', 'SURDEZ', 'Tipo de deficiencia - Surdez', TRUE);
MERGE INTO dominio_opcao (categoria, codigo, nome, ativo) KEY(categoria, codigo) VALUES ('ALUNO_TIPO_DEFICIENCIA', 'DEF_AUDITIVA', 'Tipo de deficiencia - Auditiva', TRUE);
MERGE INTO dominio_opcao (categoria, codigo, nome, ativo) KEY(categoria, codigo) VALUES ('ALUNO_TIPO_DEFICIENCIA', 'DEF_FISICA', 'Tipo de deficiencia - Fisica', TRUE);
MERGE INTO dominio_opcao (categoria, codigo, nome, ativo) KEY(categoria, codigo) VALUES ('ALUNO_TIPO_DEFICIENCIA', 'SURDOCEGUEIRA', 'Tipo de deficiencia - Surdocegueira', TRUE);
MERGE INTO dominio_opcao (categoria, codigo, nome, ativo) KEY(categoria, codigo) VALUES ('ALUNO_TIPO_DEFICIENCIA', 'DEF_INTELECTUAL', 'Tipo de deficiencia - Intelectual', TRUE);
MERGE INTO dominio_opcao (categoria, codigo, nome, ativo) KEY(categoria, codigo) VALUES ('ALUNO_TIPO_DEFICIENCIA', 'TEA', 'Tipo de deficiencia - Transtorno do espectro autista (TEA)', TRUE);
MERGE INTO dominio_opcao (categoria, codigo, nome, ativo) KEY(categoria, codigo) VALUES ('ALUNO_TIPO_DEFICIENCIA', 'ALTAS_HABILIDADES', 'Tipo de deficiencia - Altas habilidades ou superdotacao', TRUE);

-- CURSO - Recursos de tecnologia assistiva (Registro 21, campos 54 a 65)
MERGE INTO dominio_opcao (categoria, codigo, nome, ativo) KEY(categoria, codigo) VALUES ('CURSO_RECURSO_TECNOLOGIA_ASSISTIVA', 'BRAILLE', 'Material em Braille', TRUE);
MERGE INTO dominio_opcao (categoria, codigo, nome, ativo) KEY(categoria, codigo) VALUES ('CURSO_RECURSO_TECNOLOGIA_ASSISTIVA', 'AUDIO', 'Material em audio', TRUE);
MERGE INTO dominio_opcao (categoria, codigo, nome, ativo) KEY(categoria, codigo) VALUES ('CURSO_RECURSO_TECNOLOGIA_ASSISTIVA', 'INFORMATICA_ACESSIVEL', 'Recursos de informatica acessivel', TRUE);
MERGE INTO dominio_opcao (categoria, codigo, nome, ativo) KEY(categoria, codigo) VALUES ('CURSO_RECURSO_TECNOLOGIA_ASSISTIVA', 'CARACTERE_AMPLIADO', 'Material em caractere ampliado', TRUE);
MERGE INTO dominio_opcao (categoria, codigo, nome, ativo) KEY(categoria, codigo) VALUES ('CURSO_RECURSO_TECNOLOGIA_ASSISTIVA', 'MATERIAL_TATIL', 'Material pedagogico tatil', TRUE);
MERGE INTO dominio_opcao (categoria, codigo, nome, ativo) KEY(categoria, codigo) VALUES ('CURSO_RECURSO_TECNOLOGIA_ASSISTIVA', 'ACESSIBILIDADE_COMUNICACAO', 'Recursos de acessibilidade a comunicacao', TRUE);
MERGE INTO dominio_opcao (categoria, codigo, nome, ativo) KEY(categoria, codigo) VALUES ('CURSO_RECURSO_TECNOLOGIA_ASSISTIVA', 'TRADUTOR_LIBRAS', 'Tradutor e interprete de libras', TRUE);
MERGE INTO dominio_opcao (categoria, codigo, nome, ativo) KEY(categoria, codigo) VALUES ('CURSO_RECURSO_TECNOLOGIA_ASSISTIVA', 'GUIA_INTERPRETE', 'Guia interprete', TRUE);
MERGE INTO dominio_opcao (categoria, codigo, nome, ativo) KEY(categoria, codigo) VALUES ('CURSO_RECURSO_TECNOLOGIA_ASSISTIVA', 'MATERIAL_LIBRAS', 'Material didatico em libras', TRUE);
MERGE INTO dominio_opcao (categoria, codigo, nome, ativo) KEY(categoria, codigo) VALUES ('CURSO_RECURSO_TECNOLOGIA_ASSISTIVA', 'DISCIPLINA_LIBRAS', 'Disciplina de libras no curso', TRUE);
MERGE INTO dominio_opcao (categoria, codigo, nome, ativo) KEY(categoria, codigo) VALUES ('CURSO_RECURSO_TECNOLOGIA_ASSISTIVA', 'IMPRESSO_ACESSIVEL', 'Material didatico impresso acessivel', TRUE);
MERGE INTO dominio_opcao (categoria, codigo, nome, ativo) KEY(categoria, codigo) VALUES ('CURSO_RECURSO_TECNOLOGIA_ASSISTIVA', 'DIGITAL_ACESSIVEL', 'Material didatico digital acessivel', TRUE);

-- CURSO_ALUNO - Tipos de financiamento (Registro 42, campos 29 a 39)
MERGE INTO dominio_opcao (categoria, codigo, nome, ativo) KEY(categoria, codigo) VALUES ('CURSO_ALUNO_TIPO_FINANCIAMENTO', 'FIES', 'FIES', TRUE);
MERGE INTO dominio_opcao (categoria, codigo, nome, ativo) KEY(categoria, codigo) VALUES ('CURSO_ALUNO_TIPO_FINANCIAMENTO', 'GOV_ESTADUAL_REEMB', 'Governo estadual reembolsavel', TRUE);
MERGE INTO dominio_opcao (categoria, codigo, nome, ativo) KEY(categoria, codigo) VALUES ('CURSO_ALUNO_TIPO_FINANCIAMENTO', 'GOV_MUNICIPAL_REEMB', 'Governo municipal reembolsavel', TRUE);
MERGE INTO dominio_opcao (categoria, codigo, nome, ativo) KEY(categoria, codigo) VALUES ('CURSO_ALUNO_TIPO_FINANCIAMENTO', 'IES_REEMB', 'IES reembolsavel', TRUE);
MERGE INTO dominio_opcao (categoria, codigo, nome, ativo) KEY(categoria, codigo) VALUES ('CURSO_ALUNO_TIPO_FINANCIAMENTO', 'ENTIDADE_EXTERNA_REEMB', 'Entidade externa reembolsavel', TRUE);
MERGE INTO dominio_opcao (categoria, codigo, nome, ativo) KEY(categoria, codigo) VALUES ('CURSO_ALUNO_TIPO_FINANCIAMENTO', 'PROUNI_INTEGRAL', 'ProUni integral', TRUE);
MERGE INTO dominio_opcao (categoria, codigo, nome, ativo) KEY(categoria, codigo) VALUES ('CURSO_ALUNO_TIPO_FINANCIAMENTO', 'PROUNI_PARCIAL', 'ProUni parcial', TRUE);
MERGE INTO dominio_opcao (categoria, codigo, nome, ativo) KEY(categoria, codigo) VALUES ('CURSO_ALUNO_TIPO_FINANCIAMENTO', 'ENTIDADE_EXTERNA_NAO_REEMB', 'Entidade externa nao reembolsavel', TRUE);
MERGE INTO dominio_opcao (categoria, codigo, nome, ativo) KEY(categoria, codigo) VALUES ('CURSO_ALUNO_TIPO_FINANCIAMENTO', 'GOV_ESTADUAL_NAO_REEMB', 'Governo estadual nao reembolsavel', TRUE);
MERGE INTO dominio_opcao (categoria, codigo, nome, ativo) KEY(categoria, codigo) VALUES ('CURSO_ALUNO_TIPO_FINANCIAMENTO', 'IES_NAO_REEMB', 'IES nao reembolsavel', TRUE);
MERGE INTO dominio_opcao (categoria, codigo, nome, ativo) KEY(categoria, codigo) VALUES ('CURSO_ALUNO_TIPO_FINANCIAMENTO', 'GOV_MUNICIPAL_NAO_REEMB', 'Governo municipal nao reembolsavel', TRUE);

-- CURSO_ALUNO - Apoio social (Registro 42, campos 41 a 46)
MERGE INTO dominio_opcao (categoria, codigo, nome, ativo) KEY(categoria, codigo) VALUES ('CURSO_ALUNO_APOIO_SOCIAL', 'ALIMENTACAO', 'Apoio social - Alimentacao', TRUE);
MERGE INTO dominio_opcao (categoria, codigo, nome, ativo) KEY(categoria, codigo) VALUES ('CURSO_ALUNO_APOIO_SOCIAL', 'MORADIA', 'Apoio social - Moradia', TRUE);
MERGE INTO dominio_opcao (categoria, codigo, nome, ativo) KEY(categoria, codigo) VALUES ('CURSO_ALUNO_APOIO_SOCIAL', 'TRANSPORTE', 'Apoio social - Transporte', TRUE);
MERGE INTO dominio_opcao (categoria, codigo, nome, ativo) KEY(categoria, codigo) VALUES ('CURSO_ALUNO_APOIO_SOCIAL', 'MATERIAL_DIDATICO', 'Apoio social - Material didatico', TRUE);
MERGE INTO dominio_opcao (categoria, codigo, nome, ativo) KEY(categoria, codigo) VALUES ('CURSO_ALUNO_APOIO_SOCIAL', 'BOLSA_TRABALHO', 'Apoio social - Bolsa trabalho', TRUE);
MERGE INTO dominio_opcao (categoria, codigo, nome, ativo) KEY(categoria, codigo) VALUES ('CURSO_ALUNO_APOIO_SOCIAL', 'BOLSA_PERMANENCIA', 'Apoio social - Bolsa permanencia', TRUE);

-- CURSO_ALUNO - Atividade extracurricular (Registro 42, campos 48 a 55)
MERGE INTO dominio_opcao (categoria, codigo, nome, ativo) KEY(categoria, codigo) VALUES ('CURSO_ALUNO_ATIVIDADE_EXTRACURRICULAR', 'PESQUISA', 'Atividade extracurricular - Pesquisa', TRUE);
MERGE INTO dominio_opcao (categoria, codigo, nome, ativo) KEY(categoria, codigo) VALUES ('CURSO_ALUNO_ATIVIDADE_EXTRACURRICULAR', 'PESQUISA_BOLSA', 'Bolsa/remuneracao - Pesquisa', TRUE);
MERGE INTO dominio_opcao (categoria, codigo, nome, ativo) KEY(categoria, codigo) VALUES ('CURSO_ALUNO_ATIVIDADE_EXTRACURRICULAR', 'EXTENSAO', 'Atividade extracurricular - Extensao', TRUE);
MERGE INTO dominio_opcao (categoria, codigo, nome, ativo) KEY(categoria, codigo) VALUES ('CURSO_ALUNO_ATIVIDADE_EXTRACURRICULAR', 'EXTENSAO_BOLSA', 'Bolsa/remuneracao - Extensao', TRUE);
MERGE INTO dominio_opcao (categoria, codigo, nome, ativo) KEY(categoria, codigo) VALUES ('CURSO_ALUNO_ATIVIDADE_EXTRACURRICULAR', 'MONITORIA', 'Atividade extracurricular - Monitoria', TRUE);
MERGE INTO dominio_opcao (categoria, codigo, nome, ativo) KEY(categoria, codigo) VALUES ('CURSO_ALUNO_ATIVIDADE_EXTRACURRICULAR', 'MONITORIA_BOLSA', 'Bolsa/remuneracao - Monitoria', TRUE);
MERGE INTO dominio_opcao (categoria, codigo, nome, ativo) KEY(categoria, codigo) VALUES ('CURSO_ALUNO_ATIVIDADE_EXTRACURRICULAR', 'ESTAGIO_NAO_OBRIGATORIO', 'Atividade extracurricular - Estagio nao obrigatorio', TRUE);
MERGE INTO dominio_opcao (categoria, codigo, nome, ativo) KEY(categoria, codigo) VALUES ('CURSO_ALUNO_ATIVIDADE_EXTRACURRICULAR', 'ESTAGIO_NAO_OBRIGATORIO_BOLSA', 'Bolsa/remuneracao - Estagio nao obrigatorio', TRUE);

-- CURSO_ALUNO - Reserva de vagas / programas diferenciados (Registro 42, campos 60 a 72)
MERGE INTO dominio_opcao (categoria, codigo, nome, ativo) KEY(categoria, codigo) VALUES ('CURSO_ALUNO_RESERVA_VAGA', 'LEI_COTAS', 'Lei de cotas', TRUE);
MERGE INTO dominio_opcao (categoria, codigo, nome, ativo) KEY(categoria, codigo) VALUES ('CURSO_ALUNO_RESERVA_VAGA', 'PPI', 'Preto, Pardo e Indigena', TRUE);
MERGE INTO dominio_opcao (categoria, codigo, nome, ativo) KEY(categoria, codigo) VALUES ('CURSO_ALUNO_RESERVA_VAGA', 'RENDA', 'Reserva por renda', TRUE);
MERGE INTO dominio_opcao (categoria, codigo, nome, ativo) KEY(categoria, codigo) VALUES ('CURSO_ALUNO_RESERVA_VAGA', 'PCD', 'Pessoa com deficiencia', TRUE);
MERGE INTO dominio_opcao (categoria, codigo, nome, ativo) KEY(categoria, codigo) VALUES ('CURSO_ALUNO_RESERVA_VAGA', 'ESCOLA_PUBLICA', 'Estudante de escola publica', TRUE);
MERGE INTO dominio_opcao (categoria, codigo, nome, ativo) KEY(categoria, codigo) VALUES ('CURSO_ALUNO_RESERVA_VAGA', 'QUILOMBOLA', 'Quilombola', TRUE);
MERGE INTO dominio_opcao (categoria, codigo, nome, ativo) KEY(categoria, codigo) VALUES ('CURSO_ALUNO_RESERVA_VAGA', 'TRANSGENERO_TRAVESTI', 'Estudante transgenero/travesti', TRUE);
MERGE INTO dominio_opcao (categoria, codigo, nome, ativo) KEY(categoria, codigo) VALUES ('CURSO_ALUNO_RESERVA_VAGA', 'INTERNACIONAL', 'Estudante internacional', TRUE);
MERGE INTO dominio_opcao (categoria, codigo, nome, ativo) KEY(categoria, codigo) VALUES ('CURSO_ALUNO_RESERVA_VAGA', 'REFUGIADO_APATRIDA_HUMANITARIO', 'Refugiado/Apatrida/Visto humanitario', TRUE);
MERGE INTO dominio_opcao (categoria, codigo, nome, ativo) KEY(categoria, codigo) VALUES ('CURSO_ALUNO_RESERVA_VAGA', 'IDOSO', 'Idoso', TRUE);
MERGE INTO dominio_opcao (categoria, codigo, nome, ativo) KEY(categoria, codigo) VALUES ('CURSO_ALUNO_RESERVA_VAGA', 'POVOS_TRADICIONAIS', 'Povos e comunidades tradicionais', TRUE);
MERGE INTO dominio_opcao (categoria, codigo, nome, ativo) KEY(categoria, codigo) VALUES ('CURSO_ALUNO_RESERVA_VAGA', 'MEDALHISTA_OLIMPIADAS', 'Medalhista em olimpiadas cientificas', TRUE);
MERGE INTO dominio_opcao (categoria, codigo, nome, ativo) KEY(categoria, codigo) VALUES ('CURSO_ALUNO_RESERVA_VAGA', 'OUTRAS_RESERVAS', 'Outros tipos de reserva de vagas', TRUE);
