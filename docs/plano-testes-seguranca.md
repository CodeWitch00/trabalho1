# Plano de testes de seguranca e interface

Data de referencia: 2026-09-04  
Ambiente: PostgreSQL de homologacao, Java 17, servidor Jakarta Servlet 6 e HTTPS habilitado.

| ID | Cenario | Passos | Resultado esperado | Tipo | Status |
| --- | --- | --- | --- | --- | --- |
| AUT-01 | Login valido de administrador | Informar `admin@boaleitura.local` e a senha de homologacao | Sessao renovada, cookie HttpOnly, redirecionamento ao acervo e acoes administrativas visiveis | Manual/Automatizado | Pendente homologacao |
| AUT-02 | Senha incorreta | Informar e-mail existente e senha invalida | Mensagem generica; nenhuma sessao autenticada e nenhum detalhe sobre a conta | Manual/Automatizado | Pendente homologacao |
| AUT-03 | Usuario inexistente | Informar e-mail nao cadastrado | Mesmo retorno de AUT-02 e tempo de resposta comparavel | Manual | Pendente homologacao |
| AUT-04 | Perfil usuario | Entrar com `usuario@boaleitura.local` | Consulta e busca permitidas; botoes de criar, editar e excluir ausentes | Manual | Pendente homologacao |
| AUT-05 | Rota sem sessao | Abrir `/livros`, `/livros/novo` e `/livros/editar?id=1` sem cookie | Redirecionamento para `/login`; apos login, retorno somente a destino interno | Automatizado | Pendente homologacao |
| AUT-06 | Rota administrativa por usuario comum | Em sessao de usuario, chamar GET `/livros/novo` e POST `/livros/excluir` | HTTP 403; nenhum livro alterado | Automatizado | Pendente homologacao |
| AUT-07 | CSRF | Enviar POST de cadastro/edicao/exclusao sem `_csrf` ou com valor alterado | HTTP 403; nenhuma alteracao no banco | Automatizado | Pendente homologacao |
| AUT-08 | Fixacao e expiração de sessao | Autenticar com uma sessao previa e aguardar 30 minutos sem atividade | ID de sessao renovado no login; rota protegida volta ao login apos expiracao | Manual | Pendente homologacao |
| VAL-01 | Dados invalidos | Submeter ano menor que 1500, exemplares negativos, status invalido e ISBN invalido | Erros por campo e nenhum registro persistido | Automatizado | Coberto por testes de servico |
| VAL-02 | Injecao em formulario | Informar `' OR '1'='1`, tags HTML e texto muito longo nos campos | Sem SQL executado fora dos parametros; saida escapada; validacao ou armazenamento seguro | Manual/Automatizado | Pendente homologacao |
| UI-01 | Marca em paginas protegidas | Abrir lista e formulario em sessao autenticada | Marca `BL Boa Leitura` visivel no cabecalho | Manual | Pendente homologacao |
| UI-02 | Responsividade | Testar login, lista e formulario em 320px, 768px e 1440px | Sem corte/ sobreposicao; a tabela permanece rolavel horizontalmente quando necessario | Manual | Pendente homologacao |

## Criterios de liberacao

- Todos os testes AUT e VAL com resultado aprovado no ambiente de homologacao.
- Credenciais demonstrativas removidas ou trocadas por senhas unicas com pelo menos 12 caracteres antes de producao.
- HTTPS ativo no proxy/servidor, com o cookie de sessao marcado `Secure` na configuracao do ambiente.
- Revisao manual de UI-01 e UI-02 aprovada usando a logo oficial, caso ela seja diferente da marca textual provisoria.
