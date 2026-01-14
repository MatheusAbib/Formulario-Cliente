-- phpMyAdmin SQL Dump
-- version 5.1.3
-- https://www.phpmyadmin.net/
--
-- Host: localhost
-- Tempo de geração: 27-Nov-2024 às 23:53
-- Versão do servidor: 5.7.36
-- versão do PHP: 8.1.3

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Banco de dados: `correios`
--

-- --------------------------------------------------------

--
-- Estrutura da tabela `cliente`
--

CREATE TABLE `cliente` (
  `id` bigint(20) NOT NULL,
  `nome` varchar(255) DEFAULT NULL,
  `email` varchar(255) DEFAULT NULL,
  `senha` varchar(255) DEFAULT NULL,
  `endereco_entrega` varchar(255) DEFAULT NULL,
  `descricao_endereco` varchar(255) DEFAULT NULL,
  `codigo_seguranca` varchar(255) DEFAULT NULL,
  `numero_cartao` varchar(255) DEFAULT NULL,
  `data_alteracao` datetime DEFAULT NULL,
  `data_cadastro` datetime DEFAULT NULL,
  `data_validade` varchar(255) DEFAULT NULL,
  `endereco_id` bigint(20) DEFAULT NULL,
  `bairro` varchar(255) DEFAULT NULL,
  `cep` varchar(255) DEFAULT NULL,
  `cidade` varchar(255) DEFAULT NULL,
  `estado` varchar(255) DEFAULT NULL,
  `logradouro` varchar(255) DEFAULT NULL,
  `numero` varchar(255) DEFAULT NULL,
  `pais` varchar(255) DEFAULT NULL,
  `tipo_residencia` varchar(255) DEFAULT NULL,
  `nome_cartao` varchar(255) DEFAULT NULL,
  `bandeira` varchar(255) DEFAULT NULL,
  `genero` varchar(255) DEFAULT NULL,
  `data_nascimento` varchar(255) DEFAULT NULL,
  `cpf` varchar(255) DEFAULT NULL,
  `telefone` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=COMPACT;

--
-- Extraindo dados da tabela `cliente`
--

INSERT INTO `cliente` (`id`, `nome`, `email`, `senha`, `endereco_entrega`, `descricao_endereco`, `codigo_seguranca`, `numero_cartao`, `data_alteracao`, `data_cadastro`, `data_validade`, `endereco_id`, `bairro`, `cep`, `cidade`, `estado`, `logradouro`, `numero`, `pais`, `tipo_residencia`, `nome_cartao`, `bandeira`, `genero`, `data_nascimento`, `cpf`, `telefone`) VALUES
(11, 'Matheus', 'matheus@gmail.com', 'Math20092004#', 'Rodeio', 'Condominio', '1234', '123356', '2024-11-27 20:47:01.874230', '2024-11-27 20:46:28.048588', NULL, NULL, 'Rodeio', '12345', 'São paulo', 'São paulo', 'Rua oliveira', '1122', 'Brasil', 'casa', 'Matheus Abib', 'Bando do Brasil', 'masculino', '2024-11-01', '222.222.222-2', '(22) 22222-2222');

--
-- Índices para tabelas despejadas
--

--
-- Índices para tabela `cliente`
--
ALTER TABLE `cliente`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `UK7v21uy9djyl7hh9464kkjsjg0` (`endereco_id`);

--
-- AUTO_INCREMENT de tabelas despejadas
--

--
-- AUTO_INCREMENT de tabela `cliente`
--
ALTER TABLE `cliente`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=12;

--
-- Restrições para despejos de tabelas
--

--
-- Limitadores para a tabela `cliente`
--
-- ALTER TABLE `cliente`
--   ADD CONSTRAINT `FK64nr9yt889by5lufr1boo5i4s` FOREIGN KEY (`endereco_id`) REFERENCES `endereco` (`id`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
