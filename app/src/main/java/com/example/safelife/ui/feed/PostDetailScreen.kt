// Composable que exibe os detalhes de uma postagem individual
@Composable
fun PostDetailScreen(
    postId: String,
    viewModel: PostDetailViewModel = viewModel(factory =
        PostDetailViewModelFactory(postId))
) {
    // Observa o post carregado
    val post by viewModel.post.collectAsState()
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Comentários") }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier.padding(padding).fillMaxSize().padding(16.dp)
        ) {
            // Mostra a postagem dentro de um Card
            post?.let { loadedPost ->
                Card(modifier = Modifier.fillMaxWidth(), elevation =
                    CardDefaults.cardElevation(4.dp)) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = loadedPost.authorName, style =
                            MaterialTheme.typography.titleMedium)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = loadedPost.content, style =
                            MaterialTheme.typography.bodyLarge)
                    }
                }
            }
        }
        // Observa a lista de comentários
        val comments by viewModel.comments.collectAsState()
        Text(
            text = "Comentários (${comments.size})",
            style = MaterialTheme.typography.titleSmall
        )
        LazyColumn(
            modifier = Modifier.fillMaxWidth().weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(comments) { comment ->
                // Composable separado para cada comentário
                CommentItem(comment)
            }
        }

    }
}
