# OpenCast

<div align="center">
  <img src="https://github.com/Arturo254/OpenTune/blob/master/fastlane/metadata/android/en-US/images/featureGraphic.png" alt="Banner do OpenCast" width="100%"/>

  ### Cliente avançado de YouTube Music com Material Design 3 para Android, agora com Google Cast, seletor de dispositivos e melhorias para Android Auto

  [![Licença](https://img.shields.io/github/license/Piettro-Freit/OpenCast?style=flat-square&logo=gnu&color=2B3137&labelColor=161B22)](LICENSE)
  [![Android](https://img.shields.io/badge/Plataforma-Android%208.0+-3DDC84.svg?style=flat-square&logo=android&logoColor=white&labelColor=161B22)](https://www.android.com)
  [![Estrelas](https://img.shields.io/github/stars/Piettro-Freit/OpenCast?style=flat-square&logo=github&color=yellow&labelColor=161B22&cacheSeconds=21600)](https://github.com/Piettro-Freit/OpenCast/stargazers)
  [![Forks](https://img.shields.io/github/forks/Piettro-Freit/OpenCast?style=flat-square&logo=github&color=blue&labelColor=161B22&cacheSeconds=21600)](https://github.com/Piettro-Freit/OpenCast/network/members)
</div>

> **OpenCast é um fork do [OpenTune](https://github.com/Arturo254/OpenTune)**, criado por Arturo Cervantes. Todo o crédito pelo aplicativo original é dele. Este fork adiciona as funcionalidades descritas em [Novidades do OpenCast](#novidades-do-opencast).

[![English](https://img.shields.io/badge/readme.md-english-blue?style=for-the-badge)](README.en.md)

---

## Tabela de Conteúdo

- [Visão Geral](#visão-geral)
- [Novidades do OpenCast](#novidades-do-opencast)
- [Stack Tecnológico](#stack-tecnológico)
- [Características Principais](#características-principais)
- [Documentação](#documentação)
- [Instalação](#instalação)
- [Compilação a partir do Código-Fonte](#compilação-a-partir-do-código-fonte)
- [Contribuições](#contribuições)
- [Apoie o Projeto](#apoie-o-projeto)
- [Reconhecimentos](#reconhecimentos)
- [Licença](#licença)

---

## Visão Geral

**OpenCast** é um cliente de YouTube Music de código aberto para dispositivos Android, baseado no OpenTune. Oferece uma interface moderna em Material Design 3 e recursos avançados para explorar, reproduzir e gerenciar música sem as limitações do aplicativo oficial. Este fork acrescenta transmissão para Chromecast, um seletor de dispositivos de saída no estilo "Conectar" e ajustes para Android Auto e assistente de voz.

### Principais Benefícios

- **Experiência sem anúncios**: música sem interrupções publicitárias
- **Desempenho aprimorado**: otimizado para reprodução e navegação fluidas
- **Foco na privacidade**: sem coleta de dados nem rastreamento
- **Interface personalizável**: adapte a experiência musical ao seu gosto
- **Modo offline**: baixe e ouça sem conexão com a internet
- **Transmissão e saídas de áudio**: Chromecast, Bluetooth, fones com fio e USB em um só lugar

> **Observação**: OpenCast é um projeto independente e não é afiliado, patrocinado nem endossado pelo YouTube ou pelo Google.

---

## Novidades do OpenCast

Funcionalidades adicionadas sobre o OpenTune 1.3.2.

<table>
<tr>
<th width="30%">Novidade</th>
<th width="70%">Descrição</th>
</tr>
<tr>
<td><strong>📺 Google Cast (Chromecast)</strong></td>
<td>Transmita a música para Chromecasts e TVs com Chromecast embutido. O player do celular continua controlando fila, letras e progresso, enquanto o som toca no dispositivo remoto. Usa o <em>Default Media Receiver</em> do Google, sem necessidade de cadastro.</td>
</tr>
<tr>
<td><strong>🔌 Folha "Conectar"</strong></td>
<td>Seletor de dispositivos em Material 3, no estilo Spotify Connect: mostra o dispositivo ativo com a música em reprodução e lista "Este telefone", saídas Bluetooth, fones com fio, áudio USB e dispositivos Cast encontrados na rede. Inclui atalho para as configurações de Bluetooth. Abre pelo ícone de celular com caixa de som no player e pelo menu de opções do player, então funciona em todos os designs.</td>
</tr>
<tr>
<td><strong>🔊 Volume do Cast pelos botões do celular</strong></td>
<td>Durante a transmissão, os botões físicos de volume controlam o volume do Chromecast. Mudanças feitas pela TV ou pelo diálogo do Google também são refletidas no celular.</td>
</tr>
<tr>
<td><strong>🚗 Android Auto aprimorado</strong></td>
<td>Ícone do OpenTune no cabeçalho do player e nome da playlist, álbum ou mix enviado como título da fila. Quando a música não tem álbum ou fila conhecidos, o cabeçalho usa o contexto da fila ou o artista. O que é exibido depende da versão do Android Auto.</td>
</tr>
<tr>
<td><strong>🎙️ Assistente de voz</strong></td>
<td>Pedidos como "toque <em>música</em> no OpenTune" no Google Assistente ou Gemini. A busca procura primeiro na biblioteca e, se não encontrar, pesquisa no YouTube Music. Funciona no carro (Android Auto) e no celular.</td>
</tr>
<tr>
<td><strong>🔊 Atalho de normalização de volume</strong></td>
<td>Botão no menu de opções do player para ligar ou desligar a normalização rapidamente, além da opção que já existe nas configurações.</td>
</tr>
<tr>
<td><strong>⚙️ Build automatizado</strong></td>
<td>Workflow do GitHub Actions que gera o APK universal de debug e de release.</td>
</tr>
</table>

### Limitações conhecidas

- O Cast só encontra dispositivos **Google Cast**. AirPlay e DLNA não são suportados.
- Enquanto transmite, o player local fica mudo, mas continua baixando o áudio, então o consumo de dados aumenta. Pode haver uma pausa de cerca de 1 a 2 segundos entre as músicas.
- A normalização de volume não atua no som do Chromecast.
- O celular e o Chromecast precisam estar na **mesma rede Wi‑Fi**.

---

## Stack Tecnológico

<div align="center">

| Frontend | Backend | Ferramentas de Desenvolvimento |
|:--------:|:-------:|:-----------------------------:|
| ![Kotlin](https://img.shields.io/badge/Kotlin-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white) | ![Media3](https://img.shields.io/badge/Media3-4285F4?style=for-the-badge&logo=android&logoColor=white) | ![Android Studio](https://img.shields.io/badge/Android%20Studio-3DDC84?style=for-the-badge&logo=androidstudio&logoColor=white) |
| ![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-4285F4?style=for-the-badge&logo=jetpackcompose&logoColor=white) | ![Google Cast](https://img.shields.io/badge/Google%20Cast-4285F4?style=for-the-badge&logo=googlecast&logoColor=white) | ![Gradle](https://img.shields.io/badge/Gradle-02303A?style=for-the-badge&logo=gradle&logoColor=white) |
| ![Material Design 3](https://img.shields.io/badge/Material%20Design%203-757575?style=for-the-badge&logo=materialdesign&logoColor=white) | | ![GitHub Actions](https://img.shields.io/badge/GitHub%20Actions-2088FF?style=for-the-badge&logo=githubactions&logoColor=white) |

</div>

---

## Características Principais

### Funcionalidades Principais
<table>
<tr>
<th width="30%">Característica</th>
<th width="70%">Descrição</th>
</tr>
<tr>
<td><strong>🎵 Reprodução sem anúncios</strong></td>
<td>Ouça música sem interrupções publicitárias</td>
</tr>
<tr>
<td><strong>🔄 Reprodução em segundo plano</strong></td>
<td>Continue ouvindo enquanto usa outros aplicativos</td>
</tr>
<tr>
<td><strong>🔍 Busca avançada</strong></td>
<td>Encontre rapidamente músicas, vídeos, álbuns e playlists</td>
</tr>
<tr>
<td><strong>👤 Integração de conta</strong></td>
<td>Entre para sincronizar preferências e coleções</td>
</tr>
<tr>
<td><strong>📚 Gerenciamento da biblioteca</strong></td>
<td>Organize e administre toda a sua coleção musical</td>
</tr>
<tr>
<td><strong>📱 Modo offline</strong></td>
<td>Baixe conteúdo para ouvir sem conexão</td>
</tr>
</table>

### Melhorias de Áudio
<table>
<tr>
<th width="30%">Característica</th>
<th width="70%">Descrição</th>
</tr>
<tr>
<td><strong>🎤 Letras sincronizadas</strong></td>
<td>Veja as letras das músicas perfeitamente sincronizadas</td>
</tr>
<tr>
<td><strong>⚡ Pulo inteligente de silêncio</strong></td>
<td>Pula automaticamente trechos sem áudio</td>
</tr>
<tr>
<td><strong>🔊 Normalização de volume</strong></td>
<td>Equilibra os níveis de som entre faixas diferentes, com atalho no menu do player</td>
</tr>
<tr>
<td><strong>🎛️ Controle de tempo e tom</strong></td>
<td>Ajuste a velocidade e o tom da reprodução conforme sua preferência</td>
</tr>
</table>

### Personalização e Integração
<table>
<tr>
<th width="30%">Característica</th>
<th width="70%">Descrição</th>
</tr>
<tr>
<td><strong>🎨 Temas dinâmicos</strong></td>
<td>A interface se adapta às cores das capas dos álbuns</td>
</tr>
<tr>
<td><strong>🌐 Suporte a vários idiomas</strong></td>
<td>Disponível em vários idiomas para usuários do mundo todo</td>
</tr>
<tr>
<td><strong>🚗 Compatível com Android Auto</strong></td>
<td>Integração com sistemas de infoentretenimento veicular, com ícone e nome da fila no cabeçalho do player</td>
</tr>
<tr>
<td><strong>📺 Cast e seletor de saída</strong></td>
<td>Transmita para Chromecast e alterne entre telefone, Bluetooth, fones e USB</td>
</tr>
<tr>
<td><strong>🎯 Material Design 3</strong></td>
<td>Design alinhado às diretrizes mais recentes do Google</td>
</tr>
<tr>
<td><strong>🖼️ Exportação de capas</strong></td>
<td>Salve imagens de álbuns em alta resolução</td>
</tr>
</table>

---

## Documentação

Para informações detalhadas sobre configuração, recursos avançados e guias de uso do aplicativo original, consulte a documentação oficial do OpenTune:

<div align="center">

[![Documentação](https://img.shields.io/badge/Documentação-GitBook-4285F4?style=for-the-badge&logo=gitbook&logoColor=white)](https://opentune.gitbook.io/)

</div>

---

## Instalação

### Requisitos do Sistema

| Componente | Requisito Mínimo |
|:-----------|:-----------------|
| Sistema operacional | Android 8.0 (Oreo) ou superior |
| Espaço de armazenamento | 10 MB livres |
| Rede | Conexão com a internet para streaming (Wi‑Fi para usar o Cast) |
| RAM | 2 GB recomendados |
| Google Play Services | Necessário para o Google Cast |

### Métodos de Instalação

#### Opção 1: Releases do GitHub (Recomendado)

1. Acesse a seção de [Releases](https://github.com/Piettro-Freit/OpenCast/releases) do GitHub
2. Baixe o arquivo APK da versão mais recente
3. Ative "Instalar de fontes desconhecidas" nas configurações de segurança do aparelho
4. Abra o APK baixado para concluir a instalação

#### Opção 2: APK gerado pelo GitHub Actions

1. Abra a aba **Actions** do repositório e entre na execução mais recente do workflow **Compilar APK**
2. Em **Artifacts**, baixe `app-release-apk` (mais fluido) ou `app-debug-apk`
3. Instale o APK no aparelho

> **Observações**: o APK de release é assinado com a chave de debug. Se o OpenTune original já estiver instalado, desinstale-o antes, porque a assinatura é diferente. O APK de debug usa o pacote `com.Arturo254.opentune.debug` e é bem mais lento que o de release.

> **Aviso de segurança**: por segurança, obtenha o aplicativo apenas por canais confiáveis. Evite APKs de fontes não verificadas.

---

## Compilação a partir do Código-Fonte

### Pré-requisitos

<table>
<tr>
<th>Ferramenta</th>
<th>Versão</th>
<th>Finalidade</th>
</tr>
<tr>
<td>Gradle</td>
<td>9.4.1</td>
<td>Automação de build</td>
</tr>
<tr>
<td>Android Gradle Plugin</td>
<td>9.1.0</td>
<td>Build Android</td>
</tr>
<tr>
<td>Kotlin</td>
<td>2.3.20</td>
<td>Linguagem de programação</td>
</tr>
<tr>
<td>JDK</td>
<td>21</td>
<td>Ambiente de execução Java</td>
</tr>
<tr>
<td>Android SDK</td>
<td>API 36 (compileSdk e targetSdk), minSdk 26</td>
<td>Ferramentas de desenvolvimento Android</td>
</tr>
<tr>
<td>Android Studio</td>
<td>Versão recente com suporte ao AGP 9</td>
<td>IDE e ambiente de desenvolvimento</td>
</tr>
</table>

### Configuração do Ambiente

```bash
# Clonar o repositório
git clone https://github.com/Piettro-Freit/OpenCast.git

# Entrar no diretório do projeto
cd OpenCast

# Atualizar submódulos (se houver)
git submodule update --init --recursive
```

### Métodos de Compilação

O projeto tem uma variante por arquitetura (`universal`, `arm64`, `armeabi`, `x86`, `x86_64`). Para instalar em qualquer aparelho, use a **universal**.

#### Compilação com Android Studio

1. Abra o Android Studio
2. Selecione "Open an existing project"
3. Escolha o diretório do OpenCast
4. Aguarde a sincronização e a indexação
5. Em **Build Variants**, escolha `universalDebug` ou `universalRelease`
6. Selecione Build → Build Bundle(s) / APK(s) → Build APK(s)

#### Compilação pela Linha de Comando

```bash
# APK universal de release (recomendado para uso no dia a dia)
gradle assembleUniversalRelease

# APK universal de debug
gradle assembleUniversalDebug

# Limpar a compilação
gradle clean
```

> **Observação**: os APKs ficam em `app/build/outputs/apk/universal/debug/` e `app/build/outputs/apk/universal/release/`.

#### Compilação pelo GitHub Actions

O arquivo `.github/workflows/build-apk.yml` compila o APK universal de debug e de release a cada push na branch `main`/`master`, ou manualmente em **Actions → Compilar APK → Run workflow**. Os APKs ficam disponíveis como artefatos da execução.

---

## Contribuições

### Código de Conduta

Todos os participantes devem seguir o código de conduta, que promove um ambiente inclusivo, respeitoso e construtivo. Leia o [Código de Conduta completo](CODE_OF_CONDUCT.md) antes de contribuir.

### Tradução

Ajude a traduzir o OpenTune para o seu idioma ou a melhorar as traduções existentes (projeto original):

<div align="center">

[![POEditor](https://img.shields.io/badge/POEditor-2196F3?style=for-the-badge&logo=translate&logoColor=white)](https://poeditor.com/join/project/208BwCVazA)
[![Crowdin](https://img.shields.io/badge/Crowdin-2E3440?style=for-the-badge&logo=crowdin&logoColor=white)](https://crowdin.com/project/opentune)

</div>

### Canais da Comunidade (OpenTune)

<div align="center">

[![Chat do Telegram](https://img.shields.io/badge/Telegram-Chat-2CA5E0?style=for-the-badge&logo=telegram&logoColor=white)](https://t.me/OpenTune_chat)
[![Atualizações do Telegram](https://img.shields.io/badge/Telegram-Atualizações-2CA5E0?style=for-the-badge&logo=telegram&logoColor=white)](https://t.me/opentune_updates)

</div>

### Fluxo de Desenvolvimento

1. **Revisão de issues**: veja as [issues abertas](https://github.com/Piettro-Freit/OpenCast/issues) ou crie uma nova descrevendo o problema ou a funcionalidade
2. **Fork do repositório**: crie um fork pessoal
3. **Branch de funcionalidade**: crie uma branch (`git checkout -b feature/nova-funcionalidade`)
4. **Implementação**: faça as mudanças seguindo as convenções de código do projeto
5. **Testes**: confirme que o código compila e passa nos testes
6. **Commit**: use mensagens descritivas (`git commit -m 'feat: adicionar nova funcionalidade'`)
7. **Push**: envie as mudanças ao seu fork (`git push origin feature/nova-funcionalidade`)
8. **Pull Request**: abra um PR descrevendo as mudanças e citando a issue correspondente

> **Diretrizes de desenvolvimento**: consulte as [diretrizes de contribuição](CONTRIBUTING.md) para detalhes sobre o processo, os padrões de código e o fluxo de trabalho.

---

## Apoie o Projeto

O OpenCast é construído sobre o trabalho do **OpenTune**. Se você gosta do aplicativo e quer apoiar o desenvolvimento, considere apoiar o criador do projeto original:

<div align="center">

[![GitHub Sponsors](https://img.shields.io/badge/GitHub_Sponsors-181717?style=for-the-badge&logo=github&logoColor=white)](https://github.com/sponsors/Arturo254)

</div>

> **Observação**: as doações são totalmente opcionais. O aplicativo sempre será gratuito e de código aberto, independentemente de qualquer apoio financeiro.

---

## 🌟 Reconhecimentos

O OpenCast existe graças ao trabalho, à inspiração e ao apoio de vários projetos e pessoas da comunidade de aplicativos musicais de código aberto.

---

### 🏗️ Projeto base

- **[OpenTune](https://github.com/Arturo254/OpenTune)**, de Arturo Cervantes  
  Aplicativo original sobre o qual este fork foi construído.

---

### 💡 Projetos que inspiraram o OpenTune

- **[ArchiveTune](https://github.com/koiverse/ArchiveTune)**  
  Projeto base e inspiração inicial de muitas das ideias e funcionalidades do OpenTune.

- **[Vivi Music](https://github.com/vivizzz007/vivi-music)**  
  Código-fonte do Canvas, da API e inspiração.

---

### 🤝 Colaboradores principais do OpenTune

- **[@Fabito02](https://github.com/Fabito02)**  
  Pelo apoio constante, feedback, ideias e acompanhamento durante o desenvolvimento inicial do projeto.

---

### 🌍 Comunidade

- **Tradutores da comunidade**  
  Obrigado a todos que ajudam a manter o aplicativo disponível em vários idiomas.

- **Beta testers**  
  Obrigado a quem testa novas versões, reporta erros e dá feedback para melhorar a estabilidade, o desempenho e a experiência de uso.

---

### ❤️ Inspiração Open Source

Também reconhecemos o trabalho de todos os desenvolvedores que criam ferramentas, bibliotecas e projetos de código aberto que tornam possível construir aplicativos modernos para Android. Sem a comunidade open source, este projeto não seria possível.

---

## Licença

**Copyright © 2025 Arturo Cervantes** (projeto original OpenTune)  
Modificações do OpenCast © 2026 Piettro Freit

Este programa é software livre: você pode redistribuí-lo e/ou modificá-lo sob os termos da Licença Pública Geral GNU, publicada pela Free Software Foundation, na versão 3 da Licença ou (a seu critério) em qualquer versão posterior.

Este programa é distribuído na esperança de que seja útil, mas **SEM NENHUMA GARANTIA**; sem mesmo a garantia implícita de COMERCIALIZAÇÃO ou ADEQUAÇÃO A UM PROPÓSITO PARTICULAR. Consulte a [Licença Pública Geral GNU](LICENSE) para mais detalhes.

<div align="center">

[![GPL v3](https://img.shields.io/badge/Licença-GPLv3-blue.svg?style=for-the-badge&logo=gnu&logoColor=white)](https://www.gnu.org/licenses/gpl-3.0)

</div>

> **Importante**: este é um fork de software GPLv3. Qualquer redistribuição deve manter a mesma licença e o aviso de direitos autorais do projeto original.

---

<div align="center">
  <p>Baseado no OpenTune, desenvolvido por <a href="https://github.com/Arturo254">Arturo Cervantes</a></p>
  <p>Fork e novidades por <a href="https://github.com/Piettro-Freit">Piettro Freit</a></p>

  <br>

  [![GitHub](https://img.shields.io/badge/GitHub-100000?style=for-the-badge&logo=github&logoColor=white)](https://github.com/Piettro-Freit)

</div>
