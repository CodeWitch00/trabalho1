**Objetivo:** Auditar, testar e reforçar as camadas de segurança e estabilidade da aplicação web desenvolvida, implementando autenticação segura, controle de acesso e elaboração do plano de testes antes do envio à produção.

**Atividade Assíncrona:**

**Implementação e Auditoria de Segurança:**

- Garanta que as senhas cadastradas no sistema passem por algoritmo de **hash criptográfico** (ex: BCrypt, SHA-256) antes de serem armazenadas no banco de dados.
- Implemente o **Controle de Acesso por Perfis** (ex: *Admin vs Usuário Comum*), utilizando controle de sessão HTTP/Cookies para proteger rotas e páginas restritas.

**Plano de Testes e Ajustes de Interface:**

- Monte uma planilha/documento de **Plano de Testes** validando os cenários de autenticação (login válido, senha incorreta, acesso a rotas não autorizadas, injeção de dados inválidos em formulários).
- Valide se as páginas protegidas apresentam a **logo do cliente parceiro** e mantêm a **responsividade** (layouts fluidos para desktop e dispositivos móveis).

**Entregável:**

Repositório no GitHub atualizado com a implementação das camadas de segurança e verificação de hash. Documento em PDF do **Plano de Testes Executado** contendo os cenários testados, entradas utilizadas e os resultados obtidos (passou/falhou).