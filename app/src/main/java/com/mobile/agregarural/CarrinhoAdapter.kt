class CarrinhoAdapter(
    private val itens: List<ItemCarrinho>
) : RecyclerView.Adapter<CarrinhoAdapter.CarrinhoViewHolder>() {

    inner class CarrinhoViewHolder(val binding: ItemCarrinhoBinding)
        : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CarrinhoViewHolder {
        val binding = ItemCarrinhoBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return CarrinhoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CarrinhoViewHolder, position: Int) {
        val item = itens[position]
        with(holder.binding) {
            // Substitua pelos IDs reais do seu item_carrinho.xml
            // nomeProduto.text = item.nome
            // precoProduto.text = "R$ %.2f".format(item.preco)
            // quantidadeProduto.text = item.quantidade.toString()
        }
    }

    override fun getItemCount() = itens.size
}