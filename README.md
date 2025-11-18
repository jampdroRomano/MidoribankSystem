# 🚀 Projeto Midoribank

![Status](https://img.shields.io/badge/status-em%20desenvolvimento-yellow)
![Frontend](https://img.shields.io/badge/tecnologia-JavaFx-blue)
![Backend](https://img.shields.io/badge/tecnologia-Java-red)
![Banco](https://img.shields.io/badge/banco-MySql-green)



---

## 📖 Descrição do Projeto
**Midoribank** é um simulador de **operações bancaria**s de **Desktop** desenvolvido em **Java com JavaFX**, projetado para reproduzir as principais operações bancárias de forma prática e visual. O sistema permite ao usuário realizar login, saques, depósitos, transferências e consultas de saldo, tudo dentro de uma interface gráfica intuitiva e moderna

---

## 🔗 Links Importantes
- **Protótipo (Figma)**: [Acessar protótipo](https://www.figma.com/design/o684O0pI69p8i0iEg3pWfs/Untitled?node-id=182-6)
- **Documentação (Em produção)**: na pasta [`documentacao`](https://github.com/jampdroRomano/MidoribankSystem/blob/main/docs/Documenta%C3%A7%C3%A3o%20MidoriBank.pdf)

---

## 🗂 Estrutura do Repositório
```text
MidoribankSystem/
│
├─ data/
│  └─ midoribank_database.sql   # Schema e DDL do banco
│
├─ src/main/java/com/midoribank/atm/
│  ├─ controllers/            # Controladores JavaFX para as telas
│  ├─ dao/                    # Lógica de acesso ao banco (DAO)
│  ├─ models/                 # Modelos de dados (POJOs)
│  ├─ services/               # Lógica de negócio
│  └─ utils/                  # Utilitários (Cripto, Animação)
│
├─ src/main/resources/com/midoribank/atm/
│  ├─ [Pastas FXML]/          # Telas (Login, Home, etc.)
│  └─ config.properties.example # Molde para config. de email
│
└─ pom.xml                     # Dependências e configuração do Maven  

```

## ⚙ Funcionalidades Principais
| ID    | Funcionalidade           | Descrição                                                   |
|-------|--------------------------|-------------------------------------------------------------|
| RF01  | Autenticação             | Login com email e senhaAuth via MySql, a senha é verificada usando hash jBCrypt                  |
| RF02  | Cadastro Completo        | Fluxo de cadastro de usuário (Nome, Email, Senha), seguido pelo cadastro de cartão, e criação de PIN de 4 dígitos                |
| RF03  | Recuperação de Senha        | Implementa um fluxo de recuperação de senha. O usuário informa o email, recebe um código de 6 dígitos, valida o código e redefine a senha         |
| RF04  | Operação de Saque        | Permite ao usuário sacar dinheiro, validando o saldo disponível e exigindo a senha do cartão
| RF05  | Operação de Depósito     | Permite ao usuário depositar dinheiro em sua conta                                    |
| RF06  | Transferência      | UI para iniciar transferência para outra conta (em desenvolvimento)      |
| RF07  | Funções Futuras     | Telas de Extrato, Cartão de Crédito e Detalhes da Conta estão presentes, mas marcadas como "em desenvolvimento         |


---

## 🛠 Tecnologias Utilizadas
- **Core** Java 17
- **Interface Gráfica:** JavaFX 17
- **Banco de Dados:**  MySQL 8.0  
- **Segurança (Hash):**   jBCrypt
- **Envio de EmaiL:**  Jakarta Mail 
- **Gerenciamento de Dependências:**  Maven 
- **Ferramentas Auxiliares:**  Git, Figma.

---

## 🔑 Configuração de Email
Para utilizar a funcionalidade de Recuperação de Senha por Código, é necessário configurar suas credenciais de email.

- Crie um arquivo chamado ```config.properties``` dentro da pasta ```src/main/resources/.```
- Use o arquivo config.properties.example como molde.

- Você deve informar um email GMAIL ```(GMAIL_USER)``` e uma Senha de App ```(GMAIL_PASSWORD)``` gerada na sua conta Google. Senhas comuns não funcionarão 
---

## ⚠️ Pré-requisitos

- Java JDK  >= 17 
- Apache Maven  >= 3.x
- Servidor MySQL (recomendado 8.x) rodando na porta padrão (3306).
- JavaFX  >= 17

---

## 🚀 Instalação de Dependências

O projeto utiliza Maven para gerenciar as dependências. Elas serão baixadas automaticamente. Para baixar manualmente, execute:

```
mvn clean install
```

---

## 💻 Como Rodar o Projeto

- Execute o script ```data/midoribank_database.sql``` apos criar o Schema midoribank no seu servidor MySQL.
- Credenciais do Banco: Verifique se as credenciais do banco em ```src/main/java/com/midoribank/atm/dao/ConnectionFactory.java``` (usuário: root, senha: 1234) batem com as do seu servidor MySQL.
- Configuração de Email: Siga os passos da seção 🔑 Configuração de Email.
- Executar: Utilize o plugin do Maven para JavaFX:
- Compilar em uma IDE (Intellij, NetBens, VsCode...)
```
mvn clean javafx:run
```

---

## 📂 Documentação
Toda a documentação técnica e de banco de dados está no próprio código e no arquivo de schema:
- Schema do Banco: ```data/midoribank_database.sql```
- Lógica de Negócio: ```src/main/java/com/midoribank/atm/services/```
- Controladores das Telas: ```src/main/java/com/midoribank/atm/controllers/```
