DROP DATABASE IF EXISTS lava_jato;
CREATE DATABASE lava_jato;
USE lava_jato;

CREATE TABLE cliente (
    id_cliente INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(120) NOT NULL,
    cpf VARCHAR(14) NOT NULL UNIQUE,
    email VARCHAR(255) UNIQUE,
    rua VARCHAR(150),
    bairro VARCHAR(100),
    cidade VARCHAR(100)
);

CREATE TABLE funcionario (
    id_funcionario INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(120) NOT NULL,
    cargo VARCHAR(60) NOT NULL,
    id_supervisor INT,
    CONSTRAINT fk_funcionario_supervisor
        FOREIGN KEY (id_supervisor) REFERENCES funcionario(id_funcionario)
);

CREATE TABLE servico (
    id_servico INT AUTO_INCREMENT PRIMARY KEY,
    nome_servico VARCHAR(120) NOT NULL,
    preco DECIMAL(10,2) NOT NULL,
    tempo_min INT NOT NULL,
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
    placa VARCHAR(10) PRIMARY KEY,
    modelo VARCHAR(80) NOT NULL,
    cor VARCHAR(40) NOT NULL,
    ano INT NOT NULL,
    id_cliente INT NOT NULL,
    CONSTRAINT fk_veiculo_cliente
        FOREIGN KEY (id_cliente) REFERENCES cliente(id_cliente)
);

CREATE TABLE carro (
    placa VARCHAR(10) PRIMARY KEY,
    combust VARCHAR(20) NOT NULL,
    CONSTRAINT fk_carro_placa_veiculo
        FOREIGN KEY (placa) REFERENCES veiculo(placa)
);

CREATE TABLE moto (
    placa VARCHAR(10) PRIMARY KEY,
    cilindrada INT NOT NULL,
    CONSTRAINT fk_moto_placa_veiculo
        FOREIGN KEY (placa) REFERENCES veiculo(placa)
);

CREATE TABLE lavador (
    id_funcionario INT PRIMARY KEY,
    habilidade TEXT,
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
    data DATE NOT NULL,
    hora TIME NOT NULL,
    status VARCHAR(20) NOT NULL,
    placa_veiculo VARCHAR(10) NOT NULL,
    id_cliente INT NOT NULL,
    id_funcionario INT NOT NULL,
    CONSTRAINT fk_atendimento_placa_veiculo_veiculo
        FOREIGN KEY (placa_veiculo) REFERENCES veiculo(placa),
    CONSTRAINT fk_atendimento_id_cliente_cliente
        FOREIGN KEY (id_cliente) REFERENCES cliente(id_cliente),
    CONSTRAINT fk_atendimento_id_funcionario_funcionario
        FOREIGN KEY (id_funcionario) REFERENCES funcionario(id_funcionario)
);

CREATE TABLE atendimento_servico (
    id_atendimento INT NOT NULL,
    id_servico INT NOT NULL,
    PRIMARY KEY (id_atendimento, id_servico),
    CONSTRAINT fk_atendimento_servico_id_atendimento_atendimento
        FOREIGN KEY (id_atendimento) REFERENCES atendimento(id_atendimento),
    CONSTRAINT fk_atendimento_servico_id_servico_servico
        FOREIGN KEY (id_servico) REFERENCES servico(id_servico)
);

CREATE TABLE pagamento (
    id_pagamento INT AUTO_INCREMENT PRIMARY KEY,
    forma_pagamento VARCHAR(30) NOT NULL,
    valor_total DECIMAL(10,2) NOT NULL,
    desconto DECIMAL(10,2) DEFAULT 0,
    comprovante TEXT,
    id_atendimento INT NOT NULL UNIQUE,
    CONSTRAINT fk_pagamento_id_atendimento_atendimento
        FOREIGN KEY (id_atendimento) REFERENCES atendimento(id_atendimento)
);

CREATE TABLE avaliacao (
    id_cliente INT NOT NULL,
    id_atendimento INT NOT NULL,
    data DATE NOT NULL,
    nota SMALLINT NOT NULL,
    comentarios TEXT,
    PRIMARY KEY (id_cliente, id_atendimento),
    CONSTRAINT fk_avaliacao_id_cliente_cliente
        FOREIGN KEY (id_cliente) REFERENCES cliente(id_cliente),
    CONSTRAINT fk_avaliacao_id_atendimento_atendimento
        FOREIGN KEY (id_atendimento) REFERENCES atendimento(id_atendimento)
);