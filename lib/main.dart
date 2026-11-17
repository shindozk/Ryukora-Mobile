import 'dart:convert';

import 'package:flutter/material.dart';
import 'package:shared_preferences/shared_preferences.dart';

void main() {
  WidgetsFlutterBinding.ensureInitialized();
  runApp(const RyukoraNotesApp());
}

const _categories = ['Pessoal', 'Trabalho', 'Ideias', 'Estudos'];
const _noteColors = [
  Color(0xFFF4E8D8),
  Color(0xFFE5EEE6),
  Color(0xFFE8E8F4),
  Color(0xFFF3E5E8),
  Color(0xFFF2EFD9),
];

class Note {
  const Note({
    required this.id,
    required this.title,
    required this.body,
    required this.category,
    required this.updatedAt,
    this.isFavorite = false,
    this.colorIndex = 0,
  });

  final String id;
  final String title;
  final String body;
  final String category;
  final DateTime updatedAt;
  final bool isFavorite;
  final int colorIndex;

  Note copyWith({
    String? title,
    String? body,
    String? category,
    DateTime? updatedAt,
    bool? isFavorite,
    int? colorIndex,
  }) =>
      Note(
        id: id,
        title: title ?? this.title,
        body: body ?? this.body,
        category: category ?? this.category,
        updatedAt: updatedAt ?? this.updatedAt,
        isFavorite: isFavorite ?? this.isFavorite,
        colorIndex: colorIndex ?? this.colorIndex,
      );

  Map<String, dynamic> toJson() => {
        'id': id,
        'title': title,
        'body': body,
        'category': category,
        'updatedAt': updatedAt.toIso8601String(),
        'isFavorite': isFavorite,
        'colorIndex': colorIndex,
      };

  factory Note.fromJson(Map<String, dynamic> json) => Note(
        id: json['id'] as String,
        title: json['title'] as String? ?? '',
        body: json['body'] as String? ?? '',
        category: json['category'] as String? ?? _categories.first,
        updatedAt: DateTime.tryParse(json['updatedAt'] as String? ?? '') ??
            DateTime.now(),
        isFavorite: json['isFavorite'] as bool? ?? false,
        colorIndex: (json['colorIndex'] as int? ?? 0) % _noteColors.length,
      );
}

class RyukoraNotesApp extends StatefulWidget {
  const RyukoraNotesApp({super.key});

  @override
  State<RyukoraNotesApp> createState() => _RyukoraNotesAppState();
}

class _RyukoraNotesAppState extends State<RyukoraNotesApp> {
  ThemeMode _themeMode = ThemeMode.light;

  @override
  void initState() {
    super.initState();
    _loadTheme();
  }

  Future<void> _loadTheme() async {
    final prefs = await SharedPreferences.getInstance();
    if (!mounted) return;
    setState(() {
      _themeMode = prefs.getBool('darkMode') == true
          ? ThemeMode.dark
          : ThemeMode.light;
    });
  }

  Future<void> _toggleTheme() async {
    final isDark = _themeMode != ThemeMode.dark;
    setState(() => _themeMode = isDark ? ThemeMode.dark : ThemeMode.light);
    final prefs = await SharedPreferences.getInstance();
    await prefs.setBool('darkMode', isDark);
  }

  @override
  Widget build(BuildContext context) => MaterialApp(
        title: 'Ryukora Notes',
        debugShowCheckedModeBanner: false,
        themeMode: _themeMode,
        theme: _buildTheme(Brightness.light),
        darkTheme: _buildTheme(Brightness.dark),
        home: NotesHomePage(
          isDark: _themeMode == ThemeMode.dark,
          onToggleTheme: _toggleTheme,
        ),
      );

  ThemeData _buildTheme(Brightness brightness) {
    final isDark = brightness == Brightness.dark;
    final scheme = ColorScheme.fromSeed(
      seedColor: const Color(0xFF526B5B),
      brightness: brightness,
      surface: isDark ? const Color(0xFF171A18) : const Color(0xFFFAF9F6),
    );
    return ThemeData(
      useMaterial3: true,
      colorScheme: scheme,
      scaffoldBackgroundColor: scheme.surface,
      fontFamily: 'Roboto',
      appBarTheme: AppBarTheme(
        backgroundColor: scheme.surface,
        foregroundColor: scheme.onSurface,
        elevation: 0,
        surfaceTintColor: Colors.transparent,
      ),
      inputDecorationTheme: InputDecorationTheme(
        filled: true,
        fillColor: isDark ? const Color(0xFF222623) : const Color(0xFFF0EFEA),
        border: OutlineInputBorder(
          borderRadius: BorderRadius.circular(16),
          borderSide: BorderSide.none,
        ),
        contentPadding: const EdgeInsets.symmetric(horizontal: 16, vertical: 14),
      ),
    );
  }
}

class NotesHomePage extends StatefulWidget {
  const NotesHomePage({
    required this.isDark,
    required this.onToggleTheme,
    super.key,
  });

  final bool isDark;
  final VoidCallback onToggleTheme;

  @override
  State<NotesHomePage> createState() => _NotesHomePageState();
}

class _NotesHomePageState extends State<NotesHomePage> {
  static const _storageKey = 'ryukora_notes_v1';
  final _searchController = TextEditingController();
  List<Note> _notes = [];
  String _selectedCategory = 'Todas';
  bool _favoritesOnly = false;
  bool _loading = true;

  @override
  void initState() {
    super.initState();
    _loadNotes();
  }

  @override
  void dispose() {
    _searchController.dispose();
    super.dispose();
  }

  Future<void> _loadNotes() async {
    final prefs = await SharedPreferences.getInstance();
    final stored = prefs.getString(_storageKey);
    var notes = <Note>[];
    if (stored != null) {
      try {
        notes = (jsonDecode(stored) as List<dynamic>)
            .map((item) => Note.fromJson(item as Map<String, dynamic>))
            .toList();
      } on FormatException {
        notes = [];
      } on TypeError {
        notes = [];
      }
    }
    if (!mounted) return;
    setState(() {
      _notes = notes..sort((a, b) => b.updatedAt.compareTo(a.updatedAt));
      _loading = false;
    });
  }

  Future<void> _saveNotes() async {
    final prefs = await SharedPreferences.getInstance();
    await prefs.setString(
      _storageKey,
      jsonEncode(_notes.map((note) => note.toJson()).toList()),
    );
  }

  List<Note> get _visibleNotes {
    final query = _searchController.text.trim().toLowerCase();
    return _notes.where((note) {
      final matchesCategory = _selectedCategory == 'Todas' ||
          note.category == _selectedCategory;
      final matchesFavorite = !_favoritesOnly || note.isFavorite;
      final matchesSearch = query.isEmpty ||
          note.title.toLowerCase().contains(query) ||
          note.body.toLowerCase().contains(query);
      return matchesCategory && matchesFavorite && matchesSearch;
    }).toList();
  }

  Future<void> _openEditor([Note? note]) async {
    final edited = await Navigator.of(context).push<Note>(
      MaterialPageRoute(builder: (_) => NoteEditorPage(note: note)),
    );
    if (edited == null || !mounted) return;
    setState(() {
      final index = _notes.indexWhere((item) => item.id == edited.id);
      if (index == -1) {
        _notes.insert(0, edited);
      } else {
        _notes[index] = edited;
      }
      _notes.sort((a, b) => b.updatedAt.compareTo(a.updatedAt));
    });
    await _saveNotes();
  }

  Future<void> _toggleFavorite(Note note) async {
    setState(() {
      final index = _notes.indexWhere((item) => item.id == note.id);
      if (index != -1) {
        _notes[index] = note.copyWith(isFavorite: !note.isFavorite);
      }
    });
    await _saveNotes();
  }

  Future<void> _deleteNote(Note note) async {
    setState(() => _notes.removeWhere((item) => item.id == note.id));
    await _saveNotes();
    if (!mounted) return;
    ScaffoldMessenger.of(context).showSnackBar(
      SnackBar(
        content: const Text('Anotação excluída'),
        behavior: SnackBarBehavior.floating,
        action: SnackBarAction(
          label: 'Desfazer',
          onPressed: () async {
            setState(() => _notes.insert(0, note));
            await _saveNotes();
          },
        ),
      ),
    );
  }

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);
    final visibleNotes = _visibleNotes;
    return Scaffold(
      body: SafeArea(
        child: CustomScrollView(
          slivers: [
            SliverToBoxAdapter(child: _buildHeader(theme)),
            SliverToBoxAdapter(child: _buildSearch(theme)),
            SliverToBoxAdapter(child: _buildFilters(theme)),
            SliverToBoxAdapter(child: _buildSectionHeader(theme, visibleNotes.length)),
            if (_loading)
              const SliverFillRemaining(
                hasScrollBody: false,
                child: Center(child: CircularProgressIndicator()),
              )
            else if (visibleNotes.isEmpty)
              SliverFillRemaining(
                hasScrollBody: false,
                child: _buildEmptyState(theme),
              )
            else
              SliverPadding(
                padding: const EdgeInsets.fromLTRB(20, 2, 20, 110),
                sliver: SliverList.separated(
                  itemCount: visibleNotes.length,
                  separatorBuilder: (_, __) => const SizedBox(height: 12),
                  itemBuilder: (context, index) => _NoteCard(
                    note: visibleNotes[index],
                    onTap: () => _openEditor(visibleNotes[index]),
                    onFavorite: () => _toggleFavorite(visibleNotes[index]),
                    onDelete: () => _deleteNote(visibleNotes[index]),
                  ),
                ),
              ),
          ],
        ),
      ),
      floatingActionButton: FloatingActionButton.extended(
        onPressed: () => _openEditor(),
        icon: const Icon(Icons.add_rounded),
        label: const Text('Nova anotação'),
      ),
    );
  }

  Widget _buildHeader(ThemeData theme) => Padding(
        padding: const EdgeInsets.fromLTRB(22, 18, 18, 18),
        child: Row(
          children: [
            Container(
              width: 44,
              height: 44,
              decoration: BoxDecoration(
                color: theme.colorScheme.primary,
                borderRadius: BorderRadius.circular(15),
              ),
              child: Icon(Icons.edit_note_rounded,
                  color: theme.colorScheme.onPrimary, size: 28),
            ),
            const SizedBox(width: 12),
            Expanded(
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Text('ryukora',
                      style: theme.textTheme.titleLarge?.copyWith(
                        fontWeight: FontWeight.w800,
                        letterSpacing: -0.8,
                      )),
                  Text('seu espaço de ideias',
                      style: theme.textTheme.bodySmall?.copyWith(
                        color: theme.colorScheme.onSurfaceVariant,
                      )),
                ],
              ),
            ),
            IconButton(
              tooltip: widget.isDark ? 'Ativar tema claro' : 'Ativar tema escuro',
              onPressed: widget.onToggleTheme,
              icon: Icon(widget.isDark
                  ? Icons.light_mode_outlined
                  : Icons.dark_mode_outlined),
            ),
          ],
        ),
      );

  Widget _buildSearch(ThemeData theme) => Padding(
        padding: const EdgeInsets.symmetric(horizontal: 20),
        child: TextField(
          controller: _searchController,
          onChanged: (_) => setState(() {}),
          textInputAction: TextInputAction.search,
          decoration: InputDecoration(
            hintText: 'Buscar anotações...',
            prefixIcon: const Icon(Icons.search_rounded),
            suffixIcon: _searchController.text.isEmpty
                ? null
                : IconButton(
                    tooltip: 'Limpar busca',
                    onPressed: () {
                      _searchController.clear();
                      setState(() {});
                    },
                    icon: const Icon(Icons.close_rounded),
                  ),
          ),
        ),
      );

  Widget _buildFilters(ThemeData theme) => Padding(
        padding: const EdgeInsets.fromLTRB(20, 18, 20, 20),
        child: SizedBox(
          height: 38,
          child: ListView(
            scrollDirection: Axis.horizontal,
            children: [
              _FilterChip(
                label: 'Todas',
                selected: _selectedCategory == 'Todas' && !_favoritesOnly,
                onTap: () => setState(() {
                  _selectedCategory = 'Todas';
                  _favoritesOnly = false;
                }),
              ),
              const SizedBox(width: 8),
              _FilterChip(
                label: 'Favoritas',
                icon: Icons.favorite_rounded,
                selected: _favoritesOnly,
                onTap: () => setState(() => _favoritesOnly = !_favoritesOnly),
              ),
              ..._categories.map((category) => Padding(
                    padding: const EdgeInsets.only(left: 8),
                    child: _FilterChip(
                      label: category,
                      selected: _selectedCategory == category,
                      onTap: () => setState(() {
                        _selectedCategory = category;
                        _favoritesOnly = false;
                      }),
                    ),
                  )),
            ],
          ),
        ),
      );

  Widget _buildSectionHeader(ThemeData theme, int count) => Padding(
        padding: const EdgeInsets.fromLTRB(22, 0, 22, 14),
        child: Row(
          children: [
            Text('Suas anotações',
                style: theme.textTheme.titleMedium?.copyWith(
                  fontWeight: FontWeight.w700,
                  letterSpacing: -0.2,
                )),
            const SizedBox(width: 8),
            Container(
              padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 3),
              decoration: BoxDecoration(
                color: theme.colorScheme.primary.withOpacity(0.1),
                borderRadius: BorderRadius.circular(20),
              ),
              child: Text('$count',
                  style: theme.textTheme.labelSmall?.copyWith(
                    color: theme.colorScheme.primary,
                    fontWeight: FontWeight.w700,
                  )),
            ),
          ],
        ),
      );

  Widget _buildEmptyState(ThemeData theme) {
    final hasFilter = _searchController.text.isNotEmpty ||
        _selectedCategory != 'Todas' ||
        _favoritesOnly;
    return Center(
      child: Padding(
        padding: const EdgeInsets.all(36),
        child: Column(
          mainAxisSize: MainAxisSize.min,
          children: [
            Container(
              width: 76,
              height: 76,
              decoration: BoxDecoration(
                color: theme.colorScheme.primary.withOpacity(0.08),
                shape: BoxShape.circle,
              ),
              child: Icon(
                hasFilter ? Icons.search_off_rounded : Icons.notes_rounded,
                size: 34,
                color: theme.colorScheme.primary,
              ),
            ),
            const SizedBox(height: 18),
            Text(
              hasFilter ? 'Nada por aqui' : 'Uma página em branco.',
              style: theme.textTheme.titleMedium?.copyWith(
                fontWeight: FontWeight.w700,
              ),
            ),
            const SizedBox(height: 7),
            Text(
              hasFilter
                  ? 'Tente outra busca ou categoria.'
                  : 'Guarde aqui aquela ideia que não quer esquecer.',
              textAlign: TextAlign.center,
              style: theme.textTheme.bodyMedium?.copyWith(
                color: theme.colorScheme.onSurfaceVariant,
                height: 1.5,
              ),
            ),
          ],
        ),
      ),
    );
  }
}

class _FilterChip extends StatelessWidget {
  const _FilterChip({
    required this.label,
    required this.selected,
    required this.onTap,
    this.icon,
  });

  final String label;
  final bool selected;
  final VoidCallback onTap;
  final IconData? icon;

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);
    return ChoiceChip(
      label: Text(label),
      avatar: icon == null ? null : Icon(icon, size: 15),
      selected: selected,
      onSelected: (_) => onTap(),
      showCheckmark: false,
      labelStyle: theme.textTheme.labelMedium?.copyWith(
        fontWeight: FontWeight.w600,
        color: selected ? theme.colorScheme.onPrimary : theme.colorScheme.onSurface,
      ),
      selectedColor: theme.colorScheme.primary,
      backgroundColor: theme.colorScheme.surface,
      side: BorderSide(
        color: selected
            ? theme.colorScheme.primary
            : theme.colorScheme.outlineVariant.withOpacity(0.55),
      ),
      shape: const StadiumBorder(),
      padding: const EdgeInsets.symmetric(horizontal: 8),
    );
  }
}

class _NoteCard extends StatelessWidget {
  const _NoteCard({
    required this.note,
    required this.onTap,
    required this.onFavorite,
    required this.onDelete,
  });

  final Note note;
  final VoidCallback onTap;
  final VoidCallback onFavorite;
  final VoidCallback onDelete;

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);
    final isDark = theme.brightness == Brightness.dark;
    final noteColor = isDark
        ? Color.lerp(_noteColors[note.colorIndex], const Color(0xFF202421), 0.78)!
        : _noteColors[note.colorIndex];
    return Material(
      color: noteColor,
      borderRadius: BorderRadius.circular(20),
      child: InkWell(
        onTap: onTap,
        borderRadius: BorderRadius.circular(20),
        child: Padding(
          padding: const EdgeInsets.fromLTRB(18, 16, 12, 16),
          child: Row(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              Expanded(
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    Row(
                      children: [
                        Container(
                          padding: const EdgeInsets.symmetric(
                              horizontal: 9, vertical: 4),
                          decoration: BoxDecoration(
                            color: theme.colorScheme.surface.withOpacity(0.62),
                            borderRadius: BorderRadius.circular(20),
                          ),
                          child: Text(note.category,
                              style: theme.textTheme.labelSmall?.copyWith(
                                fontWeight: FontWeight.w600,
                              )),
                        ),
                        const Spacer(),
                        Text(_formatDate(note.updatedAt),
                            style: theme.textTheme.labelSmall?.copyWith(
                              color: theme.colorScheme.onSurfaceVariant,
                            )),
                      ],
                    ),
                    const SizedBox(height: 12),
                    Text(
                      note.title.isEmpty ? 'Sem título' : note.title,
                      maxLines: 2,
                      overflow: TextOverflow.ellipsis,
                      style: theme.textTheme.titleMedium?.copyWith(
                        fontWeight: FontWeight.w700,
                        letterSpacing: -0.25,
                      ),
                    ),
                    if (note.body.trim().isNotEmpty) ...[
                      const SizedBox(height: 6),
                      Text(
                        note.body,
                        maxLines: 2,
                        overflow: TextOverflow.ellipsis,
                        style: theme.textTheme.bodyMedium?.copyWith(
                          color: theme.colorScheme.onSurfaceVariant,
                          height: 1.45,
                        ),
                      ),
                    ],
                  ],
                ),
              ),
              PopupMenuButton<String>(
                tooltip: 'Opções da anotação',
                onSelected: (value) {
                  if (value == 'favorite') onFavorite();
                  if (value == 'delete') onDelete();
                },
                icon: Icon(Icons.more_horiz_rounded,
                    color: theme.colorScheme.onSurfaceVariant),
                itemBuilder: (_) => [
                  PopupMenuItem(
                    value: 'favorite',
                    child: Row(children: [
                      Icon(note.isFavorite
                          ? Icons.favorite_border_rounded
                          : Icons.favorite_rounded),
                      const SizedBox(width: 10),
                      Text(note.isFavorite
                          ? 'Remover dos favoritos'
                          : 'Adicionar aos favoritos'),
                    ]),
                  ),
                  const PopupMenuItem(
                    value: 'delete',
                    child: Row(children: [
                      Icon(Icons.delete_outline_rounded),
                      SizedBox(width: 10),
                      Text('Excluir'),
                    ]),
                  ),
                ],
              ),
            ],
          ),
        ),
      ),
    );
  }
}

class NoteEditorPage extends StatefulWidget {
  const NoteEditorPage({this.note, super.key});

  final Note? note;

  @override
  State<NoteEditorPage> createState() => _NoteEditorPageState();
}

class _NoteEditorPageState extends State<NoteEditorPage> {
  late final TextEditingController _titleController;
  late final TextEditingController _bodyController;
  late String _category;
  late int _colorIndex;

  @override
  void initState() {
    super.initState();
    _titleController = TextEditingController(text: widget.note?.title ?? '');
    _bodyController = TextEditingController(text: widget.note?.body ?? '');
    _category = widget.note?.category ?? _categories.first;
    _colorIndex = widget.note?.colorIndex ?? 0;
  }

  @override
  void dispose() {
    _titleController.dispose();
    _bodyController.dispose();
    super.dispose();
  }

  void _save() {
    if (_titleController.text.trim().isEmpty &&
        _bodyController.text.trim().isEmpty) {
      Navigator.of(context).pop();
      return;
    }
    final now = DateTime.now();
    final existing = widget.note;
    Navigator.of(context).pop(Note(
      id: existing?.id ?? now.microsecondsSinceEpoch.toString(),
      title: _titleController.text.trim(),
      body: _bodyController.text.trim(),
      category: _category,
      updatedAt: now,
      isFavorite: existing?.isFavorite ?? false,
      colorIndex: _colorIndex,
    ));
  }

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);
    final isEditing = widget.note != null;
    return Scaffold(
      appBar: AppBar(
        leading: IconButton(
          tooltip: 'Voltar',
          icon: const Icon(Icons.close_rounded),
          onPressed: () => Navigator.of(context).maybePop(),
        ),
        title: Text(isEditing ? 'Editar anotação' : 'Nova anotação',
            style: theme.textTheme.titleMedium?.copyWith(
              fontWeight: FontWeight.w700,
            )),
        centerTitle: true,
        actions: [
          TextButton.icon(
            onPressed: _save,
            icon: const Icon(Icons.check_rounded, size: 19),
            label: const Text('Salvar'),
          ),
          const SizedBox(width: 10),
        ],
      ),
      body: SafeArea(
        child: Column(
          children: [
            Padding(
              padding: const EdgeInsets.fromLTRB(22, 12, 22, 12),
              child: Row(
                children: [
                  Container(
                    padding: const EdgeInsets.symmetric(
                        horizontal: 12, vertical: 3),
                    decoration: BoxDecoration(
                      color: theme.colorScheme.primary.withOpacity(0.08),
                      borderRadius: BorderRadius.circular(14),
                    ),
                    child: DropdownButtonHideUnderline(
                      child: DropdownButton<String>(
                        value: _category,
                        borderRadius: BorderRadius.circular(14),
                        items: _categories
                            .map((item) => DropdownMenuItem(
                                  value: item,
                                  child: Text(item),
                                ))
                            .toList(),
                        onChanged: (value) {
                          if (value != null) setState(() => _category = value);
                        },
                      ),
                    ),
                  ),
                  const Spacer(),
                  Text('TOQUE PARA ESCREVER',
                      style: theme.textTheme.labelSmall?.copyWith(
                        color: theme.colorScheme.onSurfaceVariant,
                        letterSpacing: 0.8,
                      )),
                ],
              ),
            ),
            Expanded(
              child: Padding(
                padding: const EdgeInsets.fromLTRB(22, 4, 22, 12),
                child: Column(
                  children: [
                    TextField(
                      controller: _titleController,
                      textCapitalization: TextCapitalization.sentences,
                      style: theme.textTheme.headlineSmall?.copyWith(
                        fontWeight: FontWeight.w700,
                        letterSpacing: -0.6,
                      ),
                      decoration: const InputDecoration(
                        hintText: 'Dê um título à sua ideia',
                        filled: false,
                        contentPadding: EdgeInsets.zero,
                      ),
                      maxLines: null,
                    ),
                    const SizedBox(height: 18),
                    Expanded(
                      child: TextField(
                        controller: _bodyController,
                        textCapitalization: TextCapitalization.sentences,
                        keyboardType: TextInputType.multiline,
                        expands: true,
                        maxLines: null,
                        textAlignVertical: TextAlignVertical.top,
                        style: theme.textTheme.bodyLarge?.copyWith(height: 1.65),
                        decoration: InputDecoration(
                          hintText: 'Comece a escrever...',
                          filled: false,
                          contentPadding: EdgeInsets.zero,
                          hintStyle: TextStyle(
                            color: theme.colorScheme.onSurfaceVariant
                                .withOpacity(0.7),
                          ),
                        ),
                      ),
                    ),
                  ],
                ),
              ),
            ),
            Padding(
              padding: const EdgeInsets.fromLTRB(22, 8, 22, 18),
              child: Row(
                children: [
                  Text('COR DA ANOTAÇÃO',
                      style: theme.textTheme.labelSmall?.copyWith(
                        color: theme.colorScheme.onSurfaceVariant,
                        letterSpacing: 0.8,
                      )),
                  const SizedBox(width: 14),
                  ...List.generate(_noteColors.length, (index) {
                    final selected = _colorIndex == index;
                    return GestureDetector(
                      onTap: () => setState(() => _colorIndex = index),
                      child: Container(
                        width: 27,
                        height: 27,
                        margin: const EdgeInsets.only(right: 9),
                        decoration: BoxDecoration(
                          color: _noteColors[index],
                          shape: BoxShape.circle,
                          border: Border.all(
                            color: selected
                                ? theme.colorScheme.primary
                                : theme.colorScheme.outlineVariant,
                            width: selected ? 2 : 1,
                          ),
                        ),
                        child: selected
                            ? Icon(Icons.check_rounded,
                                size: 15, color: theme.colorScheme.primary)
                            : null,
                      ),
                    );
                  }),
                ],
              ),
            ),
          ],
        ),
      ),
    );
  }
}

String _formatDate(DateTime date) {
  final now = DateTime.now();
  final today = DateTime(now.year, now.month, now.day);
  final day = DateTime(date.year, date.month, date.day);
  if (day == today) {
    final hour = date.hour.toString().padLeft(2, '0');
    final minute = date.minute.toString().padLeft(2, '0');
    return 'Hoje, $hour:$minute';
  }
  if (day == today.subtract(const Duration(days: 1))) return 'Ontem';
  const months = [
    'jan', 'fev', 'mar', 'abr', 'mai', 'jun',
    'jul', 'ago', 'set', 'out', 'nov', 'dez'
  ];
  return '${date.day} ${months[date.month - 1]}';
}
