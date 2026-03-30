DROP DATABASE IF EXISTS lava_jato;
CREATE DATABASE lava_jato;
USE lava_jato;

CREATE TABLE cliente (
    id_cliente INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    cpf CHAR(11) NOT NULL UNIQUE,
    email VARCHAR(100) UNIQUE,
    endereco_rua VARCHAR(100),
    endereco_bairro VARCHAR(80),
    endereco_cidade VARCHAR(80)
);

CREATE TABLE funcionario (
    id_funcionario INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    cargo VARCHAR(60),
    id_supervisor INT,
    CONSTRAINT fk_funcionario_supervisor
        FOREIGN KEY (id_supervisor) REFERENCES funcionario(id_funcionario)
);

CREATE TABLE servico (
    id_servico INT AUTO_INCREMENT PRIMARY KEY,
    nome_servico VARCHAR(100) NOT NULL,
    preco DECIMAL(10,2) NOT NULL,
    tempo_min INT,
    descricao TEXT
);

CREATE TABLE cliente_telefone (
    id_cliente INT NOT NULL,
    telefone VARCHAR(20) NOT NULL,
    PRIMARY KEY (id_cliente, telefone),
    CONSTRAINT fk_cliente_telefone_cliente
        FOREIGN KEY (id_cliente) REFERENCES cliente(id_cliente)
);

CREATE TABLE funcionario_telefone (
    id_funcionario INT NOT NULL,
    telefone VARCHAR(20) NOT NULL,
    PRIMARY KEY (id_funcionario, telefone),
    CONSTRAINT fk_funcionario_telefone_funcionario
        FOREIGN KEY (id_funcionario) REFERENCES funcionario(id_funcionario)
);

CREATE TABLE veiculo (
    placa CHAR(7) NOT NULL,
    id_cliente INT NOT NULL,
    modelo VARCHAR(80) NOT NULL,
    cor VARCHAR(40),
    ano SMALLINT,
    PRIMARY KEY (placa, id_cliente),
    CONSTRAINT fk_veiculo_cliente
        FOREIGN KEY (id_cliente) REFERENCES cliente(id_cliente)
);

CREATE TABLE carro (
    placa CHAR(7) NOT NULL,
    id_cliente INT NOT NULL,
    tipo_combustivel VARCHAR(30),
    PRIMARY KEY (placa, id_cliente),
    CONSTRAINT fk_carro_veiculo
        FOREIGN KEY (placa, id_cliente) REFERENCES veiculo(placa, id_cliente)
);

CREATE TABLE moto (
    placa CHAR(7) NOT NULL,
    id_cliente INT NOT NULL,
    cilindrada VARCHAR(20),
    PRIMARY KEY (placa, id_cliente),
    CONSTRAINT fk_moto_veiculo
        FOREIGN KEY (placa, id_cliente) REFERENCES veiculo(placa, id_cliente)
);

CREATE TABLE lavador (
    id_funcionario INT PRIMARY KEY,
    habilidade VARCHAR(60),
    CONSTRAINT fk_lavador_id_funcionario_funcionario
        FOREIGN KEY (id_funcionario) REFERENCES funcionario(id_funcionario)
);

CREATE TABLE gerente (
    id_funcionario INT PRIMARY KEY,
    bonus DECIMAL(10,2),
    CONSTRAINT fk_gerente_id_funcionario_funcionario
        FOREIGN KEY (id_funcionario) REFERENCES funcionario(id_funcionario)
);

CREATE TABLE atendimento (
    id_atendimento INT AUTO_INCREMENT PRIMARY KEY,
    placa_veiculo CHAR(7) NOT NULL,
    id_cliente_veiculo INT NOT NULL,
    id_servico INT NOT NULL,
    id_funcionario INT NOT NULL,
    data DATE NOT NULL,
    hora TIME NOT NULL,
    status VARCHAR(20) NOT NULL,
    CONSTRAINT fk_atendimento_veiculo
        FOREIGN KEY (placa_veiculo, id_cliente_veiculo) REFERENCES veiculo(placa, id_cliente),
    CONSTRAINT fk_atendimento_id_servico_servico
        FOREIGN KEY (id_servico) REFERENCES servico(id_servico),
    CONSTRAINT fk_atendimento_id_funcionario_funcionario
        FOREIGN KEY (id_funcionario) REFERENCES funcionario(id_funcionario)
);

CREATE TABLE pagamento (
    id_pagamento INT AUTO_INCREMENT PRIMARY KEY,
    id_atendimento INT NOT NULL UNIQUE,
    forma_pagto VARCHAR(30) NOT NULL,
    valor_total DECIMAL(10,2) NOT NULL,
    descontos DECIMAL(10,2) DEFAULT 0.00,
    comprovante VARCHAR(200),
    CONSTRAINT fk_pagamento_id_atendimento_atendimento
        FOREIGN KEY (id_atendimento) REFERENCES atendimento(id_atendimento)
);

CREATE TABLE avaliacao (
    id_avaliacao INT AUTO_INCREMENT PRIMARY KEY,
    id_cliente INT NOT NULL,
    id_atendimento INT NOT NULL UNIQUE,
    nota TINYINT NOT NULL,
    comentarios TEXT,
    data DATE,
    CONSTRAINT fk_avaliacao_id_cliente_cliente
        FOREIGN KEY (id_cliente) REFERENCES cliente(id_cliente),
    CONSTRAINT fk_avaliacao_id_atendimento_atendimento
        FOREIGN KEY (id_atendimento) REFERENCES atendimento(id_atendimento)
);