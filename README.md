# Sistema de Biblioteca de Filmes

Trabalho Prático de AEDS III – PUC Minas

## Integrantes

* Felipe Tadeu Silva
* Isabela Demaria Costa Braga
* Laura Rodrigues Portugal

---

## Descrição

O projeto consiste em um sistema de gerenciamento de biblioteca de filmes desenvolvido em Java.

O sistema implementa:

* CRUD de Filmes
* CRUD de Usuários
* CRUD de Empréstimos
* Persistência em arquivos binários
* Índice Direto
* Hash Extensível
* Relacionamento N:N entre Usuários e Filmes
* Consulta Ordenada
* Interface Gráfica com Java Swing
* Compressão utilizando Huffman
* Compressão utilizando LZW
* Geração de Backups Compactados

---

## Estrutura do Projeto

```text
TP3/
│
├── compressao/
├── controller/
├── dao/
├── model/
├── persistencia/
├── view/
├── dados/
└── Main.java
```

---

## Requisitos

* Java JDK 22 ou superior
* VS Code, IntelliJ IDEA ou Eclipse

---

## Compilação

### VS Code

1. Abra a pasta do projeto.
2. Aguarde o carregamento das extensões Java.
3. Execute o arquivo:

```text
Main.java
```

### IntelliJ IDEA

1. Abra o projeto.
2. Configure o SDK Java 22.
3. Execute:

```text
Main.java
```

---

## Execução

Ao iniciar o sistema será exibida uma interface gráfica contendo as abas:

* Filmes
* Usuários
* Empréstimos
* Backup

---

## Funcionalidades

### Filmes

* Cadastrar filme
* Consultar filme
* Alterar filme
* Excluir filme

### Usuários

* Cadastrar usuário
* Consultar usuário
* Alterar usuário
* Excluir usuário

### Empréstimos

* Registrar empréstimos
* Associar usuários e filmes
* Consultar relacionamentos N:N

### Backup

* Gerar Backup LZW
* Gerar Backup Huffman
* Visualizar estatísticas de compressão

---

## Persistência

Todos os dados são armazenados em arquivos binários localizados na pasta:

```text
dados/
```

O sistema utiliza:

* Registros de tamanho variável
* Lápide lógica
* Reaproveitamento de espaço livre
* Índices persistentes

---

## Compressão

Foram implementados os algoritmos:

### LZW

* Compressão
* Descompressão
* Backup compactado

### Huffman

* Construção da árvore
* Compressão
* Descompressão
* Backup compactado

---

## Execução dos Backups

Na aba **Backup**, selecione:

* Gerar Backup LZW
* Gerar Backup Huffman

Os arquivos gerados serão criados na raiz do projeto:

```text
backup_lzw.bin
backup_huffman.bin
```

---

## Disciplina

Algoritmos e Estruturas de Dados III (AED III)

Pontifícia Universidade Católica de Minas Gerais – PUC Minas
