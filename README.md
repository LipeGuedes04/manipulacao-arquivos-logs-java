# 🛡️ Analisador Automatizado de Logs e Auditoria SRE - LogShield Matrix

## 🎯 Escopo do Sistema

O sistema simula um motor digital de infraestrutura (LogShield) desenvolvido para automação de tarefas de auditoria, segurança defensiva e Engenharia de Confiabilidade de Sites (SRE). Rompendo com processos manuais de varredura textual, a aplicação implementa o conceito avançado de **Manipulação de Streams de Entrada/Saída (Java I/O)** acoplado ao gerenciamento seguro de recursos do sistema operacional. O operador aponta para buffers físicos de armazenamento de infraestrutura (`servidor_log.txt`), processa a leitura sequencial assíncrona orientada a tokens, isola incidentes de severidade crítica por meio de filtragem de strings de exceção (`[ERROR]`) e consolida uma trilha de auditoria contínua (*Append Matrix*) em um repositório central de logs (`relatorio_erros.txt`). O motor é blindado contra vazamentos de memória e falhas de runtime físicas de leitura de disco hardware.

### 🔄 Fluxo de Processamento e Pipeline de Dados
```text
[ Servidor Core ] ➔ [ servidor_log.txt ] ➔ [ Leitura Dinâmica (Scanner) ] ➔ [ Intercepção: [ERROR] ]
                                                                                   │
                                                                                   ▼
[ relatorio_erros.txt ] ◄── [ Escrita em Lote (BufferedWriter) ] ◄── [ Modo Append (True) ]
```

---

## 🎨 Arquitetura do Software (Java I/O Stack & Resource Management)

O design deste script demonstra alto nível de engenharia e maturidade ao isolar a alocação de pontes nativas do sistema operacional por meio do padrão **Try-with-Resources**, evitando sobrecarga e gargalos de processamento de hardware (RAM/CPU).

```text
========================================================================================
  DIAGRAMA DE ARQUITETURA DE STREAMS E BUFFERING (I/O RAM)
========================================================================================

    [ DISCO RÍGIDO / SSD ]
              │
              ├─── Caminho Origem: servidor_log.txt
              │         │
              │         ▼ (Canal de Entrada Nativo)
              │   ┌───────────────────────────┐
              │   │      Scanner (Leitor)     │ ──► Varredura linha por linha (.nextLine)
              │   └───────────────────────────┘
              │
              └─── Caminho Destino: relatorio_erros.txt
                        ▲
                        │ (Canal de Saída Otimizado com Cache em RAM)
                  ┌───────────────────────────┐
                  │      BufferedWriter       │ ──► Economia de escritas mecânicas no disco
                  └─────────────▲─────────────┘
                                │
                  ┌───────────────────────────┐
                  │        FileWriter         │ ──► Parâmetro: Append = true (Preserva Histórico)
                  └───────────────────────────┘
```

### 📋 Mapeamento de Componentes e Responsabilidades

| Componente Técnico | Tipo de Recurso | Atuação e Responsabilidade no Sistema |
| :--- | :--- | :--- |
| 🚀 `Main.java` | Orquestrador | Gerencia os pontos de montagem dos caminhos, valida a árvore de diretórios raiz e starta o pipeline. |
| 📁 `File.separator` | Portabilidade | Garante a neutralidade do software, adaptando as barras de caminhos dinamicamente para Windows (`\`) ou Linux/Unix (`/`). |
| 🔍 `Scanner` | Input Stream | Realiza a leitura sequencial orientada a caracteres do arquivo físico de origem de forma eficiente sem estourar a memória. |
| ✒️ `FileWriter` | Output Stream | Abre o canal de comunicação física para gravação, configurado em modo cumulativo (`append: true`) para evitar perda de dados anteriores. |
| ⚡ `BufferedWriter` | Buffer Stream | Cria uma camada de cache em memória RAM intermediária, descarregando os dados coletados em lote para reduzir ciclos de escrita no disco. |
| 🛡️ `IOException` | Guard | Cláusula de escape interceptadora que captura falhas críticas de hardware (como disco cheio, arquivos bloqueados ou sem permissão). |

---

## 🧠 Conceitos Avançados Praticados

* **Padrão de Gerenciamento Automatizado de Recursos (Try-With-Resources):** Declaração de objetos de fluxo de dados diretamente no escopo do `try`, forçando o Java a executar a desalocação mecânica e o fechamento automático dos canais ao fim da execução, prevenindo *Memory Leaks*.
* **Arquitetura Histórica Cumulativa (*Append Trail*):** Uso estratégico de sinalizadores lógicos booleanos no construtor de saída para converter uma rotina comum de sobrescrita de dados em um banco de registros consolidados imutáveis.
* **Validação Robusta de Diretórios Primários (`mkdirs`):** Implementação de checagem defensiva de infraestrutura que analisa se a árvore de pastas pai existe em disco e, caso ausente, realiza a criação em cascata automatizada para mitigar erros de execução.
* **Otimização de Performance por Cache Flutuante (Buffering):** Redução drástica do overhead de operações de entrada e saída (I/O) do processador através do barramento de retenção temporária em memória RAM antes do commit de escrita física.

---

## 💻 Demonstração Operacional (Painel de Auditoria)

Veja a simulação analítica do comportamento do console processando logs reais e o reflexo das saídas consolidadas no arquivo final de auditoria:

### ⚙️ Massa de Dados de Entrada (`servidor_log.txt`):
```text
[2026-11-09 10:15:30] [INFO] Servidor central inicializado na porta 8080 com sucesso.
[2026-11-09 10:16:02] [DEBUG] Conexão estabelecida com o pool de banco de dados.
[2026-11-09 10:17:45] [ERROR] Falha crítica de conexão: Banco de dados relacional inacessível (Timeout).
[2026-11-09 10:19:12] [INFO] Requisição GET /api/v1/checkout processada em 45ms.
[2026-11-09 10:22:11] [ERROR] Autenticação rejeitada: Assinatura de Token JWT expirada ou corrompida.
```

### 🖥️ Saída de Execução no Terminal:
```text
Iniciando a filtragem de logs...
Análise concluída! Verifique o arquivo 'relatorio_erros.txt'.
```

### 📄 Arquivo de Auditoria Gerado (`relatorio_erros.txt`):
```text
--- NOVA ANÁLISE DE LOGS EM: 2026-11-09 ---
[2026-11-09 10:17:45] [ERROR] Falha crítica de conexão: Banco de dados relacional inacessível (Timeout).
[2026-11-09 10:22:11] [ERROR] Autenticação rejeitada: Assinatura de Token JWT expirada ou corrompida.

