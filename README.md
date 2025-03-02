# Gerador de Senhas Seguras

Este é um gerador de senhas seguras baseado em Java, que permite a criação de senhas fortes e criptografadas. O aplicativo oferece opções personalizáveis como o comprimento da senha, a inclusão de caracteres especiais, números e a exclusão de caracteres semelhantes. Além disso, ele também criptografa e salva as senhas geradas em um arquivo.

## Funcionalidades

- Geração de senhas seguras com diferentes critérios de personalização (comprimento, números, caracteres especiais).
- Opção para excluir caracteres semelhantes (como "O" e "0", "I" e "1").
- Exibição da força da senha gerada com uma barra de progresso.
- Criptografia AES das senhas geradas.
- Salvamento das senhas criptografadas em um arquivo `senhas_encriptadas.txt`.
- Interface gráfica amigável utilizando o **Swing**.

## Tecnologias Usadas

- **Java 8+**
- **Swing** para a interface gráfica.
- **Criptografia AES** (Java Cryptography Extension - JCE).

## Pré-requisitos

Para executar o projeto, você precisa ter o **Java 8 ou superior** instalado em sua máquina.

- [Download do Java JDK](https://www.oracle.com/java/technologies/javase-jdk8-downloads.html)
