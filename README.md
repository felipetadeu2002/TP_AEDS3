# Sistema de Biblioteca de Filmes

Trabalho Prático de Algoritmos e Estruturas de Dados III (AED III) – PUC Minas

---

# Integrantes

* Felipe Tadeu Silva
* Isabela Demaria Costa Braga
* Laura Rodrigues Portugal

---

# Descrição

O projeto consiste em um sistema de gerenciamento de biblioteca de filmes desenvolvido em Java.

O sistema implementa:

* CRUD de Filmes
* CRUD de Usuários
* CRUD de Empréstimos
* Persistência em arquivos binários
* Índice Direto
* Hash Extensível
* Árvore B+
* Relacionamento N:N entre Usuários e Filmes
* Consulta Ordenada
* Interface Gráfica com Java Swing
* Compressão utilizando Huffman
* Compressão utilizando LZW
* Casamento de Padrões utilizando KMP
* Casamento de Padrões utilizando Boyer-Moore
* Criptografia XOR
* Geração de Backups Compactados

---

# Estrutura do Projeto

```text
TP3/
│
├── busca/
│   ├── KMP.java
│   └── BoyerMoore.java
│
├── compressao/
│   ├── BackupInfo.java
│   ├── BackupManager.java
│   ├── Huffman.java
│   ├── HuffmanNode.java
│   ├── HuffmanResultado.java
│   └── LZW.java
│
├── seguranca/
│   └── XORCipher.java
│
├── controller/
├── dao/
├── model/
├── persistencia/
├── view/
├── dados/
│
└── Main.java
```

---

# Requisitos

* Java JDK 22 ou superior
* VS Code, IntelliJ IDEA ou Eclipse

---

# Compilação

## VS Code

1. Abra a pasta do projeto.
2. Aguarde o carregamento das extensões Java.
3. Execute o arquivo:

```text
Main.java
```

## IntelliJ IDEA

1. Abra o projeto.
2. Configure o SDK Java 22.
3. Execute:

```text
Main.java
```

---

# Execução

Ao iniciar o sistema será exibida uma interface gráfica contendo as abas:

* Filmes
* Usuários
* Empréstimos
* Backup

---

# Funcionalidades

## Filmes

* Cadastrar filme
* Consultar filme
* Atualizar filme
* Excluir filme
* Listagem ordenada utilizando Árvore B+
* Pesquisa por padrão utilizando KMP
* Pesquisa por padrão utilizando Boyer-Moore

## Usuários

* Cadastrar usuário
* Consultar usuário
* Atualizar usuário
* Excluir usuário
* Criptografia automática do CPF

## Empréstimos

* Registrar empréstimos
* Associar usuários e filmes
* Consultar relacionamentos N:N

## Backup

* Gerar Backup LZW
* Gerar Backup Huffman
* Visualizar estatísticas de compressão

---

# Persistência

Todos os dados são armazenados em arquivos binários localizados na pasta:

```text
dados/
```

O sistema utiliza:

* Registros de tamanho variável
* Lápide lógica
* Reaproveitamento de espaço livre
* Índices persistentes
* Hash Extensível
* Árvore B+

---

# Compressão

Foram implementados os algoritmos:

## LZW

* Compressão
* Descompressão
* Backup compactado

## Huffman

* Construção da árvore
* Compressão
* Descompressão
* Backup compactado

---

# Casamento de Padrões

Foram implementados os algoritmos:

## KMP (Knuth-Morris-Pratt)

* Busca eficiente de padrões em títulos de filmes
* Utilização do vetor LPS
* Integrado à interface gráfica

## Boyer-Moore

* Busca eficiente utilizando a heurística Bad Character
* Integrado à interface gráfica

## Utilização

Na aba Filmes:

1. Informe um padrão de busca.
2. Escolha o algoritmo:

   * KMP
   * Boyer-Moore
3. Clique em "Pesquisar".

Os filmes encontrados serão exibidos na tabela.

---

# Criptografia

Foi implementada criptografia XOR para proteção de dados sensíveis.

## Campo protegido

* CPF dos usuários

## Funcionamento

* O CPF é criptografado automaticamente antes de ser armazenado.
* O CPF é descriptografado automaticamente durante a leitura.
* O usuário não percebe o processo de criptografia.

Algoritmo utilizado:

* XOR (Exclusive OR)

---

# Execução dos Backups

Na aba **Backup**, selecione:

* Gerar Backup LZW
* Gerar Backup Huffman

Os arquivos gerados serão criados na raiz do projeto:

```text
backup_lzw.bin
backup_huffman.bin
```

---

# Tecnologias Utilizadas

* Java 22
* Java Swing
* Persistência em Arquivos Binários
* Hash Extensível
* Árvore B+
* LZW
* Huffman
* KMP
* Boyer-Moore
* XOR
* Git
* GitHub

---

# Disciplina

Algoritmos e Estruturas de Dados III (AEDS III)

Pontifícia Universidade Católica de Minas Gerais – PUC Minas
