-- This file allow to write SQL commands that will be emitted in test and dev.
-- The commands are commented as their support depends of the database
-- insert into myentity (id, field) values(1, 'field-1');
-- insert into myentity (id, field) values(2, 'field-2');
-- insert into myentity (id, field) values(3, 'field-3');
-- alter sequence myentity_seq restart with 4;

-- Inserção de usuários
INSERT INTO Usuario (email, nomeCompleto, senha) VALUES
-- Senha: 123456
  ('maria@gmail.com', 'Maria de Sá', 'noR4QMFC8v7kWQVV/DHj4AD55JqqSNHWrxFw8J4ZczMI6F8C3Y4PueT8ffLurzwQ3yO9jRhKdhmCsL+CbUzzGA=='),
-- Senha: 123456
  ('joao@gmail.com', 'João Bobo', 'noR4QMFC8v7kWQVV/DHj4AD55JqqSNHWrxFw8J4ZczMI6F8C3Y4PueT8ffLurzwQ3yO9jRhKdhmCsL+CbUzzGA==');
