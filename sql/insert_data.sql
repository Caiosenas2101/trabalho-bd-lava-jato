USE lava_jato;

INSERT INTO cliente (nome, cpf, email, rua, bairro, cidade) VALUES
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

INSERT INTO veiculo (placa, modelo, cor, ano, id_cliente) VALUES
('QWE1A23', 'Onix', 'Prata', 2020, 1),
('RTY2B34', 'HB20', 'Branco', 2021, 2),
('UIO3C45', 'CG 160', 'Preta', 2022, 3),
('PAS4D56', 'Corolla', 'Cinza', 2019, 1);

INSERT INTO carro (placa, combust) VALUES
('QWE1A23', 'Flex'),
('RTY2B34', 'Flex'),
('PAS4D56', 'Gasolina');

INSERT INTO moto (placa, cilindrada) VALUES
('UIO3C45', 160);

INSERT INTO servico (nome_servico, preco, tempo_min, descricao) VALUES
('Lavagem Simples', 30.00, 40, 'Lavagem externa do veiculo'),
('Lavagem Completa', 50.00, 70, 'Lavagem externa e interna'),
('Polimento', 80.00, 120, 'Polimento da pintura'),
('Higienizacao Interna', 60.00, 90, 'Limpeza detalhada do interior');

INSERT INTO atendimento (data, hora, status, placa_veiculo, id_cliente, id_funcionario) VALUES
('2026-03-28', '08:00:00', 'finalizado', 'QWE1A23', 1, 2),
('2026-03-29', '10:30:00', 'em_execucao', 'RTY2B34', 2, 3),
('2026-03-30', '14:00:00', 'agendado', 'UIO3C45', 3, 2);

INSERT INTO atendimento_servico (id_atendimento, id_servico) VALUES
(1, 1),
(1, 2),
(2, 2),
(3, 1),
(3, 4);

INSERT INTO pagamento (forma_pagamento, valor_total, desconto, comprovante, id_atendimento) VALUES
('Pix', 80.00, 0.00, 'Pagamento realizado via Pix', 1),
('Cartao', 50.00, 0.00, 'Pagamento em processamento', 2);

INSERT INTO avaliacao (id_cliente, id_atendimento, data, nota, comentarios) VALUES
(1, 1, '2026-03-28', 5, 'Excelente atendimento'),
(2, 2, '2026-03-29', 4, 'Servico muito bom');