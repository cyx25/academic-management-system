# AMS: Exercício Prático BL125/2025

O **DEI Academic Management System** (ou _AMS_) é uma plataforma interna do Departamento de Engenharia Informática (DEI) que permite a gestão centralizada das Unidades Curriculares (UCs), facilitando a organização de alunos, professores, avaliações e projetos.

Para melhorar a gestão académica, é necessário o desenvolvimento de um subsistema simples para este efeito: o **AMS: Academic Management System** (o seu exercício prático).

Este subsistema deve permitir a gestão de:

- **Pessoas**, incluindo, para cada uma, pelo menos o nome, IST ID, email e o seu tipo no sistema (aluno, professor regente, professor assistente, administrador, ou outros tipos que considere relevantes);
- **Cursos**, incluindo código e nome;
- **Unidades Curriculares**, incluindo código, nome, semestre, ECTS e curso(s) a que pertence(m);
- **Avaliações**, organizadas em duas secções: testes e projetos;
- **Recursos**, incluindo enunciados, materiais de estudo e documentos associados a cada UC;
- **Calendário**, com agendamento de avaliações e gestão de prazos.

## Estrutura e Regras de Gestão

- Cada **Unidade Curricular** deve ter **um único Professor Regente**, responsável pela sua gestão.
- O Professor Regente pode associar **Professores Assistentes** e **Alunos** à UC, bem como definir as suas permissões.
- Os Professores Assistentes podem atribuir avaliações e notas aos alunos, mas não podem alterar a composição da UC.
- Os Alunos podem visualizar as suas avaliações, submeter projetos e pedir revisões de prova.

## Avaliações

### Testes
- Cada UC pode ter vários testes, definidos pelo Professor Regente (título, data, peso na nota final).
- Professores Regentes e Professores Assistentes podem:
  - Atribuir notas a cada aluno;
  - Fazer upload do teste individual corrigido em formato PDF, para posterior consulta pelo aluno;
  - Fazer upload do enunciado do teste para consulta posterior.
- Alunos podem:
  - Consultar as suas notas;
  - Submeter pedidos de revisão de prova (com justificação e prazo limite);
  - Visualizar testes corrigidos quando disponibilizados.

### Projetos
- Cada UC pode ter um ou mais projetos, definidos pelo Professor Regente (título, prazo de submissão, peso na nota final).
- Projetos podem ser **individuais ou em grupo**:
  - Para projetos em grupo, o Professor Regente define o tamanho máximo dos grupos;
  - De modo a simplificar a implementação, os grupos podem ser atribuídos automaticamente e aleatoriamente;
  - Submissões são feitas pelo grupo, assim como as suas notas finais.
- Alunos podem submeter ficheiros de projeto (ex: `.py`, `.c`, `.zip`) através da aplicação.
- Professores Regentes e Professores Assistentes podem atribuir notas ao projeto.
- Opcionalmente, cada submissão de projeto pode desencadear uma avaliação automática por parte do backend, que atribui uma **nota provisória**. Esta nota pode depois ser confirmada ou ajustada pelo professor.

## Workflow de Revisão de Provas

O sistema implementa um workflow estruturado para revisão de provas:

1. **Estudante solicita revisão** - Submete pedido com justificação dentro do prazo limite
2. **Professor Assistente analisa** - Revê o pedido e emite parecer inicial (se aplicável)
3. **Professor Regente decide** - Toma a decisão final (aprovar/rejeitar)
4. **Histórico mantido** - Todas as decisões de revisão ficam registadas

## Gestão de Recursos

O sistema permite a gestão centralizada de materiais por UC:

- **Upload de enunciados** de testes e projetos pelos professores
- **Biblioteca de materiais** de estudo organizados por UC

## Calendário de Avaliações

- **Agendamento** de testes e prazos de entrega de projetos
- **Deteção automática de conflitos** entre as várias UCs

## Implementação Obrigatória

O objetivo deste exercício é desenvolver o subsistema **AMS** usando [Spring Boot](https://spring.io/projects/spring-boot) (backend) e [Vue.js](https://vuejs.org/) (frontend).

O subsistema **AMS** deverá permitir, pelo menos:

- Visualizar todos os Cursos numa tabela, incluindo código, nome e duração;
- Criar, atualizar e remover Unidades Curriculares (apenas pelos Administradores);
- Visualizar todas as Unidades Curriculares numa tabela, incluindo código, nome, semestre, curso e Professor Regente associado;
- Visualizar todos os alunos de uma UC, incluindo nome, IST ID, email e estado (inscrito, aprovado, reprovado);
- Visualizar todos os professores de uma UC, incluindo nome, IST ID, email e tipo (regente, assistente);
- Criar, atualizar e remover Unidades Curriculares (apenas pelos Administradores);
- Gerir as pessoas associadas a cada UC: adicionar/remover Alunos e Professores Assistentes (apenas pelo Professor Regente);
- Visualizar todas as pessoas associadas a uma UC, incluindo nome, IST ID, email e tipo (aluno, professor regente, professor assistente);
- Criar, visualizar e gerir testes e projetos associados a uma UC;
- Gerir grupos de projeto: criação, modificação e atribuição de alunos;
- Fazer upload e gerir recursos (enunciados, materiais) por UC;
- Atribuir notas a alunos em testes e projetos (pelos Professores Regentes e Assistentes);
- Submissão de projetos por parte dos alunos (individual ou em grupo);
- Implementar o workflow completo de revisão de provas conforme descrito;
- Consultar notas individuais de cada aluno numa UC, bem como a sua média final (com base nos pesos dos testes e projetos);
- Visualizar calendário de avaliações com deteção de conflitos;
- Consultar o perfil do aluno, onde contém as suas UCs, notas, avaliações pendentes e projetos submetidos (apenas visível para o próprio aluno e Administradores).

A solução que cumpra os requisitos descritos acima será avaliada com a **cotação máxima de 14 valores**.

**Nota:** Quando alguma situação ou detalhe não estiver explicitamente descrito neste enunciado, deve ser usado o **bom senso** e tomar decisões consistentes com a lógica do sistema, documentando-as no ficheiro `README` se necessário.

### Funcionalidades Adicionais

- **Submissões múltiplas de projetos** com histórico de versões, sendo a última submissão considerada como válida.
- **Avaliação automática de projetos** com feedback detalhado (ex: testes unitários acionados pelo backend).
- **Dashboard personalizado** com vistas específicas:
  - Vista do estudante: notas, prazos, progresso nas UCs
  - Vista do professor: alunos, correções pendentes, estatísticas
  - Vista do assistente: tarefas de correção, estatísticas
- **Sistema de estatísticas avançado** da UC: distribuição de notas, médias por teste/projeto, número de revisões submetidas/aprovadas...
- **Notificações na aplicação e/ou Email** para eventos importantes (ex: submissão de projeto, atribuição de nota, pedido de revisão...). Para envio de emails, é recomendado usar [MailCrab](https://github.com/tweedegolf/mailcrab).



