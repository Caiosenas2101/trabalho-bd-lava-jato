# Sistema Lava Jato

Projeto acadêmico de banco de dados com modelagem conceitual, esquema relacional, scripts SQL e uma implementação web simples em Spring Boot.

O sistema representa um cenário de lava jato com cadastro de clientes, veículos, funcionários, serviços, atendimentos, pagamentos e avaliações.

## Objetivo

Este projeto reúne:

- modelagem conceitual do domínio;
- dicionário de dados;
- esquema relacional;
- scripts de criação e carga do banco;
- aplicação web para consulta e cadastro de dados básicos.

## Estrutura Do Projeto

```text
trabalho-bd-lava-jato/
|- modelo/
|  |- dicionario.txt
|  |- esquema-relacional.txt
|  |- esquema-conceitual.png
|- sql/
|  |- create_tables.sql
|  |- insert_data.sql
|- interface/
|  |- pom.xml
|  |- mvnw
|  |- mvnw.cmd
|  |- src/
|     |- main/
|     |  |- java/
|     |  |- resources/
```

## Tecnologias Utilizadas

- Java 17
- Spring Boot
- Spring Web MVC
- Spring Data JPA
- Maven
- MySQL
- HTML, CSS e JavaScript

## Modelo De Dados

As principais entidades do sistema são:

- `CLIENTE`
- `CLIENTE_TELEFONE`
- `VEICULO`
- `CARRO`
- `MOTO`
- `FUNCIONARIO`
- `FUNCIONARIO_TELEFONE`
- `LAVADOR`
- `GERENTE`
- `SERVICO`
- `ATENDIMENTO`
- `AVALIACAO`
- `PAGAMENTO`

Arquivos importantes da modelagem:

- `modelo/esquema-conceitual.png`: modelo conceitual.
- `modelo/dicionario.txt`: dicionário de dados.
- `modelo/esquema-relacional.txt`: esquema relacional em texto.

## Banco De Dados

Os scripts do banco estão em `sql/`.

- `sql/create_tables.sql`: cria o banco `lava_jato` e as tabelas.
- `sql/insert_data.sql`: insere dados de exemplo.

### Ordem De Execução

1. Execute `sql/create_tables.sql`.
2. Execute `sql/insert_data.sql`.

## Configuração Da Aplicação

As configurações da aplicação estão em `interface/src/main/resources/application.properties`.

Configuração atual:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/lava_jato
spring.datasource.username=root
spring.datasource.password=V1nte101@@
spring.jpa.hibernate.ddl-auto=none
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQLDialect
server.port=8080
```

## Pré-Requisitos

Antes de rodar o projeto, você precisa ter instalado:

- MySQL em execução localmente
- JDK 17 configurado no sistema
- Maven ou usar o Maven Wrapper do projeto

Importante:

- Para compilar e testar a aplicação, é necessário um `JDK`, não apenas um `JRE`.
- O banco `lava_jato` deve existir e estar populado antes de subir a aplicação.

## Como Rodar A Aplicação Web

Entre na pasta `interface` e execute:

```powershell
.\mvnw.cmd spring-boot:run
```

Depois, acesse:

[`http://localhost:8080`](http://localhost:8080)

## Funcionalidades Implementadas Na Interface

Atualmente, a interface web implementa apenas operações básicas para:

- clientes
- serviços

### Endpoints Disponíveis

#### Clientes

- `GET /clientes`: lista os clientes
- `POST /clientes`: cadastra um cliente

Exemplo de JSON:

```json
{
  "nome": "João Silva",
  "cpf": "12345678901",
  "email": "joao@email.com",
  "enderecoRua": "Rua das Flores",
  "enderecoBairro": "Boa Viagem",
  "enderecoCidade": "Recife"
}
```

#### Serviços

- `GET /servicos`: lista os serviços
- `POST /servicos`: cadastra um serviço

Exemplo de JSON:

```json
{
  "nomeServico": "Lavagem Simples",
  "preco": 30.00,
  "tempoMin": 40,
  "descricao": "Lavagem externa do veiculo"
}
```

## Observações Importantes

- A aplicação web ainda cobre apenas uma parte do modelo de dados.
- O projeto possui entidades no banco que ainda não têm telas ou endpoints na interface.
- As credenciais do banco estão em texto puro no arquivo `application.properties`.
- Para uso mais seguro, o ideal é mover usuário e senha para variáveis de ambiente no futuro.

## Testes

O projeto possui um teste básico de contexto em:

- `interface/src/test/java/com/lavajato/lavajato/LavajatoApplicationTests.java`

Para executar os testes:

```powershell
.\mvnw.cmd test
```

Se aparecer erro informando ausência de compilador Java, isso indica que o ambiente está com `JRE` ou com `JAVA_HOME` incorreto.

## Próximos Passos Sugeridos

- expandir a interface para veículos, atendimentos, pagamentos e avaliações;
- mover credenciais para variáveis de ambiente;
- adicionar testes de integração para os endpoints;
- criar validações de entrada para CPF, e-mail e campos obrigatórios.
