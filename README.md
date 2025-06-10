# 🧠 SafeLife

**SafeLife** é um aplicativo de saúde mental que conecta pacientes e profissionais, promovendo bem-estar através de consultas, fóruns, bate-papo e agendamentos intuitivos.

---

## 📱 Funcionalidades

- 👤 Cadastro e login de pacientes e profissionais
- 📅 Agendamento de consultas com profissionais
- 💬 Chat em tempo real entre paciente e terapeuta
- 📰 Feed de postagens e comentários (estilo fórum)
- 🔐 Integração com Firebase Authentication & Firestore
- 🎨 Interface moderna com Jetpack Compose
- 📆 Agenda profissional com horários personalizáveis

---

## 🧰 Tecnologias utilizadas

- **Kotlin** com **Jetpack Compose**
- **Firebase** (Auth, Firestore)
- **MVVM Architecture**
- **Coroutines + StateFlow**
- **Android Studio (KTS + Gradle)**

---

## 🏗️ Estrutura do Projeto

```
app/
├── ui/                  # Telas Compose
├── viewModel/           # ViewModels MVVM
├── model/               # Modelos de dados
├── repository/          # Camada de dados (Firestore)
└── MainActivity.kt      # Navegação e entrada
```

---

## 🚀 Como rodar o projeto

1. Clone o repositório:
   ```bash
   git clone https://github.com/Mente-Livre-App/mente-livre-app.git
   ```

2. Abra no Android Studio.

3. Conecte com seu projeto Firebase e configure o `google-services.json`.

4. Sincronize o Gradle e execute o app.

---

## 🔒 Conformidade com a LGPD

O app inclui:
- Consentimento explícito do usuário
- Política de privacidade clara
- Opção de exclusão de dados

---

## 📢 Contribuições

Contribuições são bem-vindas! Crie uma issue ou envie um pull request. 🙌

---

## 📄 Licença

Este projeto está licenciado sob a **MIT License**. Consulte o arquivo `LICENSE` para mais detalhes.

---
