USE lava_jato;

INSERT INTO cliente (nome, cpf, email, endereco_rua, endereco_bairro, endereco_cidade) VALUES
('João Silva', '12345678901', 'joao@email.com', 'Rua das Flores', 'Boa Viagem', 'Recife'),
('Maria Souza', '23456789012', 'maria@email.com', 'Av. Recife', 'Pina', 'Recife'),
('Carlos Lima', '34567890123', 'carlos@email.com', 'Rua do Sol', 'Casa Forte', 'Recife');

INSERT INTO cliente_telefone (id_cliente, telefone) VALUES
(1, '81999990001'),
(1, '81333340001'),
(2, '81999990002'),
(3, '81999990003');

INSERT INTO funcionario (nome, cargo, id_supervisor) VALUES
('Ana Costa', 'Gerente', NULL),
('Bruno Martins', 'Lavador', 1),
('Felipe Rocha', 'Lavador', 1);

INSERT INTO funcionario_telefone (id_funcionario, telefone) VALUES
(1, '81988880001'),
(2, '81988880002'),
(3, '81988880003');

INSERT INTO gerente (id_funcionario, bonus) VALUES
(1, 500.00);

INSERT INTO lavador (id_funcionario, habilidade) VALUES
(2, 'Lavagem completa e polimento'),
(3, 'Lavagem simples e higienizacao interna');

INSERT INTO veiculo (placa, id_cliente, modelo, cor, ano) VALUES
('QWE1A23', 1, 'Onix', 'Prata', 2020),
('RTY2B34', 2, 'HB20', 'Branco', 2021),
('UIO3C45', 3, 'CG 160', 'Preta', 2022),
('PAS4D56', 1, 'Corolla', 'Cinza', 2019);

INSERT INTO carro (placa, id_cliente, tipo_combustivel) VALUES
('QWE1A23', 1, 'Flex'),
('RTY2B34', 2, 'Flex'),
('PAS4D56', 1, 'Gasolina');

INSERT INTO moto (placa, id_cliente, cilindrada) VALUES
('UIO3C45', 3, '160cc');

INSERT INTO servico (nome_servico, preco, tempo_min, descricao) VALUES
('Lavagem Simples', 30.00, 40, 'Lavagem externa do veiculo'),
('Lavagem Completa', 50.00, 70, 'Lavagem externa e interna'),
('Polimento', 80.00, 120, 'Polimento da pintura'),
('Higienizacao Interna', 60.00, 90, 'Limpeza detalhada do interior');

INSERT INTO atendimento (placa_veiculo, id_cliente_veiculo, id_servico, id_funcionario, data, hora, status) VALUES
('QWE1A23', 1, 1, 2, '2026-03-28', '08:00:00', 'finalizado'),
('QWE1A23', 1, 2, 2, '2026-03-28', '09:00:00', 'finalizado'),
('RTY2B34', 2, 2, 3, '2026-03-29', '10:30:00', 'em_execucao'),
('UIO3C45', 3, 1, 2, '2026-03-30', '14:00:00', 'agendado'),
('UIO3C45', 3, 4, 2, '2026-03-30', '15:00:00', 'agendado');

INSERT INTO pagamento (id_atendimento, forma_pagto, valor_total, descontos, comprovante) VALUES
(1, 'Pix', 30.00, 0.00, 'Pagamento realizado via Pix'),
(2, 'Cartao', 50.00, 0.00, 'Pagamento realizado no cartao');

INSERT INTO avaliacao (id_cliente, id_atendimento, nota, comentarios, data) VALUES
(1, 1, 5, 'Excelente atendimento', '2026-03-28');