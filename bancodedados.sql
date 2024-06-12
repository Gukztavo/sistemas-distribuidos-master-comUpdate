-- --------------------------------------------------------
-- Servidor:                     127.0.0.1
-- Versão do servidor:           8.0.30 - MySQL Community Server - GPL
-- OS do Servidor:               Win64
-- HeidiSQL Versão:              12.1.0.6537
-- --------------------------------------------------------

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET NAMES utf8 */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

-- Copiando estrutura para tabela sistemas_distribuidos.competencia_experiencia
CREATE TABLE IF NOT EXISTS `competencia_experiencia` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `competencia` varchar(100) NOT NULL,
  `experiencia` int NOT NULL,
  `emailCandidato` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_competencia_experiencia_pessoa` (`emailCandidato`),
  CONSTRAINT `FK_competencia_experiencia_pessoa` FOREIGN KEY (`emailCandidato`) REFERENCES `pessoa` (`email`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=21 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Copiando dados para a tabela sistemas_distribuidos.competencia_experiencia: ~15 rows (aproximadamente)
INSERT INTO `competencia_experiencia` (`id`, `competencia`, `experiencia`, `emailCandidato`) VALUES
	(6, 'Python', 5, 'marcosartemio221@gmail.com'),
	(7, 'JS', 1, 'marcosartemio221@gmail.com'),
	(8, 'PHP', 1, 'marcosartemio221@gmail.com'),
	(9, 'Swift', 10, 'g@hotmail.com'),
	(10, 'Python', 5, 'teste@teste.com'),
	(11, 'C#', 1, 'teste@teste.com'),
	(12, 'Python', 5, 'candi@gmail.com'),
	(13, 'CSS', 7, 'candi@gmail.com'),
	(14, 'PHP', 8, 'candi@gmail.com'),
	(15, 'Python', 4, 'candi@gmail.com'),
	(16, 'CSS', 6, 'candi@gmail.com'),
	(17, 'PHP', 9, 'candi@gmail.com'),
	(18, 'C++', 6, 'duda@email.com'),
	(19, 'Swift', 64, 'duda@email.com'),
	(20, 'Python', 644, 'duda@email.com');

-- Copiando estrutura para tabela sistemas_distribuidos.empresa
CREATE TABLE IF NOT EXISTS `empresa` (
  `id` char(36) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT 'AUTO_INCREMENT',
  `cnpj` varchar(14) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `email` varchar(50) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `senha` varchar(50) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `descricao` varchar(100) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `ramo` varchar(100) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `razaoSocial` varchar(50) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `email` (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Copiando dados para a tabela sistemas_distribuidos.empresa: ~4 rows (aproximadamente)
INSERT INTO `empresa` (`id`, `cnpj`, `email`, `senha`, `descricao`, `ramo`, `razaoSocial`) VALUES
	('07fcb537-baad-41b3-8435-946fd76e4d70', '10293847561234', 'muriloo@gmail.com', '12345', 'dksokdsods', 'ramo', 'muriloempre2'),
	('2f204bee-18f8-4f71-89db-40729fa09dc0', '12345678910111', 'teste@teste.com', '123456', 'Teste', 'Teste', 'Teste'),
	('448c861e-6a56-4067-9495-c5a4845e078b', '12345678901234', 'muriloempre@gmail.com', '123456', 'kdkdkd', 'tech', 'murilo'),
	('ae28835a-fe91-435d-8600-6040f81fe29a', '12345678902569', 'g@hotmail.com', '123456', 'sdfsfsffs', 'teste', 'testedois'),
	('ced67536-90b8-4442-aa87-51910145167f', '83838383838383', 'muriloempresa@gmail.com', '123456', 'dksodkos', 'techzz', 'murilososo'),
	('e00fdfbd-0f32-4e8a-a6a5-497b0f7232e9', '12312312312312', 'muriloempre2@gmail.com', '123456', 'fkdofkofd', 'tech', 'murilo');

-- Copiando estrutura para tabela sistemas_distribuidos.pessoa
CREATE TABLE IF NOT EXISTS `pessoa` (
  `id` char(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `nome` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  `senha` varchar(50) DEFAULT NULL,
  `email` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `email` (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Copiando dados para a tabela sistemas_distribuidos.pessoa: ~5 rows (aproximadamente)
INSERT INTO `pessoa` (`id`, `nome`, `senha`, `email`) VALUES
	('7d1d04a0-bf52-44db-9370-5a3a7b00cf54', 'candi@gmail.com', '12345', 'candi@gmail.com'),
	('b38e3889-f7a3-4a15-bd47-dda15c59218b', 'franciele', '123456', 'f@hotmail.com'),
	('dde037b6-97c9-47c8-87d0-84d549100a90', 'gustavo', '123456', 'g@hotmail.com'),
	('e16ca6dc-6bed-43ee-a4d3-7c168976733f', 'duda123', '123456', 'duda@email.com'),
	('f258fc53-c628-4c7e-adc8-b32c60489cb5', 'Marcos Arte', '123456', 'teste@teste.com'),
	('f7c3b5ba-5354-404b-a403-57760497d17b', 'Marcos Arte', '84627913', 'marcosartemio221@gmail.com');

-- Copiando estrutura para tabela sistemas_distribuidos.vaga
CREATE TABLE IF NOT EXISTS `vaga` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `email` varchar(50) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `nome` varchar(100) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `faixaSalarial` double DEFAULT NULL,
  `descricao` varchar(100) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `estado` varchar(100) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `emailEmpresa` (`email`) USING BTREE,
  CONSTRAINT `FK_vaga_empresa` FOREIGN KEY (`email`) REFERENCES `empresa` (`email`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Copiando dados para a tabela sistemas_distribuidos.vaga: ~6 rows (aproximadamente)
INSERT INTO `vaga` (`id`, `email`, `nome`, `faixaSalarial`, `descricao`, `estado`) VALUES
	(1, 'g@hotmail.com', 'Teste', 2000, 'teste', 'Disponivel'),
	(2, NULL, 'TEste', 1234, 'Teste', 'Dispon�vel'),
	(3, NULL, 'vaga2543', 12345, 'kdkdkdkd', 'divulgavel'),
	(4, 'muriloempre@gmail.com', 'gustavo', 21345, 'ksksksks', 'divulgavel'),
	(5, 'muriloempre2@gmail.com', 'matheus', 12345, 'dksaods', 'divulgavel'),
	(7, 'muriloempresa@gmail.com', 'gustavoematheus', 48394893, 'dksokds', 'divulgavel');

-- Copiando estrutura para tabela sistemas_distribuidos.vaga_competencia
CREATE TABLE IF NOT EXISTS `vaga_competencia` (
  `competencia` varchar(50) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `vaga_id` bigint NOT NULL,
  `id` bigint NOT NULL AUTO_INCREMENT,
  PRIMARY KEY (`id`) USING BTREE,
  KEY `FK_vaga_competencia_vaga` (`vaga_id`),
  CONSTRAINT `FK_vaga_competencia_vaga` FOREIGN KEY (`vaga_id`) REFERENCES `vaga` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=19 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Copiando dados para a tabela sistemas_distribuidos.vaga_competencia: ~12 rows (aproximadamente)
INSERT INTO `vaga_competencia` (`competencia`, `vaga_id`, `id`) VALUES
	('Python', 3, 3),
	('CSS', 3, 4),
	('PHP', 3, 5),
	('Python', 4, 6),
	('Python', 5, 7),
	('CSS', 5, 8),
	('PHP', 5, 9),
	('Python', 7, 12),
	('CSS', 7, 13),
	('PHP', 7, 14),
	('Go', 7, 15),
	('Kotlin', 7, 16);

/*!40103 SET TIME_ZONE=IFNULL(@OLD_TIME_ZONE, 'system') */;
/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IFNULL(@OLD_FOREIGN_KEY_CHECKS, 1) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40111 SET SQL_NOTES=IFNULL(@OLD_SQL_NOTES, 1) */;
