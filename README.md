# Ryukora Notes

Um app de anotações Flutter com visual minimalista, feito para capturar ideias sem distrações.

## Recursos

- Criar, editar e excluir anotações, com opção de desfazer a exclusão.
- Busca por título e conteúdo.
- Categorias: Pessoal, Trabalho, Ideias e Estudos.
- Favoritos e cores para organizar as anotações.
- Tema claro e escuro, com preferência salva.
- Persistência local usando `shared_preferences`.

## Executar

Instale o [Flutter SDK](https://docs.flutter.dev/get-started/install) e, na raiz do projeto, rode:

```bash
flutter pub get
flutter run
```

Para criar os arquivos de plataforma (Android, iOS, Web e desktop) com o Flutter instalado, execute `flutter create .` na raiz antes de rodar.

> As anotações ficam armazenadas localmente neste dispositivo. O `shared_preferences` é adequado para dados pequenos; para grandes volumes ou sincronização, considere migrar para um banco de dados.
