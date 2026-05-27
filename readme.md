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
|  |- esquema-conceitual.jpeg
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
- Spring JDBC
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
- `REALIZA`
- `AVALIACAO`
- `PAGAMENTO`

Arquivos importantes da modelagem:

- `modelo/esquema-conceitual.jpeg`: modelo conceitual.
- `modelo/dicionario.txt`: dicionário de dados.
- `modelo/esquema-relacional.txt`: esquema relacional em texto.

## Banco De Dados

Os scripts do banco estão em `sql/`.

- `sql/create_tables.sql`: cria o banco `lava_jato` e as tabelas.
- `sql/insert_data.sql`: insere dados de exemplo.
- `sql/etapa04/consultas/consultas.sql`: consultas SQL da etapa 04.
- `sql/etapa04/visoes/visoes.sql`: visões SQL da etapa 04.
- `sql/etapa04/indices/indices.sql`: índices SQL da etapa 04.
- `sql/etapa05/log/log_operacao.sql`: tabela de log da etapa 05.
- `sql/etapa05/funcoes/funcoes.sql`: funções da etapa 05.
- `sql/etapa05/procedimentos/procedimentos.sql`: procedimentos da etapa 05.
- `sql/etapa05/triggers/triggers.sql`: triggers da etapa 05.

### Ordem De Execução

1. Execute `sql/create_tables.sql`.
2. Execute `sql/insert_data.sql`.
3. Execute os arquivos da etapa 04, se necessário:
   - `sql/etapa04/consultas/consultas.sql`
   - `sql/etapa04/visoes/visoes.sql`
   - `sql/etapa04/indices/indices.sql`
4. Execute os arquivos da etapa 05 nesta ordem:
   - `sql/etapa05/log/log_operacao.sql`
   - `sql/etapa05/funcoes/funcoes.sql`
   - `sql/etapa05/procedimentos/procedimentos.sql`
   - `sql/etapa05/triggers/triggers.sql`

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

A interface web atualmente oferece:

- CRUD completo de clientes, serviços, veículos, funcionários, atendimentos e avaliações
- Consultas e visões da etapa 04 (faturamento, atendimentos por período, anti-join, subconsulta, views)
- Operações da etapa 05 (funções, procedimentos com cursor, triggers e log)
- Dashboard estatístico integrado com indicadores resumidos, estatísticas descritivas e 7 gráficos dinâmicos baseados em dados do banco

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

#### Dashboard Estatístico

Todos os endpoints aceitam os parâmetros opcionais `inicio` e `fim` (formato `YYYY-MM-DD`). Os dados retornados são lidos diretamente das tabelas do banco `lava_jato`.

- `GET /relatorios/dashboard-resumo`: indicadores resumidos do período (totais, ticket médio, média, mediana, moda, variância, desvio padrão das notas e taxa de conclusão).
- `GET /relatorios/dashboard-faturamento-servico?limite=10`: faturamento líquido por serviço (gráfico de barras).
- `GET /relatorios/dashboard-status-atendimento`: distribuição de atendimentos por status (gráfico de pizza).
- `GET /relatorios/dashboard-tendencia?granularidade=dia|semana|mes`: tendência temporal de atendimentos e faturamento (gráfico de linha).
- `GET /relatorios/dashboard-radar-servicos?limite=5`: comparativo entre os top serviços (gráfico de radar).
- `GET /relatorios/dashboard-distribuicao-notas`: histograma das notas das avaliações (gráfico de barras horizontais).
- `GET /relatorios/dashboard-formas-pagamento`: distribuição de faturamento por forma de pagamento (gráfico de rosca).
- `GET /relatorios/dashboard-top-clientes?limite=10`: ranking dos clientes que mais gastaram no período (gráfico de barras).

A tela do dashboard, em `interface/src/main/resources/static/index.html`, consome esses endpoints e usa a biblioteca [Chart.js](https://www.chartjs.org/) (via CDN) para renderizar:

- 12 indicadores resumidos (totais, ticket médio, faturamento, taxa de conclusão)
- 6 cards de estatísticas descritivas (média, mediana, moda + frequência, variância, desvio padrão, mínimo/máximo)
- 7 gráficos interativos (barras, pizza, linha de dupla escala, radar, barras horizontais, rosca e ranking)
- Filtros interativos de período (data início/fim), granularidade temporal (dia/semana/mês) e limite de itens nos rankings

## Observações Importantes

- A aplicação usa `JdbcTemplate`, não `Spring Data JPA`.
- Toda a aba "Dashboard" da interface é alimentada exclusivamente por consultas SQL contra o banco `lava_jato`.
- O dashboard usa funções nativas do MySQL para estatísticas: `AVG`, `VAR_POP`, `STDDEV_POP`, `MIN`, `MAX`, além de `ROW_NUMBER() OVER (...)` para o cálculo da mediana e `GROUP BY ... ORDER BY COUNT(*)` para a moda.
- A biblioteca Chart.js é carregada via CDN. Caso a aplicação rode em rede sem internet, baixe `chart.umd.min.js` para `interface/src/main/resources/static/` e ajuste a tag `<script>` em `index.html`.
- As credenciais do banco devem ser passadas por variáveis de ambiente em produção.

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
- adicionar testes de integração para os endpoints;
- criar validações de entrada para CPF, e-mail e campos obrigatórios.
