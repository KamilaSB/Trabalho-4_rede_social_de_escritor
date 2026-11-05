# 📚 Rede Social de Escritor - Trabalho 4 (Arquitetura MVVM)

Este repositório contém a versão refatorada e organizada da aplicação **Rede Social de Escritor**, desenvolvida para Android utilizando **Kotlin**, **Jetpack Compose** e **Room Database**, agora estruturada de acordo com o padrão arquitetural **MVVM (Model-View-ViewModel)** recomendado pelo Android Jetpack.

---

## 🏛️ Documentação das Decisões de Arquitetura

### 1. Como estava o projeto no Trabalho 3, e o que mudou na reorganização?
- **Trabalho 3:** Todo o código da interface visual (`@Composable`) e o acesso ao banco de dados Room estavam acoplados. As telas buscavam dados diretamente via `AppDatabase.getInstance(context)` e chamavam os DAOs manualmente dentro de blocos `LaunchedEffect` ou de eventos de clique dos botões.
- **Trabalho 4:** Toda a aplicação foi refatorada para a arquitetura MVVM, dividindo o código em 4 pacotes distintos: `ui`, `data.local`, `data.repository` e `viewmodel`. As telas agora são puramente declarativas, sem nenhum conhecimento de banco de dados, corrotinas de I/O ou DAOs.

---

### 2. Divisão dos Pacotes e Responsabilidades
A equipe organizou o código nos seguintes 4 pacotes principais:

- **`ui`**: Contém as telas da interface gráfica e navegação (`FeedScreen`, `ExplorarScreen`, `UsuarioScreen`, `EditProfileScreen`, `CreateStoryScreen`, `ChatListScreen`, `ChatDetailScreen` e `MainActivity`). As telas apenas coletam o estado exposicionado pelo ViewModel (`collectAsState()`) e chamam métodos do ViewModel em resposta a ações do usuário.
- **`data.local`**: Camada de persistência local contendo as entidades Room (`Texto`, `Usuario`, `Comentario`), os DAOs (`TextoDao`, `UsuarioDao`, `FeedDao`, `ChatDao`) e o banco de dados `AppDatabase.kt`.
- **`data.repository`**: Camada de repositório (`UsuarioRepository`, `TextoRepository`, `ChatRepository`). É a **única fonte de verdade** que se comunica diretamente com os DAOs e abstrai o acesso aos dados para os ViewModels.
- **`viewmodel`**: Camada de lógica de apresentação contendo os ViewModels, suas respetivas `data class ...UiState` e `ViewModelFactory`s.

---

### 3. Por que o Repository existe e o que ele resolve?
O **Repository** foi introduzido como um intermediário entre a fonte de dados (Room DAO) e a lógica de apresentação (ViewModel).
- **Abstração da Fonte de Dados:** Se no futuro a aplicação passar a buscar dados de uma API REST na nuvem ou Firebase além do Room local, **apenas a classe Repository precisará ser alterada**. As telas e os ViewModels continuarão funcionando sem nenhuma modificação.
- **Centralização:** Evita duplicação de chamadas aos DAOs e garante uma única fonte da verdade para o estado dos dados da aplicação.

---

### 4. Estrutura dos `UiState`s de cada tela

Cada tela possui uma `data class ...UiState` imutável que agrupa todo o estado necessário para renderizar a interface:

- **`FeedUiState`**:
  - `posts: List<Texto>`: Lista de publicações a serem exibidas no feed.
  - `isLoading: Boolean`: Estado indicando se a busca inicial está em andamento.
- **`PostDetailUiState`**:
  - `texto: Texto?`: Obra atualmente aberta.
  - `comentarios: List<Comentario>`: Lista de comentários reativos da obra.
  - `novoComentario: String`: Conteúdo digitado no campo para enviar um novo comentário.
  - `isLoading: Boolean`: Estado de carregamento da obra.
- **`CreateStoryUiState`**:
  - `title: String`, `description: String`, `storyText: String`: Campos do formulário de criação/edição.
  - `isEditing: Boolean`: Define se a tela está em modo de edição de uma obra existente ou criação de uma nova.
  - `textoId: Int`: ID da obra (se em modo de edição).
  - `isLoading: Boolean`: Estado de carregamento ao buscar obra existente.
- **`ExplorarUiState`**:
  - `searchText: String`: Texto digitado na barra de pesquisa.
  - `searchResults: List<Texto>`: Lista de obras encontradas em tempo real via `Flow`.
  - `isLoading: Boolean`: Estado de carregamento da pesquisa.
- **`UsuarioUiState`**:
  - `usuario: Usuario?`: Informações do perfil do autor.
  - `textos: List<Texto>`: Lista de obras publicadas por este autor.
  - `isLoading: Boolean`: Indicador de carregamento.
- **`EditProfileUiState`**:
  - `usuario: Usuario?`: Dados originais do usuário.
  - `name: String`, `bio: String`, `email: String`: Campos de formulário para edição.
  - `isLoading: Boolean`: Indicador de carregamento.
- **`ChatUiState`**:
  - `chats: List<ChatSummary>`: Lista de conversas.
  - `currentChatName: String`: Nome do contato na conversa aberta.
  - `messages: List<Message>`: Histórico de mensagens da conversa.
  - `isLoading: Boolean`: Indicador de carregamento.

---

### 5. Desafios Encontrados e Como Foram Resolvidos

- **Desafio no DAO Reativo com `Flow`**:
  Ao migrar as consultas do Room para retornarem `Flow<List<...>>` (conforme exigido na Seção 3.3), as buscas em tempo real (como a tela de busca `ExplorarScreen` ou a lista de comentários `PostDetailScreen`) precisavam cancelar a coleta do `Flow` anterior ao mudar os parâmetros.
  - **Solução:** No `ExplorarViewModel` e `PostDetailViewModel`, foi utilizado um controle de `Job` (ex: `searchJob?.cancel()`), garantindo que novas pesquisas cancelem corrotinas ativas e atualizem a UI somente com o estado mais recente.

- **Dificuldade na Injeção com `ViewModelFactory`**:
  Como o Jetpack Compose por padrão instancia ViewModels sem parâmetros (`viewModel()`), criar ViewModels que recebem o `Repository` no construtor exigia uma solução de injeção manual sem bibliotecas externas (como Hilt).
  - **Solução:** Foi criada uma `ViewModelFactory` customizada para cada ViewModel implementando `ViewModelProvider.Factory`. Na `MainActivity.kt`, os repositórios são instanciados e repassados para a factory na criação do ViewModel (`viewModel(factory = FeedViewModelFactory(textoRepo, usuarioRepo))`).

---

### 6. Integridade das Funcionalidades
Nenhuma funcionalidade existente no Trabalho 3 foi quebrada ou alterada. Todas as telas (Feed, Perfil, Editar Perfil, Criar/Editar Obra, Excluir Obra, Correspondência/Chat e Comentários no Detalhe da Postagem) permanecem funcionais, porém agora operam com reatividade automática do Room via `Flow` e arquitetura MVVM limpa.

---

## 🛠️ Configurações e Pré-requisitos do Projeto

- **Linguagem:** Kotlin
- **UI Framework:** Jetpack Compose
- **Persistência:** Room Database (com suporte a Kotlin Flow)
- **Arquitetura:** MVVM (Model-View-ViewModel + Repository Pattern)
- **Min SDK:** 24 | **Target SDK:** 35
