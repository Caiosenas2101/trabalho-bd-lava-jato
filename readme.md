# Sistema Lava Jato

Projeto acadêmico de banco de dados (BD 2026.1). Sistema de lava jato com Spring Boot + JDBC + MySQL e interface web em HTML/CSS/JS.

## Pré-Requisitos

- JDK 17
- MySQL rodando em `localhost:3306`
- Maven Wrapper (já incluso no projeto)

## Como Rodar

### 1. Criar e popular o banco

No MySQL, execute na ordem:

```text
sql/create_tables.sql
sql/insert_data.sql
sql/etapa04/visoes/visoes.sql
sql/etapa04/indices/indices.sql
sql/etapa05/log/log_operacao.sql
sql/etapa05/funcoes/funcoes.sql
sql/etapa05/procedimentos/procedimentos.sql
sql/etapa05/triggers/triggers.sql
```

Os arquivos em `sql/etapa04/consultas/` são apenas para inspeção manual, não precisam ser executados.

### 2. Ajustar a senha do MySQL

Edite `interface/src/main/resources/application.properties` se a senha do seu MySQL for diferente:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/lava_jato
spring.datasource.username=root
spring.datasource.password=SUA_SENHA
server.port=8080
```

### 3. Subir a aplicação

Entre na pasta `interface` e rode:

- **Windows (PowerShell):**

```powershell
.\mvnw.cmd spring-boot:run
```

- **Mac/Linux:**

```bash
./mvnw spring-boot:run
```

Aguarde a linha `Started LavajatoApplication`.

### 4. Acessar

Abra no navegador:

```text
http://localhost:8080
```

Para testar se o backend está vivo:

```text
http://localhost:8080/clientes
```

## Estrutura

```text
trabalho-bd-lava-jato/
|- modelo/                 modelo conceitual, dicionário e esquema relacional
|- sql/                    scripts do banco (etapas 04 e 05)
|- interface/              aplicação Spring Boot
```

## O Que A Interface Tem

- CRUD de clientes, serviços, veículos, funcionários, atendimentos e avaliações
- 4 consultas e 2 views da Etapa 04
- Funções, procedures (incluindo uma com cursor) e triggers da Etapa 05
- Dashboard com indicadores, estatísticas descritivas e 5 gráficos

## Observações

- O projeto **não usa ORM**. Todo SQL é explícito via `JdbcTemplate`.
- Se o `fetch` da tela der "Failed to fetch", o backend caiu ou o MySQL não está rodando.
- O dashboard usa Chart.js via CDN, então precisa de internet ao abrir a página.
