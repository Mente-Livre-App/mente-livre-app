# 🧠 Mente Livre – Plataforma de Apoio Psicológico

## 🔍 Visão Geral

O **Mente Livre** é um aplicativo Android desenvolvido em Kotlin com Jetpack Compose. Ele conecta pessoas em situação de vulnerabilidade emocional a profissionais e voluntários da saúde mental. A plataforma oferece chat anônimo, agendamento de consultas, feed de apoio emocional e gestão de disponibilidade.

---

## 👥 Público-Alvo

- Pessoas em vulnerabilidade social e emocional;
- Adolescentes e jovens em crise emocional;
- Profissionais com burnout;
- Familiares e cuidadores de pessoas com transtornos mentais;
- Sobreviventes de violência física e psicológica;
- Moradores de regiões com pouco acesso à saúde mental.

---

## 🛠 Tecnologias Utilizadas

- **Kotlin** + **Jetpack Compose (Material 3)**
- **Arquitetura MVVM**
- **Firebase Auth** e **Cloud Firestore**
- **Kotlin Coroutines** + **StateFlow / Flow**
- **Navigation Compose**

---

## 🧩 Estrutura do Projeto

```
com.example.safelife
├── ui                  → Telas em Compose
├── viewModel           → Estados e lógicas de interface
├── repository          → Integrações com Firebase
├── model               → Entidades como Post, Message, Agendamento
├── preferences         → Armazenamento local (LGPD)
```

---

## 🚀 Funcionalidades Principais

### ✅ Autenticação
- Login, cadastro e redefinição de senha
- Identificação de tipo de conta (paciente ou profissional)

### 📆 Agendamento de Consultas
- Marcação de consultas com profissionais disponíveis
- Status de confirmação (pendente / confirmado)

### 📋 Gestão de Disponibilidade
- Profissionais configuram seus horários por dia da semana
- Armazenamento em Firestore na coleção `disponibilidade`

### 💬 Chat em Tempo Real
- Chat individual paciente ↔ profissional com Firestore + Flow
- Suporte a anonimato e múltiplos chats simultâneos

### 🧵 Feed Social
- Postagens públicas com comentários e curtidas
- Atualizações em tempo real por snapshot listeners

### 🕵️ Atendimento Anônimo
- Canal seguro onde usuários podem receber escuta e orientação sem se identificar

---

## 🔄 Fluxo de Navegação

- `login` → `signup` → `home`
- `home` → `lista_profissionais` → `chat`
- `home` → `lista_pacientes` → `chat_profissional`
- `home` → `agendamento` ou `agendaProfissional`
- `home` → `feed` → `nova_postagem` → `post_detail`

---

## 🔗 Integração com Firebase

| Coleção         | Finalidade                                 |
|-----------------|---------------------------------------------|
| `usuarios`      | Dados de perfil e tipo de conta             |
| `agendamentos`  | Controle de consultas e status              |
| `disponibilidade` | Horários disponíveis de profissionais     |
| `posts`         | Publicações no feed                         |
| `comments`      | Comentários por post                        |
| `chats`         | Identificação de conversas                  |
| `messages`      | Mensagens de cada chat                      |

---

## 🧪 Processo de Desenvolvimento

1. Planejamento e levantamento de requisitos
2. Modelagem de entidades: usuário, agendamento, post, mensagem
3. Implementação modular (auth, agendamento, chat, feed)
4. Uso do GitHub com branches temáticas
5. Testes manuais e em emulador físico
6. Boas práticas com ViewModel, separação de responsabilidades e corrotinas

---

## 👨‍💻 Equipe

Projeto acadêmico desenvolvido por equipe multidisciplinar com suporte de psicóloga orientadora para validação funcional e ética.
